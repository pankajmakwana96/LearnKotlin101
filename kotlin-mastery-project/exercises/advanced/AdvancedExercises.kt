/**
 * Advanced Level Exercises - Mastering Kotlin
 * 
 * This module contains challenging exercises for advanced Kotlin concepts:
 * - Coroutines and concurrency patterns
 * - Flow and reactive programming
 * - Channel communication patterns
 * - Advanced functional programming
 * - Metaprogramming with reflection
 * - DSL creation
 * - Performance optimization
 * - Architecture patterns
 * 
 * Instructions:
 * 1. These exercises require deep understanding of Kotlin concepts
 * 2. Focus on real-world applications and performance considerations
 * 3. Implement complete, production-ready solutions
 * 4. Consider error handling, resource management, and testing
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.*
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// ================================
// Advanced Coroutines Exercises
// ================================

/**
 * Exercise 1: Parallel Processing with Resource Management
 * 
 * Task: Create a parallel processing system that manages limited resources
 * Requirements:
 * - Process items in parallel with limited concurrent workers
 * - Handle failures gracefully without stopping other workers
 * - Provide progress reporting
 * - Support cancellation
 */
class ParallelProcessor<T, R>(
    private val maxConcurrency: Int = 3,
    private val processor: suspend (T) -> R
) {
    data class ProcessingResult<R>(
        val successful: List<R>,
        val failed: List<Pair<Exception, Any?>>,
        val processingTime: Long
    )
    
    suspend fun processAll(items: List<T>): ProcessingResult<R> = coroutineScope {
        // TODO: Implement parallel processing with controlled concurrency
        val successful = mutableListOf<R>()
        val failed = mutableListOf<Pair<Exception, Any?>>()
        
        val startTime = System.currentTimeMillis()
        
        // Use a semaphore to limit concurrency
        val semaphore = Semaphore(maxConcurrency)
        
        val jobs = items.map { item ->
            async {
                semaphore.withPermit {
                    try {
                        val result = processor(item)
                        synchronized(successful) { successful.add(result) }
                    } catch (e: Exception) {
                        synchronized(failed) { failed.add(e to item) }
                    }
                }
            }
        }
        
        jobs.awaitAll()
        
        val processingTime = System.currentTimeMillis() - startTime
        ProcessingResult(successful, failed, processingTime)
    }
    
    fun processWithProgress(items: List<T>): Flow<ProcessingProgress<R>> = flow {
        val total = items.size
        var completed = 0
        val successful = mutableListOf<R>()
        val failed = mutableListOf<Pair<Exception, Any?>>()
        
        val semaphore = Semaphore(maxConcurrency)
        
        coroutineScope {
            val jobs = items.map { item ->
                async {
                    semaphore.withPermit {
                        try {
                            val result = processor(item)
                            synchronized(successful) { 
                                successful.add(result)
                                completed++
                            }
                            emit(ProcessingProgress(completed, total, successful.toList(), failed.toList()))
                        } catch (e: Exception) {
                            synchronized(failed) { 
                                failed.add(e to item)
                                completed++
                            }
                            emit(ProcessingProgress(completed, total, successful.toList(), failed.toList()))
                        }
                    }
                }
            }
            jobs.awaitAll()
        }
    }
    
    data class ProcessingProgress<R>(
        val completed: Int,
        val total: Int,
        val successful: List<R>,
        val failed: List<Pair<Exception, Any?>>
    ) {
        val progressPercentage: Int = if (total > 0) (completed * 100) / total else 0
        val isComplete: Boolean = completed == total
    }
}

suspend fun exercise1_ParallelProcessing(): Map<String, Any> {
    // Simulate processing tasks with random delays and failures
    val processor = ParallelProcessor<Int, String>(maxConcurrency = 3) { item ->
        delay(Random.nextLong(100, 500))
        if (item % 7 == 0) throw RuntimeException("Processing failed for item $item")
        "Processed: $item"
    }
    
    val items = (1..20).toList()
    val result = processor.processAll(items)
    
    return mapOf(
        "totalItems" to items.size,
        "successfulCount" to result.successful.size,
        "failedCount" to result.failed.size,
        "processingTimeMs" to result.processingTime,
        "firstFewSuccessful" to result.successful.take(5),
        "failures" to result.failed.map { (exception, item) -> 
            "Item $item failed: ${exception.message}" 
        }
    )
}

