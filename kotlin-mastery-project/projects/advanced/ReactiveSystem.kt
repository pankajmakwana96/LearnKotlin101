/**
 * Advanced Project: Reactive Data Processing System
 * 
 * This project demonstrates building a reactive system in Kotlin:
 * - Event-driven architecture with Flow
 * - Real-time data processing pipelines
 * - Backpressure handling and flow control
 * - State management with StateFlow/SharedFlow
 * - Event sourcing patterns
 * - Reactive streams with transformations
 * - Error handling in reactive systems
 * - Testing reactive components
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// ================================
// Event Models
// ================================

@Serializable
sealed class DomainEvent {
    abstract val id: String
    abstract val timestamp: String
    abstract val eventType: String
}

@Serializable
data class UserRegisteredEvent(
    override val id: String,
    override val timestamp: String = LocalDateTime.now().toString(),
    override val eventType: String = "UserRegistered",
    val userId: String,
    val username: String,
    val email: String
) : DomainEvent()

@Serializable
data class OrderCreatedEvent(
    override val id: String,
    override val timestamp: String = LocalDateTime.now().toString(),
    override val eventType: String = "OrderCreated",
    val orderId: String,
    val userId: String,
    val amount: Double,
    val items: List<String>
) : DomainEvent()

@Serializable
data class PaymentProcessedEvent(
    override val id: String,
    override val timestamp: String = LocalDateTime.now().toString(),
    override val eventType: String = "PaymentProcessed",
    val paymentId: String,
    val orderId: String,
    val amount: Double,
    val status: String // "SUCCESS" or "FAILED"
) : DomainEvent()

@Serializable
data class InventoryUpdatedEvent(
    override val id: String,
    override val timestamp: String = LocalDateTime.now().toString(),
    override val eventType: String = "InventoryUpdated",
    val productId: String,
    val quantityChange: Int,
    val newQuantity: Int
) : DomainEvent()

@Serializable
data class NotificationEvent(
    override val id: String,
    override val timestamp: String = LocalDateTime.now().toString(),
    override val eventType: String = "Notification",
    val userId: String,
    val message: String,
    val channel: String // "EMAIL", "SMS", "PUSH"
) : DomainEvent()

// ================================
// Data Models and State
// ================================

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val registrationDate: String = LocalDateTime.now().toString()
)

@Serializable
data class Order(
    val id: String,
    val userId: String,
    val items: List<String>,
    val amount: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: String = LocalDateTime.now().toString()
)

@Serializable
enum class OrderStatus {
    PENDING, PAID, SHIPPED, COMPLETED, CANCELLED
}

@Serializable
data class Payment(
    val id: String,
    val orderId: String,
    val amount: Double,
    val status: PaymentStatus,
    val processedAt: String = LocalDateTime.now().toString()
)

@Serializable
enum class PaymentStatus {
    PENDING, SUCCESS, FAILED, REFUNDED
}

@Serializable
data class SystemMetrics(
    val eventsProcessed: Long = 0,
    val activeUsers: Int = 0,
    val totalOrders: Long = 0,
    val revenue: Double = 0.0,
    val lastUpdated: String = LocalDateTime.now().toString()
)

// ================================
// Event Store and Sourcing
// ================================

interface EventStore {
    suspend fun append(event: DomainEvent)
    fun getEventStream(): Flow<DomainEvent>
    fun getEventsByType(eventType: String): Flow<DomainEvent>
    suspend fun getEventsForAggregate(aggregateId: String): List<DomainEvent>
    suspend fun getEventCount(): Long
}

class InMemoryEventStore : EventStore {
    private val events = mutableListOf<DomainEvent>()
    private val eventChannel = Channel<DomainEvent>(Channel.UNLIMITED)
    
    override suspend fun append(event: DomainEvent) {
        synchronized(events) {
            events.add(event)
        }
        eventChannel.send(event)
    }
    
    override fun getEventStream(): Flow<DomainEvent> {
        return eventChannel.receiveAsFlow()
    }
    
    override fun getEventsByType(eventType: String): Flow<DomainEvent> {
        return getEventStream().filter { it.eventType == eventType }
    }
    
    override suspend fun getEventsForAggregate(aggregateId: String): List<DomainEvent> {
        return synchronized(events) {
            events.filter { event ->
                when (event) {
                    is UserRegisteredEvent -> event.userId == aggregateId
                    is OrderCreatedEvent -> event.orderId == aggregateId
                    is PaymentProcessedEvent -> event.orderId == aggregateId
                    is InventoryUpdatedEvent -> event.productId == aggregateId
                    else -> false
                }
            }
        }
    }
    
    override suspend fun getEventCount(): Long {
        return synchronized(events) { events.size.toLong() }
    }
    
    fun getAllEvents(): List<DomainEvent> {
        return synchronized(events) { events.toList() }
    }
}

// ================================
// Event Processors and Handlers
// ================================

abstract class EventProcessor<T : DomainEvent> {
    abstract suspend fun process(event: T)
    abstract fun canHandle(event: DomainEvent): Boolean
    
    @Suppress("UNCHECKED_CAST")
    suspend fun handleEvent(event: DomainEvent) {
        if (canHandle(event)) {
            process(event as T)
        }
    }
}

class UserEventProcessor : EventProcessor<UserRegisteredEvent>() {
    private val users = ConcurrentHashMap<String, User>()
    
    override suspend fun process(event: UserRegisteredEvent) {
        val user = User(
            id = event.userId,
            username = event.username,
            email = event.email,
            registrationDate = event.timestamp
        )
        users[event.userId] = user
        println("👤 User registered: ${user.username} (${user.email})")
    }
    
    override fun canHandle(event: DomainEvent): Boolean = event is UserRegisteredEvent
    
    fun getUser(userId: String): User? = users[userId]
    fun getAllUsers(): List<User> = users.values.toList()
    fun getUserCount(): Int = users.size
}

class OrderEventProcessor : EventProcessor<OrderCreatedEvent>() {
    private val orders = ConcurrentHashMap<String, Order>()
    
    override suspend fun process(event: OrderCreatedEvent) {
        val order = Order(
            id = event.orderId,
            userId = event.userId,
            items = event.items,
            amount = event.amount,
            createdAt = event.timestamp
        )
        orders[event.orderId] = order
        println("🛒 Order created: ${order.id} for user ${order.userId} - $${order.amount}")
    }
    
    override fun canHandle(event: DomainEvent): Boolean = event is OrderCreatedEvent
    
    fun getOrder(orderId: String): Order? = orders[orderId]
    fun getOrdersForUser(userId: String): List<Order> = orders.values.filter { it.userId == userId }
    fun getAllOrders(): List<Order> = orders.values.toList()
    fun getTotalRevenue(): Double = orders.values.sumOf { it.amount }
}

class PaymentEventProcessor(
    private val orderProcessor: OrderEventProcessor
) : EventProcessor<PaymentProcessedEvent>() {
    
    override suspend fun process(event: PaymentProcessedEvent) {
        val order = orderProcessor.getOrder(event.orderId)
        if (order != null) {
            val status = if (event.status == "SUCCESS") "PAID" else "PAYMENT_FAILED"
            println("💳 Payment ${event.status.lowercase()}: ${event.paymentId} for order ${event.orderId}")
        }
    }
    
    override fun canHandle(event: DomainEvent): Boolean = event is PaymentProcessedEvent
}

class NotificationEventProcessor : EventProcessor<NotificationEvent>() {
    private val notifications = mutableListOf<NotificationEvent>()
    
    override suspend fun process(event: NotificationEvent) {
        synchronized(notifications) {
            notifications.add(event)
        }
        
        when (event.channel) {
            "EMAIL" -> sendEmail(event)
            "SMS" -> sendSms(event)
            "PUSH" -> sendPushNotification(event)
        }
    }
    
    override fun canHandle(event: DomainEvent): Boolean = event is NotificationEvent
    
    private suspend fun sendEmail(notification: NotificationEvent) {
        delay(50) // Simulate email sending delay
        println("📧 Email sent to user ${notification.userId}: ${notification.message}")
    }
    
    private suspend fun sendSms(notification: NotificationEvent) {
        delay(30) // Simulate SMS sending delay
        println("📱 SMS sent to user ${notification.userId}: ${notification.message}")
    }
    
    private suspend fun sendPushNotification(notification: NotificationEvent) {
        delay(10) // Simulate push notification delay
        println("🔔 Push notification sent to user ${notification.userId}: ${notification.message}")
    }
    
    fun getNotificationHistory(): List<NotificationEvent> = 
        synchronized(notifications) { notifications.toList() }
}

// ================================
// Reactive Event Bus
// ================================

class ReactiveEventBus(
    private val eventStore: EventStore
) {
    private val processors = mutableListOf<EventProcessor<*>>()
    private val _metrics = MutableStateFlow(SystemMetrics())
    val metrics: StateFlow<SystemMetrics> = _metrics.asStateFlow()
    
    fun addProcessor(processor: EventProcessor<*>) {
        processors.add(processor)
    }
    
    suspend fun publish(event: DomainEvent) {
        // Store event
        eventStore.append(event)
        
        // Process event with all applicable processors
        processors.forEach { processor ->
            try {
                processor.handleEvent(event)
            } catch (e: Exception) {
                println("❌ Error processing event ${event.id} with ${processor::class.simpleName}: ${e.message}")
            }
        }
        
        // Update metrics
        updateMetrics()
    }
    
    fun getEventStream(): Flow<DomainEvent> = eventStore.getEventStream()
    
    fun getEventsByType(eventType: String): Flow<DomainEvent> = eventStore.getEventsByType(eventType)
    
    private suspend fun updateMetrics() {
        val userProcessor = processors.find { it is UserEventProcessor } as? UserEventProcessor
        val orderProcessor = processors.find { it is OrderEventProcessor } as? OrderEventProcessor
        
        _metrics.value = SystemMetrics(
            eventsProcessed = eventStore.getEventCount(),
            activeUsers = userProcessor?.getUserCount() ?: 0,
            totalOrders = orderProcessor?.getAllOrders()?.size?.toLong() ?: 0L,
            revenue = orderProcessor?.getTotalRevenue() ?: 0.0
        )
    }
}

// ================================
// Real-time Data Processing Pipelines
// ================================

class DataProcessingPipeline(
    private val eventBus: ReactiveEventBus
) {
    
    fun createUserActivityStream(): Flow<String> {
        return eventBus.getEventStream()
            .filter { it is UserRegisteredEvent || it is OrderCreatedEvent }
            .map { event ->
                when (event) {
                    is UserRegisteredEvent -> "User ${event.username} registered"
                    is OrderCreatedEvent -> "User ${event.userId} created order ${event.orderId}"
                    else -> "Unknown activity"
                }
            }
    }
    
    fun createRevenueStream(): Flow<Double> {
        return eventBus.getEventsByType("OrderCreated")
            .map { event -> (event as OrderCreatedEvent).amount }
            .scan(0.0) { total, orderAmount -> total + orderAmount }
    }
    
    fun createFailedPaymentAlerts(): Flow<String> {
        return eventBus.getEventsByType("PaymentProcessed")
            .map { it as PaymentProcessedEvent }
            .filter { it.status == "FAILED" }
            .map { "🚨 Payment failed for order ${it.orderId}: $${it.amount}" }
    }
    
    fun createOrderVolumeMetrics(): Flow<OrderVolumeMetric> {
        return eventBus.getEventsByType("OrderCreated")
            .map { it as OrderCreatedEvent }
            .windowed(Duration.ofSeconds(10)) { orders ->
                OrderVolumeMetric(
                    timeWindow = "Last 10 seconds",
                    orderCount = orders.size,
                    totalAmount = orders.sumOf { it.amount },
                    averageAmount = if (orders.isNotEmpty()) orders.sumOf { it.amount } / orders.size else 0.0
                )
            }
    }
    
    fun createHighValueOrderAlerts(): Flow<String> {
        return eventBus.getEventsByType("OrderCreated")
            .map { it as OrderCreatedEvent }
            .filter { it.amount > 1000.0 }
            .map { "💎 High-value order: ${it.orderId} - $${it.amount}" }
    }
}

@Serializable
data class OrderVolumeMetric(
    val timeWindow: String,
    val orderCount: Int,
    val totalAmount: Double,
    val averageAmount: Double
)

// ================================
// Backpressure and Flow Control
// ================================

class BackpressureManager {
    
    fun <T> Flow<T>.withBackpressure(bufferSize: Int = 100): Flow<T> {
        return buffer(capacity = bufferSize, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    }
    
    fun <T> Flow<T>.withRateLimiting(maxEmissionsPerSecond: Int): Flow<T> {
        val delayBetweenEmissions = 1000L / maxEmissionsPerSecond
        return flow {
            collect { value ->
                emit(value)
                delay(delayBetweenEmissions)
            }
        }
    }
    
    fun <T> Flow<T>.batchProcessing(batchSize: Int, timeWindow: Duration): Flow<List<T>> {
        return windowed(timeWindow) { items ->
            items.chunked(batchSize)
        }.flatMapConcat { batches ->
            batches.asFlow()
        }
    }
    
    fun <T> Flow<T>.retryWithBackoff(
        maxRetries: Int = 3,
        initialDelay: Duration = 100.milliseconds,
        maxDelay: Duration = 1.seconds,
        backoffFactor: Double = 2.0
    ): Flow<T> {
        return retryWhen { cause, attempt ->
            if (attempt < maxRetries) {
                val delay = minOf(
                    initialDelay.inWholeMilliseconds * (backoffFactor.toInt().shl(attempt.toInt())),
                    maxDelay.inWholeMilliseconds
                )
                delay(delay)
                true
            } else {
                false
            }
        }
    }
}

// ================================
// Event Sourcing Projections
// ================================

class ProjectionManager(
    private val eventStore: EventStore
) {
    
    suspend fun buildUserProjection(): Map<String, User> {
        val users = mutableMapOf<String, User>()
        
        eventStore.getEventStream()
            .filter { it is UserRegisteredEvent }
            .map { it as UserRegisteredEvent }
            .collect { event ->
                users[event.userId] = User(
                    id = event.userId,
                    username = event.username,
                    email = event.email,
                    registrationDate = event.timestamp
                )
            }
        
        return users
    }
    
    suspend fun buildOrderSummaryProjection(): Map<String, OrderSummary> {
        val orderSummaries = mutableMapOf<String, OrderSummary>()
        
        eventStore.getEventStream()
            .collect { event ->
                when (event) {
                    is OrderCreatedEvent -> {
                        orderSummaries[event.orderId] = OrderSummary(
                            orderId = event.orderId,
                            userId = event.userId,
                            amount = event.amount,
                            status = "CREATED",
                            lastUpdated = event.timestamp
                        )
                    }
                    is PaymentProcessedEvent -> {
                        orderSummaries[event.orderId]?.let { summary ->
                            orderSummaries[event.orderId] = summary.copy(
                                status = if (event.status == "SUCCESS") "PAID" else "PAYMENT_FAILED",
                                lastUpdated = event.timestamp
                            )
                        }
                    }
                }
            }
        
        return orderSummaries
    }
}

@Serializable
data class OrderSummary(
    val orderId: String,
    val userId: String,
    val amount: Double,
    val status: String,
    val lastUpdated: String
)

// ================================
// Event Generator for Testing
// ================================

class EventGenerator(
    private val eventBus: ReactiveEventBus
) {
    private val userNames = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry")
    private val productNames = listOf("Laptop", "Phone", "Tablet", "Watch", "Headphones", "Camera")
    private val idGenerator = AtomicLong(1)
    
    suspend fun generateRandomEvents(eventCount: Int, delayBetweenEvents: Duration = 100.milliseconds) {
        repeat(eventCount) {
            val event = generateRandomEvent()
            eventBus.publish(event)
            delay(delayBetweenEvents)
        }
    }
    
    private fun generateRandomEvent(): DomainEvent {
        val eventTypes = listOf("user", "order", "payment", "notification")
        val eventType = eventTypes.random()
        
        return when (eventType) {
            "user" -> generateUserRegisteredEvent()
            "order" -> generateOrderCreatedEvent()
            "payment" -> generatePaymentProcessedEvent()
            else -> generateNotificationEvent()
        }
    }
    
    private fun generateUserRegisteredEvent(): UserRegisteredEvent {
        val username = userNames.random()
        val userId = "user_${idGenerator.getAndIncrement()}"
        
        return UserRegisteredEvent(
            id = "event_${idGenerator.getAndIncrement()}",
            userId = userId,
            username = username,
            email = "${username.lowercase()}@example.com"
        )
    }
    
    private fun generateOrderCreatedEvent(): OrderCreatedEvent {
        val orderId = "order_${idGenerator.getAndIncrement()}"
        val userId = "user_${Random.nextInt(1, 10)}"
        val items = (1..Random.nextInt(1, 4)).map { productNames.random() }
        val amount = Random.nextDouble(10.0, 2000.0)
        
        return OrderCreatedEvent(
            id = "event_${idGenerator.getAndIncrement()}",
            orderId = orderId,
            userId = userId,
            amount = amount,
            items = items
        )
    }
    
    private fun generatePaymentProcessedEvent(): PaymentProcessedEvent {
        val paymentId = "payment_${idGenerator.getAndIncrement()}"
        val orderId = "order_${Random.nextInt(1, 20)}"
        val amount = Random.nextDouble(10.0, 2000.0)
        val status = if (Random.nextDouble() > 0.1) "SUCCESS" else "FAILED"
        
        return PaymentProcessedEvent(
            id = "event_${idGenerator.getAndIncrement()}",
            paymentId = paymentId,
            orderId = orderId,
            amount = amount,
            status = status
        )
    }
    
    private fun generateNotificationEvent(): NotificationEvent {
        val userId = "user_${Random.nextInt(1, 10)}"
        val messages = listOf(
            "Welcome to our platform!",
            "Your order has been confirmed",
            "Payment successful",
            "New features available"
        )
        val channels = listOf("EMAIL", "SMS", "PUSH")
        
        return NotificationEvent(
            id = "event_${idGenerator.getAndIncrement()}",
            userId = userId,
            message = messages.random(),
            channel = channels.random()
        )
    }
}

// ================================
// System Monitoring and Analytics
// ================================

class SystemMonitor(
    private val eventBus: ReactiveEventBus,
    private val pipeline: DataProcessingPipeline
) {
    
    suspend fun startMonitoring() = coroutineScope {
        // Launch monitoring coroutines
        launch { monitorSystemMetrics() }
        launch { monitorUserActivity() }
        launch { monitorRevenue() }
        launch { monitorFailedPayments() }
        launch { monitorHighValueOrders() }
    }
    
    private suspend fun monitorSystemMetrics() {
        eventBus.metrics.collect { metrics ->
            println("📊 System Metrics: " +
                "Events: ${metrics.eventsProcessed}, " +
                "Users: ${metrics.activeUsers}, " +
                "Orders: ${metrics.totalOrders}, " +
                "Revenue: $${String.format("%.2f", metrics.revenue)}")
        }
    }
    
    private suspend fun monitorUserActivity() {
        pipeline.createUserActivityStream()
            .collect { activity ->
                println("👥 Activity: $activity")
            }
    }
    
    private suspend fun monitorRevenue() {
        pipeline.createRevenueStream()
            .sample(5.seconds)
            .collect { totalRevenue ->
                println("💰 Total Revenue: $${String.format("%.2f", totalRevenue)}")
            }
    }
    
    private suspend fun monitorFailedPayments() {
        pipeline.createFailedPaymentAlerts()
            .collect { alert ->
                println(alert)
            }
    }
    
    private suspend fun monitorHighValueOrders() {
        pipeline.createHighValueOrderAlerts()
            .collect { alert ->
                println(alert)
            }
    }
}

// ================================
// Extension Functions for Flow
// ================================

fun <T> Flow<T>.windowed(duration: Duration, collector: suspend (List<T>) -> Unit): Flow<Unit> = flow {
    val buffer = mutableListOf<T>()
    var lastEmission = System.currentTimeMillis()
    
    collect { item ->
        buffer.add(item)
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastEmission >= duration.inWholeMilliseconds) {
            if (buffer.isNotEmpty()) {
                collector(buffer.toList())
                buffer.clear()
                lastEmission = currentTime
                emit(Unit)
            }
        }
    }
    
    if (buffer.isNotEmpty()) {
        collector(buffer.toList())
        emit(Unit)
    }
}

fun <T, R> Flow<T>.windowed(duration: Duration, transform: (List<T>) -> R): Flow<R> = flow {
    val buffer = mutableListOf<T>()
    var lastEmission = System.currentTimeMillis()
    
    collect { item ->
        buffer.add(item)
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastEmission >= duration.inWholeMilliseconds) {
            if (buffer.isNotEmpty()) {
                emit(transform(buffer.toList()))
                buffer.clear()
                lastEmission = currentTime
            }
        }
    }
    
    if (buffer.isNotEmpty()) {
        emit(transform(buffer.toList()))
    }
}

// ================================
// Main Demo Function
// ================================

suspend fun main() = coroutineScope {
    println("🔄 Reactive Data Processing System Demo")
    println("=" * 50)
    
    // Set up the system
    val eventStore = InMemoryEventStore()
    val eventBus = ReactiveEventBus(eventStore)
    
    // Add processors
    val userProcessor = UserEventProcessor()
    val orderProcessor = OrderEventProcessor()
    val paymentProcessor = PaymentEventProcessor(orderProcessor)
    val notificationProcessor = NotificationEventProcessor()
    
    eventBus.addProcessor(userProcessor)
    eventBus.addProcessor(orderProcessor)
    eventBus.addProcessor(paymentProcessor)
    eventBus.addProcessor(notificationProcessor)
    
    // Set up pipeline and monitoring
    val pipeline = DataProcessingPipeline(eventBus)
    val monitor = SystemMonitor(eventBus, pipeline)
    val eventGenerator = EventGenerator(eventBus)
    
    // Start monitoring in the background
    val monitoringJob = launch { monitor.startMonitoring() }
    
    println("🚀 Starting event generation...")
    
    // Generate events
    eventGenerator.generateRandomEvents(50, 200.milliseconds)
    
    println("\n⏱️  Waiting for event processing to complete...")
    delay(5.seconds)
    
    // Show final statistics
    println("\n📈 Final System State:")
    println("Users: ${userProcessor.getUserCount()}")
    println("Orders: ${orderProcessor.getAllOrders().size}")
    println("Total Revenue: $${String.format("%.2f", orderProcessor.getTotalRevenue())}")
    println("Notifications Sent: ${notificationProcessor.getNotificationHistory().size}")
    
    // Clean up
    monitoringJob.cancel()
    
    println("\n=== Reactive System Architecture Summary ===")
    println("✓ Event-driven architecture with domain events")
    println("✓ Event sourcing with in-memory event store")
    println("✓ Real-time data processing pipelines using Flow")
    println("✓ Backpressure handling and flow control")
    println("✓ State management with StateFlow")
    println("✓ Event processors with error handling")
    println("✓ System monitoring and metrics collection")
    println("✓ Reactive streams with transformations")
    
    println("\n💡 Production Considerations:")
    println("• Use persistent event store (EventStore DB, Kafka)")
    println("• Implement proper error recovery and compensation")
    println("• Add event schema evolution and versioning")
    println("• Include comprehensive monitoring and alerting")
    println("• Implement event replay and debugging tools")
    println("• Add distributed tracing and correlation IDs")
    println("• Consider event ordering and causality")
    println("• Implement snapshot generation for projections")
}

/**
 * TODO: Advanced Reactive System Features
 * 
 * 1. Distributed event sourcing with Kafka or EventStore
 * 2. Event schema registry and evolution
 * 3. Saga pattern for distributed transactions
 * 4. CQRS with separate read/write models
 * 5. Event replay and time-travel debugging
 * 6. Snapshot generation and restoration
 * 7. Distributed state management
 * 8. Event-driven microservices communication
 * 9. Real-time dashboard with WebSocket updates
 * 10. Advanced stream processing with Apache Kafka Streams
 * 11. Event-driven testing frameworks
 * 12. Performance optimization for high-throughput scenarios
 * 13. Multi-tenant event isolation
 * 14. Event encryption and security
 * 15. Disaster recovery and backup strategies
 */