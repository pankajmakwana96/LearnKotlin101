/**
 * Kotlin Coroutines - Channels
 * 
 * This module covers channels for coroutine communication, including:
 * - Channel basics and types
 * - Producer-consumer patterns
 * - Channel capacity and buffering
 * - Channel closing and iteration
 * - Select expressions for multiple channels
 * - Fan-in and fan-out patterns
 * - Pipeline patterns
 * - Real-world applications
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.*
import kotlin.random.Random

// ================================
// Basic Channel Operations
// ================================

/**
 * Demonstrates basic channel send and receive operations
 */
fun demonstrateBasicChannels() = runBlocking {
    println("=== Basic Channels ===")
    
    // Create a basic channel
    val channel = Channel<String>()
    
    // Producer coroutine
    launch {
        val messages = listOf("Hello", "World", "From", "Channel")
        for (message in messages) {
            println("Sending: $message")
            channel.send(message)
            delay(100)
        }
        channel.close() // Important: close the channel when done
    }
    
    // Consumer coroutine
    for (message in channel) {
        println("Received: $message")
    }
    
    println("Channel communication completed")
}

/**
 * Demonstrates different channel capacities
 */
fun demonstrateChannelCapacities() = runBlocking {
    println("\n=== Channel Capacities ===")
    
    // Rendezvous channel (capacity 0) - default
    val rendezvousChannel = Channel<Int>()
    
    // Buffered channel with specific capacity
    val bufferedChannel = Channel<Int>(capacity = 3)
    
    // Unlimited channel
    val unlimitedChannel = Channel<Int>(capacity = Channel.UNLIMITED)
    
    // Conflated channel - keeps only the latest value
    val conflatedChannel = Channel<Int>(capacity = Channel.CONFLATED)
    
    println("Demonstrating rendezvous channel:")
    launch {
        repeat(3) { i ->
            println("Sending $i to rendezvous channel")
            rendezvousChannel.send(i)
            println("Sent $i to rendezvous channel")
        }
        rendezvousChannel.close()
    }
    
    launch {
        delay(500) // Delay to show blocking behavior
        for (value in rendezvousChannel) {
            println("Received $value from rendezvous channel")
            delay(200)
        }
    }
    
    delay(2000) // Wait for rendezvous demo to complete
    
    println("\nDemonstrating buffered channel:")
    launch {
        repeat(5) { i ->
            println("Sending $i to buffered channel")
            bufferedChannel.send(i)
            println("Sent $i to buffered channel (buffer size: 3)")
        }
        bufferedChannel.close()
    }
    
    launch {
        delay(1000) // Delay to show buffering behavior
        for (value in bufferedChannel) {
            println("Received $value from buffered channel")
            delay(100)
        }
    }
    
    delay(3000) // Wait for buffered demo to complete
}

// ================================
// Producer Pattern
// ================================

/**
 * Producer function that creates numbers
 */
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

/**
 * Producer function that creates squares
 */
fun CoroutineScope.produceSquares(numbers: ReceiveChannel<Int>) = produce<Int> {
    for (x in numbers) {
        send(x * x)
    }
}

/**
 * Demonstrates producer-consumer pattern
 */
fun demonstrateProducerConsumer() = runBlocking {
    println("\n=== Producer-Consumer Pattern ===")
    
    val numbers = produceNumbers()
    val squares = produceSquares(numbers)
    
    // Consume first 10 squares
    repeat(10) {
        println("Square: ${squares.receive()}")
    }
    
    println("Cancelling producers...")
    numbers.cancel()
    squares.cancel()
}

/**
 * More complex producer example - web scraper
 */
data class WebPage(val url: String, val content: String, val timestamp: Long)

fun CoroutineScope.produceUrls(urls: List<String>) = produce<String> {
    for (url in urls) {
        send(url)
        delay(50) // Simulate rate limiting
    }
}

suspend fun fetchWebPage(url: String): WebPage {
    delay(Random.nextLong(100, 500)) // Simulate network request
    return WebPage(url, "Content of $url", System.currentTimeMillis())
}

