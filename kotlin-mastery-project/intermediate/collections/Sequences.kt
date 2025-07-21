package com.kotlinmastery.intermediate.collections

import kotlin.random.Random

/**
 * # Sequences in Kotlin
 * 
 * Sequences provide lazy evaluation for collection operations, offering better performance
 * for large datasets and complex processing chains. This module covers sequence creation,
 * operations, and performance optimization techniques.
 * 
 * ## Learning Objectives
 * - Understand lazy vs eager evaluation
 * - Create sequences using various methods
 * - Apply sequence operations efficiently  
 * - Handle infinite sequences safely
 * - Optimize performance with sequences
 * 
 * ## Prerequisites: Collection types and functional operations
 * ## Estimated Time: 4 hours
 */

fun main() {
    println("=== Kotlin Sequences Demo ===\n")
    
    sequenceBasics()
    sequenceCreation()
    lazyEvaluation()
    infiniteSequences()
    sequencePerformance()
    realWorldSequences()
    customSequences()
    sequenceBestPractices()
}

/**
 * ## Sequence Basics
 * 
 * Understanding what sequences are and how they differ from regular collections.
 */
fun sequenceBasics() {
    println("--- Sequence Basics ---")
    
    // Collection vs Sequence comparison
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    println("Collection operations (eager evaluation):")
    val listResult = numbers
        .map { 
            println("  List: mapping $it")
            it * 2 
        }
        .filter { 
            println("  List: filtering $it")
            it > 10 
        }
        .take(3)
    
    println("List result: $listResult")
    
    println("\nSequence operations (lazy evaluation):")
    val sequenceResult = numbers.asSequence()
        .map { 
            println("  Sequence: mapping $it")
            it * 2 
        }
        .filter { 
            println("  Sequence: filtering $it")
            it > 10 
        }
        .take(3)
        .toList()  // Terminal operation triggers evaluation
    
    println("Sequence result: $sequenceResult")
    
    // Basic sequence characteristics
    println("\nSequence characteristics:")
    println("- Lazy evaluation: Operations not performed until needed")
    println("- One-time consumption: Each element processed through entire chain")
    println("- Memory efficient: No intermediate collections created")
    println("- Short-circuiting: Can stop early when condition met")
    
    // Creating sequences
    println("\nBasic sequence creation:")
    val fromList = listOf(1, 2, 3, 4, 5).asSequence()
    val fromRange = (1..5).asSequence()
    val fromArray = arrayOf("a", "b", "c").asSequence()
    
    println("From list: ${fromList.toList()}")
    println("From range: ${fromRange.toList()}")
    println("From array: ${fromArray.toList()}")
    
    // Empty sequences
    val emptySequence = emptySequence<Int>()
    val singleElementSequence = sequenceOf(42)
    val multiElementSequence = sequenceOf(1, 2, 3, 4, 5)
    
    println("\nSpecial sequences:")
    println("Empty: ${emptySequence.toList()}")
    println("Single: ${singleElementSequence.toList()}")
    println("Multiple: ${multiElementSequence.toList()}")
    
    // Sequence operations are intermediate until terminal operation
    val intermediateSequence = numbers.asSequence()
        .map { it * 2 }
        .filter { it > 5 }
        .map { "Number: $it" }
    
    println("\nIntermediate operations create no output until terminal operation:")
    println("Intermediate sequence created (no output yet)")
    
    // Terminal operations
    println("Calling toList() (terminal operation):")
    val finalResult = intermediateSequence.toList()
    println("Final result: $finalResult")
    
    println()
}

/**
 * ## Sequence Creation
 * 
 * Various methods to create sequences from different sources.
 */