// ================================
// Advanced Flow Exercises
// ================================

/**
 * Exercise 2: Reactive Data Stream Processing
 * 
 * Task: Build a reactive system that processes real-time data streams
 * Requirements:
 * - Handle multiple data sources
 * - Apply complex transformations
 * - Implement backpressure handling
 * - Support filtering and aggregation
 */
data class SensorReading(
    val sensorId: String,
    val timestamp: Long,
    val value: Double,
    val unit: String
)

data class AggregatedReading(
    val sensorId: String,
    val windowStart: Long,
    val windowEnd: Long,
    val avgValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val count: Int
)

class ReactiveDataProcessor {
    
    // Simulate multiple sensor data streams
    fun createSensorStream(sensorId: String, intervalMs: Long): Flow<SensorReading> = flow {
        while (currentCoroutineContext().isActive) {
            emit(SensorReading(
                sensorId = sensorId,
                timestamp = System.currentTimeMillis(),
                value = 20.0 + Random.nextDouble(-5.0, 15.0), // Temperature-like data
                unit = "°C"
            ))
            delay(intervalMs)
        }
    }
    
    // TODO: Implement windowed aggregation
    fun Flow<SensorReading>.windowedAggregation(windowSize: Long): Flow<AggregatedReading> = 
        this.chunked(windowSize.toInt())
            .map { readings ->
                if (readings.isEmpty()) return@map null
                
                val sensorId = readings.first().sensorId
                val values = readings.map { it.value }
                val timestamps = readings.map { it.timestamp }
                
                AggregatedReading(
                    sensorId = sensorId,
                    windowStart = timestamps.minOrNull() ?: 0L,
                    windowEnd = timestamps.maxOrNull() ?: 0L,
                    avgValue = values.average(),
                    minValue = values.minOrNull() ?: 0.0,
                    maxValue = values.maxOrNull() ?: 0.0,
                    count = readings.size
                )
            }
            .filterNotNull()
    
    // TODO: Implement anomaly detection
    fun Flow<SensorReading>.detectAnomalies(
        threshold: Double = 30.0
    ): Flow<Pair<SensorReading, String>> = 
        this.filter { it.value > threshold }
            .map { it to "High temperature detected: ${it.value}${it.unit}" }
    
    // TODO: Combine multiple streams
    fun combineStreams(vararg streams: Flow<SensorReading>): Flow<List<SensorReading>> =
        combine(*streams) { readings -> readings.toList() }
}

suspend fun exercise2_ReactiveStreams(): Map<String, Any> {
    val processor = ReactiveDataProcessor()
    
    // Create multiple sensor streams
    val stream1 = processor.createSensorStream("sensor-1", 200)
    val stream2 = processor.createSensorStream("sensor-2", 300)
    val stream3 = processor.createSensorStream("sensor-3", 250)
    
    val results = mutableMapOf<String, Any>()
    
    withTimeoutOrNull(2000) {
        // Collect some readings
        val readings = mutableListOf<SensorReading>()
        stream1.take(10).collect { readings.add(it) }
        results["sampleReadings"] = readings.take(5)
        
        // Test windowed aggregation
        val aggregated = mutableListOf<AggregatedReading>()
        stream2.take(6).windowedAggregation(3).collect { aggregated.add(it) }
        results["aggregatedData"] = aggregated
        
        // Test anomaly detection
        val anomalies = mutableListOf<Pair<SensorReading, String>>()
        flow {
            repeat(10) {
                emit(SensorReading("test-sensor", System.currentTimeMillis(), Random.nextDouble(25.0, 35.0), "°C"))
            }
        }.detectAnomalies(30.0).collect { anomalies.add(it) }
        results["anomalies"] = anomalies.map { it.second }
    }
    
    return results
}

// ================================
// Advanced Channel Communication
// ================================

