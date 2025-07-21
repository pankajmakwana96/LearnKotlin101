/**
 * Kotlin Architecture Patterns and Design Principles
 * 
 * This comprehensive guide covers architectural patterns and design principles for Kotlin:
 * - SOLID principles in Kotlin
 * - Clean Architecture implementation
 * - Repository pattern
 * - Dependency Injection patterns
 * - Observer pattern with Kotlin Flow
 * - Command pattern
 * - Strategy pattern
 * - State pattern with sealed classes
 * - Factory patterns
 * - Builder pattern with DSL
 * - Adapter and Facade patterns
 * - MVI (Model-View-Intent) architecture
 * 
 * These patterns help create maintainable, testable, and scalable applications.
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// ================================
// SOLID Principles Implementation
// ================================

/**
 * Single Responsibility Principle (SRP)
 * A class should have only one reason to change.
 */

// ❌ Violates SRP - multiple responsibilities
class BadUserManager {
    fun createUser(userData: Map<String, String>): User = TODO()
    fun saveUserToDatabase(user: User) = TODO()
    fun sendWelcomeEmail(user: User) = TODO()
    fun validateUserData(userData: Map<String, String>): Boolean = TODO()
    fun logUserCreation(user: User) = TODO()
}

// ✅ Follows SRP - single responsibility per class
class UserFactory {
    fun createUser(userData: Map<String, String>): User = TODO()
}

class UserRepository {
    fun save(user: User): User = TODO()
    fun findById(id: String): User? = TODO()
}

class EmailService {
    fun sendWelcomeEmail(user: User) = TODO()
}

class UserValidator {
    fun validate(userData: Map<String, String>): ValidationResult = TODO()
}

class AuditLogger {
    fun logUserCreation(user: User) = TODO()
}

/**
 * Open/Closed Principle (OCP)
 * Classes should be open for extension, closed for modification.
 */

// ✅ Base abstraction
interface PaymentProcessor {
    fun processPayment(amount: Double): PaymentResult
}

// ✅ Extensible without modifying existing code
class CreditCardProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): PaymentResult = TODO()
}

class PayPalProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): PaymentResult = TODO()
}

class CryptoProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): PaymentResult = TODO()
}

// ✅ Context that doesn't need modification
class PaymentService(private val processors: Map<PaymentType, PaymentProcessor>) {
    fun processPayment(type: PaymentType, amount: Double): PaymentResult {
        val processor = processors[type] 
            ?: throw IllegalArgumentException("Unsupported payment type: $type")
        return processor.processPayment(amount)
    }
}

/**
 * Liskov Substitution Principle (LSP)
 * Subtypes must be substitutable for their base types.
 */

// ✅ Proper inheritance hierarchy
abstract class Shape {
    abstract fun area(): Double
}

class Rectangle(private val width: Double, private val height: Double) : Shape() {
    override fun area(): Double = width * height
}

class Circle(private val radius: Double) : Shape() {
    override fun area(): Double = Math.PI * radius * radius
}

// ✅ All shapes can be used interchangeably
class AreaCalculator {
    fun calculateTotalArea(shapes: List<Shape>): Double {
        return shapes.sumOf { it.area() }
    }
}

/**
 * Interface Segregation Principle (ISP)
 * Clients shouldn't depend on interfaces they don't use.
 */

// ❌ Fat interface - violates ISP
interface BadWorker {
    fun work()
    fun eat()
    fun sleep()
}

// ✅ Segregated interfaces
interface Worker {
    fun work()
}

interface Eater {
    fun eat()
}

interface Sleeper {
    fun sleep()
}

class Human : Worker, Eater, Sleeper {
    override fun work() = println("Working")
    override fun eat() = println("Eating")
    override fun sleep() = println("Sleeping")
}

class Robot : Worker {
    override fun work() = println("Working efficiently")
    // Robot doesn't need to eat or sleep
}

/**
 * Dependency Inversion Principle (DIP)
 * High-level modules shouldn't depend on low-level modules.
 */

// ✅ Abstraction
interface DataStorage {
    suspend fun save(key: String, data: String)
    suspend fun load(key: String): String?
}

// ✅ Low-level implementation
class DatabaseStorage : DataStorage {
    override suspend fun save(key: String, data: String) = TODO()
    override suspend fun load(key: String): String? = TODO()
}

