/**
 * Advanced Object-Oriented Programming in Kotlin
 * 
 * This module covers advanced OOP concepts in Kotlin, including:
 * - Sealed classes and interfaces
 * - Data classes and their features
 * - Enum classes with advanced features
 * - Nested and inner classes
 * - Object expressions and declarations
 * - Extension functions and properties
 * - Operator overloading
 * - Type aliases and inline classes
 */

// ================================
// Sealed Classes and Interfaces
// ================================

/**
 * Sealed class for representing different states
 * All subclasses must be in the same file or package
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
        is Loading -> Loading
    }
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
        is Loading -> null
    }
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
}

/**
 * Sealed interface for UI events
 */
sealed interface UiEvent {
    data class Click(val elementId: String) : UiEvent
    data class Scroll(val direction: String, val amount: Int) : UiEvent
    data class KeyPress(val key: String) : UiEvent
    object Refresh : UiEvent
}

/**
 * Sealed class for navigation
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    data class Details(val id: String) : Screen("details/$id")
    data class Settings(val section: String = "general") : Screen("settings/$section")
}

/**
 * Sealed class for payment methods
 */
sealed class PaymentMethod {
    data class CreditCard(
        val number: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val cvv: String
    ) : PaymentMethod()
    
    data class PayPal(val email: String) : PaymentMethod()
    
    data class BankTransfer(
        val accountNumber: String,
        val routingNumber: String
    ) : PaymentMethod()
    
    object Cash : PaymentMethod()
}

// ================================
// Data Classes
// ================================

/**
 * Basic data class with auto-generated functions
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int = 0
) {
    // Additional methods can be added to data classes
    fun getDisplayName(): String = if (name.isNotEmpty()) name else email.substringBefore('@')
    
    fun isAdult(): Boolean = age >= 18
    
    companion object {
        fun createGuest(): User = User("guest", "Guest User", "guest@example.com")
    }
}

/**
 * Data class with validation
 */
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Product ID cannot be blank" }
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(price >= 0) { "Product price cannot be negative" }
    }
    
    fun getDiscountedPrice(discountPercent: Double): Double {
        require(discountPercent in 0.0..100.0) { "Discount must be between 0 and 100%" }
        return price * (1 - discountPercent / 100)
    }
}

/**
 * Data class with nested data classes
 */
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String
) {
    fun getFullAddress(): String = "$street, $city, $state $zipCode"
}

data class Customer(
    val id: String,
    val personalInfo: PersonalInfo,
    val address: Address,
    val preferences: CustomerPreferences = CustomerPreferences()
) {
    data class PersonalInfo(
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone: String? = null
    ) {
        fun getFullName(): String = "$firstName $lastName"
    }
    
    data class CustomerPreferences(
        val emailNotifications: Boolean = true,
        val smsNotifications: Boolean = false,
        val preferredLanguage: String = "en"
    )
}

// ================================
// Advanced Enum Classes
// ================================

/**
 * Enum with properties and methods
 */
enum class Planet(
    val mass: Double,           // in kilograms
    val radius: Double,         // in meters
    val gravity: Double = calculateGravity(mass, radius)
) {
    MERCURY(3.303e23, 2.4397e6),
    VENUS(4.869e24, 6.0518e6),
    EARTH(5.976e24, 6.37814e6),
    MARS(6.421e23, 3.3972e6),
    JUPITER(1.9e27, 7.1492e7),
    SATURN(5.688e26, 6.0268e7),
    URANUS(8.686e25, 2.5559e7),
    NEPTUNE(1.024e26, 2.4746e7);
    
    companion object {
        private fun calculateGravity(mass: Double, radius: Double): Double {
            val G = 6.67300E-11 // Universal gravitational constant
            return G * mass / (radius * radius)
        }
    }
    
    fun surfaceWeight(otherMass: Double): Double = otherMass * gravity
}

/**
 * Enum implementing interface
 */
interface Operation {
    fun apply(x: Double, y: Double): Double
}