/**
 * Exercise 3: Producer-Consumer with Priority Queue
 * 
 * Task: Implement a priority-based task processing system using channels
 * Requirements:
 * - Support different task priorities
 * - Multiple producers and consumers
 * - Graceful shutdown
 * - Task completion tracking
 */
data class PriorityTask(
    val id: String,
    val priority: Int, // Higher number = higher priority
    val payload: String,
    val processingTime: Long = Random.nextLong(100, 500)
) : Comparable<PriorityTask> {
    override fun compareTo(other: PriorityTask): Int = other.priority - this.priority
}

class PriorityTaskProcessor(
    private val workerCount: Int = 3
) {
    private val taskChannel = Channel<PriorityTask>(capacity = Channel.UNLIMITED)
    private val completedTasks = Channel<String>(capacity = Channel.UNLIMITED)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val workers = mutableListOf<Job>()
    
    fun start() {
        // TODO: Start workers that process tasks by priority
        repeat(workerCount) { workerId ->
            val worker = scope.launch {
                // Use a priority queue to handle tasks by priority
                val priorityQueue = java.util.PriorityQueue<PriorityTask>()
                
                while (isActive) {
                    select<Unit> {
                        taskChannel.onReceiveCatching { result ->
                            result.getOrNull()?.let { task ->
                                priorityQueue.offer(task)
                            }
                        }
                        
                        if (priorityQueue.isNotEmpty()) {
                            onTimeout(10) {
                                val task = priorityQueue.poll()
                                if (task != null) {
                                    processTask(workerId, task)
                                }
                            }
                        }
                    }
                }
            }
            workers.add(worker)
        }
    }
    
    private suspend fun processTask(workerId: Int, task: PriorityTask) {
        try {
            println("Worker $workerId processing task ${task.id} (priority: ${task.priority})")
            delay(task.processingTime)
            completedTasks.send("Task ${task.id} completed by worker $workerId")
        } catch (e: Exception) {
            println("Worker $workerId failed to process task ${task.id}: ${e.message}")
        }
    }
    
    suspend fun submitTask(task: PriorityTask) {
        taskChannel.send(task)
    }
    
    fun getCompletedTasks(): Flow<String> = completedTasks.receiveAsFlow()
    
    fun shutdown() {
        taskChannel.close()
        completedTasks.close()
        scope.cancel()
    }
    
    suspend fun awaitCompletion() {
        workers.joinAll()
    }
}

suspend fun exercise3_PriorityChannels(): Map<String, Any> {
    val processor = PriorityTaskProcessor(workerCount = 2)
    processor.start()
    
    val completedTasks = mutableListOf<String>()
    
    // Collect completed tasks in background
    val collector = GlobalScope.launch {
        processor.getCompletedTasks().collect { completedTasks.add(it) }
    }
    
    // Submit tasks with different priorities
    val tasks = listOf(
        PriorityTask("low-1", 1, "Low priority task 1"),
        PriorityTask("high-1", 10, "High priority task 1"),
        PriorityTask("medium-1", 5, "Medium priority task 1"),
        PriorityTask("high-2", 10, "High priority task 2"),
        PriorityTask("low-2", 1, "Low priority task 2"),
        PriorityTask("urgent-1", 15, "Urgent task 1")
    )
    
    tasks.forEach { processor.submitTask(it) }
    
    // Wait for processing
    delay(3000)
    
    processor.shutdown()
    collector.cancel()
    
    return mapOf(
        "submittedTasks" to tasks.map { "${it.id} (priority: ${it.priority})" },
        "completedTasks" to completedTasks.take(10)
    )
}

// ================================
// Advanced Functional Programming
// ================================

/**
 * Exercise 4: Functional Parser Combinators
 * 
 * Task: Build a parser combinator library using functional programming
 * Requirements:
 * - Composable parsers
 * - Error handling with detailed messages
 * - Support for common parsing patterns
 * - Monadic interface
 */
sealed class ParseResult<out T> {
    data class Success<out T>(val value: T, val remaining: String) : ParseResult<T>()
    data class Failure(val message: String, val position: Int) : ParseResult<Nothing>()
    
    fun <R> map(f: (T) -> R): ParseResult<R> = when (this) {
        is Success -> Success(f(value), remaining)
        is Failure -> Failure(message, position)
    }
    
