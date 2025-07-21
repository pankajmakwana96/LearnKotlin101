/**
 * Advanced Type System in Kotlin
 * 
 * This module covers Kotlin's advanced type system features:
 * - Type aliases and their applications
 * - Inline classes and value classes
 * - Nothing type and its uses
 * - Star projections and type projections
 * - Higher-kinded types simulation
 * - Type-level programming
 * - Phantom types
 * - Tagged unions with sealed classes
 * - Type safety patterns
 * - Advanced generic constraints
 */

import kotlin.reflect.KClass

// ================================
// Type Aliases
// ================================

/**
 * Basic type aliases for better readability
 */
typealias UserId = String
typealias ProductId = String  
typealias Timestamp = Long
typealias Money = Double
typealias JSON = String

// Function type aliases
typealias Predicate<T> = (T) -> Boolean
typealias Transformer<T, R> = (T) -> R
typealias EventHandler<T> = suspend (T) -> Unit
typealias Validator<T> = (T) -> ValidationResult
typealias Factory<T> = () -> T

// Complex type aliases
typealias UserRepository = Repository<User, UserId>
typealias ProductCatalog = Map<ProductId, Product>
typealias EventBus = Map<KClass<*>, List<EventHandler<*>>>
typealias ValidationRules<T> = List<Validator<T>>

/**
 * Type alias for result types
 */
typealias Result<T> = kotlin.Result<T>
typealias AsyncResult<T> = suspend () -> Result<T>

/**
 * Domain-specific type aliases
 */
typealias Coordinates = Pair<Double, Double>  // Latitude, Longitude
typealias RGB = Triple<Int, Int, Int>          // Red, Green, Blue values
typealias Dimension = Pair<Int, Int>           // Width, Height
typealias Range = Pair<Int, Int>               // Start, End

/**
 * Generic type aliases
 */
typealias Callback<T> = (T) -> Unit
typealias AsyncCallback<T> = suspend (T) -> Unit
typealias OptionalCallback<T> = ((T) -> Unit)?

// ================================
// Inline Classes and Value Classes
// ================================

/**
 * Value classes for type safety without runtime overhead
 */
@JvmInline
value class StrongUserId(val value: String) {
    init {
        require(value.isNotBlank()) { "User ID cannot be blank" }
        require(value.matches(Regex("^[a-zA-Z0-9_-]+$"))) { "Invalid user ID format" }
    }
    
    fun isAdmin(): Boolean = value.startsWith("admin_")
}

@JvmInline
value class StrongProductId(val value: String) {
    init {
        require(value.matches(Regex("^PRD-\\d{6}$"))) { "Product ID must be in format PRD-XXXXXX" }
    }
    
    fun getCategory(): String = value.substring(4, 6)
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@") && value.contains(".")) { "Invalid email format" }
    }
    
    fun getDomain(): String = value.substringAfter("@")
    fun getUsername(): String = value.substringBefore("@")
}

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(value.matches(Regex("^\\+?[1-9]\\d{1,14}$"))) { "Invalid phone number format" }
    }
    
    fun getCountryCode(): String? {
        return if (value.startsWith("+")) {
            value.substring(1, 3)
        } else null
    }
}

/**
 * Numeric value classes with operations
 */
@JvmInline
value class Price(val cents: Long) {
    constructor(dollars: Double) : this((dollars * 100).toLong())
    
    fun toDollars(): Double = cents / 100.0
    
    operator fun plus(other: Price) = Price(cents + other.cents)
    operator fun minus(other: Price) = Price(cents - other.cents)
    operator fun times(factor: Double) = Price((cents * factor).toLong())
    operator fun div(divisor: Double) = Price((cents / divisor).toLong())
    
    fun applyTax(taxRate: Double): Price = Price((cents * (1 + taxRate)).toLong())
    
    override fun toString(): String = "$${String.format("%.2f", toDollars())}"
}