fun CoroutineScope.scrapeWebPages(urls: ReceiveChannel<String>, concurrency: Int = 3) = produce<WebPage> {
    // Create multiple workers
    val workers = List(concurrency) { workerId ->
        launch {
            for (url in urls) {
                try {
                    val page = fetchWebPage(url)
                    send(page)
                    println("Worker $workerId scraped: ${page.url}")
                } catch (e: Exception) {
                    println("Worker $workerId failed to scrape $url: ${e.message}")
                }
            }
        }
    }
    
    // Wait for all workers to complete
    workers.forEach { it.join() }
}

// ================================
// Select Expressions
// ================================

/**
 * Demonstrates select expressions for handling multiple channels
 */
fun demonstrateSelectExpressions() = runBlocking {
    println("\n=== Select Expressions ===")
    
    val channel1 = Channel<String>()
    val channel2 = Channel<String>()
    
    // Producer for channel 1
    launch {
        repeat(5) { i ->
            delay(Random.nextLong(100, 300))
            channel1.send("Channel1-$i")
        }
        channel1.close()
    }
    
    // Producer for channel 2
    launch {
        repeat(5) { i ->
            delay(Random.nextLong(100, 300))
            channel2.send("Channel2-$i")
        }
        channel2.close()
    }
    
    // Consumer using select
    var channel1Closed = false
    var channel2Closed = false
    
    while (!channel1Closed || !channel2Closed) {
        select<Unit> {
            if (!channel1Closed) {
                channel1.onReceiveCatching { result ->
                    if (result.isSuccess) {
                        println("From select - ${result.getOrNull()}")
                    } else {
                        println("Channel 1 closed")
                        channel1Closed = true
                    }
                }
            }
            
            if (!channel2Closed) {
                channel2.onReceiveCatching { result ->
                    if (result.isSuccess) {
                        println("From select - ${result.getOrNull()}")
                    } else {
                        println("Channel 2 closed")
                        channel2Closed = true
                    }
                }
            }
        }
    }
    
    println("Select demonstration completed")
}

/**
 * Select with timeout
 */
suspend fun selectWithTimeout() {
    println("\n=== Select with Timeout ===")
    
    val channel = Channel<String>()
    
    launch {
        delay(2000) // Long delay
        channel.send("Finally!")
    }
    
    select<Unit> {
        channel.onReceive { value ->
            println("Received: $value")
        }
        
        onTimeout(1000) {
            println("Timeout reached!")
        }
    }
    
    channel.close()
}

// ================================
// Fan-in and Fan-out Patterns
// ================================

/**
 * Fan-in pattern - multiple producers, single consumer
 */
fun CoroutineScope.fanIn(input1: ReceiveChannel<String>, input2: ReceiveChannel<String>) = produce<String> {
    var input1Active = true
    var input2Active = true
    
    while (input1Active || input2Active) {
        select<Unit> {
            if (input1Active) {
                input1.onReceiveCatching { result ->
                    if (result.isSuccess) {
                        send("Input1: ${result.getOrNull()}")
                    } else {
                        input1Active = false
                    }
                }
            }
            
            if (input2Active) {
                input2.onReceiveCatching { result ->
                    if (result.isSuccess) {
                        send("Input2: ${result.getOrNull()}")
                    } else {
                        input2Active = false
                    }
                }
            }
        }
    }
}

/**
 * Demonstrates fan-in pattern
 */
fun demonstrateFanIn() = runBlocking {
    println("\n=== Fan-in Pattern ===")
    
    val producer1 = produce<String> {
        repeat(5) { i ->
            send("Producer1-$i")
            delay(Random.nextLong(100, 300))
        }
    }
    
    val producer2 = produce<String> {
        repeat(5) { i ->
            send("Producer2-$i")
            delay(Random.nextLong(100, 300))
        }
    }
    
    val combined = fanIn(producer1, producer2)
    
    for (message in combined) {
        println("Fan-in received: $message")
    }
}

/**
 * Fan-out pattern - single producer, multiple consumers
 */
suspend fun fanOutDemo() {
    println("\n=== Fan-out Pattern ===")
    
    val producer = GlobalScope.produce<Int> {
        repeat(20) { i ->
            send(i)
            delay(100)
        }
    }
    
    // Create multiple consumers
    val consumers = List(3) { consumerId ->
        GlobalScope.launch {
            for (value in producer) {
                println("Consumer $consumerId processed: $value")
                delay(Random.nextLong(200, 500)) // Simulate processing time
            }
            println("Consumer $consumerId finished")
        }
    }
    
    // Wait for all consumers
    consumers.joinAll()
}