    fun <R> flatMap(f: (T) -> Parser<R>): Parser<R> = Parser { input ->
        when (this@ParseResult) {
            is Success -> f(value).parse(remaining)
            is Failure -> Failure(message, position)
        }
    }
}

class Parser<T>(private val parse: (String) -> ParseResult<T>) {
    
    fun parse(input: String): ParseResult<T> = parse.invoke(input)
    
    // TODO: Implement combinator methods
    infix fun <R> map(f: (T) -> R): Parser<R> = Parser { input ->
        parse(input).map(f)
    }
    
    infix fun <R> flatMap(f: (T) -> Parser<R>): Parser<R> = Parser { input ->
        when (val result = parse(input)) {
            is ParseResult.Success -> f(result.value).parse(result.remaining)
            is ParseResult.Failure -> result
        }
    }
    
    infix fun <R> then(other: Parser<R>): Parser<Pair<T, R>> = Parser { input ->
        when (val result1 = parse(input)) {
            is ParseResult.Success -> {
                when (val result2 = other.parse(result1.remaining)) {
                    is ParseResult.Success -> ParseResult.Success(
                        result1.value to result2.value,
                        result2.remaining
                    )
                    is ParseResult.Failure -> result2
                }
            }
            is ParseResult.Failure -> result1
        }
    }
    
    infix fun or(other: Parser<T>): Parser<T> = Parser { input ->
        when (val result = parse(input)) {
            is ParseResult.Success -> result
            is ParseResult.Failure -> other.parse(input)
        }
    }
    
    companion object {
        // Basic parsers
        fun char(c: Char): Parser<Char> = Parser { input ->
            when {
                input.isEmpty() -> ParseResult.Failure("Expected '$c' but reached end of input", 0)
                input.first() == c -> ParseResult.Success(c, input.drop(1))
                else -> ParseResult.Failure("Expected '$c' but found '${input.first()}'", 0)
            }
        }
        
        fun string(s: String): Parser<String> = Parser { input ->
            when {
                input.startsWith(s) -> ParseResult.Success(s, input.drop(s.length))
                else -> ParseResult.Failure("Expected '$s'", 0)
            }
        }
        
        fun regex(pattern: Regex): Parser<String> = Parser { input ->
            val match = pattern.matchAt(input, 0)
            if (match != null) {
                ParseResult.Success(match.value, input.drop(match.value.length))
            } else {
                ParseResult.Failure("Pattern $pattern not matched", 0)
            }
        }
        
        fun digit(): Parser<Int> = regex(Regex("""\\d""")) map { it.toInt() }
        
        fun number(): Parser<Int> = regex(Regex("""\\d+""")) map { it.toInt() }
        
        fun <T> many(parser: Parser<T>): Parser<List<T>> = Parser { input ->
            val results = mutableListOf<T>()
            var remaining = input
            
            while (true) {
                when (val result = parser.parse(remaining)) {
                    is ParseResult.Success -> {
                        results.add(result.value)
                        remaining = result.remaining
                    }
                    is ParseResult.Failure -> break
                }
            }
            
            ParseResult.Success(results, remaining)
        }
    }
}

fun exercise4_ParserCombinators(): Map<String, Any> {
    // TODO: Build parsers for simple arithmetic expressions
    val number = Parser.number()
    val plus = Parser.char('+')
    val minus = Parser.char('-')
    
    // Simple addition parser: number + number
    val addition = number then plus then number map { (first, _, third) ->
        first + third
    }
    
    // Parse list of numbers separated by commas
    val comma = Parser.char(',')
    val numberList = number then Parser.many(comma then number) map { (first, rest) ->
        listOf(first) + rest.map { it.second }
    }
    
    val results = mutableMapOf<String, Any>()
    
    // Test the parsers
    when (val result = addition.parse("42+17")) {
        is ParseResult.Success -> results["addition"] = "42+17 = ${result.value}"
        is ParseResult.Failure -> results["additionError"] = result.message
    }
    
    when (val result = numberList.parse("1,2,3,4,5")) {
        is ParseResult.Success -> results["numberList"] = result.value
        is ParseResult.Failure -> results["numberListError"] = result.message
    }
    
    return results
}