@JvmInline
value class Weight(val grams: Int) {
    fun toKilograms(): Double = grams / 1000.0
    fun toPounds(): Double = grams * 0.00220462
    
    operator fun plus(other: Weight) = Weight(grams + other.grams)
    operator fun minus(other: Weight) = Weight(grams - other.grams)
    
    companion object {
        fun fromKilograms(kg: Double) = Weight((kg * 1000).toInt())
        fun fromPounds(lbs: Double) = Weight((lbs / 0.00220462).toInt())
    }
}

/**
 * Units of measurement as value classes
 */
@JvmInline
value class Meters(val value: Double) {
    fun toFeet(): Feet = Feet(value * 3.28084)
    fun toInches(): Inches = Inches(value * 39.3701)
    
    operator fun plus(other: Meters) = Meters(value + other.value)
    operator fun minus(other: Meters) = Meters(value - other.value)
    operator fun times(factor: Double) = Meters(value * factor)
}

@JvmInline
value class Feet(val value: Double) {
    fun toMeters(): Meters = Meters(value / 3.28084)
    fun toInches(): Inches = Inches(value * 12)
}

@JvmInline
value class Inches(val value: Double) {
    fun toMeters(): Meters = Meters(value / 39.3701)
    fun toFeet(): Feet = Feet(value / 12)
}

// ================================
// Nothing Type and Bottom Types
// ================================

/**
 * Functions that never return (return Nothing)
 */
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

fun notImplemented(): Nothing = TODO("This feature is not implemented yet")

fun infiniteLoop(): Nothing {
    while (true) {
        Thread.sleep(1000)
    }
}

/**
 * Using Nothing in generic contexts
 */
sealed class Optional<out T> {
    object None : Optional<Nothing>()  // Nothing as a type parameter
    data class Some<out T>(val value: T) : Optional<T>()
    
    fun <R> map(transform: (T) -> R): Optional<R> = when (this) {
        is None -> None
        is Some -> Some(transform(value))
    }
    
    fun getOrElse(default: @UnsafeVariance T): T = when (this) {
        is None -> default
        is Some -> value
    }
}

/**
 * Nothing in error handling
 */
sealed class AsyncResult<out T, out E> {
    data class Success<out T>(val value: T) : AsyncResult<T, Nothing>()
    data class Error<out E>(val error: E) : AsyncResult<Nothing, E>()
    object Loading : AsyncResult<Nothing, Nothing>()
    
    fun <R> map(transform: (T) -> R): AsyncResult<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Error -> Error(error)
        is Loading -> Loading
    }
    
    fun <R> mapError(transform: (E) -> R): AsyncResult<T, R> = when (this) {
        is Success -> Success(value)
        is Error -> Error(transform(error))
        is Loading -> Loading
    }
}

// ================================
// Phantom Types
// ================================

/**
 * Phantom types for compile-time state tracking
 */
sealed class ConnectionState
object Connected : ConnectionState()
object Disconnected : ConnectionState()

class Database<State : ConnectionState> private constructor() {
    companion object {
        fun create(): Database<Disconnected> = Database()
    }
    
    fun connect(): Database<Connected> {
        println("Connecting to database...")
        @Suppress("UNCHECKED_CAST")
        return this as Database<Connected>
    }
}

// Extension functions only available for specific states
fun Database<Disconnected>.connect(): Database<Connected> = this.connect()

fun Database<Connected>.query(sql: String): List<String> {
    println("Executing query: $sql")
    return listOf("Result 1", "Result 2")
}

fun Database<Connected>.disconnect(): Database<Disconnected> {
    println("Disconnecting from database...")
    @Suppress("UNCHECKED_CAST")
    return this as Database<Disconnected>
}

/**
 * Phantom types for compile-time validation
 */
sealed class ValidationState
object Validated : ValidationState()
object Unvalidated : ValidationState()

data class UserInput<State : ValidationState>(
    val name: String,
    val email: String,
    val age: Int
) {
    companion object {
        fun raw(name: String, email: String, age: Int): UserInput<Unvalidated> {
            return UserInput(name, email, age)
        }
    }
}