enum class BasicOperation : Operation {
    PLUS {
        override fun apply(x: Double, y: Double) = x + y
    },
    MINUS {
        override fun apply(x: Double, y: Double) = x - y
    },
    TIMES {
        override fun apply(x: Double, y: Double) = x * y
    },
    DIVIDE {
        override fun apply(x: Double, y: Double) = x / y
    };
    
    override fun toString(): String = when (this) {
        PLUS -> "+"
        MINUS -> "-"
        TIMES -> "×"
        DIVIDE -> "÷"
    }
}

/**
 * Enum with custom properties and abstract methods
 */
enum class HttpStatus(val code: Int, val description: String) {
    OK(200, "OK") {
        override fun isSuccess() = true
        override fun getMessage() = "Request successful"
    },
    NOT_FOUND(404, "Not Found") {
        override fun isSuccess() = false
        override fun getMessage() = "Resource not found"
    },
    INTERNAL_ERROR(500, "Internal Server Error") {
        override fun isSuccess() = false
        override fun getMessage() = "Internal server error occurred"
    };
    
    abstract fun isSuccess(): Boolean
    abstract fun getMessage(): String
    
    fun isClientError() = code in 400..499
    fun isServerError() = code in 500..599
}

// ================================
// Nested and Inner Classes
// ================================

/**
 * Class with nested classes
 */
class OuterClass(private val outerProperty: String) {
    
    /**
     * Nested class (static in Java terms)
     * Cannot access outer class instance members
     */
    class NestedClass {
        fun doSomething(): String = "Nested class action"
        
        companion object {
            fun createInstance(): NestedClass = NestedClass()
        }
    }
    
    /**
     * Inner class can access outer class members
     */
    inner class InnerClass {
        fun accessOuter(): String = "Inner class accessing: $outerProperty"
        
        fun getOuter(): OuterClass = this@OuterClass
    }
    
    /**
     * Local class inside a function
     */
    fun createLocalClass(): String {
        class LocalClass {
            fun localAction() = "Local class in function, outer property: $outerProperty"
        }
        
        return LocalClass().localAction()
    }
}

/**
 * Builder pattern using nested class
 */
class DatabaseConfig private constructor(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val maxConnections: Int,
    val timeoutMs: Long
) {
    
    class Builder {
        private var host: String = "localhost"
        private var port: Int = 5432
        private var database: String = ""
        private var username: String = ""
        private var password: String = ""
        private var maxConnections: Int = 10
        private var timeoutMs: Long = 30000
        
        fun host(host: String) = apply { this.host = host }
        fun port(port: Int) = apply { this.port = port }
        fun database(database: String) = apply { this.database = database }
        fun username(username: String) = apply { this.username = username }
        fun password(password: String) = apply { this.password = password }
        fun maxConnections(max: Int) = apply { this.maxConnections = max }
        fun timeoutMs(timeout: Long) = apply { this.timeoutMs = timeout }
        
        fun build(): DatabaseConfig {
            require(database.isNotBlank()) { "Database name is required" }
            require(username.isNotBlank()) { "Username is required" }
            
            return DatabaseConfig(host, port, database, username, password, maxConnections, timeoutMs)
        }
    }
    
    companion object {
        fun builder() = Builder()
    }
    
    override fun toString(): String {
        return "DatabaseConfig(host='$host', port=$port, database='$database', " +
               "username='$username', maxConnections=$maxConnections, timeoutMs=$timeoutMs)"
    }
}

// ================================
// Object Expressions and Declarations
// ================================

/**
 * Object declaration (singleton)
 */
object DatabaseManager {
    private var isConnected = false
    private val connections = mutableMapOf<String, String>()
    
    fun connect(connectionString: String): Boolean {
        connections["default"] = connectionString
        isConnected = true
        println("Connected to database: $connectionString")
        return true
    }
    
    fun disconnect() {
        connections.clear()
        isConnected = false
        println("Disconnected from database")
    }
    
    fun isConnected(): Boolean = isConnected
    
    fun executeQuery(sql: String): String {
        return if (isConnected) {
            "Query result for: $sql"
        } else {
            throw IllegalStateException("Not connected to database")
        }
    }
}

