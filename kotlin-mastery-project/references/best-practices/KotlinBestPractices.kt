/**
 * Kotlin Best Practices and Coding Standards
 * 
 * This reference guide covers industry best practices for Kotlin development:
 * - Naming conventions and code style
 * - Idiomatic Kotlin patterns
 * - Performance optimization techniques
 * - Security considerations
 * - Error handling patterns
 * - Testing strategies
 * - Architecture patterns
 * - Code documentation standards
 * 
 * These practices are based on official Kotlin guidelines, industry standards,
 * and real-world production experience.
 */

// ================================
// Naming Conventions
// ================================

/**
 * BEST PRACTICE: Use descriptive, intention-revealing names
 * 
 * Good naming makes code self-documenting and easier to maintain.
 */

// ❌ Poor naming
class Manager {
    fun process(data: Any): Any = TODO()
    var temp: String = ""
    val list1 = mutableListOf<String>()
}

// ✅ Good naming
class UserAccountManager {
    fun processUserRegistration(registrationData: UserRegistrationRequest): UserAccount = TODO()
    var currentSessionToken: String = ""
    val activeUserSessions = mutableListOf<UserSession>()
}

/**
 * NAMING CONVENTIONS REFERENCE:
 * 
 * Classes: PascalCase (UserManager, HttpClient)
 * Functions: camelCase (calculateTotal, isUserValid)
 * Properties: camelCase (userName, accountBalance)
 * Constants: SCREAMING_SNAKE_CASE (MAX_RETRY_COUNT, API_BASE_URL)
 * Packages: lowercase.with.dots (com.company.module)
 * Files: PascalCase.kt (UserManager.kt, HttpUtils.kt)
 */

// ✅ Proper constant naming
class Constants {
    companion object {
        const val MAX_RETRY_ATTEMPTS = 3
        const val DEFAULT_TIMEOUT_SECONDS = 30
        const val API_BASE_URL = "https://api.example.com"
    }
}

// ✅ Boolean property naming (use is/has/can prefixes)
data class User(
    val name: String,
    val email: String,
    val isActive: Boolean,        // Not: active
    val hasPermission: Boolean,   // Not: permission
    val canEdit: Boolean          // Not: editable
)

// ================================
// Idiomatic Kotlin Patterns
// ================================

/**
 * BEST PRACTICE: Leverage Kotlin's language features
 * 
 * Use Kotlin's features to write more concise and expressive code.
 */

// ❌ Java-style getter/setter
class BadUserClass {
    private var name: String = ""
    
    fun getName(): String = name
    fun setName(value: String) { name = value }
}

// ✅ Kotlin properties
class GoodUserClass {
    var name: String = ""
        get() = field.trim()
        set(value) {
            field = value.trim()
        }
}

// ✅ Use data classes for value objects
data class Address(
    val street: String,
    val city: String,
    val zipCode: String
)

// ✅ Use sealed classes for restricted hierarchies
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// ✅ Use extension functions instead of utility classes
// ❌ Utility class approach
object StringUtils {
    fun isValidEmail(email: String): Boolean = 
        email.contains("@") && email.contains(".")
}

// ✅ Extension function approach
fun String.isValidEmail(): Boolean = 
    contains("@") && contains(".")

// ✅ Use scope functions appropriately
fun processUser(user: User?) {
    // Use 'let' for null-safe operations
    user?.let { safeUser ->
        println("Processing user: ${safeUser.name}")
        // Process user
    }
    
    // Use 'apply' for object configuration
    val newUser = User("", "", false).apply {
        // Configure user
    }
    
    // Use 'run' for scoped computations
    val result = run {
        val processedName = user?.name?.trim() ?: "Unknown"
        val processedEmail = user?.email?.lowercase() ?: ""
        "$processedName - $processedEmail"
    }
}

// ================================
// Null Safety Best Practices
// ================================

/**
 * BEST PRACTICE: Embrace null safety
 * 
 * Use Kotlin's null safety features to prevent NPEs.
 */

// ✅ Use nullable types explicitly
fun findUser(id: String): User? = TODO()

// ✅ Use safe call operator
fun getUserEmail(user: User?): String? = user?.email