fun UserInput<Unvalidated>.validate(): UserInput<Validated> {
    require(name.isNotBlank()) { "Name cannot be blank" }
    require(email.contains("@")) { "Invalid email" }
    require(age >= 0) { "Age cannot be negative" }
    
    @Suppress("UNCHECKED_CAST")
    return this as UserInput<Validated>
}

fun UserInput<Validated>.save(): String {
    // Only validated input can be saved
    return "User saved: $name"
}

// ================================
// Tagged Unions with Sealed Classes
// ================================

/**
 * HTTP Response as tagged union
 */
sealed class HttpResponse {
    abstract val statusCode: Int
    
    data class Success(
        override val statusCode: Int,
        val body: String,
        val headers: Map<String, String> = emptyMap()
    ) : HttpResponse()
    
    data class ClientError(
        override val statusCode: Int,
        val error: String
    ) : HttpResponse()
    
    data class ServerError(
        override val statusCode: Int,
        val error: String,
        val details: String? = null
    ) : HttpResponse()
    
    data class NetworkError(
        val exception: Throwable
    ) : HttpResponse() {
        override val statusCode: Int = -1
    }
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is ClientError || this is ServerError || this is NetworkError
}

/**
 * JSON value as tagged union
 */
sealed class JsonValue {
    object JsonNull : JsonValue()
    data class JsonBoolean(val value: Boolean) : JsonValue()
    data class JsonNumber(val value: Double) : JsonValue()
    data class JsonString(val value: String) : JsonValue()
    data class JsonArray(val values: List<JsonValue>) : JsonValue()
    data class JsonObject(val fields: Map<String, JsonValue>) : JsonValue()
    
    fun asString(): String? = (this as? JsonString)?.value
    fun asNumber(): Double? = (this as? JsonNumber)?.value
    fun asBoolean(): Boolean? = (this as? JsonBoolean)?.value
    fun asArray(): List<JsonValue>? = (this as? JsonArray)?.values
    fun asObject(): Map<String, JsonValue>? = (this as? JsonObject)?.fields
}

// ================================
// Advanced Generic Constraints
// ================================

/**
 * Generic constraints with multiple bounds
 */
interface Identifiable {
    val id: String
}

interface Timestamped {
    val timestamp: Long
}

interface Serializable {
    fun serialize(): String
}

/**
 * Function with multiple type constraints
 */
fun <T> processEntity(entity: T): String 
    where T : Identifiable, 
          T : Timestamped, 
          T : Serializable {
    return buildString {
        append("Processing entity ${entity.id}")
        append(" created at ${entity.timestamp}")
        append(" with data: ${entity.serialize()}")
    }
}

/**
 * Generic repository with constraints
 */
interface Repository<T, ID> where T : Identifiable {
    suspend fun save(entity: T): T
    suspend fun findById(id: ID): T?
    suspend fun findAll(): List<T>
    suspend fun delete(id: ID): Boolean
}

/**
 * Self-referencing generic types
 */
interface Comparable<T : Comparable<T>> {
    fun compareTo(other: T): Int
}

data class Version(val major: Int, val minor: Int, val patch: Int) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        return when {
            major != other.major -> major.compareTo(other.major)
            minor != other.minor -> minor.compareTo(other.minor)
            else -> patch.compareTo(other.patch)
        }
    }
}

/**
 * Builder pattern with type constraints
 */
abstract class Builder<T, B : Builder<T, B>> {
    abstract fun build(): T
    
    @Suppress("UNCHECKED_CAST")
    protected fun self(): B = this as B
}

class PersonBuilder : Builder<Person, PersonBuilder>() {
    private var name: String = ""
    private var age: Int = 0
    private var email: String = ""
    
    fun name(name: String): PersonBuilder = self().apply { this.name = name }
    fun age(age: Int): PersonBuilder = self().apply { this.age = age }
    fun email(email: String): PersonBuilder = self().apply { this.email = email }
    
    override fun build(): Person = Person(name, age, email)
}

data class Person(val name: String, val age: Int, val email: String)

// ================================
// Type-Level Programming Patterns
// ================================

/**
 * Type-level lists (compile-time)
 */
