/**
 * Exception Handling in Kotlin
 * 
 * This module covers comprehensive exception handling in Kotlin:
 * - Try-catch-finally blocks
 * - Exception types and hierarchy
 * - Custom exception creation
 * - Exception best practices
 * - Resource management
 * - Exception handling in coroutines
 * - Functional error handling patterns
 */

import kotlinx.coroutines.*
import java.io.*
import java.net.*

// ================================
// Basic Exception Handling
// ================================

/**
 * Basic try-catch-finally structure
 */
fun basicExceptionHandling() {
    println("=== Basic Exception Handling ===")
    
    try {
        val result = 10 / 0  // Will throw ArithmeticException
        println("Result: $result")
    } catch (e: ArithmeticException) {
        println("Caught ArithmeticException: ${e.message}")
    } catch (e: Exception) {
        println("Caught general exception: ${e.message}")
    } finally {
        println("Finally block always executes")
    }
}

/**
 * Multiple catch blocks for different exception types
 */
fun multipleCatchBlocks(input: String) {
    try {
        val number = input.toInt()
        val result = 100 / number
        val array = arrayOf(1, 2, 3)
        println("Array element: ${array[number]}")
        
    } catch (e: NumberFormatException) {
        println("Invalid number format: ${e.message}")
    } catch (e: ArithmeticException) {
        println("Division by zero: ${e.message}")
    } catch (e: ArrayIndexOutOfBoundsException) {
        println("Array index out of bounds: ${e.message}")
    } catch (e: Exception) {
        println("Unexpected error: ${e.message}")
    }
}

/**
 * Exception handling with when expression
 */
fun handleExceptionWithWhen(exception: Exception): String {
    return when (exception) {
        is IllegalArgumentException -> "Invalid argument provided"
        is IllegalStateException -> "Object is in invalid state"
        is NullPointerException -> "Unexpected null value"
        is IOException -> "I/O operation failed"
        is SecurityException -> "Security violation detected"
        else -> "Unknown error occurred: ${exception.javaClass.simpleName}"
    }
}

// ================================
// Custom Exception Classes
// ================================

/**
 * Custom exception hierarchy
 */
abstract class BusinessException(message: String, cause: Throwable? = null) : Exception(message, cause)

class ValidationException(message: String, val field: String? = null) : BusinessException(message)

class AuthenticationException(message: String) : BusinessException(message)

class AuthorizationException(message: String, val requiredRole: String? = null) : BusinessException(message)

class DataNotFoundException(message: String, val entityType: String, val entityId: String) : BusinessException(message)

class ExternalServiceException(message: String, val serviceName: String, cause: Throwable? = null) : BusinessException(message, cause)

/**
 * Exception with additional context
 */
data class ErrorContext(
    val userId: String? = null,
    val requestId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val additionalInfo: Map<String, Any> = emptyMap()
)

class ContextualException(
    message: String,
    val context: ErrorContext,
    cause: Throwable? = null
) : BusinessException(message, cause) {
    
    override fun toString(): String {
        return buildString {
            append(super.toString())
            append("\nContext: ")
            append("User ID: ${context.userId}, ")
            append("Request ID: ${context.requestId}, ")
            append("Timestamp: ${context.timestamp}")
            if (context.additionalInfo.isNotEmpty()) {
                append("\nAdditional Info: ${context.additionalInfo}")
            }
        }
    }
}

// ================================
// Resource Management
// ================================

/**
 * Safe resource management with use function
 */
fun safeFileReading(fileName: String): String? {
    return try {
        File(fileName).inputStream().use { input ->
            input.bufferedReader().use { reader ->
                reader.readText()
            }
        }
    } catch (e: FileNotFoundException) {
        println("File not found: $fileName")
        null
    } catch (e: IOException) {
        println("Error reading file: ${e.message}")
        null
    }
}

/**
 * Custom resource management with AutoCloseable
 */
class DatabaseConnection : AutoCloseable {
    private var isOpen = true
    
    fun executeQuery(sql: String): List<String> {
        check(isOpen) { "Connection is closed" }
        
        // Simulate query execution
        if (sql.contains("DROP")) {
            throw SecurityException("DROP operations not allowed")
        }
        
        return listOf("Result 1", "Result 2", "Result 3")
    }
    
    override fun close() {
        if (isOpen) {
            isOpen = false
            println("Database connection closed")
        }
    }
    
    fun isClosed(): Boolean = !isOpen
}

