/**
 * Performance Optimization in Kotlin
 * 
 * This module covers practical performance optimization techniques:
 * - Memory management and garbage collection
 * - Inline functions and lambda optimization
 * - Collection performance patterns
 * - String manipulation optimization
 * - Coroutine performance best practices
 * - Benchmarking and profiling
 * - JVM optimization techniques
 * - Data class and object allocation optimization
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*
import kotlin.time.*

// ================================
// Memory Management Optimization
// ================================

/**
 * Object pooling to reduce allocations
 */
class ObjectPool<T>(
    private val factory: () -> T,
    private val reset: (T) -> Unit,
    maxSize: Int = 10
) {
    private val available = ArrayDeque<T>(maxSize)
    private val maxSize = maxSize
    
    fun acquire(): T {
        return available.removeFirstOrNull() ?: factory()
    }
    
    fun release(obj: T) {
        if (available.size < maxSize) {
            reset(obj)
            available.addLast(obj)
        }
    }
    
    inline fun <R> use(block: (T) -> R): R {
        val obj = acquire()
        return try {
            block(obj)
        } finally {
            release(obj)
        }
    }
}

/**
 * String builder optimization
 */
class OptimizedStringBuilder {
    private val buffer = StringBuilder(1024) // Pre-allocate reasonable size
    
    fun append(value: String): OptimizedStringBuilder {
        buffer.append(value)
        return this
    }
    
    fun append(value: Int): OptimizedStringBuilder {
        buffer.append(value)
        return this
    }
    
    fun appendLine(value: String = ""): OptimizedStringBuilder {
        buffer.appendLine(value)
        return this
    }
    
    fun clear(): OptimizedStringBuilder {
        buffer.clear()
        return this
    }
    
    override fun toString(): String = buffer.toString()
    
    fun length(): Int = buffer.length
}

/**
 * Memory-efficient data structures
 */
class CompactIntArray(initialCapacity: Int = 16) {
    private var data = IntArray(initialCapacity)
    private var size = 0
    
    fun add(value: Int) {
        ensureCapacity()
        data[size++] = value
    }
    
    fun get(index: Int): Int {
        if (index >= size) throw IndexOutOfBoundsException()
        return data[index]
    }
    
    fun size(): Int = size
    
    private fun ensureCapacity() {
        if (size >= data.size) {
            data = data.copyOf(data.size * 2)
        }
    }
    
    fun toArray(): IntArray = data.copyOf(size)
    
    // Memory-efficient operations
    inline fun forEach(action: (Int) -> Unit) {
        for (i in 0 until size) {
            action(data[i])
        }
    }
    
    inline fun sum(): Int {
        var total = 0
        for (i in 0 until size) {
            total += data[i]
        }
        return total
    }
}

// ================================
// Inline Function Optimization
// ================================

/**
 * Inline functions for performance-critical operations
 */
inline fun <T> measureTimeAndResult(block: () -> T): Pair<Duration, T> {
    val start = TimeSource.Monotonic.markNow()
    val result = block()
    val duration = start.elapsedNow()
    return duration to result
}

/**
 * Inline extension for repeated operations
 */
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> T): T {
    return if (condition) block() else this
}

/**
 * Inline higher-order functions with reified types
 */
inline fun <reified T> List<*>.filterIsInstance(): List<T> {
    val result = ArrayList<T>()
    for (element in this) {
        if (element is T) {
            result.add(element)
        }
    }
    return result
}

/**
 * Crossinline for lambda optimization
 */
inline fun <T, R> Iterable<T>.mapNotNullFast(crossinline transform: (T) -> R?): List<R> {
    val result = ArrayList<R>()
    for (element in this) {
        transform(element)?.let { result.add(it) }
    }
    return result
}

// ================================
// Collection Performance Optimization
// ================================

/**
 * Optimized collection operations
 */
object CollectionOptimizations {
    
    /**
     * Pre-sized collections for better performance
     */
    fun createOptimizedList<T>(expectedSize: Int): MutableList<T> {
        return ArrayList(expectedSize)
    }
    