fun sequenceCreation() {
    println("--- Sequence Creation ---")
    
    // From existing collections
    val listSequence = listOf(1, 2, 3, 4, 5).asSequence()
    val setSequence = setOf("a", "b", "c").asSequence()
    val mapSequence = mapOf(1 to "one", 2 to "two").asSequence()
    
    println("From collections:")
    println("List sequence: ${listSequence.toList()}")
    println("Set sequence: ${setSequence.toList()}")
    println("Map sequence: ${mapSequence.toList()}")
    
    // sequenceOf function
    val directSequence = sequenceOf(10, 20, 30, 40, 50)
    println("\nDirect creation: ${directSequence.toList()}")
    
    // generateSequence - infinite or finite sequences
    println("\nGenerated sequences:")
    
    // Infinite sequence with termination
    val powersOfTwo = generateSequence(1) { it * 2 }.take(10)
    println("Powers of 2: ${powersOfTwo.toList()}")
    
    // Finite sequence (stops when function returns null)
    val countdown = generateSequence(5) { if (it > 1) it - 1 else null }
    println("Countdown: ${countdown.toList()}")
    
    // Sequence with seed and next function
    val fibonacci = generateSequence(Pair(0, 1)) { (a, b) -> Pair(b, a + b) }
        .map { it.first }
        .take(15)
    println("Fibonacci: ${fibonacci.toList()}")
    
    // sequence builder
    println("\nSequence builder:")
    val customSequence = sequence {
        yield(1)
        yield(2)
        yieldAll(listOf(3, 4, 5))
        yield(6)
        
        // Can include logic
        for (i in 7..10) {
            yield(i)
        }
    }
    println("Custom sequence: ${customSequence.toList()}")
    
    // Conditional sequence building
    val conditionalSequence = sequence {
        yield("start")
        
        for (i in 1..5) {
            if (i % 2 == 0) {
                yield("even-$i")
            } else {
                yield("odd-$i")  
            }
        }
        
        yield("end")
    }
    println("Conditional sequence: ${conditionalSequence.toList()}")
    
    // File-like sequence (simulated)
    fun readLinesSequence(filename: String) = sequence {
        println("  Opening file: $filename")
        
        // Simulate reading lines
        repeat(5) { lineNumber ->
            val line = "Line ${lineNumber + 1} from $filename"
            println("  Reading: $line")
            yield(line)
        }
        
        println("  Closing file: $filename")
    }
    
    println("\nFile reading simulation:")
    val fileLines = readLinesSequence("data.txt").take(3).toList()
    println("Read lines: $fileLines")
    
    // Recursive sequence
    fun treeTraversal(node: String, depth: Int): Sequence<String> = sequence {
        yield("${"  ".repeat(depth)}$node")
        
        if (depth < 2) {  // Limit depth to avoid infinite recursion
            yieldAll(treeTraversal("${node}L", depth + 1))
            yieldAll(treeTraversal("${node}R", depth + 1))
        }
    }
    
    println("\nTree traversal:")
    val treeNodes = treeTraversal("root", 0).toList()
    treeNodes.forEach { println(it) }
    
    // Stateful sequence
    fun statisticsSequence(data: List<Int>) = sequence {
        var sum = 0
        var count = 0
        
        for (value in data) {
            sum += value
            count++
            val average = sum.toDouble() / count
            yield("After $count values: sum=$sum, avg=${"%.2f".format(average)}")
        }
    }
    
    println("\nStateful sequence:")
    val stats = statisticsSequence(listOf(10, 20, 30, 40, 50)).toList()
    stats.forEach { println("  $it") }
    
    println()
}

/**
 * ## Lazy Evaluation
 * 
 * Understanding how lazy evaluation works and its benefits.
 */
fun lazyEvaluation() {
    println("--- Lazy Evaluation ---")
    
    // Demonstrating lazy evaluation with tracking
    var operationCount = 0
    
    fun expensiveOperation(x: Int): Int {
        operationCount++
        println("    Expensive operation #$operationCount on $x")
        Thread.sleep(10)  // Simulate work
        return x * x
    }
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    println("Eager evaluation (List):")
    operationCount = 0
    val startTime1 = System.nanoTime()
    
    val eagerResult = numbers
        .map { expensiveOperation(it) }
        .filter { it > 25 }
        .take(2)
    
    val time1 = (System.nanoTime() - startTime1) / 1_000_000
    println("  Result: $eagerResult")
    println("  Operations performed: $operationCount")
    println("  Time: ${time1}ms")
    
    println("\nLazy evaluation (Sequence):")
    operationCount = 0
    val startTime2 = System.nanoTime()
    
    val lazyResult = numbers.asSequence()
        .map { expensiveOperation(it) }
        .filter { it > 25 }
        .take(2)
        .toList()
    
    val time2 = (System.nanoTime() - startTime2) / 1_000_000
    println("  Result: $lazyResult")
    println("  Operations performed: $operationCount")
    println("  Time: ${time2}ms")
    
    // Short-circuiting demonstration
    println("\nShort-circuiting with first():")
    operationCount = 0
    
    val firstMatch = numbers.asSequence()
        .map { expensiveOperation(it) }
        .filter { it > 25 }
        .first()
    
    println("  First match: $firstMatch")
    println("  Operations performed: $operationCount (stopped early!)")
    
    // Multiple terminal operations require recreation
    println("\nSequence consumption (one-time use):")
    val sequence = numbers.asSequence()
        .map { it * 2 }
        .filter { it > 10 }
    
    println("  First consumption: ${sequence.take(3).toList()}")
    
    // This would fail - sequence is already consumed
    // println("Second consumption: ${sequence.take(2).toList()}")
    
    // Need to recreate sequence for multiple uses
    val freshSequence = numbers.asSequence()
        .map { it * 2 }
        .filter { it > 10 }
    
    println("  Fresh sequence: ${freshSequence.take(2).toList()}")
    
    // Intermediate vs terminal operations
    println("\nIntermediate vs Terminal operations:")
    
    val intermediateOps = numbers.asSequence()
        .map { 
            println("    Mapping $it (this shouldn't print yet)")
            it * 2 
        }
        .filter { 
            println("    Filtering $it (this shouldn't print yet)")
            it > 10 
        }
    
    println("  Intermediate operations defined (no output above)")
    
    // Terminal operation triggers the chain
    println("  Calling terminal operation count():")
    val count = intermediateOps.count()
    println("  Count result: $count")
    
    // Lazy evaluation with side effects
    println("\nLazy evaluation with side effects:")
    val sideEffectsList = mutableListOf<String>()
    
    val sequenceWithSideEffects = (1..5).asSequence()
        .map { 
            sideEffectsList.add("Mapped $it")
            it * 2 
        }
        .filter { 
            sideEffectsList.add("Filtered $it")
            it > 6 
        }
    
    println("  Side effects list after sequence creation: $sideEffectsList")
    
    val result = sequenceWithSideEffects.toList()
    println("  Result: $result")
    println("  Side effects after evaluation: $sideEffectsList")
    
    println()
}

