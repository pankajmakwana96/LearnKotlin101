/**
 * Kotlin Performance Optimization Guide
 * 
 * This comprehensive guide covers performance optimization techniques for Kotlin:
 * - Memory management and garbage collection
 * - Collection performance characteristics
 * - Coroutines performance optimization
 * - JVM-specific optimizations
 * - Profiling and benchmarking
 * - Common performance pitfalls
 * - Platform-specific optimizations (JVM, Android, Native)
 * 
 * These guidelines are based on empirical testing and real-world production experience.
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

// ================================
// Memory Management Best Practices
// ================================

/**
 * PERFORMANCE TIP: Minimize object allocation
 * 
 * Object creation is one of the biggest performance bottlenecks.
 */

// ❌ Poor: Creates many temporary objects
fun poorStringConcatenation(items: List<String>): String {
    var result = ""
    for (item in items) {
        result += item + ", " // Creates new String objects
    }
    return result
}

// ✅ Good: Use StringBuilder for string concatenation
fun efficientStringConcatenation(items: List<String>): String = buildString {
    for (item in items) {
        append(item)
        append(", ")
    }
}

// ✅ Better: Use joinToString
fun bestStringConcatenation(items: List<String>): String = items.joinToString(", ")

/**
 * PERFORMANCE TIP: Reuse objects when possible
 */
class ObjectPool<T>(private val factory: () -> T, initialSize: Int = 10) {
    private val pool = ArrayDeque<T>()
    
    init {
        repeat(initialSize) {
            pool.addLast(factory())
        }
    }
    
    fun acquire(): T = pool.removeFirstOrNull() ?: factory()
    
    fun release(obj: T) {
        if (pool.size < 50) { // Limit pool size
            pool.addLast(obj)
        }
    }
}

// Example usage of object pooling
class ExpensiveObject {
    fun reset() { /* Reset object state */ }
    fun process(data: String): String = data.uppercase()
}

class WorkerService {
    private val objectPool = ObjectPool({ ExpensiveObject() })
    
    fun processData(data: String): String {
        val processor = objectPool.acquire()
        try {
            processor.reset()
            return processor.process(data)
        } finally {
            objectPool.release(processor)
        }
    }
}

// ================================
// Collection Performance
// ================================

/**
 * PERFORMANCE TIP: Choose the right collection type
 * 
 * Different collections have different performance characteristics.
 */

class CollectionPerformanceExamples {
    
    // ✅ Use ArrayList for indexed access and iteration
    fun efficientIndexedAccess() {
        val list = arrayListOf<String>()
        // Fast random access: O(1)
        // Fast iteration: O(n)
        // Insertion at end: O(1) amortized
    }
    
    // ✅ Use LinkedList for frequent insertion/deletion at arbitrary positions
    fun efficientInsertion() {
        val list = mutableListOf<String>()
        // Insertion/deletion at arbitrary position: O(1) if you have the node
        // Random access: O(n)
    }
    
    // ✅ Use HashSet for unique elements and fast lookups
    fun efficientMembershipTesting() {
        val set = hashSetOf<String>()
        // Contains check: O(1) average
        // Add/remove: O(1) average
    }
    
    // ✅ Use TreeSet for sorted unique elements
    fun efficientSortedSet() {
        val sortedSet = sortedSetOf<String>()
        // Contains/add/remove: O(log n)
        // Maintains sorted order
    }
    
    // ✅ Use HashMap for key-value lookups
    fun efficientKeyValueLookup() {
        val map = hashMapOf<String, String>()
        // Get/put/remove: O(1) average
        // Use LinkedHashMap to maintain insertion order
    }
}

/**
 * PERFORMANCE TIP: Use sequences for large data processing
 */
class SequencePerformance {
    
    // ❌ Poor: Eager evaluation creates intermediate collections
    fun eagerProcessing(data: List<Int>): List<String> {
        return data
            .filter { it > 10 }      // Creates intermediate list
            .map { it * 2 }          // Creates another intermediate list
            .map { "Result: $it" }   // Creates final list
    }
    
    // ✅ Good: Lazy evaluation with sequences
    fun lazyProcessing(data: List<Int>): List<String> {
        return data.asSequence()
            .filter { it > 10 }      // No intermediate collections
            .map { it * 2 }          // Lazy evaluation
            .map { "Result: $it" }   // Only final list is created
            .toList()
    }
    
    // ✅ Best: Use sequences for infinite or large datasets
    fun infiniteSequence(): Sequence<Int> = generateSequence(1) { it + 1 }
    
    fun processLargeDataset(limit: Int) {
        infiniteSequence()
            .filter { it % 2 == 0 }
            .take(limit)
            .forEach { println(it) }
    }
}