fun safeDatabaseOperation(sql: String): List<String>? {
    return try {
        DatabaseConnection().use { connection ->
            connection.executeQuery(sql)
        }
    } catch (e: SecurityException) {
        println("Security violation: ${e.message}")
        null
    } catch (e: Exception) {
        println("Database operation failed: ${e.message}")
        null
    }
}

// ================================
// Exception Handling Patterns
// ================================

/**
 * Result-based error handling (functional approach)
 */
sealed class Result<out T, out E> {
    data class Success<out T>(val value: T) : Result<T, Nothing>()
    data class Failure<out E>(val error: E) : Result<Nothing, E>()
    
    inline fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> Failure(error)
    }
    
    inline fun <R> flatMap(transform: (T) -> Result<R, E>): Result<R, E> = when (this) {
        is Success -> transform(value)
        is Failure -> Failure(error)
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T, E> {
        if (this is Success) action(value)
        return this
    }
    
    inline fun onFailure(action: (E) -> Unit): Result<T, E> {
        if (this is Failure) action(error)
        return this
    }
    
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }
    
    fun getOrDefault(default: T): T = when (this) {
        is Success -> value
        is Failure -> default
    }
    
    inline fun getOrElse(transform: (E) -> T): T = when (this) {
        is Success -> value
        is Failure -> transform(error)
    }
}

/**
 * Service class demonstrating Result pattern
 */
class UserService {
    private val users = mutableMapOf<String, User>()
    
    fun createUser(name: String, email: String): Result<User, String> {
        return try {
            when {
                name.isBlank() -> Result.Failure("Name cannot be blank")
                email.isBlank() -> Result.Failure("Email cannot be blank")
                !email.contains("@") -> Result.Failure("Invalid email format")
                users.values.any { it.email == email } -> Result.Failure("Email already exists")
                else -> {
                    val user = User(generateId(), name, email)
                    users[user.id] = user
                    Result.Success(user)
                }
            }
        } catch (e: Exception) {
            Result.Failure("Unexpected error: ${e.message}")
        }
    }
    
    fun findUser(id: String): Result<User, String> {
        return users[id]?.let { Result.Success(it) } 
            ?: Result.Failure("User not found: $id")
    }
    
    private fun generateId(): String = "user_${System.currentTimeMillis()}"
}

/**
 * Retry mechanism with exponential backoff
 */
suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 1000,
    maxDelay: Long = 10000,
    factor: Double = 2.0,
    operation: suspend () -> T
): T {
    var currentDelay = initialDelay
    var lastException: Exception? = null
    
    repeat(maxRetries) { attempt ->
        try {
            return operation()
        } catch (e: Exception) {
            lastException = e
            
            if (attempt == maxRetries - 1) {
                throw e  // Last attempt failed
            }
            
            println("Attempt ${attempt + 1} failed: ${e.message}. Retrying in ${currentDelay}ms...")
            delay(currentDelay)
            
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
    
    throw lastException ?: IllegalStateException("Retry mechanism failed")
}

// ================================
// Exception Handling in Coroutines
// ================================

/**
 * Exception handling in coroutines
 */
class CoroutineExceptionHandling {
    
    suspend fun basicCoroutineException() {
        println("\n=== Coroutine Exception Handling ===")
        
        try {
            withTimeout(1000) {
                delay(2000)  // Will timeout
                "Success"
            }
        } catch (e: TimeoutCancellationException) {
            println("Operation timed out")
        }
    }
    
    suspend fun supervisorJobExample() {
        supervisorScope {
            val job1 = launch {
                try {
                    delay(100)
                    throw RuntimeException("Job 1 failed")
                } catch (e: Exception) {
                    println("Job 1 exception: ${e.message}")
                }
            }
            
            val job2 = launch {
                delay(200)
                println("Job 2 completed successfully")
            }
            
            joinAll(job1, job2)
        }
    }
    
    suspend fun asyncExceptionHandling() {
        coroutineScope {
            val deferred = async {
                delay(100)
                throw IllegalStateException("Async operation failed")
            }
            
            try {
                val result = deferred.await()
                println("Result: $result")
            } catch (e: IllegalStateException) {
                println("Caught async exception: ${e.message}")
            }
        }
    }
    
    suspend fun exceptionHandlerExample() {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Unhandled coroutine exception: ${exception.message}")
        }
        
        val scope = CoroutineScope(Dispatchers.Default + handler)
        
        scope.launch {
            throw RuntimeException("Unhandled exception in coroutine")
        }.join()
    }
}

// ================================
// Validation and Error Aggregation
// ================================

/**
 * Validation framework with error accumulation
 */
data class ValidationError(
    val field: String,
    val code: String,
    val message: String
)

class ValidationResult {
    private val errors = mutableListOf<ValidationError>()
    
    fun addError(field: String, code: String, message: String) {
        errors.add(ValidationError(field, code, message))
    }
    
    fun isValid(): Boolean = errors.isEmpty()
    
    fun getErrors(): List<ValidationError> = errors.toList()
    
    fun getErrorsForField(field: String): List<ValidationError> =
        errors.filter { it.field == field }
    
    override fun toString(): String {
        return if (isValid()) {
            "Validation passed"
        } else {
            "Validation failed with ${errors.size} error(s):\n" +
                    errors.joinToString("\n") { "  - ${it.field}: ${it.message}" }
        }
    }
}

/**
 * User registration validator
 */
class UserRegistrationValidator {
    
    fun validate(registrationData: RegistrationData): ValidationResult {
        val result = ValidationResult()
        
        // Validate name
        when {
            registrationData.name.isBlank() -> 
                result.addError("name", "REQUIRED", "Name is required")
            registrationData.name.length < 2 -> 
                result.addError("name", "MIN_LENGTH", "Name must be at least 2 characters")
            registrationData.name.length > 50 -> 
                result.addError("name", "MAX_LENGTH", "Name cannot exceed 50 characters")
        }
        
        // Validate email
        when {
            registrationData.email.isBlank() -> 
                result.addError("email", "REQUIRED", "Email is required")
            !registrationData.email.matches(Regex("[^@]+@[^@]+\\.[^@]+")) ->
                result.addError("email", "INVALID_FORMAT", "Invalid email format")
        }
        
        // Validate password
        when {
            registrationData.password.isBlank() -> 
                result.addError("password", "REQUIRED", "Password is required")
            registrationData.password.length < 8 -> 
                result.addError("password", "MIN_LENGTH", "Password must be at least 8 characters")
            !registrationData.password.any { it.isDigit() } ->
                result.addError("password", "MISSING_DIGIT", "Password must contain at least one digit")
            !registrationData.password.any { it.isUpperCase() } ->
                result.addError("password", "MISSING_UPPERCASE", "Password must contain at least one uppercase letter")
        }
        
        // Validate age
        if (registrationData.age < 13) {
            result.addError("age", "MIN_VALUE", "Must be at least 13 years old")
        }
        
        return result
    }
}

// ================================
// Exception Recovery Strategies
// ================================

/**
 * Circuit breaker pattern for external service calls
 */
class CircuitBreaker(
    private val failureThreshold: Int = 5,
    private val recoveryTimeout: Long = 60000,  // 1 minute
    private val monitoringPeriod: Long = 10000   // 10 seconds
) {
    private enum class State { CLOSED, OPEN, HALF_OPEN }
    
    private var state = State.CLOSED
    private var failureCount = 0
    private var lastFailureTime = 0L
    private var nextAttempt = 0L
    
    suspend fun <T> execute(operation: suspend () -> T): T {
        when (state) {
            State.OPEN -> {
                if (System.currentTimeMillis() >= nextAttempt) {
                    state = State.HALF_OPEN
                    println("Circuit breaker: Transitioning to HALF_OPEN")
                } else {
                    throw CircuitBreakerOpenException("Circuit breaker is OPEN")
                }
            }
            
            State.HALF_OPEN -> {
                // Allow one test request
            }
            
            State.CLOSED -> {
                // Normal operation
            }
        }
        
        return try {
            val result = operation()
            onSuccess()
            result
        } catch (e: Exception) {
            onFailure()
            throw e
        }
    }
    
    private fun onSuccess() {
        failureCount = 0
        state = State.CLOSED
        println("Circuit breaker: Operation succeeded, state = CLOSED")
    }
    
    private fun onFailure() {
        failureCount++
        lastFailureTime = System.currentTimeMillis()
        
        if (failureCount >= failureThreshold) {
            state = State.OPEN
            nextAttempt = System.currentTimeMillis() + recoveryTimeout
            println("Circuit breaker: Too many failures, state = OPEN")
        }
    }
}

class CircuitBreakerOpenException(message: String) : Exception(message)

// ================================
// Demonstration Functions
// ================================

fun demonstrateExceptionHandling() {
    println("=== Exception Handling Demonstrations ===")
    
    // Basic exception handling
    basicExceptionHandling()
    
    // Multiple catch blocks
    println("\nTesting multiple catch blocks:")
    multipleCatchBlocks("abc")    // NumberFormatException
    multipleCatchBlocks("0")      // ArithmeticException
    multipleCatchBlocks("5")      // ArrayIndexOutOfBoundsException
    
    // Custom exceptions
    println("\nTesting custom exceptions:")
    try {
        throw ValidationException("Invalid user data", "email")
    } catch (e: ValidationException) {
        println("Caught ValidationException: ${e.message}, Field: ${e.field}")
    }
    
    // Resource management
    println("\nTesting resource management:")
    val result = safeDatabaseOperation("SELECT * FROM users")
    println("Database result: $result")
    
    val dangerousResult = safeDatabaseOperation("DROP TABLE users")
    println("Dangerous operation result: $dangerousResult")
}

fun demonstrateResultPattern() {
    println("\n=== Result Pattern Demonstration ===")
    
    val userService = UserService()
    
    // Successful user creation
    userService.createUser("John Doe", "john@example.com")
        .onSuccess { user -> println("User created: $user") }
        .onFailure { error -> println("Failed to create user: $error") }
    
    // Failed user creation
    userService.createUser("", "invalid-email")
        .onSuccess { user -> println("User created: $user") }
        .onFailure { error -> println("Failed to create user: $error") }
    
    // Chain operations
    val result = userService.createUser("Jane Doe", "jane@example.com")
        .flatMap { user -> userService.findUser(user.id) }
        .map { user -> "Found user: ${user.name}" }
    
    println("Chained result: ${result.getOrNull()}")
}

fun demonstrateValidation() {
    println("\n=== Validation Demonstration ===")
    
    val validator = UserRegistrationValidator()
    
    // Valid registration
    val validData = RegistrationData("John Doe", "john@example.com", "SecurePass123", 25)
    val validResult = validator.validate(validData)
    println("Valid data: $validResult")
    
    // Invalid registration
    val invalidData = RegistrationData("", "invalid-email", "weak", 10)
    val invalidResult = validator.validate(invalidData)
    println("Invalid data: $invalidResult")
}

suspend fun demonstrateCoroutineExceptions() {
    println("\n=== Coroutine Exception Handling ===")
    
    val handler = CoroutineExceptionHandling()
    
    handler.basicCoroutineException()
    handler.supervisorJobExample()
    handler.asyncExceptionHandling()
    handler.exceptionHandlerExample()
}

suspend fun demonstrateRetryMechanism() {
    println("\n=== Retry Mechanism Demonstration ===")
    
    var attempts = 0
    try {
        val result = retryWithBackoff(maxRetries = 3) {
            attempts++
            if (attempts < 3) {
                throw IOException("Network error (attempt $attempts)")
            }
            "Success after $attempts attempts"
        }
        println("Result: $result")
    } catch (e: Exception) {
        println("All retries failed: ${e.message}")
    }
}

// ================================
// Data Classes
// ================================

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class RegistrationData(
    val name: String,
    val email: String,
    val password: String,
    val age: Int
)

// ================================
// Main Function
// ================================

suspend fun main() {
    demonstrateExceptionHandling()
    demonstrateResultPattern()
    demonstrateValidation()
    demonstrateCoroutineExceptions()
    demonstrateRetryMechanism()
    
    println("\n=== Exception Handling Summary ===")
    println("✓ Basic try-catch-finally blocks")
    println("✓ Custom exception hierarchies")
    println("✓ Resource management with use function")
    println("✓ Result-based error handling")
    println("✓ Validation with error accumulation")
    println("✓ Coroutine exception handling")
    println("✓ Retry mechanisms with backoff")
    println("✓ Circuit breaker pattern")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a file processing system with proper exception handling
 * 2. Implement a network client with retry and circuit breaker patterns
 * 3. Build a validation framework for complex data structures
 * 4. Create a logging system with different error levels
 * 5. Implement a transaction system with rollback capabilities
 * 6. Build an error reporting system with context aggregation
 * 7. Create a monitoring system for application health checks
 * 8. Implement graceful degradation for external service failures
 * 9. Build a comprehensive error handling middleware
 * 10. Create a testing framework for exception scenarios
 */