/**
 * ## Infinite Sequences
 * 
 * Working with infinite sequences safely using lazy evaluation.
 */
fun infiniteSequences() {
    println("--- Infinite Sequences ---")
    
    // Natural numbers
    val naturals = generateSequence(1) { it + 1 }
    println("Natural numbers (first 10): ${naturals.take(10).toList()}")
    
    // Even numbers
    val evens = generateSequence(0) { it + 2 }
    println("Even numbers (first 8): ${evens.take(8).toList()}")
    
    // Powers of 2
    val powersOf2 = generateSequence(1) { it * 2 }
    println("Powers of 2 (first 12): ${powersOf2.take(12).toList()}")
    
    // Fibonacci sequence
    val fibonacci = generateSequence(0 to 1) { (prev, curr) -> curr to (prev + curr) }
        .map { it.first }
    
    println("Fibonacci (first 20): ${fibonacci.take(20).toList()}")
    
    // Random number sequence
    val randomSequence = generateSequence { Random.nextInt(1, 101) }
    println("Random numbers (first 5): ${randomSequence.take(5).toList()}")
    
    // Mathematical sequences
    println("\nMathematical sequences:")
    
    // Prime numbers
    fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n == 2) return true
        if (n % 2 == 0) return false
        return (3..kotlin.math.sqrt(n.toDouble()).toInt() step 2).none { n % it == 0 }
    }
    
    val primes = generateSequence(2) { it + 1 }.filter { isPrime(it) }
    println("First 15 primes: ${primes.take(15).toList()}")
    
    // Perfect squares
    val perfectSquares = generateSequence(1) { it + 1 }.map { it * it }
    println("Perfect squares (first 10): ${perfectSquares.take(10).toList()}")
    
    // Triangular numbers
    val triangularNumbers = generateSequence(1) { it + 1 }
        .map { n -> n * (n + 1) / 2 }
    println("Triangular numbers (first 10): ${triangularNumbers.take(10).toList()}")
    
    // Working with conditions on infinite sequences
    println("\nConditional operations on infinite sequences:")
    
    // First 10 numbers divisible by both 3 and 5
    val divisibleBy15 = naturals
        .filter { it % 15 == 0 }
        .take(10)
    println("First 10 numbers divisible by 15: ${divisibleBy15.toList()}")
    
    // First prime number greater than 100
    val firstPrimeOver100 = primes
        .filter { it > 100 }
        .first()
    println("First prime > 100: $firstPrimeOver100")
    
    // Sum of first 50 even numbers
    val sumOfEvens = evens
        .take(50)
        .sum()
    println("Sum of first 50 even numbers: $sumOfEvens")
    
    // Collatz sequence (3n+1 problem)
    fun collatzSequence(start: Int) = generateSequence(start) { n ->
        when {
            n == 1 -> null  // Stop at 1
            n % 2 == 0 -> n / 2
            else -> 3 * n + 1
        }
    }
    
    println("\nCollatz sequences:")
    val collatz7 = collatzSequence(7).toList()
    val collatz27 = collatzSequence(27).toList()
    
    println("Collatz(7): $collatz7 (length: ${collatz7.size})")
    println("Collatz(27) length: ${collatz27.size} (first 10: ${collatz27.take(10)})")
    
    // Infinite sequence with state
    fun walkSequence(stepSize: Int = 1) = sequence {
        var position = 0
        while (true) {
            yield(position)
            // Random walk: 50% chance to go forward or backward
            position += if (Random.nextBoolean()) stepSize else -stepSize
        }
    }
    
    val randomWalk = walkSequence()
        .take(20)
        .toList()
    println("Random walk (20 steps): $randomWalk")
    
    // Time-based infinite sequence
    fun timestampSequence() = generateSequence { System.currentTimeMillis() }
    
    val timestamps = timestampSequence()
        .take(3)
        .toList()
    println("Timestamps: $timestamps")
    
    // Infinite sequences with practical limits
    println("\nPractical infinite sequence usage:")
    
    // Process data until condition met
    val numbersUntilSum = generateSequence(1) { it + 1 }
        .scan(0) { acc, n -> acc + n }
        .takeWhile { it < 100 }
        .toList()
    
    println("Cumulative sums until > 100: $numbersUntilSum")
    
    // Safe infinite sequence consumption
    println("\nSafe consumption patterns:")
    println("✅ Always use take(), takeWhile(), or first() with infinite sequences")
    println("✅ Use dropWhile() to skip initial elements safely")
    println("❌ Never call toList() directly on infinite sequences")
    println("❌ Avoid count() or sum() without limiting the sequence")
    
    println()
}