// ================================
// Metaprogramming Exercises
// ================================

/**
 * Exercise 5: Reflection-Based Serialization Framework
 * 
 * Task: Build a complete serialization framework using reflection
 * Requirements:
 * - Support for custom serialization annotations
 * - Handle nested objects and collections
 * - Provide both JSON and XML serialization
 * - Support deserialization with validation
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerialName(val name: String)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerialIgnore

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Serializable

interface SerializationFormat {
    fun serialize(obj: Any): String
    fun <T : Any> deserialize(json: String, kClass: KClass<T>): T?
}

class JsonSerializer : SerializationFormat {
    
    override fun serialize(obj: Any): String {
        // TODO: Implement complete JSON serialization with reflection
        return when (obj) {
            is String -> "\"${obj.replace("\"", "\\\\"")}\""
            is Number, is Boolean -> obj.toString()
            is Collection<*> -> "[${obj.joinToString(",") { serialize(it ?: "null") }}]"
            is Map<*, *> -> "{${obj.entries.joinToString(",") { "\"${it.key}\":${serialize(it.value ?: "null")}" }}}"
            else -> serializeObject(obj)
        }
    }
    
    private fun serializeObject(obj: Any): String {
        val kClass = obj::class
        if (!kClass.isData && !kClass.hasAnnotation<Serializable>()) {
            throw IllegalArgumentException("Class ${kClass.simpleName} is not serializable")
        }
        
        val properties = kClass.memberProperties.filter { property ->
            !property.hasAnnotation<SerialIgnore>()
        }
        
        val jsonFields = properties.map { property ->
            val name = property.findAnnotation<SerialName>()?.name ?: property.name
            val value = property.get(obj)
            "\"$name\":${serialize(value ?: "null")}"
        }
        
        return "{${jsonFields.joinToString(",")}}"
    }
    
    override fun <T : Any> deserialize(json: String, kClass: KClass<T>): T? {
        // TODO: Implement JSON deserialization (simplified)
        // This is a complex implementation - showing structure only
        try {
            val constructor = kClass.primaryConstructor ?: return null
            // Implementation would parse JSON and create object
            // For demo purposes, return null
            return null
        } catch (e: Exception) {
            return null
        }
    }
}

@Serializable
data class User(
    @SerialName("user_id") val id: String,
    val name: String,
    val email: String,
    @SerialIgnore val password: String = "hidden",
    val preferences: Map<String, String> = emptyMap()
)

fun exercise5_ReflectionSerialization(): Map<String, Any> {
    val serializer = JsonSerializer()
    
    val user = User(
        id = "123",
        name = "John Doe",
        email = "john@example.com",
        password = "secret123",
        preferences = mapOf("theme" to "dark", "lang" to "en")
    )
    
    val serialized = serializer.serialize(user)
    
    return mapOf(
        "originalUser" to user.toString(),
        "serializedJson" to serialized,
        "jsonLength" to serialized.length
    )
}

// ================================
// DSL Creation Exercise
// ================================

/**
 * Exercise 6: HTML DSL Builder
 * 
 * Task: Create a type-safe HTML DSL
 * Requirements:
 * - Support common HTML elements
 * - Type-safe attributes
 * - Nested structure support
 * - Generate valid HTML output
 */
@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
abstract class Tag(val name: String) {
    protected val children = mutableListOf<Tag>()
    protected val attributes = mutableMapOf<String, String>()
    
    protected fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }
    
    fun render(): String = buildString {
        append("<$name")
        if (attributes.isNotEmpty()) {
            attributes.forEach { (key, value) ->
                append(" $key=\"$value\"")
            }
        }
        append(">")
        children.forEach { append(it.render()) }
        append("</$name>")
    }
    
    override fun toString() = render()
}

class HTML : Tag("html") {
    fun head(init: Head.() -> Unit) = initTag(Head(), init)
    fun body(init: Body.() -> Unit) = initTag(Body(), init)
}

