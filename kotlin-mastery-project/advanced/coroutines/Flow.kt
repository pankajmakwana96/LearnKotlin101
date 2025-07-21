/**
 * Kotlin Coroutines - Flow
 * 
 * This module covers Flow for asynchronous data streams, including:
 * - Flow basics and cold streams
 * - Flow builders and operators
 * - Flow collection and terminal operations
 * - Flow context and dispatchers
 * - Exception handling in flows
 * - Hot vs cold flows (SharedFlow, StateFlow)
 * - Flow testing and best practices
 * - Real-world applications
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// ================================
// Basic Flow Operations
// ================================

/**
 * Simple flow that emits numbers
 */
fun simpleFlow(): Flow<Int> = flow {
    println("Flow started")
    for (i in 1..5) {
        delay(100)
        println("Emitting $i")
        emit(i)
    }
    println("Flow completed")
}

/**
 * Flow from collections
 */
fun flowFromCollection(): Flow<String> {
    return listOf("apple", "banana", "cherry", "date").asFlow()
}

/**
 * Flow using flowOf builder
 */
fun flowOfValues(): Flow<Double> {
    return flowOf(1.0, 2.5, 3.14, 4.2)
}

/**
 * Demonstrates basic flow collection
 */
fun demonstrateBasicFlow() = runBlocking {
    println("=== Basic Flow ===")
    
    // Collect simple flow
    println("Collecting simple flow:")
    simpleFlow().collect { value ->
        println("Collected: $value")
    }
    
    println("\nCollecting flow from collection:")
    flowFromCollection().collect { value ->
        println("Fruit: $value")
    }
    
    println("\nFlow is cold - each collection starts from beginning:")
    val coldFlow = simpleFlow()
    
    println("First collection:")
    coldFlow.take(3).collect { println("First: $it") }
    
    println("Second collection:")
    coldFlow.take(2).collect { println("Second: $it") }
}

// ================================
// Flow Operators
// ================================

/**
 * Demonstrates transformation operators
 */
fun demonstrateTransformationOperators() = runBlocking {
    println("\n=== Transformation Operators ===")
    
    val numbers = (1..5).asFlow()
    
    println("Map operator:")
    numbers.map { it * it }
        .collect { println("Square: $it") }
    
    println("\nFilter operator:")
    numbers.filter { it % 2 == 0 }
        .collect { println("Even: $it") }
    
    println("\nTransform operator (flexible):")
    numbers.transform { value ->
        emit("Number: $value")
        emit("Square: ${value * value}")
    }.collect { println(it) }
    
    println("\nFlatMapConcat operator:")
    flowOf("a", "b", "c")
        .flatMapConcat { letter ->
            flow {
                emit("${letter}1")
                delay(100)
                emit("${letter}2")
            }
        }
        .collect { println("FlatMapConcat: $it") }
    
    println("\nFlatMapMerge operator (parallel):")
    flowOf("x", "y", "z")
        .flatMapMerge { letter ->
            flow {
                emit("${letter}1")
                delay(Random.nextLong(50, 150))
                emit("${letter}2")
            }
        }
        .collect { println("FlatMapMerge: $it") }
}

/**
 * Demonstrates terminal operators
 */
fun demonstrateTerminalOperators() = runBlocking {
    println("\n=== Terminal Operators ===")
    
    val numbers = (1..10).asFlow()
    
    println("Reduce operator:")
    val sum = numbers.reduce { acc, value -> acc + value }
    println("Sum: $sum")
    
    println("Fold operator:")
    val product = numbers.fold(1) { acc, value -> acc * value }
    println("Product: $product")
    
    println("First and last:")
    val first = numbers.first()
    val last = numbers.last()
    println("First: $first, Last: $last")
    
    println("Single (from flow with one element):")
    val single = flowOf(42).single()
    println("Single value: $single")
    
    println("toList operator:")
    val list = numbers.filter { it <= 5 }.toList()
    println("List: $list")
    
    println("Count operator:")
    val count = numbers.filter { it % 2 == 0 }.count()
    println("Even numbers count: $count")
}

/**
 * Demonstrates size limiting operators
 */
fun demonstrateSizeLimitingOperators() = runBlocking {
    println("\n=== Size Limiting Operators ===")
    
    val infiniteFlow = flow {
        var i = 1
        while (true) {
            emit(i++)
            delay(100)
        }
    }
    
    println("Take operator:")
    infiniteFlow.take(5).collect { println("Take: $it") }
    
    println("\nTakeWhile operator:")
    (1..10).asFlow()
        .takeWhile { it < 6 }
        .collect { println("TakeWhile: $it") }
    
    println("\nDrop operator:")
    (1..8).asFlow()
        .drop(3)
        .collect { println("Drop: $it") }
    
    println("\nDropWhile operator:")
    (1..10).asFlow()
        .dropWhile { it < 5 }
        .collect { println("DropWhile: $it") }
}