/**
 * ## Sequence Performance
 * 
 * Analyzing performance characteristics and optimization techniques.
 */
fun sequencePerformance() {
    println("--- Sequence Performance ---")
    
    fun measureTime(description: String, operation: () -> Unit): Long {
        val start = System.nanoTime()
        operation()
        val duration = (System.nanoTime() - start) / 1_000_000
        println("$description: ${duration}ms")
        return duration
    }
    
    // Large dataset performance comparison
    val largeData = (1..1_000_000).toList()
    
    println("Performance comparison with large dataset (1M elements):")
    
    val listTime = measureTime("List operations") {
        largeData
            .map { it * 2 }
            .filter { it > 1_000_000 }
            .take(10)
    }
    
    val sequenceTime = measureTime("Sequence operations") {
        largeData.asSequence()
            .map { it * 2 }
            .filter { it > 1_000_000 }
            .take(10)
            .toList()
    }
    
    println("Sequence was ${listTime.toDouble() / sequenceTime}x faster\n")
    
    // Memory usage demonstration
    println("Memory usage comparison:")
    
    fun getMemoryUsed(): Long {
        System.gc()
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }
    
    val initialMemory = getMemoryUsed()
    
    // List approach - creates intermediate collections
    val memoryBefore = getMemoryUsed()
    val listResults = largeData
        .map { it * 2 }
        .filter { it % 1000 == 0 }
        .take(100)
    val memoryAfterList = getMemoryUsed()
    
    // Sequence approach - no intermediate collections
    val memoryBeforeSeq = getMemoryUsed()
    val sequenceResults = largeData.asSequence()
        .map { it * 2 }
        .filter { it % 1000 == 0 }
        .take(100)
        .toList()
    val memoryAfterSeq = getMemoryUsed()
    
    println("List memory usage: ${(memoryAfterList - memoryBefore) / 1024}KB")
    println("Sequence memory usage: ${(memoryAfterSeq - memoryBeforeSeq) / 1024}KB")
    
    // Chain length performance
    println("\nChain length performance:")
    val data = (1..100_000).toList()
    
    measureTime("Short chain (2 ops)") {
        data.asSequence()
            .map { it * 2 }
            .filter { it > 50000 }
            .toList()
    }
    
    measureTime("Long chain (6 ops)") {
        data.asSequence()
            .map { it * 2 }
            .filter { it > 1000 }
            .map { it + 1 }
            .filter { it % 2 == 0 }
            .map { it / 2 }
            .filter { it > 25000 }
            .toList()
    }
    
    // When sequences are NOT beneficial
    println("\nWhen sequences are not beneficial:")
    val smallData = (1..100).toList()
    
    measureTime("Small list operations") {
        smallData
            .map { it * 2 }
            .filter { it > 50 }
    }
    
    measureTime("Small sequence operations") {
        smallData.asSequence()
            .map { it * 2 }
            .filter { it > 50 }
            .toList()
    }
    
    // Multiple terminal operations performance
    println("\nMultiple terminal operations:")
    
    val baseSequence = (1..10_000).asSequence()
        .map { it * 2 }
        .filter { it % 3 == 0 }
    
    measureTime("Sequence recreation (3 terminals)") {
        val seq1 = baseSequence.take(100).toList()
        val seq2 = (1..10_000).asSequence().map { it * 2 }.filter { it % 3 == 0 }.count()
        val seq3 = (1..10_000).asSequence().map { it * 2 }.filter { it % 3 == 0 }.sum()
    }
    
    // vs pre-computed list
    val precomputed = (1..10_000)
        .map { it * 2 }
        .filter { it % 3 == 0 }
    
    measureTime("Pre-computed list (3 operations)") {
        val list1 = precomputed.take(100)
        val list2 = precomputed.size
        val list3 = precomputed.sum()
    }
    
    // Performance guidelines
    println("\nPerformance guidelines:")
    println("✅ Use sequences for:")
    println("   - Large datasets (>1000 elements)")
    println("   - Complex operation chains (>3 operations)")
    println("   - Early termination scenarios (take, first, any)")
    println("   - Memory-constrained environments")
    
    println("❌ Avoid sequences for:")
    println("   - Small datasets (<100 elements)")
    println("   - Simple operations (single map/filter)")
    println("   - Multiple terminal operations on same data")
    println("   - Random access requirements")
    
    println()
}

/**
 * ## Real-World Sequences
 * 
 * Practical applications of sequences in real-world scenarios.
 */