    fun createOptimizedMap<K, V>(expectedSize: Int): MutableMap<K, V> {
        return HashMap(expectedSize)
    }
    
    fun createOptimizedSet<T>(expectedSize: Int): MutableSet<T> {
        return HashSet(expectedSize)
    }
    
    /**
     * Batch operations for better performance
     */
    fun <T> MutableList<T>.addAllOptimized(elements: Collection<T>): Boolean {
        ensureCapacity(this.size + elements.size)
        return addAll(elements)
    }
    
    /**
     * Sequence-based lazy evaluation
     */
    fun <T> List<T>.processLazily(): Sequence<T> {
        return asSequence()
            .filter { /* some condition */ true }
            .map { /* some transformation */ it }
            .take(100) // Only process what's needed
    }
    
    /**
     * Optimized grouping with pre-sized maps
     */
    fun <T, K> List<T>.groupByOptimized(keySelector: (T) -> K): Map<K, List<T>> {
        val result = HashMap<K, MutableList<T>>(size / 4) // Estimate group count
        for (element in this) {
            val key = keySelector(element)
            result.getOrPut(key) { ArrayList() }.add(element)
        }
        return result
    }
    
    /**
     * Memory-efficient distinct operation
     */
    fun <T> List<T>.distinctOptimized(): List<T> {
        val seen = HashSet<T>(size)
        val result = ArrayList<T>(size)
        for (element in this) {
            if (seen.add(element)) {
                result.add(element)
            }
        }
        return result
    }
}

// ================================
// String Manipulation Optimization
// ================================

object StringOptimizations {
    
    /**
     * StringBuilder pooling for frequent string operations
     */
    private val stringBuilderPool = ObjectPool(
        factory = { StringBuilder(256) },
        reset = { it.clear() },
        maxSize = 5
    )
    
    fun buildStringOptimized(block: StringBuilder.() -> Unit): String {
        return stringBuilderPool.use { builder ->
            builder.block()
            builder.toString()
        }
    }
    
    /**
     * Efficient string joining
     */
    fun <T> Collection<T>.joinToStringOptimized(
        separator: String = ", ",
        prefix: String = "",
        suffix: String = "",
        transform: (T) -> String = { it.toString() }
    ): String {
        return buildStringOptimized {
            append(prefix)
            var first = true
            for (element in this@joinToStringOptimized) {
                if (!first) append(separator)
                append(transform(element))
                first = false
            }
            append(suffix)
        }
    }
    
    /**
     * Optimized string splitting
     */
    fun String.splitOptimized(delimiter: String): List<String> {
        if (isEmpty()) return emptyList()
        
        val result = ArrayList<String>()
        var start = 0
        var index = indexOf(delimiter, start)
        
        while (index != -1) {
            result.add(substring(start, index))
            start = index + delimiter.length
            index = indexOf(delimiter, start)
        }
        
        result.add(substring(start))
        return result
    }
    
    /**
     * Efficient string formatting for hot paths
     */
    private val formatCache = mutableMapOf<String, (Array<Any>) -> String>()
    
    fun formatCached(template: String, vararg args: Any): String {
        val formatter = formatCache.getOrPut(template) { template ->
            { args -> template.format(*args) }
        }
        return formatter(args)
    }
}

// ================================
// Coroutine Performance Optimization
// ================================

object CoroutineOptimizations {
    
    /**
     * Optimized dispatcher for CPU-intensive tasks
     */
    val cpuOptimizedDispatcher = Dispatchers.Default.limitedParallelism(
        Runtime.getRuntime().availableProcessors()
    )
    
    /**
     * Channel-based producer-consumer with backpressure
     */
    fun <T> CoroutineScope.createOptimizedProducer(
        capacity: Int = 100,
        producer: suspend ProducerScope<T>.() -> Unit
    ): ReceiveChannel<T> {
        return produce(capacity = capacity) {
            producer()
        }
    }
    