sealed class TypeList
object TNil : TypeList()
data class TCons<Head, Tail : TypeList>(val head: Head, val tail: Tail) : TypeList()

/**
 * Type-level computation example
 */
interface TypeComputation<Input, Output> {
    fun compute(input: Input): Output
}

/**
 * Higher-Kinded Types simulation
 */
interface Functor<F> {
    fun <A, B> map(fa: F, f: (A) -> B): F
}

interface Monad<M> : Functor<M> {
    fun <A> pure(value: A): M
    fun <A, B> flatMap(ma: M, f: (A) -> M): M
}

// Optional as a Monad
object OptionalMonad : Monad<Optional<*>> {
    override fun <A> pure(value: A): Optional<A> = Optional.Some(value)
    
    override fun <A, B> flatMap(ma: Optional<*>, f: (A) -> Optional<*>): Optional<*> {
        @Suppress("UNCHECKED_CAST")
        return when (ma as Optional<A>) {
            is Optional.None -> Optional.None
            is Optional.Some -> f(ma.value) as Optional<B>
        }
    }
    
    override fun <A, B> map(fa: Optional<*>, f: (A) -> B): Optional<*> {
        @Suppress("UNCHECKED_CAST")
        return (fa as Optional<A>).map(f)
    }
}

// ================================
// Advanced Type Safety Patterns
// ================================

/**
 * Resource management with types
 */
sealed class ResourceState
object Open : ResourceState()
object Closed : ResourceState()

class FileHandle<State : ResourceState> private constructor(
    private val filename: String
) {
    companion object {
        fun open(filename: String): FileHandle<Open> {
            println("Opening file: $filename")
            return FileHandle(filename)
        }
    }
    
    fun read(): String where State : Open {
        return "Contents of $filename"
    }
    
    fun write(data: String) where State : Open {
        println("Writing to $filename: $data")
    }
    
    fun close(): FileHandle<Closed> {
        println("Closing file: $filename")
        @Suppress("UNCHECKED_CAST")
        return this as FileHandle<Closed>
    }
}

/**
 * State machine with types
 */
sealed class TrafficLightState
object Red : TrafficLightState()
object Yellow : TrafficLightState()
object Green : TrafficLightState()

class TrafficLight<State : TrafficLightState> private constructor() {
    companion object {
        fun start(): TrafficLight<Red> = TrafficLight()
    }
}

fun TrafficLight<Red>.toGreen(): TrafficLight<Green> {
    println("Red -> Green")
    @Suppress("UNCHECKED_CAST")
    return this as TrafficLight<Green>
}

fun TrafficLight<Green>.toYellow(): TrafficLight<Yellow> {
    println("Green -> Yellow")
    @Suppress("UNCHECKED_CAST")
    return this as TrafficLight<Yellow>
}