// ================================
// Flow Context and Dispatchers
// ================================

/**
 * Flow that shows execution context
 */
fun contextAwareFlow(): Flow<Int> = flow {
    println("Flow emission on ${Thread.currentThread().name}")
    for (i in 1..3) {
        emit(i)
        delay(100)
    }
}

/**
 * Demonstrates flow context and dispatchers
 */
fun demonstrateFlowContext() = runBlocking {
    println("\n=== Flow Context ===")
    
    println("Default flow context:")
    contextAwareFlow()
        .collect { 
            println("Collected $it on ${Thread.currentThread().name}")
        }
    
    println("\nFlow with flowOn operator:")
    contextAwareFlow()
        .flowOn(Dispatchers.IO)
        .collect { 
            println("Collected $it on ${Thread.currentThread().name}")
        }
    
    println("\nFlow with map on different dispatcher:")
    contextAwareFlow()
        .map { 
            println("Mapping $it on ${Thread.currentThread().name}")
            it * it
        }
        .flowOn(Dispatchers.Default)
        .collect { 
            println("Collected $it on ${Thread.currentThread().name}")
        }
}

// ================================
// Exception Handling
// ================================

/**
 * Flow that can throw exceptions
 */
fun errorProneFlow(): Flow<String> = flow {
    emit("Value 1")
    emit("Value 2")
    throw RuntimeException("Something went wrong!")
    emit("Value 3") // This won't be emitted
}

/**
 * Demonstrates exception handling in flows
 */
fun demonstrateExceptionHandling() = runBlocking {
    println("\n=== Exception Handling ===")
    
    println("Try-catch around collect:")
    try {
        errorProneFlow().collect { println("Received: $it") }
    } catch (e: Exception) {
        println("Caught exception: ${e.message}")
    }
    
    println("\nUsing catch operator:")
    errorProneFlow()
        .catch { e -> 
            println("Caught in flow: ${e.message}")
            emit("Recovery value")
        }
        .collect { println("Received: $it") }
    
    println("\nOnCompletion operator:")
    errorProneFlow()
        .onCompletion { cause ->
            if (cause != null) {
                println("Flow completed with exception: $cause")
            } else {
                println("Flow completed successfully")
            }
        }
        .catch { emit("Handled error") }
        .collect { println("Final: $it") }
    
    println("\nRetry on failure:")
    var attempts = 0
    flow<Int> {
        attempts++
        println("Attempt $attempts")
        if (attempts < 3) {
            throw RuntimeException("Attempt $attempts failed")
        }
        emit(42)
    }
    .retry(retries = 2) { cause ->
        println("Retrying due to: ${cause.message}")
        delay(100) // Wait before retry
        true
    }
    .catch { e -> println("All retries failed: ${e.message}") }
    .collect { println("Success: $it") }
}

// ================================
// Hot Flows: SharedFlow and StateFlow
// ================================

/**
 * Demonstrates SharedFlow
 */
fun demonstrateSharedFlow() = runBlocking {
    println("\n=== SharedFlow ===")
    
    // Create a SharedFlow with replay buffer
    val sharedFlow = MutableSharedFlow<String>(
        replay = 2, // Keep last 2 values for new subscribers
        extraBufferCapacity = 5 // Additional buffer capacity
    )
    
    // Emit some values before any collectors
    sharedFlow.emit("Early value 1")
    sharedFlow.emit("Early value 2")
    sharedFlow.emit("Early value 3")
    
    // First collector
    val job1 = launch {
        sharedFlow.take(5).collect { 
            println("Collector 1: $it") 
        }
    }
    
    delay(100)
    
    // Second collector (will receive replay values)
    val job2 = launch {
        sharedFlow.take(3).collect { 
            println("Collector 2: $it") 
        }
    }
    
    delay(100)
    
    // Emit more values
    sharedFlow.emit("Live value 1")
    sharedFlow.emit("Live value 2")
    
    joinAll(job1, job2)
}

/**
 * Demonstrates StateFlow
 */
fun demonstrateStateFlow() = runBlocking {
    println("\n=== StateFlow ===")
    
    // Create a StateFlow with initial value
    val stateFlow = MutableStateFlow("Initial state")
    
    // Collectors receive current value immediately
    val job1 = launch {
        stateFlow.take(4).collect { 
            println("State collector 1: $it") 
        }
    }
    
    delay(100)
    
    // Update state
    stateFlow.value = "Updated state 1"
    
    delay(100)
    
    // New collector gets current state immediately
    val job2 = launch {
        stateFlow.take(2).collect { 
            println("State collector 2: $it") 
        }
    }
    
    delay(100)
    
    stateFlow.value = "Updated state 2"
    stateFlow.value = "Final state"
    
    joinAll(job1, job2)
    
    println("Current state value: ${stateFlow.value}")
}