    /**
     * Batch processing with coroutines
     */
    suspend fun <T, R> Collection<T>.mapConcurrently(
        concurrency: Int = 50,
        transform: suspend (T) -> R
    ): List<R> = coroutineScope {
        val semaphore = Semaphore(concurrency)
        map { item ->
            async {
                semaphore.withPermit {
                    transform(item)
                }
            }
        }.awaitAll()
    }
    
    /**
     * Flow optimizations
     */
    fun <T> Flow<T>.optimizedBuffer(size: Int = 64): Flow<T> {
        return buffer(capacity = size)
    }
    
    fun <T> Flow<List<T>>.flattenOptimized(): Flow<T> = transform { list ->
        list.forEach { emit(it) }
    }
    
    /**
     * Optimized flow collection
     */
    suspend fun <T> Flow<T>.collectBatched(
        batchSize: Int = 100,
        processor: suspend (List<T>) -> Unit
    ) {
        val batch = mutableListOf<T>()
        collect { item ->
            batch.add(item)
            if (batch.size >= batchSize) {
                processor(batch.toList())
                batch.clear()
            }
        }
        if (batch.isNotEmpty()) {
            processor(batch)
        }
    }
}

// ================================
// Benchmarking and Profiling Tools
// ================================

class PerformanceBenchmark {
    
    fun <T> benchmark(
        name: String,
        warmupRuns: Int = 1000,
        measureRuns: Int = 10000,
        operation: () -> T
    ): BenchmarkResult<T> {
        // Warmup phase
        repeat(warmupRuns) {
            operation()
        }
        
        // Force GC before measurement
        System.gc()
        Thread.sleep(100)
        
        // Measurement phase
        val results = mutableListOf<Duration>()
        var lastResult: T? = null
        
        repeat(measureRuns) {
            val (duration, result) = measureTimeAndResult { operation() }
            results.add(duration)
            lastResult = result
        }
        
        return BenchmarkResult(
            name = name,
            measurements = results,
            result = lastResult!!
        )
    }
    
    fun <T> compare(
        operations: Map<String, () -> T>,
        warmupRuns: Int = 1000,
        measureRuns: Int = 10000
    ): ComparisonResult<T> {
        val benchmarks = operations.map { (name, operation) ->
            benchmark(name, warmupRuns, measureRuns, operation)
        }
        
        return ComparisonResult(benchmarks)
    }
}

data class BenchmarkResult<T>(
    val name: String,
    val measurements: List<Duration>,
    val result: T
) {
    val average: Duration = measurements.reduce { acc, duration -> acc + duration } / measurements.size
    val min: Duration = measurements.minOrNull() ?: Duration.ZERO
    val max: Duration = measurements.maxOrNull() ?: Duration.ZERO
    val median: Duration = measurements.sorted().let { sorted ->
        if (sorted.size % 2 == 0) {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2
        } else {
            sorted[sorted.size / 2]
        }
    }
    
    fun summary(): String {
        return """
            Benchmark: $name
            Runs: ${measurements.size}
            Average: ${average.inWholeMicroseconds}μs
            Median: ${median.inWholeMicroseconds}μs
            Min: ${min.inWholeMicroseconds}μs
            Max: ${max.inWholeMicroseconds}μs
        """.trimIndent()
    }
}

data class ComparisonResult<T>(val benchmarks: List<BenchmarkResult<T>>) {
    fun summary(): String {
        val sorted = benchmarks.sortedBy { it.average }
        val fastest = sorted.first()
        
        return buildString {
            appendLine("Performance Comparison Results:")
            appendLine("=" * 40)
            
            sorted.forEach { benchmark ->
                val ratio = benchmark.average / fastest.average
                appendLine("${benchmark.name}: ${benchmark.average.inWholeMicroseconds}μs (${String.format("%.2fx", ratio)})")
            }
            
            appendLine()
            appendLine("Winner: ${fastest.name}")
        }
    }
}

// ================================
// Memory Profiling Tools
// ================================

object MemoryProfiler {
    