/**
 * Class with companion object
 */
class MathUtils {
    companion object {
        const val PI = 3.14159265359
        const val E = 2.71828182846
        
        fun factorial(n: Int): Long {
            require(n >= 0) { "Factorial is not defined for negative numbers" }
            return if (n <= 1) 1 else n * factorial(n - 1)
        }
        
        fun isPrime(n: Int): Boolean {
            if (n < 2) return false
            if (n == 2) return true
            if (n % 2 == 0) return false
            
            for (i in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
                if (n % i == 0) return false
            }
            return true
        }
    }
}

/**
 * Object expressions (anonymous objects)
 */
interface ClickListener {
    fun onClick()
    fun onDoubleClick()
}

class Button(private val text: String) {
    private var clickListener: ClickListener? = null
    
    fun setClickListener(listener: ClickListener) {
        this.clickListener = listener
    }
    
    fun click() {
        clickListener?.onClick()
    }
    
    fun doubleClick() {
        clickListener?.onDoubleClick()
    }
}

// ================================
// Extension Functions and Properties
// ================================

/**
 * Extension functions for String
 */
fun String.isEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

fun String.toTitleCase(): String {
    return this.lowercase().split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun String.removeWhitespace(): String = this.replace("\\s+".toRegex(), "")

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (this.length <= maxLength) this
    else this.take(maxLength - suffix.length) + suffix
}

/**
 * Extension properties
 */
val String.wordCount: Int
    get() = this.trim().split("\\s+".toRegex()).size

val List<Int>.average: Double
    get() = if (this.isEmpty()) 0.0 else this.sum().toDouble() / this.size

/**
 * Extension functions for collections
 */
fun <T> List<T>.second(): T {
    if (this.size < 2) throw NoSuchElementException("List has no second element")
    return this[1]
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}

fun <T> List<T>.chunkedBy(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var currentChunk = mutableListOf<T>()
    
    for (item in this) {
        if (predicate(item) && currentChunk.isNotEmpty()) {
            result.add(currentChunk.toList())
            currentChunk = mutableListOf()
        }
        currentChunk.add(item)
    }
    
    if (currentChunk.isNotEmpty()) {
        result.add(currentChunk)
    }
    
    return result
}

/**
 * Extension functions for custom classes
 */
fun User.isValidForRegistration(): Boolean {
    return name.isNotBlank() && 
           email.isEmail() && 
           age >= 13
}

fun Product.getFormattedPrice(): String {
    return "$%.2f".format(price)
}

// ================================
// Operator Overloading
// ================================

/**
 * Custom class with operator overloading
 */
data class Vector2D(val x: Double, val y: Double) {
    
    // Arithmetic operators
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Double) = Vector2D(x / scalar, y / scalar)
    
    // Unary operators
    operator fun unaryMinus() = Vector2D(-x, -y)
    operator fun unaryPlus() = Vector2D(+x, +y)
    
    // Comparison operators
    operator fun compareTo(other: Vector2D): Int {
        val thisMagnitude = magnitude()
        val otherMagnitude = other.magnitude()
        return thisMagnitude.compareTo(otherMagnitude)
    }
    
    // Index access operators
    operator fun get(index: Int): Double = when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Vector2D has only 2 components")
    }
    
    // Invoke operator
    operator fun invoke(): Double = magnitude()
    
    // Utility methods
    fun magnitude(): Double = kotlin.math.sqrt(x * x + y * y)
    fun normalize(): Vector2D {
        val mag = magnitude()
        return if (mag != 0.0) Vector2D(x / mag, y / mag) else Vector2D(0.0, 0.0)
    }
    
    companion object {
        val ZERO = Vector2D(0.0, 0.0)
        val UNIT_X = Vector2D(1.0, 0.0)
        val UNIT_Y = Vector2D(0.0, 1.0)
    }
}

/**
 * Matrix class with operator overloading
 */
class Matrix(private val data: Array<DoubleArray>) {
    val rows: Int = data.size
    val cols: Int = if (data.isNotEmpty()) data[0].size else 0
    