// ================================
// Coroutines Performance
// ================================

/**
 * PERFORMANCE TIP: Use appropriate dispatchers
 */
class CoroutinePerformance {
    
    // ✅ Use Dispatchers.Default for CPU-intensive work
    suspend fun cpuIntensiveTask(data: List<Int>): List<Int> = withContext(Dispatchers.Default) {
        data.map { complexCalculation(it) }
    }
    
    // ✅ Use Dispatchers.IO for I/O operations
    suspend fun ioTask(url: String): String = withContext(Dispatchers.IO) {
        // Network or file operations
        fetchDataFromNetwork(url)
    }
    
    // ✅ Use custom dispatcher for specific needs
    private val customDispatcher = Dispatchers.Default.limitedParallelism(2)
    
    suspend fun limitedParallelism(tasks: List<Task>): List<Result> = withContext(customDispatcher) {
        tasks.map { async { processTask(it) } }.awaitAll()
    }
    
    // ✅ Optimize coroutine creation overhead
    suspend fun efficientCoroutineCreation(data: List<String>) {
        // ❌ Poor: Creates too many coroutines
        // data.map { async { process(it) } }.awaitAll()
        
        // ✅ Good: Batch processing
        data.chunked(100).map { chunk ->
            async {
                chunk.map { process(it) }
            }
        }.awaitAll().flatten()
    }
    
    private fun complexCalculation(value: Int): Int = value * value
    private suspend fun fetchDataFromNetwork(url: String): String = TODO()
    private suspend fun processTask(task: Task): Result = TODO()
    private fun process(data: String): String = data.uppercase()
}

/**
 * PERFORMANCE TIP: Use Flow efficiently
 */
class FlowPerformance {
    
    // ✅ Use cold flows for on-demand data
    fun coldFlow(): Flow<Int> = flow {
        repeat(1000) { emit(it) }
    }
    
    // ✅ Use hot flows for shared data streams
    private val _hotFlow = MutableSharedFlow<Int>()
    val hotFlow: SharedFlow<Int> = _hotFlow.asSharedFlow()
    
    // ✅ Use buffer to prevent backpressure
    fun bufferedFlow(): Flow<String> = flow {
        repeat(1000) {
            emit(processSlowly(it))
        }
    }.buffer(50) // Buffer up to 50 items
    
    // ✅ Use flowOn to optimize context switching
    fun optimizedFlow(): Flow<ProcessedData> = flow {
        // Producer runs on IO dispatcher
        repeat(100) {
            emit(fetchFromDatabase(it))
        }
    }.flowOn(Dispatchers.IO)
    .map { data ->
        // Consumer runs on Default dispatcher
        processData(data)
    }.flowOn(Dispatchers.Default)
    
    private fun processSlowly(value: Int): String = value.toString()
    private suspend fun fetchFromDatabase(id: Int): RawData = TODO()
    private fun processData(data: RawData): ProcessedData = TODO()
}

// ================================
// Inline Functions and Performance
// ================================

/**
 * PERFORMANCE TIP: Use inline functions judiciously
 */

// ✅ Good use of inline: Higher-order functions
inline fun <T> measureExecutionTime(operation: () -> T): Pair<T, Long> {
    val startTime = System.nanoTime()
    val result = operation()
    val endTime = System.nanoTime()
    return result to (endTime - startTime)
}

// ✅ Good use of inline: Reified type parameters
inline fun <reified T> parseJson(json: String): T? {
    // Access to T::class at runtime
    return when (T::class) {
        String::class -> json as? T
        Int::class -> json.toIntOrNull() as? T
        else -> null
    }
}

// ❌ Poor use of inline: Large functions (increases bytecode size)
// Don't inline large functions as it can cause code bloat

// ✅ Use noinline for parameters you don't want inlined
inline fun processWithCallback(
    data: String,
    noinline callback: (String) -> Unit, // This won't be inlined
    transform: (String) -> String        // This will be inlined
): String {
    val result = transform(data)
    callback("Processing: $data")
    return result
}

// ================================
// JVM-Specific Optimizations
// ================================

/**
 * PERFORMANCE TIP: Leverage JVM optimizations
 */

class JVMOptimizations {
    
    // ✅ Use @JvmStatic for better performance in static calls
    companion object {
        @JvmStatic
        fun efficientStaticMethod(value: Int): Int = value * 2
    }
    
    // ✅ Use primitive collections when possible
    fun usePrimitiveCollections() {
        // Use specialized collections for primitives to avoid boxing
        val intArray = IntArray(1000) // Better than Array<Int>
        val longArray = LongArray(1000) // Better than Array<Long>
    }
    