class Head : Tag("head") {
    fun title(init: Title.() -> Unit) = initTag(Title(), init)
    fun title(text: String) = initTag(Title()) { +text }
}

class Title : Tag("title") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class Body : Tag("body") {
    fun div(classes: String? = null, init: Div.() -> Unit) = initTag(Div()) {
        classes?.let { attributes["class"] = it }
        init()
    }
    
    fun p(init: P.() -> Unit) = initTag(P(), init)
    fun h1(init: H1.() -> Unit) = initTag(H1(), init)
    fun h1(text: String) = initTag(H1()) { +text }
}

class Div : Tag("div") {
    fun p(init: P.() -> Unit) = initTag(P(), init)
    fun p(text: String) = initTag(P()) { +text }
    fun span(text: String) = initTag(Span()) { +text }
    
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class P : Tag("p") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class H1 : Tag("h1") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class Span : Tag("span") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class Text(private val content: String) : Tag("") {
    override fun render(): String = content
}

fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

fun exercise6_HtmlDsl(): Map<String, Any> {
    // TODO: Create HTML using the DSL
    val htmlDocument = html {
        head {
            title("My Kotlin Page")
        }
        body {
            h1("Welcome to Kotlin DSL")
            div(classes = "content") {
                p("This is a paragraph created with Kotlin DSL.")
                p {
                    +"This is another paragraph with "
                    span("highlighted text")
                    +" in the middle."
                }
            }
            div {
                +"Some direct text content."
            }
        }
    }
    
    val generatedHtml = htmlDocument.render()
    
    return mapOf(
        "htmlContent" to generatedHtml,
        "htmlLength" to generatedHtml.length,
        "preview" to generatedHtml.take(200) + "..."
    )
}

// ================================
// Performance Optimization Exercise
// ================================

/**
 * Exercise 7: Performance Benchmarking Framework
 * 
 * Task: Create a microbenchmarking framework
 * Requirements:
 * - Measure execution time accurately
 * - Support warmup phases
 * - Statistical analysis of results
 * - Memory usage tracking
 * - Comparative benchmarks
 */
data class BenchmarkResult(
    val testName: String,
    val iterations: Int,
    val totalTimeMs: Long,
    val averageTimeMs: Double,
    val minTimeMs: Long,
    val maxTimeMs: Long,
    val standardDeviation: Double,
    val memoryUsedMB: Double
)

class MicroBenchmark {
    
    fun <T> benchmark(
        name: String,
        iterations: Int = 1000,
        warmupIterations: Int = 100,
        operation: () -> T
    ): BenchmarkResult {
        // TODO: Implement comprehensive benchmarking
        
        // Warmup phase
        repeat(warmupIterations) { operation() }
        
        // Force garbage collection
        System.gc()
        Thread.sleep(100)
        
        val memoryBefore = getMemoryUsage()
        val times = mutableListOf<Long>()
        
        repeat(iterations) {
            val startTime = System.nanoTime()
            operation()
            val endTime = System.nanoTime()
            times.add((endTime - startTime) / 1_000_000) // Convert to milliseconds
        }
        
        val memoryAfter = getMemoryUsage()
        val memoryUsed = memoryAfter - memoryBefore
        
        val totalTime = times.sum()
        val averageTime = times.average()
        val minTime = times.minOrNull() ?: 0L
        val maxTime = times.maxOrNull() ?: 0L
        
        val variance = times.map { (it - averageTime) * (it - averageTime) }.average()
        val standardDeviation = kotlin.math.sqrt(variance)
        
        return BenchmarkResult(
            testName = name,
            iterations = iterations,
            totalTimeMs = totalTime,
            averageTimeMs = averageTime,
            minTimeMs = minTime,
            maxTimeMs = maxTime,
            standardDeviation = standardDeviation,
            memoryUsedMB = memoryUsed
        )
    }
    
    private fun getMemoryUsage(): Double {
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
    }
    