fun realWorldSequences() {
    println("--- Real-World Sequences ---")
    
    // 1. Log File Processing
    println("Log File Processing:")
    
    fun simulateLogLines() = sequence {
        val logLevels = listOf("INFO", "WARN", "ERROR", "DEBUG")
        val services = listOf("api", "database", "cache", "auth")
        
        repeat(1000) { lineNum ->
            val timestamp = "2024-01-15 ${String.format("%02d:%02d:%02d", 
                (lineNum / 60) % 24, lineNum % 60, (lineNum * 13) % 60)}"
            val level = logLevels[lineNum % logLevels.size]
            val service = services[lineNum % services.size]
            val message = "Operation ${lineNum + 1} completed"
            
            yield("$timestamp [$level] [$service] $message")
        }
    }
    
    // Process only error logs efficiently
    val errorLogs = simulateLogLines()
        .filter { it.contains("[ERROR]") }
        .take(10)
        .toList()
    
    println("First 10 error logs:")
    errorLogs.forEach { println("  $it") }
    
    // 2. Data Stream Processing
    println("\nData Stream Processing:")
    
    data class SensorReading(val sensorId: String, val value: Double, val timestamp: Long)
    
    fun sensorDataStream() = generateSequence {
        SensorReading(
            sensorId = "SENSOR_${Random.nextInt(1, 6)}",
            value = 20.0 + Random.nextDouble(-5.0, 15.0),
            timestamp = System.currentTimeMillis()
        )
    }
    
    // Process sensor data with moving average
    val sensorAlerts = sensorDataStream()
        .filter { it.value > 30.0 }  // High temperature threshold
        .take(5)
        .toList()
    
    println("High temperature alerts:")
    sensorAlerts.forEach { reading ->
        println("  ${reading.sensorId}: ${String.format("%.2f", reading.value)}°C at ${reading.timestamp}")
    }
    
    // 3. Batch Processing with Sequences
    println("\nBatch Processing:")
    
    data class Customer(val id: String, val email: String, val segment: String, val active: Boolean)
    
    // Simulate large customer database
    fun customerDatabase() = sequence {
        val segments = listOf("Premium", "Standard", "Basic")
        val domains = listOf("gmail.com", "yahoo.com", "hotmail.com", "company.com")
        
        repeat(100000) { id ->
            yield(Customer(
                id = "CUST_${String.format("%06d", id + 1)}",
                email = "user${id + 1}@${domains[id % domains.size]}",
                segment = segments[id % segments.size],
                active = Random.nextDouble() > 0.1  // 90% active
            ))
        }
    }
    
    // Process only premium active customers for email campaign
    val campaignTargets = customerDatabase()
        .filter { it.active && it.segment == "Premium" }
        .map { "${it.id}:${it.email}" }
        .take(10)
        .toList()
    
    println("Email campaign targets (Premium active customers):")
    campaignTargets.forEach { println("  $it") }
    
    // 4. Pagination with Sequences
    println("\nAPI Pagination:")
    
    data class ApiPage<T>(val data: List<T>, val hasNext: Boolean, val nextCursor: String?)
    data class Product(val id: String, val name: String, val price: Double)
    
    fun fetchProductsPage(cursor: String? = null): ApiPage<Product> {
        // Simulate API call
        val startId = cursor?.toIntOrNull() ?: 1
        val products = (startId until startId + 5).map { id ->
            Product("P${String.format("%03d", id)}", "Product $id", Random.nextDouble(10.0, 1000.0))
        }
        
        return ApiPage(
            data = products,
            hasNext = startId < 100,
            nextCursor = if (startId < 100) (startId + 5).toString() else null
        )
    }
    
    fun allProductsSequence() = generateSequence<ApiPage<Product>>(
        seedFunction = { fetchProductsPage() }
    ) { currentPage ->
        if (currentPage.hasNext) fetchProductsPage(currentPage.nextCursor) else null
    }.flatMap { it.data.asSequence() }
    
    // Get expensive products across all pages
    val expensiveProducts = allProductsSequence()
        .filter { it.price > 500.0 }
        .take(5)
        .toList()
    
    println("Expensive products:")
    expensiveProducts.forEach { product ->
        println("  ${product.name}: $${String.format("%.2f", product.price)}")
    }
    
    // 5. Configuration Processing
    println("\nConfiguration Processing:")
    
    data class ConfigEntry(val key: String, val value: String, val environment: String)
    
    fun configurationSequence() = sequence {
        // Simulate loading from multiple sources
        val sources = listOf("default.properties", "prod.properties", "user.properties")
        val environments = listOf("default", "production", "user")
        
        sources.zip(environments).forEach { (source, env) ->
            // Simulate reading file
            repeat(10) { i ->
                yield(ConfigEntry("config.key$i", "value_${env}_$i", env))
            }
        }
    }
    
    // Process configuration with override precedence
    val finalConfig = configurationSequence()
        .groupBy { it.key }
        .mapValues { (_, entries) ->
            // Last entry wins (user overrides prod overrides default)
            entries.maxBy { 
                when (it.environment) {
                    "user" -> 3
                    "production" -> 2
                    "default" -> 1
                    else -> 0
                }
            }.value
        }
    
    println("Final configuration (first 5 entries):")
    finalConfig.entries.take(5).forEach { (key, value) ->
        println("  $key = $value")
    }
    
    // 6. ETL Pipeline with Sequences
    println("\nETL Pipeline:")
    
    data class RawData(val id: String, val data: String, val valid: Boolean)
    data class ProcessedData(val id: String, val processedValue: Int, val category: String)
    
    fun rawDataSequence() = sequence {
        repeat(1000) { i ->
            yield(RawData(
                id = "ID_${String.format("%04d", i)}",
                data = "raw_data_$i",
                valid = Random.nextDouble() > 0.05  // 95% valid
            ))
        }
    }
    
    // ETL pipeline: Extract -> Transform -> Load
    val processedData = rawDataSequence()
        .filter { it.valid }  // Extract: filter valid data
        .map { raw ->  // Transform: convert to processed format
            ProcessedData(
                id = raw.id,
                processedValue = raw.data.hashCode() % 1000,
                category = if (raw.data.hashCode() % 2 == 0) "TypeA" else "TypeB"
            )
        }
        .filter { it.processedValue > 0 }  // Additional validation
        .take(10)  // Load: take first 10 for demo
        .toList()
    
    println("ETL Pipeline results:")
    processedData.forEach { data ->
        println("  ${data.id}: ${data.processedValue} (${data.category})")
    }
    
    println()
}