    // ✅ Minimize autoboxing
    fun avoidBoxing(numbers: IntArray): Long {
        var sum = 0L // Use primitive long
        for (number in numbers) {
            sum += number // No boxing/unboxing
        }
        return sum
    }
    
    // ✅ Use StringBuilder for string operations
    fun efficientStringBuilding(count: Int): String = buildString(count * 10) {
        repeat(count) {
            append("Item ")
            append(it)
            append("\n")
        }
    }
}

// ================================
// Memory-Efficient Data Structures
// ================================

/**
 * PERFORMANCE TIP: Design memory-efficient data structures
 */

// ✅ Use value classes to avoid object overhead
@JvmInline
value class UserId(val value: String)

@JvmInline
value class Timestamp(val millis: Long) {
    fun isAfter(other: Timestamp): Boolean = millis > other.millis
}

// ✅ Use data classes with primitive types
data class Point(val x: Int, val y: Int) // More efficient than Point(Double, Double) if precision allows

// ✅ Use arrays for fixed-size collections
class FixedSizeBuffer<T>(size: Int) {
    private val buffer = arrayOfNulls<Any>(size) as Array<T?>
    private var index = 0
    
    fun add(item: T) {
        buffer[index % buffer.size] = item
        index++
    }
    
    fun get(pos: Int): T? = buffer[pos % buffer.size]
}

// ✅ Use object pooling for frequently created objects
class StringBuilderPool {
    private val pool = ArrayDeque<StringBuilder>()
    private val maxPoolSize = 10
    
    fun borrow(): StringBuilder {
        return pool.removeFirstOrNull()?.apply { setLength(0) } 
            ?: StringBuilder()
    }
    
    fun return(sb: StringBuilder) {
        if (pool.size < maxPoolSize && sb.capacity < 1024) {
            pool.addLast(sb)
        }
    }
}

inline fun <T> StringBuilderPool.use(operation: (StringBuilder) -> T): T {
    val sb = borrow()
    try {
        return operation(sb)
    } finally {
        return(sb)
    }
}

// ================================
// Profiling and Benchmarking
// ================================

/**
 * PERFORMANCE TIP: Measure, don't guess
 */

class PerformanceBenchmark {
    
    // Simple benchmark function
    fun benchmark(name: String, iterations: Int = 1000, warmup: Int = 100, operation: () -> Unit) {
        // Warmup
        repeat(warmup) { operation() }
        
        // Force GC
        System.gc()
        Thread.sleep(100)
        
        // Measure
        val times = mutableListOf<Long>()
        repeat(iterations) {
            val time = measureTimeMillis { operation() }
            times.add(time)
        }
        
        val avg = times.average()
        val min = times.minOrNull() ?: 0
        val max = times.maxOrNull() ?: 0
        val median = times.sorted()[times.size / 2]
        
        println("Benchmark: $name")
        println("  Average: %.2f ms".format(avg))
        println("  Min/Max: $min ms / $max ms")
        println("  Median: $median ms")
        println()
    }
    
    // Memory usage measurement
    fun measureMemoryUsage(operation: () -> Unit): Long {
        val runtime = Runtime.getRuntime()
        System.gc()
        Thread.sleep(100)
        
        val beforeMemory = runtime.totalMemory() - runtime.freeMemory()
        operation()
        val afterMemory = runtime.totalMemory() - runtime.freeMemory()
        
        return afterMemory - beforeMemory
    }
}

// ================================
// Common Performance Pitfalls
// ================================

/**
 * PERFORMANCE PITFALL: Examples of what to avoid
 */

class PerformancePitfalls {
    
    // ❌ Pitfall: Unnecessary object creation in loops
    fun inefficientLoop(data: List<String>): List<String> {
        val result = mutableListOf<String>()
        for (item in data) {
            // Creates new Regex object in each iteration
            val regex = Regex("\\d+")
            if (regex.containsMatchIn(item)) {
                result.add(item.uppercase()) // Creates new string
            }
        }
        return result
    }
    
    // ✅ Fix: Reuse objects and optimize allocations
    private val digitRegex = Regex("\\d+") // Reuse regex
    
    fun efficientLoop(data: List<String>): List<String> = buildList {
        for (item in data) {
            if (digitRegex.containsMatchIn(item)) {
                add(item.uppercase())
            }
        }
    }
    
    // ❌ Pitfall: Using wrong collection for the use case
    fun inefficientLookup(items: List<String>, target: String): Boolean {
        return items.contains(target) // O(n) for List
    }
    