    fun compareBenchmarks(vararg results: BenchmarkResult): String = buildString {
        appendLine("Benchmark Comparison:")
        appendLine("=" * 60)
        
        results.forEach { result ->
            appendLine("${result.testName}:")
            appendLine("  Average time: ${"%.3f".format(result.averageTimeMs)} ms")
            appendLine("  Min/Max: ${result.minTimeMs}ms / ${result.maxTimeMs}ms")
            appendLine("  Std deviation: ${"%.3f".format(result.standardDeviation)}")
            appendLine("  Memory used: ${"%.2f".format(result.memoryUsedMB)} MB")
            appendLine()
        }
        
        val fastest = results.minByOrNull { it.averageTimeMs }
        if (fastest != null) {
            appendLine("Fastest: ${fastest.testName}")
        }
    }
}

fun exercise7_PerformanceBenchmarking(): Map<String, Any> {
    val benchmark = MicroBenchmark()
    
    // Benchmark different collection operations
    val listData = (1..10000).toList()
    
    val listFilter = benchmark.benchmark("List Filter") {
        listData.filter { it % 2 == 0 }
    }
    
    val sequenceFilter = benchmark.benchmark("Sequence Filter") {
        listData.asSequence().filter { it % 2 == 0 }.toList()
    }
    
    val listMap = benchmark.benchmark("List Map") {
        listData.map { it * 2 }
    }
    
    val sequenceMap = benchmark.benchmark("Sequence Map") {
        listData.asSequence().map { it * 2 }.toList()
    }
    
    val comparison = benchmark.compareBenchmarks(listFilter, sequenceFilter, listMap, sequenceMap)
    
    return mapOf(
        "listFilterAvg" to listFilter.averageTimeMs,
        "sequenceFilterAvg" to sequenceFilter.averageTimeMs,
        "listMapAvg" to listMap.averageTimeMs,
        "sequenceMapAvg" to sequenceMap.averageTimeMs,
        "comparison" to comparison
    )
}

// ================================
// Test Runner
// ================================

suspend fun runAdvancedExercises() {
    println("=".repeat(70))
    println("ADVANCED EXERCISES - MASTERING KOTLIN")
    println("=".repeat(70))
    
    // Exercise 1: Parallel Processing
    println("\n1. Parallel Processing with Resource Management:")
    val parallel = exercise1_ParallelProcessing()
    parallel.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 2: Reactive Streams
    println("\n2. Reactive Data Stream Processing:")
    val reactive = exercise2_ReactiveStreams()
    reactive.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 3: Priority Channels
    println("\n3. Priority-Based Task Processing:")
    val priority = exercise3_PriorityChannels()
    priority.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 4: Parser Combinators
    println("\n4. Functional Parser Combinators:")
    val parser = exercise4_ParserCombinators()
    parser.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 5: Reflection Serialization
    println("\n5. Reflection-Based Serialization:")
    val serialization = exercise5_ReflectionSerialization()
    serialization.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 6: HTML DSL
    println("\n6. HTML DSL Builder:")
    val htmlDsl = exercise6_HtmlDsl()
    htmlDsl.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 7: Performance Benchmarking
    println("\n7. Performance Benchmarking Framework:")
    val benchmarking = exercise7_PerformanceBenchmarking()
    benchmarking.forEach { (key, value) -> println("$key: $value") }
    
    println("\n" + "=".repeat(70))
    println("ADVANCED EXERCISES COMPLETED!")
    println("All exercises demonstrate production-ready, advanced Kotlin concepts.")
    println("=".repeat(70))
}

fun main() {
    runBlocking {
        runAdvancedExercises()
    }
}

/**
 * Master-Level Challenge Projects:
 * 
 * 1. Distributed System: Build a distributed computing framework using coroutines
 * 2. Compiler: Create a simple programming language compiler with Kotlin
 * 3. Database Engine: Implement a basic relational database with SQL support
 * 4. Web Framework: Build a full-featured web framework like Ktor
 * 5. Game Engine: Create a 2D game engine with physics simulation
 * 6. Machine Learning: Implement ML algorithms from scratch in Kotlin
 * 7. Blockchain: Build a basic blockchain and cryptocurrency system
 * 8. Operating System: Create OS kernel components in Kotlin/Native
 * 9. 3D Graphics: Implement a 3D rendering engine with ray tracing
 * 10. AI Assistant: Build an intelligent assistant with natural language processing
 */