/**
 * ## Custom Sequences
 * 
 * Creating custom sequence implementations for specific use cases.
 */
fun customSequences() {
    println("--- Custom Sequences ---")
    
    // 1. Buffered sequence (processes in chunks)
    fun <T> Sequence<T>.buffered(size: Int): Sequence<List<T>> = sequence {
        val buffer = mutableListOf<T>()
        
        for (item in this@buffered) {
            buffer.add(item)
            if (buffer.size >= size) {
                yield(buffer.toList())
                buffer.clear()
            }
        }
        
        // Yield remaining items if buffer not empty
        if (buffer.isNotEmpty()) {
            yield(buffer.toList())
        }
    }
    
    val numbers = (1..23).asSequence()
    val bufferedNumbers = numbers.buffered(5).toList()
    
    println("Buffered sequence (chunks of 5):")
    bufferedNumbers.forEachIndexed { index, chunk ->
        println("  Chunk ${index + 1}: $chunk")
    }
    
    // 2. Distinct by key sequence
    fun <T, K> Sequence<T>.distinctByKey(keySelector: (T) -> K): Sequence<T> = sequence {
        val seen = mutableSetOf<K>()
        
        for (item in this@distinctByKey) {
            val key = keySelector(item)
            if (seen.add(key)) {
                yield(item)
            }
        }
    }
    
    data class Person(val name: String, val age: Int, val city: String)
    val people = sequenceOf(
        Person("Alice", 30, "NYC"),
        Person("Bob", 25, "LA"),
        Person("Alice", 31, "Chicago"),  // Same name, different age/city
        Person("Charlie", 35, "NYC"),
        Person("Bob", 26, "Boston")      // Same name, different age/city
    )
    
    val uniqueByName = people.distinctByKey { it.name }.toList()
    
    println("\nDistinct people by name:")
    uniqueByName.forEach { person ->
        println("  ${person.name} (${person.age}) from ${person.city}")
    }
    
    // 3. Windowed sequence with custom logic
    fun <T, R> Sequence<T>.windowedTransform(
        size: Int,
        step: Int = 1,
        transform: (List<T>) -> R
    ): Sequence<R> = sequence {
        val window = ArrayDeque<T>(size)
        var elementCount = 0
        
        for (item in this@windowedTransform) {
            window.addLast(item)
            elementCount++
            
            if (window.size > size) {
                window.removeFirst()
            }
            
            if (window.size == size && (elementCount - size) % step == 0) {
                yield(transform(window.toList()))
            }
        }
    }
    
    val data = (1..10).asSequence()
    val movingAverages = data.windowedTransform(3) { window ->
        window.average()
    }.toList()
    
    println("\nMoving averages (window size 3):")
    movingAverages.forEachIndexed { index, avg ->
        println("  Window ${index + 1}: ${"%.2f".format(avg)}")
    }
    
    // 4. Retry sequence (for unreliable operations)
    fun <T> retrySequence(
        maxAttempts: Int,
        operation: () -> T
    ): Sequence<Result<T>> = sequence {
        repeat(maxAttempts) { attempt ->
            val result = try {
                Result.success(operation())
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            yield(result)
            
            if (result.isSuccess) return@sequence
        }
    }
    
    // Simulate unreliable operation
    var attempts = 0
    fun unreliableOperation(): String {
        attempts++
        if (attempts < 3) {
            throw RuntimeException("Operation failed on attempt $attempts")
        }
        return "Success on attempt $attempts"
    }
    
    println("\nRetry sequence:")
    attempts = 0  // Reset for demo
    val retryResults = retrySequence(5) { unreliableOperation() }.toList()
    
    retryResults.forEachIndexed { index, result ->
        when {
            result.isSuccess -> println("  Attempt ${index + 1}: ${result.getOrNull()}")
            else -> println("  Attempt ${index + 1}: Failed - ${result.exceptionOrNull()?.message}")
        }
    }
    
    // 5. Rate-limited sequence
    fun <T> Sequence<T>.rateLimited(delayMs: Long): Sequence<T> = sequence {
        for (item in this@rateLimited) {
            yield(item)
            Thread.sleep(delayMs)
        }
    }
    
    println("\nRate-limited sequence (500ms delay):")
    val startTime = System.currentTimeMillis()
    
    val rateLimitedData = (1..5).asSequence()
        .rateLimited(100)  // Reduced delay for demo
        .map { 
            val elapsed = System.currentTimeMillis() - startTime
            "Item $it at ${elapsed}ms"
        }
        .toList()
    
    rateLimitedData.forEach { println("  $it") }
    
    // 6. Stateful transformation sequence
    fun <T, S, R> Sequence<T>.statefulTransform(
        initialState: S,
        transform: (S, T) -> Pair<S, R>
    ): Sequence<R> = sequence {
        var currentState = initialState
        
        for (item in this@statefulTransform) {
            val (newState, result) = transform(currentState, item)
            currentState = newState
            yield(result)
        }
    }
    
    // Running sum with state
    val values = sequenceOf(1, 2, 3, 4, 5)
    val runningSums = values.statefulTransform(0) { sum, value ->
        val newSum = sum + value
        newSum to newSum  // New state and result
    }.toList()
    
    println("\nStateful transformation (running sums):")
    println("Values: ${values.toList()}")
    println("Running sums: $runningSums")
    
    println()
}

/**
 * ## Sequence Best Practices
 * 
 * Guidelines and patterns for effective sequence usage.
 */
fun sequenceBestPractices() {
    println("--- Sequence Best Practices ---")
    
    println("1. When to Use Sequences:")
    println("   ✅ Large datasets (1000+ elements)")
    println("   ✅ Multiple chained operations")
    println("   ✅ Early termination scenarios")
    println("   ✅ Memory-constrained environments")
    println("   ✅ Expensive operations that can be avoided")
    
    println("\n2. When NOT to Use Sequences:")
    println("   ❌ Small collections (<100 elements)")
    println("   ❌ Single operations (one map/filter)")
    println("   ❌ Multiple terminal operations on same data")
    println("   ❌ Need for random access")
    
    println("\n3. Performance Considerations:")
    
    // Good: Early termination
    val largeRange = (1..1_000_000).asSequence()
    val firstEven = largeRange.filter { it % 2 == 0 }.first()
    println("   ✅ Early termination: First even number: $firstEven")
    
    // Good: Single pass through data
    val processedData = (1..1000).asSequence()
        .map { it * 2 }
        .filter { it > 500 }
        .map { it.toString() }
        .take(5)
        .toList()
    println("   ✅ Single pass processing: ${processedData}")
    
    // Avoid: Multiple terminal operations
    val data = (1..1000).asSequence().map { it * 2 }
    
    // BAD: Each terminal operation processes the entire sequence
    // val sum = data.sum()
    // val count = data.count()
    // val list = data.toList()
    
    // GOOD: Process once, use result multiple times
    val computedData = data.toList()
    val sum = computedData.sum()
    val count = computedData.size
    println("   ✅ Compute once, use multiple times: sum=$sum, count=$count")
    
    println("\n4. Common Patterns:")
    
    // Pattern 1: Filter-Map-Take
    val pattern1 = (1..100).asSequence()
        .filter { it % 3 == 0 }
        .map { "Multiple of 3: $it" }
        .take(5)
        .toList()
    
    println("   Pattern 1 (Filter-Map-Take): $pattern1")
    
    // Pattern 2: Generate-Process-Until
    val pattern2 = generateSequence(1) { it + 1 }
        .map { it * it }
        .takeWhile { it < 1000 }
        .toList()
    
    println("   Pattern 2 (Generate-Process-Until): Squares < 1000: $pattern2")
    
    // Pattern 3: Batch Processing
    val pattern3 = (1..20).asSequence()
        .chunked(5)
        .map { chunk -> "Batch: ${chunk.sum()}" }
        .toList()
    
    println("   Pattern 3 (Batch Processing): $pattern3")
    
    println("\n5. Error Handling:")
    
    // Handle errors in sequence operations
    fun safeSequenceProcessing(data: List<String>): List<Int> {
        return data.asSequence()
            .mapNotNull { str ->
                try {
                    str.toInt()
                } catch (e: NumberFormatException) {
                    null  // Skip invalid numbers
                }
            }
            .filter { it > 0 }
            .toList()
    }
    
    val mixedData = listOf("1", "invalid", "2", "3", "not-a-number", "4")
    val validNumbers = safeSequenceProcessing(mixedData)
    println("   Safe processing: $mixedData -> $validNumbers")
    
    println("\n6. Memory Management:")
    println("   ✅ Sequences don't create intermediate collections")
    println("   ✅ Process elements one at a time")
    println("   ✅ Suitable for streaming data")
    println("   ⚠️  Terminal operations may create collections")
    
    println("\n7. Testing Sequences:")
    
    // Test intermediate sequence without terminal operation
    fun createProcessingSequence(data: List<Int>) = data.asSequence()
        .filter { it > 0 }
        .map { it * 2 }
    
    // Test by converting to list
    val testData = listOf(-1, 2, -3, 4, 5)
    val result = createProcessingSequence(testData).toList()
    println("   Testing sequence: $testData -> $result")
    
    println("\n8. Debugging Sequences:")
    
    // Use onEach for debugging without affecting the sequence
    val debuggedSequence = (1..5).asSequence()
        .onEach { println("     Processing: $it") }
        .map { it * 2 }
        .onEach { println("     After map: $it") }
        .filter { it > 6 }
        .onEach { println("     After filter: $it") }
        .toList()
    
    println("   Debugging sequence (see output above): $debuggedSequence")
    
    println("\n✅ Remember: Sequences are a tool for optimization, not a replacement for all collections!")
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice sequences:
 * 
 * 1. Create a sequence that generates all Pythagorean triples
 * 2. Build a log file analyzer that processes files lazily
 * 3. Implement a sliding window sequence for time series analysis
 * 4. Create a sequence-based web crawler simulation
 * 5. Build a sequence for processing large CSV files efficiently
 */

// TODO: Exercise 1 - Pythagorean Triples
fun pythagoreanTriplesSequence(maxValue: Int): Sequence<Triple<Int, Int, Int>> {
    // TODO: Generate all Pythagorean triples (a² + b² = c²) where a, b, c <= maxValue
    // Use sequence to avoid generating all combinations at once
    // Return Triple(a, b, c) for each valid combination
    return emptySequence()
}

// TODO: Exercise 2 - Log File Analyzer
data class LogLine(
    val timestamp: String,
    val level: String,
    val service: String,
    val message: String,
    val responseTime: Int? = null
)

class LogFileAnalyzer {
    // TODO: Process log files using sequences for memory efficiency
    
    fun parseLogFile(filename: String): Sequence<LogLine> {
        // TODO: Create a sequence that parses log lines on-demand
        // Simulate file reading with generateSequence or sequence builder
        return emptySequence()
    }
    
    fun analyzeErrors(logSequence: Sequence<LogLine>): Map<String, Int> {
        // TODO: Count errors by service using sequence operations
        return emptyMap()
    }
    
    fun findSlowRequests(logSequence: Sequence<LogLine>, threshold: Int): List<LogLine> {
        // TODO: Find requests slower than threshold, limit to first 20
        return emptyList()
    }
}

// TODO: Exercise 3 - Sliding Window Time Series
data class TimeSeriesPoint(val timestamp: Long, val value: Double)

fun <T> Sequence<T>.slidingWindow(windowSize: Int, step: Int = 1): Sequence<List<T>> {
    // TODO: Create sliding windows over the sequence
    // windowSize: size of each window
    // step: how many elements to advance between windows
    return emptySequence()
}

fun movingAverageSequence(
    data: Sequence<TimeSeriesPoint>, 
    windowSize: Int
): Sequence<Pair<Long, Double>> {
    // TODO: Calculate moving average using sliding windows
    // Return pairs of (timestamp, average) for each window
    return emptySequence()
}

// TODO: Exercise 4 - Web Crawler Simulation
data class WebPage(val url: String, val links: List<String>, val content: String)

class WebCrawlerSequence(private val startUrl: String) {
    // TODO: Simulate web crawling using sequences
    
    fun crawlPages(maxDepth: Int = 3): Sequence<WebPage> {
        // TODO: Create a sequence that crawls pages breadth-first
        // Simulate fetching pages and following links
        // Use generateSequence or sequence builder with state
        return emptySequence()
    }
    
    private fun fetchPage(url: String): WebPage {
        // TODO: Simulate fetching a web page
        // Return mock page with some links
        return WebPage(url, emptyList(), "")
    }
}

// TODO: Exercise 5 - CSV File Processor
data class CsvRow(val data: Map<String, String>)

class CsvProcessor {
    // TODO: Process large CSV files efficiently with sequences
    
    fun readCsvSequence(filename: String): Sequence<CsvRow> {
        // TODO: Create sequence that reads CSV file line by line
        // Parse headers and convert each line to CsvRow
        return emptySequence()
    }
    
    fun processLargeDataset(
        csvSequence: Sequence<CsvRow>,
        filterCriteria: (CsvRow) -> Boolean,
        transformFunction: (CsvRow) -> String
    ): Sequence<String> {
        // TODO: Apply filtering and transformation using sequence operations
        return emptySequence()
    }
    
    fun aggregateData(csvSequence: Sequence<CsvRow>, groupByColumn: String): Map<String, Int> {
        // TODO: Group data and count occurrences using sequences
        // Process the data efficiently without loading everything into memory
        return emptyMap()
    }
}