// ✅ Use Elvis operator for defaults
fun getDisplayName(user: User?): String = user?.name ?: "Anonymous"

// ✅ Use safe cast operator
fun processIfUser(obj: Any) {
    (obj as? User)?.let { user ->
        println("Processing user: ${user.name}")
    }
}

// ✅ Validate parameters early
fun createAccount(email: String?, password: String?): User {
    requireNotNull(email) { "Email cannot be null" }
    requireNotNull(password) { "Password cannot be null" }
    require(email.isNotBlank()) { "Email cannot be blank" }
    require(password.length >= 8) { "Password must be at least 8 characters" }
    
    return User(email, email, true)
}

// ================================
// Error Handling Patterns
// ================================

/**
 * BEST PRACTICE: Use appropriate error handling strategies
 * 
 * Choose the right error handling pattern based on the context.
 */

// ✅ Use Result type for recoverable errors
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
    
    inline fun <R> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
    }
    
    inline fun <R> flatMap(transform: (T) -> NetworkResult<R>): NetworkResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> Error(exception)
    }
}

// ✅ Use custom exceptions for specific error cases
class ValidationException(message: String) : IllegalArgumentException(message)
class AuthenticationException(message: String) : SecurityException(message)
class BusinessLogicException(message: String) : Exception(message)

// ✅ Handle exceptions at appropriate levels
class UserService {
    @Throws(ValidationException::class, AuthenticationException::class)
    fun createUser(request: CreateUserRequest): User {
        // Validate input
        if (!request.email.isValidEmail()) {
            throw ValidationException("Invalid email format")
        }
        
        // Business logic
        if (userExists(request.email)) {
            throw BusinessLogicException("User already exists")
        }
        
        return User(request.name, request.email, true)
    }
    
    private fun userExists(email: String): Boolean = TODO()
}

// ================================
// Performance Best Practices
// ================================

/**
 * BEST PRACTICE: Write performance-conscious code
 * 
 * Consider performance implications of your code choices.
 */

// ✅ Use sequences for large data processing
fun processLargeDataSet(data: List<DataItem>): List<ProcessedItem> {
    return data.asSequence()
        .filter { it.isValid }
        .map { processItem(it) }
        .filter { it.isSuccessful }
        .toList()
}

// ✅ Use appropriate collection types
class UserCache {
    // Use LinkedHashMap to maintain insertion order
    private val cache = linkedMapOf<String, User>()
    
    // Use Set for unique lookups
    private val activeUserIds = mutableSetOf<String>()
    
    // Use List for ordered data
    private val recentUsers = mutableListOf<User>()
}

// ✅ Avoid creating unnecessary objects
fun formatUserInfo(users: List<User>): String = buildString {
    users.forEach { user ->
        append("User: ${user.name}\n")
    }
}

// ✅ Use lazy initialization for expensive resources
class DatabaseConnection {
    private val connection by lazy {
        createExpensiveConnection()
    }
    
    private fun createExpensiveConnection(): Any = TODO()
}

// ✅ Use inline functions for higher-order functions
inline fun <T> measureTime(operation: () -> T): Pair<T, Long> {
    val startTime = System.currentTimeMillis()
    val result = operation()
    val duration = System.currentTimeMillis() - startTime
    return result to duration
}

// ================================
// Concurrency Best Practices
// ================================

/**
 * BEST PRACTICE: Use coroutines effectively
 * 
 * Follow structured concurrency principles.
 */

// ✅ Use appropriate coroutine scope
class UserRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    suspend fun fetchUsers(): List<User> = withContext(Dispatchers.IO) {
        // IO operations
        TODO()
    }
    
    fun cleanup() {
        scope.cancel()
    }
}

// ✅ Handle exceptions in coroutines
suspend fun safeNetworkCall(): Result<String> = try {
    val result = performNetworkCall()
    Result.Success(result)
} catch (e: Exception) {
    Result.Error(e)
}

// ✅ Use Flow for reactive streams
class DataStream {
    fun observeData(): Flow<Data> = flow {
        while (currentCoroutineContext().isActive) {
            emit(fetchData())
            delay(1000)
        }
    }.flowOn(Dispatchers.IO)
    
    private suspend fun fetchData(): Data = TODO()
}