// ================================
// Real-World Applications
// ================================

/**
 * Reactive data repository using Flow
 */
class UserRepository {
    private val users = mutableMapOf<String, User>()
    private val userUpdates = MutableSharedFlow<UserEvent>()
    
    sealed class UserEvent {
        data class UserAdded(val user: User) : UserEvent()
        data class UserUpdated(val user: User) : UserEvent()
        data class UserDeleted(val userId: String) : UserEvent()
    }
    
    data class User(val id: String, val name: String, val email: String, val lastActive: Long)
    
    suspend fun addUser(user: User) {
        users[user.id] = user
        userUpdates.emit(UserEvent.UserAdded(user))
    }
    
    suspend fun updateUser(user: User) {
        users[user.id] = user
        userUpdates.emit(UserEvent.UserUpdated(user))
    }
    
    suspend fun deleteUser(userId: String) {
        users.remove(userId)
        userUpdates.emit(UserEvent.UserDeleted(userId))
    }
    
    fun getUser(userId: String): User? = users[userId]
    
    fun getAllUsers(): List<User> = users.values.toList()
    
    fun observeUserEvents(): Flow<UserEvent> = userUpdates.asSharedFlow()
    
    fun observeActiveUsers(): Flow<List<User>> = flow {
        while (currentCoroutineContext().isActive) {
            val activeUsers = users.values.filter { 
                System.currentTimeMillis() - it.lastActive < 300000 // 5 minutes
            }
            emit(activeUsers)
            delay(10000) // Check every 10 seconds
        }
    }
}

/**
 * Stock price monitor using Flow
 */
class StockPriceMonitor {
    data class StockPrice(val symbol: String, val price: Double, val timestamp: Long)
    
    private val priceUpdates = MutableSharedFlow<StockPrice>()
    private val stocks = listOf("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA")
    
    fun startPriceUpdates() {
        GlobalScope.launch {
            while (true) {
                val stock = stocks.random()
                val basePrice = when (stock) {
                    "AAPL" -> 150.0
                    "GOOGL" -> 2500.0
                    "MSFT" -> 300.0
                    "AMZN" -> 3200.0
                    "TSLA" -> 800.0
                    else -> 100.0
                }
                
                val price = basePrice * (0.95 + Random.nextDouble() * 0.1) // ±5% variation
                val stockPrice = StockPrice(stock, price, System.currentTimeMillis())
                
                priceUpdates.emit(stockPrice)
                delay(Random.nextLong(500, 2000))
            }
        }
    }
    
    fun observePrices(): Flow<StockPrice> = priceUpdates.asSharedFlow()
    
    fun observePriceForStock(symbol: String): Flow<StockPrice> = 
        observePrices().filter { it.symbol == symbol }
    
    fun observeMovingAverage(symbol: String, windowSize: Int): Flow<Double> =
        observePriceForStock(symbol)
            .map { it.price }
            .scan(emptyList<Double>()) { acc, price ->
                (acc + price).takeLast(windowSize)
            }
            .filter { it.size == windowSize }
            .map { it.average() }
    
    fun observePriceAlerts(symbol: String, threshold: Double): Flow<String> =
        observePriceForStock(symbol)
            .map { it.price }
            .distinctUntilChanged { old, new -> kotlin.math.abs(old - new) < threshold }
            .map { price -> "Alert: $symbol price is now ${"%.2f".format(price)}" }
}

/**
 * File processor using Flow
 */
class FileProcessor {
    data class FileEvent(val path: String, val size: Long, val timestamp: Long)
    data class ProcessingResult(val path: String, val success: Boolean, val message: String)
    
    fun processFiles(filePaths: List<String>): Flow<ProcessingResult> = flow {
        filePaths.forEach { path ->
            emit(processFile(path))
        }
    }
    
    fun processFilesParallel(filePaths: List<String>, concurrency: Int = 3): Flow<ProcessingResult> =
        filePaths.asFlow()
            .map { path -> 
                GlobalScope.async { processFile(path) }
            }
            .buffer(concurrency)
            .map { deferred -> deferred.await() }
    
    private suspend fun processFile(path: String): ProcessingResult {
        delay(Random.nextLong(100, 500)) // Simulate file processing
        
        return if (Random.nextBoolean()) {
            ProcessingResult(path, true, "File processed successfully")
        } else {
            ProcessingResult(path, false, "Processing failed")
        }
    }
    