class FileStorage : DataStorage {
    override suspend fun save(key: String, data: String) = TODO()
    override suspend fun load(key: String): String? = TODO()
}

// ✅ High-level module depends on abstraction
class UserService(private val storage: DataStorage) {
    suspend fun saveUser(user: User) {
        storage.save(user.id, serializeUser(user))
    }
    
    suspend fun loadUser(id: String): User? {
        return storage.load(id)?.let { deserializeUser(it) }
    }
    
    private fun serializeUser(user: User): String = TODO()
    private fun deserializeUser(data: String): User = TODO()
}

// ================================
// Clean Architecture Implementation
// ================================

/**
 * Clean Architecture with Kotlin
 * 
 * Layers (from inside to outside):
 * 1. Entities (Domain Models)
 * 2. Use Cases (Business Logic)
 * 3. Interface Adapters (Repositories, Presenters)
 * 4. Frameworks & Drivers (Database, UI, External APIs)
 */

// ================================
// Domain Layer (Entities & Use Cases)
// ================================

// Entities - Core business objects
data class User(
    val id: String,
    val name: String,
    val email: String,
    val isActive: Boolean = true
)

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val total: Double
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

// Domain Services - Business rules
class OrderDomainService {
    fun calculateTotal(items: List<OrderItem>): Double {
        return items.sumOf { it.quantity * it.price }
    }
    
    fun canCancelOrder(order: Order): Boolean {
        return order.status in setOf(OrderStatus.PENDING, OrderStatus.PROCESSING)
    }
}

// Use Cases - Application business rules
interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun save(user: User): User
    suspend fun findByEmail(email: String): User?
}

interface OrderRepository {
    suspend fun findById(id: String): Order?
    suspend fun save(order: Order): Order
    suspend fun findByUserId(userId: String): List<Order>
}

interface NotificationService {
    suspend fun sendOrderConfirmation(order: Order)
}