    fun measureMemoryUsage(operation: () -> Unit): MemoryUsage {
        val runtime = Runtime.getRuntime()
        
        // Force GC before measurement
        System.gc()
        Thread.sleep(50)
        
        val beforeTotal = runtime.totalMemory()
        val beforeFree = runtime.freeMemory()
        val beforeUsed = beforeTotal - beforeFree
        
        operation()
        
        System.gc()
        Thread.sleep(50)
        
        val afterTotal = runtime.totalMemory()
        val afterFree = runtime.freeMemory()
        val afterUsed = afterTotal - afterFree
        
        return MemoryUsage(
            beforeUsed = beforeUsed,
            afterUsed = afterUsed,
            difference = afterUsed - beforeUsed,
            maxMemory = runtime.maxMemory()
        )
    }
    
    fun analyzeObjectCreation(count: Int, factory: () -> Any): ObjectCreationAnalysis {
        val objects = mutableListOf<Any>()
        
        val memoryUsage = measureMemoryUsage {
            repeat(count) {
                objects.add(factory())
            }
        }
        
        val averageObjectSize = if (count > 0) memoryUsage.difference / count else 0L
        
        return ObjectCreationAnalysis(
            objectCount = count,
            totalMemory = memoryUsage.difference,
            averageObjectSize = averageObjectSize,
            memoryUsage = memoryUsage
        )
    }
}

data class MemoryUsage(
    val beforeUsed: Long,
    val afterUsed: Long,
    val difference: Long,
    val maxMemory: Long
) {
    fun summary(): String {
        return """
            Memory Usage Analysis:
            Before: ${beforeUsed / 1024}KB
            After: ${afterUsed / 1024}KB
            Difference: ${difference / 1024}KB
            Max Available: ${maxMemory / 1024 / 1024}MB
        """.trimIndent()
    }
}

data class ObjectCreationAnalysis(
    val objectCount: Int,
    val totalMemory: Long,
    val averageObjectSize: Long,
    val memoryUsage: MemoryUsage
) {
    fun summary(): String {
        return """
            Object Creation Analysis:
            Objects Created: $objectCount
            Total Memory Used: ${totalMemory / 1024}KB
            Average Object Size: ${averageObjectSize}B
            ${memoryUsage.summary()}
        """.trimIndent()
    }
}

// ================================
// Practical Optimization Examples
// ================================

/**
 * Example: Optimizing data processing pipeline
 */
class DataProcessor {
    
    // Unoptimized version
    fun processDataSlow(data: List<String>): List<String> {
        return data
            .filter { it.isNotEmpty() }
            .map { it.trim() }
            .map { it.uppercase() }
            .filter { it.length > 3 }
            .map { "PROCESSED: $it" }
            .distinct()
    }
    
    // Optimized version
    fun processDataFast(data: List<String>): List<String> {
        val seen = HashSet<String>(data.size)
        val result = ArrayList<String>(data.size)
        
        for (item in data) {
            if (item.isNotEmpty()) {
                val trimmed = item.trim()
                if (trimmed.length > 3) {
                    val processed = "PROCESSED: ${trimmed.uppercase()}"
                    if (seen.add(processed)) {
                        result.add(processed)
                    }
                }
            }
        }
        
        return result
    }
    
    // Sequence-based lazy evaluation
    fun processDataLazy(data: List<String>): Sequence<String> {
        return data.asSequence()
            .filter { it.isNotEmpty() }
            .map { it.trim() }
            .filter { it.length > 3 }
            .map { "PROCESSED: ${it.uppercase()}" }
            .distinct()
    }
}

// ================================
// Demonstration and Testing
// ================================

fun demonstrateMemoryOptimizations() {
    println("=== Memory Optimization Demo ===")
    
    // Object pooling demo
    val stringBuilderPool = ObjectPool(
        factory = { StringBuilder() },
        reset = { it.clear() }
    )
    
    val result = stringBuilderPool.use { builder ->
        builder.append("Hello")
        builder.append(" ")
        builder.append("World")
        builder.toString()
    }
    println("Pooled StringBuilder result: $result")
    
    // Compact array demo
    val compactArray = CompactIntArray()
    (1..10).forEach { compactArray.add(it) }
    println("Compact array sum: ${compactArray.sum()}")
    
    // Memory usage analysis
    val memoryUsage = MemoryProfiler.measureMemoryUsage {
        val list = mutableListOf<String>()
        repeat(10000) { 
            list.add("Item $it") 
        }
    }
    println(memoryUsage.summary())
}