    fun watchDirectory(): Flow<FileEvent> = flow {
        while (currentCoroutineContext().isActive) {
            // Simulate file system events
            val fileName = "file_${Random.nextInt(1000)}.txt"
            val fileSize = Random.nextLong(1024, 1024 * 1024) // 1KB to 1MB
            emit(FileEvent(fileName, fileSize, System.currentTimeMillis()))
            delay(Random.nextLong(1000, 3000))
        }
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateRealWorldRepository() = runBlocking {
    println("\n=== User Repository Example ===")
    
    val repository = UserRepository()
    
    // Start observing events
    val eventObserver = launch {
        repository.observeUserEvents()
            .take(5)
            .collect { event ->
                when (event) {
                    is UserRepository.UserEvent.UserAdded -> 
                        println("Event: User added - ${event.user.name}")
                    is UserRepository.UserEvent.UserUpdated -> 
                        println("Event: User updated - ${event.user.name}")
                    is UserRepository.UserEvent.UserDeleted -> 
                        println("Event: User deleted - ${event.userId}")
                }
            }
    }
    
    // Simulate user operations
    delay(100)
    repository.addUser(UserRepository.User("1", "Alice", "alice@example.com", System.currentTimeMillis()))
    
    delay(100)
    repository.addUser(UserRepository.User("2", "Bob", "bob@example.com", System.currentTimeMillis()))
    
    delay(100)
    repository.updateUser(UserRepository.User("1", "Alice Smith", "alice.smith@example.com", System.currentTimeMillis()))
    
    delay(100)
    repository.deleteUser("2")
    
    delay(100)
    repository.addUser(UserRepository.User("3", "Charlie", "charlie@example.com", System.currentTimeMillis()))
    
    eventObserver.join()
    
    println("All users: ${repository.getAllUsers()}")
}

fun demonstrateStockPriceMonitor() = runBlocking {
    println("\n=== Stock Price Monitor Example ===")
    
    val monitor = StockPriceMonitor()
    monitor.startPriceUpdates()
    
    // Observe all prices for a short time
    val priceObserver = launch {
        monitor.observePrices()
            .take(10)
            .collect { price ->
                println("${price.symbol}: ${"%.2f".format(price.price)}")
            }
    }
    
    // Observe specific stock with moving average
    val appleObserver = launch {
        monitor.observeMovingAverage("AAPL", 3)
            .take(5)
            .collect { average ->
                println("AAPL moving average (3): ${"%.2f".format(average)}")
            }
    }
    
    joinAll(priceObserver, appleObserver)
}

fun demonstrateFileProcessor() = runBlocking {
    println("\n=== File Processor Example ===")
    
    val processor = FileProcessor()
    val filePaths = (1..8).map { "file_$it.txt" }
    
    println("Processing files sequentially:")
    val sequentialTime = measureTimeMillis {
        processor.processFiles(filePaths)
            .collect { result ->
                println("${result.path}: ${if (result.success) "✓" else "✗"} ${result.message}")
            }
    }
    
    println("Sequential processing time: ${sequentialTime}ms")
    
    println("\nProcessing files in parallel:")
    val parallelTime = measureTimeMillis {
        processor.processFilesParallel(filePaths, concurrency = 3)
            .collect { result ->
                println("${result.path}: ${if (result.success) "✓" else "✗"} ${result.message}")
            }
    }
    
    println("Parallel processing time: ${parallelTime}ms")
    
    // Watch directory for a short time
    println("\nWatching directory:")
    processor.watchDirectory()
        .take(5)
        .collect { event ->
            println("File detected: ${event.path} (${event.size} bytes)")
        }
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a reactive shopping cart that emits updates when items are added/removed
 * 2. Build a real-time chat system using flows for message streaming
 * 3. Implement a search suggestions system that debounces user input
 * 4. Create a download progress monitor using flows
 * 5. Build a temperature monitoring system with alerts
 * 6. Implement a pagination system using flows for infinite scrolling
 * 7. Create a log aggregation system that processes log streams
 * 8. Build a real-time analytics dashboard using flows
 * 9. Implement a cache invalidation system using reactive flows
 * 10. Create a multiplayer game state synchronization system
 */

fun main() {
    runBlocking {
        demonstrateBasicFlow()
        demonstrateTransformationOperators()
        demonstrateTerminalOperators()
        demonstrateSizeLimitingOperators()
        demonstrateFlowContext()
        demonstrateExceptionHandling()
        demonstrateSharedFlow()
        demonstrateStateFlow()
        demonstrateRealWorldRepository()
        demonstrateStockPriceMonitor()
        demonstrateFileProcessor()
    }
    
    println("\n=== Flow Summary ===")
    println("✓ Cold flows with flow builders and operators")
    println("✓ Transformation and terminal operators for data processing")
    println("✓ Context switching with flowOn operator")
    println("✓ Exception handling with catch and retry operators")
    println("✓ Hot flows: SharedFlow for events, StateFlow for state")
    println("✓ Real-world patterns: repositories, monitoring, file processing")
    println("✓ Performance optimization with parallel processing")
}