    operator fun get(row: Int, col: Int): Double = data[row][col]
    
    operator fun set(row: Int, col: Int, value: Double) {
        data[row][col] = value
    }
    
    operator fun plus(other: Matrix): Matrix {
        require(rows == other.rows && cols == other.cols) { 
            "Matrix dimensions must match for addition" 
        }
        
        val result = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i][j] = data[i][j] + other.data[i][j]
            }
        }
        return Matrix(result)
    }
    
    operator fun times(scalar: Double): Matrix {
        val result = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i][j] = data[i][j] * scalar
            }
        }
        return Matrix(result)
    }
    
    override fun toString(): String {
        return data.joinToString("\n") { row ->
            row.joinToString(" ") { "%.2f".format(it) }
        }
    }
}

/**
 * Range class with operator overloading
 */
data class IntRange(val start: Int, val end: Int) {
    operator fun contains(value: Int): Boolean = value in start..end
    
    operator fun iterator(): Iterator<Int> = (start..end).iterator()
    
    fun size(): Int = if (end >= start) end - start + 1 else 0
}

// ================================
// Type Aliases and Inline Classes
// ================================

// Type aliases for better readability
typealias UserId = String
typealias Email = String
typealias Timestamp = Long
typealias EventHandler = (UiEvent) -> Unit
typealias ValidationResult = Pair<Boolean, String>
typealias UserMap = Map<UserId, User>

/**
 * Inline value classes (formerly inline classes)
 * Provide type safety without runtime overhead
 */
@JvmInline
value class Password(private val value: String) {
    init {
        require(value.length >= 8) { "Password must be at least 8 characters long" }
    }
    
    fun validate(): Boolean {
        return value.length >= 8 &&
               value.any { it.isDigit() } &&
               value.any { it.isUpperCase() } &&
               value.any { it.isLowerCase() }
    }
    
    override fun toString(): String = "*".repeat(value.length)
}

@JvmInline
value class Money(val cents: Long) {
    fun toDollars(): Double = cents / 100.0
    
    operator fun plus(other: Money) = Money(cents + other.cents)
    operator fun minus(other: Money) = Money(cents - other.cents)
    operator fun times(multiplier: Int) = Money(cents * multiplier)
    
    companion object {
        fun fromDollars(dollars: Double) = Money((dollars * 100).toLong())
    }
    
    override fun toString(): String = "$%.2f".format(toDollars())
}

@JvmInline
value class ProductId(val value: String) {
    init {
        require(value.matches("[A-Z]{2}\\d{6}".toRegex())) { 
            "Product ID must be in format XX000000" 
        }
    }
}

// ================================
// Real-World Application Examples
// ================================

/**
 * E-commerce system using advanced OOP features
 */
class ECommerceSystem {
    private val users = mutableMapOf<UserId, User>()
    private val products = mutableMapOf<ProductId, Product>()
    
    sealed class OrderStatus {
        object Pending : OrderStatus()
        object Processing : OrderStatus()
        object Shipped : OrderStatus()
        object Delivered : OrderStatus()
        data class Cancelled(val reason: String) : OrderStatus()
    }
    
    data class Order(
        val id: String,
        val userId: UserId,
        val items: List<OrderItem>,
        val status: OrderStatus = OrderStatus.Pending,
        val createdAt: Timestamp = System.currentTimeMillis()
    ) {
        data class OrderItem(
            val productId: ProductId,
            val quantity: Int,
            val priceAtTime: Money
        )
        
        fun getTotalAmount(): Money {
            return items.fold(Money(0)) { total, item ->
                total + (item.priceAtTime * item.quantity)
            }
        }
    }
    