fun TrafficLight<Yellow>.toRed(): TrafficLight<Red> {
    println("Yellow -> Red")
    @Suppress("UNCHECKED_CAST")
    return this as TrafficLight<Red>
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateValueClasses() {
    println("=== Value Classes Demo ===")
    
    val userId = StrongUserId("user_123")
    val productId = StrongProductId("PRD-123456")
    val email = Email("user@example.com")
    
    println("User ID: $userId, Is Admin: ${userId.isAdmin()}")
    println("Product ID: $productId, Category: ${productId.getCategory()}")
    println("Email: $email, Domain: ${email.getDomain()}")
    
    val price1 = Price(2950) // $29.50
    val price2 = Price(15.75)
    val total = price1 + price2
    val withTax = total.applyTax(0.08)
    
    println("Price 1: $price1")
    println("Price 2: $price2")
    println("Total: $total")
    println("With tax: $withTax")
}

fun demonstratePhantomTypes() {
    println("\n=== Phantom Types Demo ===")
    
    // Database connection state tracking
    val db = Database.create()  // Database<Disconnected>
    val connected = db.connect() // Database<Connected>
    val results = connected.query("SELECT * FROM users")
    val disconnected = connected.disconnect() // Database<Disconnected>
    
    println("Query results: $results")
    
    // Validation state tracking
    val rawInput = UserInput.raw("John Doe", "john@example.com", 25)
    val validatedInput = rawInput.validate()
    val saved = validatedInput.save()
    
    println("Saved: $saved")
}

fun demonstrateTaggedUnions() {
    println("\n=== Tagged Unions Demo ===")
    
    val responses = listOf<HttpResponse>(
        HttpResponse.Success(200, "Success", mapOf("Content-Type" to "application/json")),
        HttpResponse.ClientError(404, "Not Found"),
        HttpResponse.ServerError(500, "Internal Server Error", "Database connection failed"),
        HttpResponse.NetworkError(RuntimeException("Connection timeout"))
    )
    
    responses.forEach { response ->
        when (response) {
            is HttpResponse.Success -> 
                println("Success ${response.statusCode}: ${response.body}")
            is HttpResponse.ClientError -> 
                println("Client Error ${response.statusCode}: ${response.error}")
            is HttpResponse.ServerError -> 
                println("Server Error ${response.statusCode}: ${response.error}")
            is HttpResponse.NetworkError -> 
                println("Network Error: ${response.exception.message}")
        }
    }
}

fun demonstrateAdvancedConstraints() {
    println("\n=== Advanced Constraints Demo ===")
    
    data class Document(
        override val id: String,
        override val timestamp: Long,
        val title: String,
        val content: String
    ) : Identifiable, Timestamped, Serializable {
        override fun serialize(): String = "Document($id, $title)"
    }
    
    val doc = Document("doc1", System.currentTimeMillis(), "Test", "Content")
    val processed = processEntity(doc)
    println(processed)
    
    // Version comparison
    val v1 = Version(1, 0, 0)
    val v2 = Version(1, 0, 1)
    val v3 = Version(2, 0, 0)
    
    println("$v1 < $v2: ${v1 < v2}")
    println("$v2 < $v3: ${v2 < v3}")
    
    // Builder pattern
    val person = PersonBuilder()
        .name("Alice")
        .age(30)
        .email("alice@example.com")
        .build()
    
    println("Built person: $person")
}

fun demonstrateResourceManagement() {
    println("\n=== Resource Management Demo ===")
    
    val file = FileHandle.open("test.txt")
    val content = file.read()
    file.write("New content")
    val closedFile = file.close()
    
    println("File content: $content")
    
    // Traffic light state machine
    val light = TrafficLight.start()   // Red
        .toGreen()                     // Green
        .toYellow()                    // Yellow  
        .toRed()                       // Red
    
    println("Traffic light cycle completed")
}

// ================================
// Support Classes
// ================================

data class User(val id: String, val name: String, val email: String)
data class Product(val id: String, val name: String, val price: Double)
data class ValidationResult(val isValid: Boolean, val errors: List<String> = emptyList())

fun main() {
    demonstrateValueClasses()
    demonstratePhantomTypes()
    demonstrateTaggedUnions()
    demonstrateAdvancedConstraints()
    demonstrateResourceManagement()
    
    println("\n=== Advanced Type System Summary ===")
    println("✓ Type aliases for better domain modeling")
    println("✓ Value classes for zero-cost type safety")
    println("✓ Nothing type for non-returning functions")
    println("✓ Phantom types for compile-time state tracking")
    println("✓ Tagged unions with sealed classes")
    println("✓ Advanced generic constraints")
    println("✓ Type-level programming patterns")
    println("✓ Resource management with type safety")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a financial system using value classes for different currencies
 * 2. Implement a state machine for order processing with phantom types
 * 3. Build a type-safe query builder using advanced generics
 * 4. Create a validation system with tagged union results
 * 5. Implement a resource pool with compile-time state tracking
 * 6. Build a parser using tagged unions for AST representation
 * 7. Create a measurement system with unit conversions using value classes
 * 8. Implement a workflow engine with type-safe state transitions
 * 9. Build a configuration system with type-safe keys
 * 10. Create a distributed system protocol with tagged message types
 */