// ================================
// Security Best Practices
// ================================

/**
 * BEST PRACTICE: Implement security by design
 * 
 * Consider security implications in your code.
 */

// ✅ Sanitize user input
fun sanitizeInput(input: String): String {
    return input.trim()
        .replace(Regex("[<>\"'&]"), "") // Remove potentially dangerous characters
        .take(1000) // Limit length
}

// ✅ Use secure random for sensitive operations
import java.security.SecureRandom

class TokenGenerator {
    private val secureRandom = SecureRandom()
    
    fun generateToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

// ✅ Don't log sensitive information
class Logger {
    fun logUserAction(user: User, action: String) {
        // ❌ Don't log sensitive data
        // println("User ${user.email} with password ${user.password} performed $action")
        
        // ✅ Log safely
        println("User ${user.name.take(3)}*** performed $action")
    }
}

// ================================
// Architecture Patterns
// ================================

/**
 * BEST PRACTICE: Follow clean architecture principles
 * 
 * Organize code into layers with clear responsibilities.
 */

// ✅ Use repository pattern for data access
interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun save(user: User): User
    suspend fun delete(id: String): Boolean
}

class DatabaseUserRepository : UserRepository {
    override suspend fun findById(id: String): User? = TODO()
    override suspend fun save(user: User): User = TODO()
    override suspend fun delete(id: String): Boolean = TODO()
}

// ✅ Use dependency injection
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val logger: Logger
) {
    suspend fun createUser(request: CreateUserRequest): User {
        logger.info("Creating user: ${request.email}")
        
        val user = User(request.name, request.email, true)
        val savedUser = userRepository.save(user)
        
        emailService.sendWelcomeEmail(savedUser.email)
        
        return savedUser
    }
}

// ✅ Use sealed classes for state management
sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<out T>(val data: T) : LoadingState<T>()
    data class Error(val exception: Throwable) : LoadingState<Nothing>()
}

// ================================
// Testing Best Practices
// ================================

/**
 * BEST PRACTICE: Write testable code
 * 
 * Design code that is easy to test.
 */