    fun processPayment(order: Order, paymentMethod: PaymentMethod): Result<String> {
        return try {
            when (paymentMethod) {
                is PaymentMethod.CreditCard -> {
                    // Process credit card payment
                    Result.Success("Payment processed via credit card")
                }
                is PaymentMethod.PayPal -> {
                    // Process PayPal payment
                    Result.Success("Payment processed via PayPal")
                }
                is PaymentMethod.BankTransfer -> {
                    // Process bank transfer
                    Result.Success("Bank transfer initiated")
                }
                is PaymentMethod.Cash -> {
                    Result.Success("Cash payment confirmed")
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateSealedClasses() {
    println("=== Sealed Classes ===")
    
    val results = listOf(
        Result.Success("Data loaded"),
        Result.Error(RuntimeException("Network error")),
        Result.Loading
    )
    
    results.forEach { result ->
        val message = when (result) {
            is Result.Success -> "Success: ${result.data}"
            is Result.Error -> "Error: ${result.exception.message}"
            is Result.Loading -> "Loading..."
        }
        println(message)
    }
    
    val events = listOf(
        UiEvent.Click("button1"),
        UiEvent.Scroll("up", 100),
        UiEvent.KeyPress("Enter"),
        UiEvent.Refresh
    )
    
    events.forEach { event ->
        when (event) {
            is UiEvent.Click -> println("Clicked: ${event.elementId}")
            is UiEvent.Scroll -> println("Scrolled ${event.direction}: ${event.amount}px")
            is UiEvent.KeyPress -> println("Key pressed: ${event.key}")
            is UiEvent.Refresh -> println("Refreshing...")
        }
    }
}

fun demonstrateDataClasses() {
    println("\n=== Data Classes ===")
    
    val user1 = User("1", "John Doe", "john@example.com", 25)
    val user2 = user1.copy(name = "Jane Doe")
    
    println("User 1: $user1")
    println("User 2: $user2")
    println("Users are equal: ${user1 == user2}")
    println("User 1 display name: ${user1.getDisplayName()}")
    println("User 1 is adult: ${user1.isAdult()}")
    
    // Destructuring
    val (id, name, email, age) = user1
    println("Destructured: id=$id, name=$name, email=$email, age=$age")
    
    val customer = Customer(
        id = "C001",
        personalInfo = Customer.PersonalInfo("Alice", "Smith", "alice@example.com"),
        address = Address("123 Main St", "Springfield", "IL", "62701")
    )
    
    println("Customer: ${customer.personalInfo.getFullName()}")
    println("Address: ${customer.address.getFullAddress()}")
}

fun demonstrateEnums() {
    println("\n=== Advanced Enums ===")
    
    val earthMass = 75.0 // kg
    Planet.values().forEach { planet ->
        println("${planet.name}: Surface weight = %.2f N".format(planet.surfaceWeight(earthMass)))
    }
    
    val operations = BasicOperation.values()
    operations.forEach { op ->
        val result = op.apply(5.0, 3.0)
        println("5 ${op} 3 = $result")
    }
    
    val statuses = HttpStatus.values()
    statuses.forEach { status ->
        println("${status.code} ${status.description}: Success=${status.isSuccess()}, Message=${status.getMessage()}")
    }
}

fun demonstrateNestedInnerClasses() {
    println("\n=== Nested and Inner Classes ===")
    
    val outer = OuterClass("outer data")
    val nested = OuterClass.NestedClass()
    val inner = outer.InnerClass()
    
    println("Nested: ${nested.doSomething()}")
    println("Inner: ${inner.accessOuter()}")
    println("Local: ${outer.createLocalClass()}")
    
    val dbConfig = DatabaseConfig.builder()
        .host("production-db")
        .port(5432)
        .database("myapp")
        .username("appuser")
        .password("secretpass")
        .maxConnections(20)
        .build()
    
    println("Database config: $dbConfig")
}

fun demonstrateObjects() {
    println("\n=== Object Expressions and Declarations ===")
    
    DatabaseManager.connect("postgresql://localhost:5432/mydb")
    val result = DatabaseManager.executeQuery("SELECT * FROM users")
    println(result)
    
    println("Factorial of 5: ${MathUtils.factorial(5)}")
    println("Is 17 prime? ${MathUtils.isPrime(17)}")
    
    val button = Button("Click Me")
    button.setClickListener(object : ClickListener {
        override fun onClick() {
            println("Button clicked!")
        }
        
        override fun onDoubleClick() {
            println("Button double-clicked!")
        }
    })
    
    button.click()
    button.doubleClick()
}

fun demonstrateExtensions() {
    println("\n=== Extension Functions and Properties ===")
    
    val email = "user@example.com"
    println("Is email valid? ${email.isEmail()}")
    
    val text = "hello world kotlin"
    println("Title case: ${text.toTitleCase()}")
    println("Word count: ${text.wordCount}")
    println("Truncated: ${text.truncate(10)}")
    
    val numbers = listOf(1, 2, 3, 4, 5)
    println("Second element: ${numbers.second()}")
    println("Average: ${numbers.average}")
    
    val user = User("1", "John Doe", "john@example.com", 25)
    println("Valid for registration: ${user.isValidForRegistration()}")
}

fun demonstrateOperatorOverloading() {
    println("\n=== Operator Overloading ===")
    
    val v1 = Vector2D(3.0, 4.0)
    val v2 = Vector2D(1.0, 2.0)
    
    println("v1: $v1, magnitude: ${v1()}")
    println("v2: $v2, magnitude: ${v2()}")
    println("v1 + v2 = ${v1 + v2}")
    println("v1 - v2 = ${v1 - v2}")
    println("v1 * 2 = ${v1 * 2}")
    println("-v1 = ${-v1}")
    println("v1[0] = ${v1[0]}, v1[1] = ${v1[1]}")
    
    val matrix1 = Matrix(arrayOf(
        doubleArrayOf(1.0, 2.0),
        doubleArrayOf(3.0, 4.0)
    ))
    
    val matrix2 = Matrix(arrayOf(
        doubleArrayOf(5.0, 6.0),
        doubleArrayOf(7.0, 8.0)
    ))
    
    println("Matrix 1:\n$matrix1")
    println("Matrix 2:\n$matrix2")
    println("Matrix 1 + Matrix 2:\n${matrix1 + matrix2}")
}

fun demonstrateTypeAliasesInlineClasses() {
    println("\n=== Type Aliases and Inline Classes ===")
    
    val userId: UserId = "user123"
    val email: Email = "user@example.com"
    
    val password = Password("SecurePass123!")
    println("Password valid: ${password.validate()}")
    println("Password display: $password")
    
    val price1 = Money.fromDollars(29.99)
    val price2 = Money.fromDollars(15.50)
    val total = price1 + price2
    
    println("Price 1: $price1")
    println("Price 2: $price2")
    println("Total: $total")
    
    val productId = ProductId("AB123456")
    println("Product ID: $productId")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a sealed class hierarchy for different types of database operations
 * 2. Implement a data class for a complete order system with validation
 * 3. Create an enum for different log levels with custom behavior
 * 4. Design a builder pattern using nested classes for HTTP requests
 * 5. Implement extension functions for date/time manipulation
 * 6. Create a mathematical expression evaluator using operator overloading
 * 7. Design a type-safe configuration system using inline classes
 * 8. Implement a simple state machine using sealed classes and objects
 * 9. Create a DSL for SQL queries using extension functions and operators
 * 10. Design a plugin system using object expressions and interfaces
 */

fun main() {
    demonstrateSealedClasses()
    demonstrateDataClasses()
    demonstrateEnums()
    demonstrateNestedInnerClasses()
    demonstrateObjects()
    demonstrateExtensions()
    demonstrateOperatorOverloading()
    demonstrateTypeAliasesInlineClasses()
    
    println("\n=== Advanced OOP Summary ===")
    println("✓ Sealed classes for restricted hierarchies and type-safe states")
    println("✓ Data classes with auto-generated methods and destructuring")
    println("✓ Advanced enums with properties, methods, and interfaces")
    println("✓ Nested and inner classes for better organization")
    println("✓ Objects for singletons and anonymous implementations")
    println("✓ Extension functions for adding functionality to existing classes")
    println("✓ Operator overloading for natural syntax")
    println("✓ Type aliases and inline classes for type safety and performance")
}