// ================================
// Pipeline Patterns
// ================================

/**
 * Pipeline stage 1: Generate numbers
 */
fun CoroutineScope.generateNumbers() = produce<Int> {
    repeat(10) { i ->
        send(i + 1)
        delay(50)
    }
}

/**
 * Pipeline stage 2: Filter even numbers
 */
fun CoroutineScope.filterEven(numbers: ReceiveChannel<Int>) = produce<Int> {
    for (number in numbers) {
        if (number % 2 == 0) {
            send(number)
        }
    }
}

/**
 * Pipeline stage 3: Square numbers
 */
fun CoroutineScope.squareNumbers(numbers: ReceiveChannel<Int>) = produce<Int> {
    for (number in numbers) {
        send(number * number)
        delay(100) // Simulate processing time
    }
}

/**
 * Pipeline stage 4: Format results
 */
fun CoroutineScope.formatResults(numbers: ReceiveChannel<Int>) = produce<String> {
    for (number in numbers) {
        send("Result: $number")
    }
}

/**
 * Demonstrates pipeline pattern
 */
fun demonstratePipeline() = runBlocking {
    println("\n=== Pipeline Pattern ===")
    
    val numbers = generateNumbers()
    val evenNumbers = filterEven(numbers)
    val squaredNumbers = squareNumbers(evenNumbers)
    val formattedResults = formatResults(squaredNumbers)
    
    for (result in formattedResults) {
        println("Pipeline output: $result")
    }
}

// ================================
// Real-World Applications
// ================================

/**
 * Event processing system using channels
 */
sealed class SystemEvent {
    data class UserLogin(val userId: String, val timestamp: Long) : SystemEvent()
    data class UserLogout(val userId: String, val timestamp: Long) : SystemEvent()
    data class OrderPlaced(val orderId: String, val userId: String, val amount: Double) : SystemEvent()
    data class PaymentProcessed(val paymentId: String, val orderId: String, val success: Boolean) : SystemEvent()
}

class EventProcessor {
    private val eventChannel = Channel<SystemEvent>(capacity = Channel.UNLIMITED)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    fun start() {
        // Audit logger
        scope.launch {
            for (event in eventChannel) {
                logEvent(event)
            }
        }
        
        // Analytics processor
        scope.launch {
            for (event in eventChannel) {
                processAnalytics(event)
            }
        }
        
        // Notification sender
        scope.launch {
            for (event in eventChannel) {
                sendNotifications(event)
            }
        }
    }
    
    suspend fun publishEvent(event: SystemEvent) {
        eventChannel.send(event)
    }
    
    private suspend fun logEvent(event: SystemEvent) {
        delay(10) // Simulate logging latency
        println("AUDIT: $event")
    }
    
    private suspend fun processAnalytics(event: SystemEvent) {
        delay(50) // Simulate analytics processing
        when (event) {
            is SystemEvent.UserLogin -> println("ANALYTICS: User ${event.userId} logged in")
            is SystemEvent.OrderPlaced -> println("ANALYTICS: Order revenue: ${event.amount}")
            else -> {}
        }
    }
    
    private suspend fun sendNotifications(event: SystemEvent) {
        delay(100) // Simulate notification sending
        when (event) {
            is SystemEvent.OrderPlaced -> println("NOTIFICATION: Order confirmation sent for ${event.orderId}")
            is SystemEvent.PaymentProcessed -> {
                if (event.success) {
                    println("NOTIFICATION: Payment success for ${event.orderId}")
                } else {
                    println("NOTIFICATION: Payment failed for ${event.orderId}")
                }
            }
            else -> {}
        }
    }
    
    fun shutdown() {
        eventChannel.close()
        scope.cancel()
    }
}

/**
 * Task queue system using channels
 */
data class Task(val id: String, val type: String, val payload: String, val priority: Int = 0)

class TaskQueue {
    private val taskChannel = Channel<Task>(capacity = Channel.UNLIMITED)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val workers = mutableListOf<Job>()
    