// Use Case implementations
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val orderDomainService: OrderDomainService
) {
    suspend fun execute(request: CreateOrderRequest): Result<Order> {
        return try {
            // Validate user exists
            val user = userRepository.findById(request.userId)
                ?: return Result.failure(Exception("User not found"))
            
            if (!user.isActive) {
                return Result.failure(Exception("User is not active"))
            }
            
            // Create order
            val total = orderDomainService.calculateTotal(request.items)
            val order = Order(
                id = generateOrderId(),
                userId = request.userId,
                items = request.items,
                status = OrderStatus.PENDING,
                total = total
            )
            
            // Save order
            val savedOrder = orderRepository.save(order)
            
            // Send notification
            notificationService.sendOrderConfirmation(savedOrder)
            
            Result.success(savedOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateOrderId(): String = TODO()
}

class GetUserOrdersUseCase(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {
    suspend fun execute(userId: String): Result<List<Order>> {
        return try {
            // Validate user exists
            userRepository.findById(userId)
                ?: return Result.failure(Exception("User not found"))
            
            val orders = orderRepository.findByUserId(userId)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ================================
// Interface Adapters Layer
// ================================

// Repository implementations
class DatabaseUserRepository : UserRepository {
    override suspend fun findById(id: String): User? = TODO()
    override suspend fun save(user: User): User = TODO()
    override suspend fun findByEmail(email: String): User? = TODO()
}

class DatabaseOrderRepository : OrderRepository {
    override suspend fun findById(id: String): Order? = TODO()
    override suspend fun save(order: Order): Order = TODO()
    override suspend fun findByUserId(userId: String): List<Order> = TODO()
}

// External service adapters
class EmailNotificationService : NotificationService {
    override suspend fun sendOrderConfirmation(order: Order) = TODO()
}

// Presenters/Controllers
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getUserOrdersUseCase: GetUserOrdersUseCase
) {
    suspend fun createOrder(request: CreateOrderRequest): OrderResponse {
        return when (val result = createOrderUseCase.execute(request)) {
            is Result.Success -> OrderResponse.success(result.getOrNull()!!)
            is Result.Failure -> OrderResponse.error(result.exceptionOrNull()!!.message!!)
        }
    }
    
    suspend fun getUserOrders(userId: String): OrdersResponse {
        return when (val result = getUserOrdersUseCase.execute(userId)) {
            is Result.Success -> OrdersResponse.success(result.getOrNull()!!)
            is Result.Failure -> OrdersResponse.error(result.exceptionOrNull()!!.message!!)
        }
    }
}

// ================================
// Repository Pattern Implementation
// ================================

/**
 * Repository Pattern with caching and error handling
 */
interface Repository<T, ID> {
    suspend fun findById(id: ID): T?
    suspend fun save(entity: T): T
    suspend fun delete(id: ID): Boolean
    suspend fun findAll(): List<T>
}

abstract class CachedRepository<T, ID> : Repository<T, ID> {
    private val cache = mutableMapOf<ID, T>()
    private val cacheTimeout = 5 * 60 * 1000L // 5 minutes
    private val cacheTimestamps = mutableMapOf<ID, Long>()
    
    override suspend fun findById(id: ID): T? {
        // Check cache first
        val cached = getCachedEntity(id)
        if (cached != null) return cached
        
        // Fetch from persistent storage
        val entity = fetchFromStorage(id)
        entity?.let { cache(id, it) }
        
        return entity
    }
    
    override suspend fun save(entity: T): T {
        val savedEntity = saveToStorage(entity)
        val id = extractId(savedEntity)
        cache(id, savedEntity)
        return savedEntity
    }
    
    override suspend fun delete(id: ID): Boolean {
        val deleted = deleteFromStorage(id)
        if (deleted) {
            cache.remove(id)
            cacheTimestamps.remove(id)
        }
        return deleted
    }
    
    private fun getCachedEntity(id: ID): T? {
        val timestamp = cacheTimestamps[id] ?: return null
        return if (System.currentTimeMillis() - timestamp < cacheTimeout) {
            cache[id]
        } else {
            cache.remove(id)
            cacheTimestamps.remove(id)
            null
        }
    }
    
    private fun cache(id: ID, entity: T) {
        cache[id] = entity
        cacheTimestamps[id] = System.currentTimeMillis()
    }
    
    // Abstract methods to be implemented by subclasses
    protected abstract suspend fun fetchFromStorage(id: ID): T?
    protected abstract suspend fun saveToStorage(entity: T): T
    protected abstract suspend fun deleteFromStorage(id: ID): Boolean
    protected abstract fun extractId(entity: T): ID
}

class UserRepositoryImpl : CachedRepository<User, String>() {
    override suspend fun fetchFromStorage(id: String): User? = TODO()
    override suspend fun saveToStorage(entity: User): User = TODO()
    override suspend fun deleteFromStorage(id: String): Boolean = TODO()
    override suspend fun findAll(): List<User> = TODO()
    override fun extractId(entity: User): String = entity.id
}

// ================================
// Observer Pattern with Flow
// ================================

/**
 * Observer Pattern using Kotlin Flow
 */
interface EventBus {
    fun <T : Event> subscribe(eventType: Class<T>): Flow<T>
    suspend fun publish(event: Event)
}

abstract class Event(val timestamp: Long = System.currentTimeMillis())

data class UserCreatedEvent(val user: User) : Event()
data class OrderPlacedEvent(val order: Order) : Event()
data class PaymentProcessedEvent(val orderId: String, val success: Boolean) : Event()

class FlowEventBus : EventBus {
    private val eventFlow = MutableSharedFlow<Event>()
    
    override fun <T : Event> subscribe(eventType: Class<T>): Flow<T> {
        return eventFlow
            .filterIsInstance(eventType)
    }
    
    override suspend fun publish(event: Event) {
        eventFlow.emit(event)
    }
}

// Event handlers
class EmailEventHandler(private val eventBus: EventBus) {
    init {
        GlobalScope.launch {
            eventBus.subscribe(UserCreatedEvent::class.java)
                .collect { event ->
                    sendWelcomeEmail(event.user)
                }
        }
        
        GlobalScope.launch {
            eventBus.subscribe(OrderPlacedEvent::class.java)
                .collect { event ->
                    sendOrderConfirmation(event.order)
                }
        }
    }
    
    private fun sendWelcomeEmail(user: User) = TODO()
    private fun sendOrderConfirmation(order: Order) = TODO()
}

// ================================
// Strategy Pattern
// ================================

/**
 * Strategy Pattern for different algorithms
 */
interface PricingStrategy {
    fun calculatePrice(basePrice: Double, context: PricingContext): Double
}

data class PricingContext(
    val customerType: CustomerType,
    val quantity: Int,
    val seasonality: Season
)

enum class CustomerType { REGULAR, PREMIUM, VIP }
enum class Season { SPRING, SUMMER, FALL, WINTER }

class RegularPricingStrategy : PricingStrategy {
    override fun calculatePrice(basePrice: Double, context: PricingContext): Double {
        return basePrice * context.quantity
    }
}

class PremiumPricingStrategy : PricingStrategy {
    override fun calculatePrice(basePrice: Double, context: PricingContext): Double {
        val discount = if (context.quantity > 10) 0.1 else 0.05
        return basePrice * context.quantity * (1 - discount)
    }
}

class VipPricingStrategy : PricingStrategy {
    override fun calculatePrice(basePrice: Double, context: PricingContext): Double {
        val discount = when {
            context.quantity > 20 -> 0.25
            context.quantity > 10 -> 0.20
            else -> 0.15
        }
        return basePrice * context.quantity * (1 - discount)
    }
}

class PricingService {
    private val strategies = mapOf(
        CustomerType.REGULAR to RegularPricingStrategy(),
        CustomerType.PREMIUM to PremiumPricingStrategy(),
        CustomerType.VIP to VipPricingStrategy()
    )
    
    fun calculatePrice(basePrice: Double, context: PricingContext): Double {
        val strategy = strategies[context.customerType]
            ?: throw IllegalArgumentException("Unknown customer type")
        return strategy.calculatePrice(basePrice, context)
    }
}

// ================================
// State Pattern with Sealed Classes
// ================================

/**
 * State Pattern using sealed classes
 */
sealed class OrderState {
    abstract fun processPayment(order: Order): OrderState
    abstract fun ship(order: Order): OrderState
    abstract fun cancel(order: Order): OrderState
    
    object Pending : OrderState() {
        override fun processPayment(order: Order): OrderState = Processing
        override fun ship(order: Order): OrderState = 
            throw IllegalStateException("Cannot ship pending order")
        override fun cancel(order: Order): OrderState = Cancelled
    }
    
    object Processing : OrderState() {
        override fun processPayment(order: Order): OrderState = 
            throw IllegalStateException("Payment already processed")
        override fun ship(order: Order): OrderState = Shipped
        override fun cancel(order: Order): OrderState = Cancelled
    }
    
    object Shipped : OrderState() {
        override fun processPayment(order: Order): OrderState = 
            throw IllegalStateException("Order already shipped")
        override fun ship(order: Order): OrderState = 
            throw IllegalStateException("Order already shipped")
        override fun cancel(order: Order): OrderState = 
            throw IllegalStateException("Cannot cancel shipped order")
    }
    
    object Cancelled : OrderState() {
        override fun processPayment(order: Order): OrderState = 
            throw IllegalStateException("Cannot process cancelled order")
        override fun ship(order: Order): OrderState = 
            throw IllegalStateException("Cannot ship cancelled order")
        override fun cancel(order: Order): OrderState = this
    }
}

class StatefulOrder(
    val order: Order,
    private var state: OrderState = OrderState.Pending
) {
    fun processPayment(): StatefulOrder {
        state = state.processPayment(order)
        return this
    }
    
    fun ship(): StatefulOrder {
        state = state.ship(order)
        return this
    }
    
    fun cancel(): StatefulOrder {
        state = state.cancel(order)
        return this
    }
    
    fun getCurrentState(): OrderState = state
}

// ================================
// Command Pattern
// ================================

/**
 * Command Pattern for undo/redo functionality
 */
interface Command {
    suspend fun execute(): Result<Unit>
    suspend fun undo(): Result<Unit>
    val description: String
}

class CreateUserCommand(
    private val userRepository: UserRepository,
    private val user: User
) : Command {
    override val description = "Create user ${user.name}"
    
    override suspend fun execute(): Result<Unit> {
        return try {
            userRepository.save(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun undo(): Result<Unit> {
        // Implementation would depend on repository capabilities
        return Result.success(Unit)
    }
}

class CommandInvoker {
    private val history = mutableListOf<Command>()
    private var currentIndex = -1
    
    suspend fun execute(command: Command): Result<Unit> {
        val result = command.execute()
        if (result.isSuccess) {
            // Remove any commands after current index (for redo chain)
            if (currentIndex < history.size - 1) {
                history.subList(currentIndex + 1, history.size).clear()
            }
            
            history.add(command)
            currentIndex++
        }
        return result
    }
    
    suspend fun undo(): Result<Unit> {
        if (currentIndex < 0) {
            return Result.failure(Exception("Nothing to undo"))
        }
        
        val command = history[currentIndex]
        val result = command.undo()
        if (result.isSuccess) {
            currentIndex--
        }
        return result
    }
    
    suspend fun redo(): Result<Unit> {
        if (currentIndex >= history.size - 1) {
            return Result.failure(Exception("Nothing to redo"))
        }
        
        val command = history[currentIndex + 1]
        val result = command.execute()
        if (result.isSuccess) {
            currentIndex++
        }
        return result
    }
}

// ================================
// Factory Patterns
// ================================

/**
 * Factory Method Pattern
 */
abstract class NotificationFactory {
    abstract fun createNotification(): Notification
    
    fun sendNotification(message: String, recipient: String) {
        val notification = createNotification()
        notification.send(message, recipient)
    }
}

interface Notification {
    fun send(message: String, recipient: String)
}

class EmailNotification : Notification {
    override fun send(message: String, recipient: String) = TODO()
}

class SMSNotification : Notification {
    override fun send(message: String, recipient: String) = TODO()
}

class EmailNotificationFactory : NotificationFactory() {
    override fun createNotification(): Notification = EmailNotification()
}

class SMSNotificationFactory : NotificationFactory() {
    override fun createNotification(): Notification = SMSNotification()
}

/**
 * Abstract Factory Pattern
 */
interface UIFactory {
    fun createButton(): Button
    fun createTextInput(): TextInput
}

interface Button {
    fun render()
}

interface TextInput {
    fun render()
}

// Platform-specific implementations
class WebUIFactory : UIFactory {
    override fun createButton(): Button = WebButton()
    override fun createTextInput(): TextInput = WebTextInput()
}

class MobileUIFactory : UIFactory {
    override fun createButton(): Button = MobileButton()
    override fun createTextInput(): TextInput = MobileTextInput()
}

class WebButton : Button {
    override fun render() = println("Rendering web button")
}

class WebTextInput : TextInput {
    override fun render() = println("Rendering web text input")
}

class MobileButton : Button {
    override fun render() = println("Rendering mobile button")
}

class MobileTextInput : TextInput {
    override fun render() = println("Rendering mobile text input")
}

// ================================
// Builder Pattern with DSL
// ================================

/**
 * Builder Pattern using Kotlin DSL
 */
@DslMarker
annotation class ConfigDsl

@ConfigDsl
class DatabaseConfigBuilder {
    var host: String = "localhost"
    var port: Int = 5432
    var database: String = ""
    var username: String = ""
    var password: String = ""
    var maxConnections: Int = 10
    var timeout: Long = 30000
    
    fun connectionPool(init: ConnectionPoolBuilder.() -> Unit) {
        val poolBuilder = ConnectionPoolBuilder()
        poolBuilder.init()
        this.maxConnections = poolBuilder.maxConnections
        this.timeout = poolBuilder.timeout
    }
    
    fun build(): DatabaseConfig {
        require(database.isNotBlank()) { "Database name is required" }
        require(username.isNotBlank()) { "Username is required" }
        
        return DatabaseConfig(host, port, database, username, password, maxConnections, timeout)
    }
}

@ConfigDsl
class ConnectionPoolBuilder {
    var maxConnections: Int = 10
    var timeout: Long = 30000
}

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val maxConnections: Int,
    val timeout: Long
)

// DSL function
fun databaseConfig(init: DatabaseConfigBuilder.() -> Unit): DatabaseConfig {
    val builder = DatabaseConfigBuilder()
    builder.init()
    return builder.build()
}

// Usage example:
/*
val config = databaseConfig {
    host = "production-db.example.com"
    database = "myapp"
    username = "appuser"
    password = "secret"
    
    connectionPool {
        maxConnections = 20
        timeout = 60000
    }
}
*/

// ================================
// MVI Architecture Pattern
// ================================

/**
 * Model-View-Intent (MVI) Architecture
 */

// Model - Represents the state
data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Intent - Represents user actions
sealed class UserListIntent {
    object LoadUsers : UserListIntent()
    object RefreshUsers : UserListIntent()
    data class DeleteUser(val userId: String) : UserListIntent()
    object ClearError : UserListIntent()
}

// View State updates
sealed class UserListEffect {
    data class ShowError(val message: String) : UserListEffect()
    data class NavigateToUserDetail(val userId: String) : UserListEffect()
}

// ViewModel/Presenter in MVI
class UserListViewModel(
    private val userRepository: UserRepository,
    private val getUsersUseCase: GetUserOrdersUseCase
) {
    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<UserListEffect>()
    val effects: SharedFlow<UserListEffect> = _effects.asSharedFlow()
    
    fun processIntent(intent: UserListIntent) {
        when (intent) {
            is UserListIntent.LoadUsers -> loadUsers()
            is UserListIntent.RefreshUsers -> refreshUsers()
            is UserListIntent.DeleteUser -> deleteUser(intent.userId)
            is UserListIntent.ClearError -> clearError()
        }
    }
    
    private fun loadUsers() {
        if (_state.value.isLoading) return
        
        GlobalScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val users = fetchUsers()
                _state.value = _state.value.copy(
                    users = users,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
                _effects.emit(UserListEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
    
    private fun refreshUsers() {
        _state.value = _state.value.copy(users = emptyList())
        loadUsers()
    }
    
    private fun deleteUser(userId: String) {
        GlobalScope.launch {
            try {
                // Delete user logic
                val updatedUsers = _state.value.users.filter { it.id != userId }
                _state.value = _state.value.copy(users = updatedUsers)
            } catch (e: Exception) {
                _effects.emit(UserListEffect.ShowError("Failed to delete user"))
            }
        }
    }
    
    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    private suspend fun fetchUsers(): List<User> = TODO()
}

// ================================
// Dependency Injection Container
// ================================

/**
 * Simple Dependency Injection Container
 */
class DIContainer {
    private val singletons = mutableMapOf<Class<*>, Any>()
    private val factories = mutableMapOf<Class<*>, () -> Any>()
    
    inline fun <reified T> singleton(instance: T) {
        singletons[T::class.java] = instance as Any
    }
    
    inline fun <reified T> factory(noinline factory: () -> T) {
        factories[T::class.java] = factory as () -> Any
    }
    
    inline fun <reified T> get(): T {
        val clazz = T::class.java
        
        // Check singletons first
        singletons[clazz]?.let { return it as T }
        
        // Check factories
        factories[clazz]?.let { factory ->
            return factory() as T
        }
        
        throw IllegalArgumentException("No binding found for ${clazz.name}")
    }
}

// Usage example:
/*
val container = DIContainer().apply {
    singleton<UserRepository>(DatabaseUserRepository())
    singleton<OrderRepository>(DatabaseOrderRepository())
    factory<UserService> { UserService(get()) }
}

val userService = container.get<UserService>()
*/

// ================================
// Data classes and support classes
// ================================

data class CreateOrderRequest(
    val userId: String,
    val items: List<OrderItem>
)

sealed class OrderResponse {
    data class Success(val order: Order) : OrderResponse()
    data class Error(val message: String) : OrderResponse()
    
    companion object {
        fun success(order: Order) = Success(order)
        fun error(message: String) = Error(message)
    }
}

sealed class OrdersResponse {
    data class Success(val orders: List<Order>) : OrdersResponse()
    data class Error(val message: String) : OrdersResponse()
    
    companion object {
        fun success(orders: List<Order>) = Success(orders)
        fun error(message: String) = Error(message)
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)

data class PaymentResult(
    val success: Boolean,
    val transactionId: String? = null,
    val errorMessage: String? = null
)

enum class PaymentType { CREDIT_CARD, PAYPAL, CRYPTO }

/**
 * ARCHITECTURE PRINCIPLES SUMMARY:
 * 
 * 1. **SOLID Principles**: Foundation for maintainable code
 * 2. **Clean Architecture**: Separation of concerns across layers
 * 3. **Repository Pattern**: Abstraction over data access
 * 4. **Observer Pattern**: Decoupled communication with Flow
 * 5. **Strategy Pattern**: Interchangeable algorithms
 * 6. **State Pattern**: Clean state management with sealed classes
 * 7. **Command Pattern**: Encapsulated operations with undo/redo
 * 8. **Factory Patterns**: Object creation abstraction
 * 9. **Builder with DSL**: Type-safe configuration
 * 10. **MVI Architecture**: Unidirectional data flow
 * 
 * These patterns promote:
 * - Testability
 * - Maintainability  
 * - Scalability
 * - Flexibility
 * - Clear separation of concerns
 */