// ✅ Use dependency injection for testability
class OrderService(
    private val paymentGateway: PaymentGateway,
    private val inventoryService: InventoryService,
    private val notificationService: NotificationService
) {
    fun processOrder(order: Order): Result<ProcessedOrder> {
        return try {
            inventoryService.reserveItems(order.items)
            val payment = paymentGateway.processPayment(order.payment)
            notificationService.sendOrderConfirmation(order.customerEmail)
            
            Result.Success(ProcessedOrder(order, payment))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// ✅ Create testable functions (pure functions when possible)
fun calculateTax(amount: Double, taxRate: Double): Double {
    require(amount >= 0) { "Amount cannot be negative" }
    require(taxRate >= 0) { "Tax rate cannot be negative" }
    
    return amount * taxRate
}

// ✅ Use data classes for test data
data class TestUser(
    val id: String = "test-user-123",
    val name: String = "Test User",
    val email: String = "test@example.com",
    val isActive: Boolean = true
)

// ================================
// Documentation Standards
// ================================

/**
 * BEST PRACTICE: Write comprehensive documentation
 * 
 * Use KDoc for API documentation.
 */

/**
 * Manages user accounts and authentication.
 * 
 * This service provides functionality for user registration, authentication,
 * and account management. It integrates with external services for email
 * verification and password validation.
 * 
 * @property userRepository Repository for user data persistence
 * @property passwordEncoder Service for password hashing
 * @property emailService Service for sending emails
 * 
 * @since 1.0.0
 * @author Development Team
 */
class UserAccountService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService
) {
    
    /**
     * Registers a new user account.
     * 
     * Creates a new user account with the provided information. The password
     * is securely hashed before storage, and a verification email is sent.
     * 
     * @param request The registration request containing user details
     * @return The created user account
     * @throws ValidationException if the input data is invalid
     * @throws UserAlreadyExistsException if a user with this email exists
     * 
     * @sample
     * ```kotlin
     * val request = RegistrationRequest(
     *     name = "John Doe",
     *     email = "john@example.com",
     *     password = "securePassword123"
     * )
     * val user = userService.registerUser(request)
     * ```
     */
    @Throws(ValidationException::class, UserAlreadyExistsException::class)
    suspend fun registerUser(request: RegistrationRequest): User {
        validateRegistrationRequest(request)
        
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException("User with email ${request.email} already exists")
        }
        
        val hashedPassword = passwordEncoder.encode(request.password)
        val user = User(
            id = generateUserId(),
            name = request.name,
            email = request.email,
            passwordHash = hashedPassword,
            isActive = false // Requires email verification
        )
        
        val savedUser = userRepository.save(user)
        emailService.sendVerificationEmail(savedUser.email, savedUser.id)
        
        return savedUser
    }
    
    private fun validateRegistrationRequest(request: RegistrationRequest) = TODO()
    private fun generateUserId(): String = TODO()
}

// ================================
// Code Organization Best Practices
// ================================

/**
 * BEST PRACTICE: Organize code logically
 * 
 * Structure your codebase for maintainability.
 */

// ✅ Group related functionality in packages
// com.example.user.domain - Domain models and business logic
// com.example.user.infrastructure - Database, external APIs
// com.example.user.presentation - Controllers, DTOs

// ✅ Use companion objects for factory methods and constants
class User private constructor(
    val id: String,
    val name: String,
    val email: String
) {
    companion object {
        // Factory method
        fun create(name: String, email: String): User {
            require(email.isValidEmail()) { "Invalid email format" }
            return User(generateId(), name, email)
        }
        
        // Constants
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 100
        
        private fun generateId(): String = TODO()
    }
}

// ✅ Use object for singletons
object AppConfig {
    const val VERSION = "1.0.0"
    const val DEBUG_MODE = false
    
    fun getEnvironment(): String = TODO()
}

// ================================
// Example Interfaces and Contracts
// ================================

// Placeholder classes and interfaces for examples
interface EmailService {
    suspend fun sendWelcomeEmail(email: String)
    suspend fun sendVerificationEmail(email: String, userId: String)
}

interface PaymentGateway {
    fun processPayment(payment: Payment): PaymentResult
}

interface InventoryService {
    fun reserveItems(items: List<OrderItem>)
}

interface NotificationService {
    fun sendOrderConfirmation(email: String)
}

interface PasswordEncoder {
    fun encode(password: String): String
    fun matches(password: String, hash: String): Boolean
}

// Data classes for examples
data class CreateUserRequest(val name: String, val email: String)
data class RegistrationRequest(val name: String, val email: String, val password: String)
data class UserSession(val userId: String, val token: String)
data class UserRegistrationRequest(val email: String)
data class UserAccount(val id: String, val email: String)
data class DataItem(val isValid: Boolean, val data: String)
data class ProcessedItem(val isSuccessful: Boolean, val result: String)
data class Data(val value: String, val timestamp: Long)
data class Order(val items: List<OrderItem>, val payment: Payment, val customerEmail: String)
data class OrderItem(val productId: String, val quantity: Int)
data class Payment(val amount: Double, val method: String)
data class PaymentResult(val transactionId: String, val success: Boolean)
data class ProcessedOrder(val order: Order, val payment: PaymentResult)

// Exception classes
class UserAlreadyExistsException(message: String) : Exception(message)

// Extension functions for examples
private fun performNetworkCall(): String = TODO()
private fun processItem(item: DataItem): ProcessedItem = TODO()

// Mock repository methods
private suspend fun UserRepository.existsByEmail(email: String): Boolean = TODO()

/**
 * SUMMARY OF BEST PRACTICES:
 * 
 * 1. **Naming**: Use clear, descriptive names that reveal intent
 * 2. **Null Safety**: Embrace Kotlin's null safety features
 * 3. **Immutability**: Prefer immutable data structures when possible
 * 4. **Functions**: Keep functions small and focused on single responsibility
 * 5. **Error Handling**: Use appropriate error handling strategies
 * 6. **Performance**: Consider performance implications of your choices
 * 7. **Security**: Implement security best practices by default
 * 8. **Testing**: Write testable code with clear dependencies
 * 9. **Documentation**: Document public APIs comprehensively
 * 10. **Architecture**: Follow clean architecture principles
 * 
 * These practices will help you write maintainable, scalable, and robust Kotlin code.
 */