    // ✅ Fix: Use appropriate collection
    fun efficientLookup(items: Set<String>, target: String): Boolean {
        return items.contains(target) // O(1) for Set
    }
    
    // ❌ Pitfall: Excessive string concatenation
    fun inefficientStringOps(items: List<String>): String {
        var result = ""
        for (item in items) {
            result = result + item + "\n" // Creates new string each time
        }
        return result
    }
    
    // ✅ Fix: Use StringBuilder or joinToString
    fun efficientStringOps(items: List<String>): String {
        return items.joinToString("\n")
    }
    
    // ❌ Pitfall: Blocking operations in coroutines
    suspend fun blockingInCoroutine() {
        Thread.sleep(1000) // Blocks the thread
        // Synchronous I/O operations
    }
    
    // ✅ Fix: Use suspending functions
    suspend fun nonBlockingInCoroutine() {
        delay(1000) // Suspends without blocking thread
        // Use suspending I/O libraries
    }
}

// ================================
// Platform-Specific Optimizations
// ================================

/**
 * PERFORMANCE TIP: Android-specific optimizations
 */
class AndroidOptimizations {
    
    // ✅ Minimize object allocation on main thread
    fun optimizeForMainThread() {
        // Pre-allocate objects
        // Use object pooling
        // Defer heavy operations to background threads
    }
    
    // ✅ Use appropriate data structures for Android
    fun useAndroidCollections() {
        // Use SparseArray instead of HashMap<Int, Object>
        // Use ArrayMap for small maps
        // Use ArraySet for small sets
    }
}

/**
 * PERFORMANCE TIP: Kotlin/Native optimizations
 */
class NativeOptimizations {
    
    // ✅ Minimize cross-platform object sharing
    fun minimizeSharing() {
        // Keep objects thread-local when possible
        // Use primitives over objects
        // Minimize object graph complexity
    }
    
    // ✅ Use appropriate memory model
    fun useAppropriateMemoryModel() {
        // Choose between strict and relaxed memory models
        // Consider new memory model features
    }
}

// ================================
// Performance Testing Framework
// ================================

class PerformanceTestSuite {
    private val benchmark = PerformanceBenchmark()
    
    fun runAllTests() {
        testStringOperations()
        testCollectionOperations()
        testCoroutinePerformance()
    }
    
    private fun testStringOperations() {
        val data = (1..1000).map { "Item $it" }
        
        println("String Operations Performance:")
        benchmark.benchmark("String concatenation (+)") {
            poorStringConcatenation(data)
        }
        
        benchmark.benchmark("StringBuilder") {
            efficientStringConcatenation(data)
        }
        
        benchmark.benchmark("joinToString") {
            bestStringConcatenation(data)
        }
    }
    
    private fun testCollectionOperations() {
        val data = (1..10000).toList()
        
        println("Collection Operations Performance:")
        val sequencePerf = SequencePerformance()
        
        benchmark.benchmark("Eager processing") {
            sequencePerf.eagerProcessing(data)
        }
        
        benchmark.benchmark("Lazy processing") {
            sequencePerf.lazyProcessing(data)
        }
    }
    
    private fun testCoroutinePerformance() {
        println("Coroutine Performance:")
        // Add coroutine-specific benchmarks here
    }
}

// Placeholder data classes and functions for examples
data class Task(val id: String, val data: String)
data class Result(val success: Boolean, val data: String)
data class RawData(val value: String)
data class ProcessedData(val processed: String)

// Performance test runner
fun main() {
    val testSuite = PerformanceTestSuite()
    testSuite.runAllTests()
}

/**
 * PERFORMANCE SUMMARY:
 * 
 * 1. **Memory Management**:
 *    - Minimize object allocation
 *    - Reuse objects when possible
 *    - Use object pooling for expensive objects
 * 
 * 2. **Collections**:
 *    - Choose the right collection for the use case
 *    - Use sequences for large data processing
 *    - Prefer primitive collections when applicable
 * 
 * 3. **Coroutines**:
 *    - Use appropriate dispatchers
 *    - Optimize coroutine creation overhead
 *    - Use Flow efficiently with buffering
 * 
 * 4. **JVM Optimizations**:
 *    - Minimize autoboxing
 *    - Use inline functions judiciously
 *    - Leverage JVM-specific features
 * 
 * 5. **Measurement**:
 *    - Always measure performance
 *    - Use proper benchmarking techniques
 *    - Profile in production-like conditions
 * 
 * 6. **Platform-Specific**:
 *    - Apply Android-specific optimizations
 *    - Consider Kotlin/Native constraints
 *    - Use platform-appropriate data structures
 * 
 * Remember: Premature optimization is the root of all evil. Profile first, then optimize.
 */