    fun start(workerCount: Int = 3) {
        repeat(workerCount) { workerId ->
            val worker = scope.launch {
                println("Worker $workerId started")
                
                for (task in taskChannel) {
                    try {
                        processTask(workerId, task)
                    } catch (e: Exception) {
                        println("Worker $workerId failed to process task ${task.id}: ${e.message}")
                    }
                }
                
                println("Worker $workerId finished")
            }
            workers.add(worker)
        }
    }
    
    suspend fun enqueueTask(task: Task) {
        taskChannel.send(task)
        println("Task enqueued: ${task.id}")
    }
    
    private suspend fun processTask(workerId: Int, task: Task) {
        println("Worker $workerId processing task: ${task.id}")
        
        // Simulate task processing based on type
        val processingTime = when (task.type) {
            "email" -> Random.nextLong(100, 300)
            "image" -> Random.nextLong(500, 1000)
            "data" -> Random.nextLong(200, 600)
            else -> 100
        }
        
        delay(processingTime)
        println("Worker $workerId completed task: ${task.id}")
    }
    
    fun shutdown() {
        taskChannel.close()
        scope.cancel()
    }
    
    suspend fun waitForCompletion() {
        workers.joinAll()
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateRealWorldEventProcessor() = runBlocking {
    println("\n=== Event Processing System ===")
    
    val processor = EventProcessor()
    processor.start()
    
    // Simulate events
    processor.publishEvent(SystemEvent.UserLogin("user123", System.currentTimeMillis()))
    processor.publishEvent(SystemEvent.OrderPlaced("order456", "user123", 99.99))
    processor.publishEvent(SystemEvent.PaymentProcessed("payment789", "order456", true))
    processor.publishEvent(SystemEvent.UserLogout("user123", System.currentTimeMillis()))
    
    delay(1000) // Let events process
    processor.shutdown()
}

fun demonstrateTaskQueue() = runBlocking {
    println("\n=== Task Queue System ===")
    
    val taskQueue = TaskQueue()
    taskQueue.start(workerCount = 3)
    
    // Enqueue various tasks
    val tasks = listOf(
        Task("task1", "email", "Send welcome email"),
        Task("task2", "image", "Resize profile picture"),
        Task("task3", "data", "Process user analytics"),
        Task("task4", "email", "Send notification"),
        Task("task5", "image", "Generate thumbnail"),
        Task("task6", "data", "Update search index")
    )
    
    tasks.forEach { task ->
        taskQueue.enqueueTask(task)
        delay(100) // Stagger task submission
    }
    
    taskQueue.shutdown()
    taskQueue.waitForCompletion()
    println("All tasks completed")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Build a chat system where messages are distributed to multiple clients via channels
 * 2. Create a web crawler that uses channels to coordinate URL discovery and processing
 * 3. Implement a stock price monitor that aggregates data from multiple sources
 * 4. Build a log aggregation system that collects logs from multiple services
 * 5. Create a rate-limited API client using channels for request queuing
 * 6. Implement a batch processor that groups items and processes them in batches
 * 7. Build a distributed cache invalidation system using channels
 * 8. Create a load balancer that distributes requests based on server capacity
 * 9. Implement a real-time data pipeline with multiple processing stages
 * 10. Build a monitoring system that collects metrics from multiple sources
 */

fun main() {
    runBlocking {
        demonstrateBasicChannels()
        demonstrateChannelCapacities()
        demonstrateProducerConsumer()
        demonstrateSelectExpressions()
        selectWithTimeout()
        demonstrateFanIn()
        fanOutDemo()
        demonstratePipeline()
        demonstrateRealWorldEventProcessor()
        demonstrateTaskQueue()
    }
    
    println("\n=== Channels Summary ===")
    println("✓ Channel basics: send, receive, close operations")
    println("✓ Different channel capacities: rendezvous, buffered, unlimited, conflated")
    println("✓ Producer-consumer patterns with produce builder")
    println("✓ Select expressions for handling multiple channels")
    println("✓ Fan-in and fan-out patterns for scalable architectures")
    println("✓ Pipeline patterns for data processing workflows")
    println("✓ Real-world applications: event processing, task queues")
}