fun demonstratePerformanceBenchmarking() {
    println("\n=== Performance Benchmarking Demo ===")
    
    val benchmark = PerformanceBenchmark()
    val testData = (1..1000).map { "Item $it with some longer text" }
    val processor = DataProcessor()
    
    val comparison = benchmark.compare(
        operations = mapOf(
            "Slow Processing" to { processor.processDataSlow(testData) },
            "Fast Processing" to { processor.processDataFast(testData) },
            "Lazy Processing" to { processor.processDataLazy(testData).toList() }
        ),
        warmupRuns = 100,
        measureRuns = 1000
    )
    
    println(comparison.summary())
}

fun demonstrateStringOptimizations() {
    println("\n=== String Optimization Demo ===")
    
    val items = listOf("apple", "banana", "cherry", "date")
    
    // Optimized string joining
    val joined = items.joinToStringOptimized(
        separator = " | ",
        prefix = "[",
        suffix = "]"
    ) { it.uppercase() }
    println("Optimized join: $joined")
    
    // Optimized string building
    val built = StringOptimizations.buildStringOptimized {
        appendLine("Header")
        items.forEach { item ->
            append("- ").appendLine(item)
        }
        append("Footer")
    }
    println("Built string:\n$built")
    
    // String splitting optimization demo
    val text = "one,two,three,four,five"
    val splitResult = text.splitOptimized(",")
    println("Split result: $splitResult")
}

suspend fun demonstrateCoroutineOptimizations() {
    println("\n=== Coroutine Optimization Demo ===")
    
    val items = (1..100).toList()
    
    // Concurrent processing with limited parallelism
    val (duration, results) = measureTimeAndResult {
        items.mapConcurrently(concurrency = 10) { item ->
            delay(10) // Simulate work
            item * 2
        }
    }
    
    println("Concurrent processing took ${duration.inWholeMilliseconds}ms")
    println("Results count: ${results.size}")
    
    // Flow batching demonstration
    val flow = flow {
        repeat(50) { emit(it) }
    }
    
    flow.collectBatched(batchSize = 10) { batch ->
        println("Processing batch of ${batch.size} items: ${batch.take(3)}...")
    }
}

fun main() {
    demonstrateMemoryOptimizations()
    demonstratePerformanceBenchmarking()
    demonstrateStringOptimizations()
    
    // Run coroutine demo
    runBlocking {
        demonstrateCoroutineOptimizations()
    }
    
    println("\n=== Performance Optimization Best Practices ===")
    println("✓ Use object pooling for frequently allocated objects")
    println("✓ Pre-size collections when size is known")
    println("✓ Use sequences for lazy evaluation of large datasets")
    println("✓ Prefer StringBuilder for multiple string concatenations")
    println("✓ Use inline functions for high-frequency operations")
    println("✓ Measure before optimizing - avoid premature optimization")
    println("✓ Consider memory allocation patterns")
    println("✓ Use appropriate data structures for your use case")
    println("✓ Optimize hot paths identified by profiling")
    println("✓ Balance readability with performance needs")
}

/**
 * TODO: Advanced Performance Exercises
 * 
 * 1. Implement a lock-free data structure for high concurrency
 * 2. Create a memory-mapped file processor for large datasets
 * 3. Build a custom allocator for specific object types
 * 4. Implement batch processing with optimal buffer sizes
 * 5. Create a performance monitoring system with metrics collection
 * 6. Build a caching layer with LRU eviction and TTL
 * 7. Optimize JSON serialization/deserialization
 * 8. Implement parallel algorithms for CPU-intensive tasks
 * 9. Create efficient bloom filters for membership testing
 * 10. Build a high-performance event processing system
 */