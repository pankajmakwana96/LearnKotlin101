/**
 * Generics in Kotlin
 * 
 * This module covers Kotlin's generic type system, including:
 * - Generic classes and interfaces
 * - Generic functions
 * - Type variance (covariance, contravariance, invariance)
 * - Type constraints and bounds
 * - Type erasure and reified types
 * - Star projections and type projections
 * - Real-world applications
 */

// ================================
// Generic Classes and Interfaces
// ================================

/**
 * Basic generic class with type parameter T
 */
class Box<T>(private val value: T) {
    fun getValue(): T = value
    
    fun isEmpty(): Boolean = value == null
    
    override fun toString(): String = "Box($value)"
}

/**
 * Generic interface for data transformation
 */
interface Transformer<In, Out> {
    fun transform(input: In): Out
}

/**
 * Multiple type parameters with constraints
 */
class Repository<K, V : Any> where K : Comparable<K> {
    private val storage = mutableMapOf<K, V>()
    
    fun store(key: K, value: V) {
        storage[key] = value
    }
    
    fun retrieve(key: K): V? = storage[key]
    
    fun getAllSorted(): List<Pair<K, V>> = 
        storage.toList().sortedBy { it.first }
}

// ================================
// Generic Functions
// ================================

/**
 * Generic function with single type parameter
 */
fun <T> swap(pair: Pair<T, T>): Pair<T, T> = Pair(pair.second, pair.first)

/**
 * Generic function with multiple type parameters
 */
fun <T, R> mapAndFilter(
    items: List<T>,
    transform: (T) -> R,
    predicate: (R) -> Boolean
): List<R> {
    return items.map(transform).filter(predicate)
}

/**
 * Generic function with upper bound constraint
 */
fun <T : Comparable<T>> findMax(items: List<T>): T? {
    if (items.isEmpty()) return null
    
    var max = items[0]
    for (item in items) {
        if (item > max) {
            max = item
        }
    }
    return max
}

/**
 * Generic function with multiple constraints
 */
fun <T> processSerializableComparable(item: T): String 
    where T : Comparable<T>, 
          T : java.io.Serializable {
    return "Processing: $item (hashCode: ${item.hashCode()})"
}

// ================================
// Variance: Covariance and Contravariance
// ================================

/**
 * Covariant generic class (producer)
 * out T means T can only be produced (returned), not consumed (as parameter)
 */
interface Producer<out T> {
    fun produce(): T
    // fun consume(item: T) // This would be a compile error
}

/**
 * Contravariant generic class (consumer)
 * in T means T can only be consumed (as parameter), not produced (returned)
 */
interface Consumer<in T> {
    fun consume(item: T)
    // fun produce(): T // This would be a compile error
}

/**
 * Invariant generic class
 * T can be both consumed and produced
 */
interface Processor<T> {
    fun process(item: T): T
}

/**
 * Real-world example: Event system with variance
 */
abstract class Event
class UserEvent(val userId: String) : Event()
class SystemEvent(val message: String) : Event()

class EventProducer<out T : Event> {
    private val events = mutableListOf<T>()
    
    fun addEvent(event: @UnsafeVariance T) { // @UnsafeVariance for internal use
        events.add(event)
    }
    
    fun getEvents(): List<T> = events.toList()
    
    fun getLatestEvent(): T? = events.lastOrNull()
}

class EventHandler<in T : Event> {
    fun handle(event: T) {
        println("Handling event: $event")
    }
    
    fun handleAll(events: List<T>) {
        events.forEach { handle(it) }
    }
}

// ================================
// Type Constraints and Bounds
// ================================

/**
 * Upper bound constraint (T must be a subtype of Number)
 */
fun <T : Number> calculateAverage(numbers: List<T>): Double {
    if (numbers.isEmpty()) return 0.0
    
    val sum = numbers.sumOf { it.toDouble() }
    return sum / numbers.size
}

/**
 * Multiple upper bound constraints
 */
interface Named {
    val name: String
}

interface Aged {
    val age: Int
}

fun <T> describeEntity(entity: T): String 
    where T : Named, 
          T : Aged {
    return "${entity.name} is ${entity.age} years old"
}

/**
 * Generic function with nullable upper bound
 */
fun <T : Any?> processNullable(item: T): String {
    return when (item) {
        null -> "Item is null"
        else -> "Item: $item"
    }
}

// ================================
// Reified Type Parameters
// ================================

/**
 * Reified type parameter allows access to type information at runtime
 * Only works with inline functions
 */
inline fun <reified T> isInstanceOf(obj: Any?): Boolean {
    return obj is T
}

/**
 * Reified type parameter for JSON parsing simulation
 */
inline fun <reified T> parseJson(json: String): T? {
    // In real implementation, would use actual JSON parsing
    println("Parsing JSON to ${T::class.simpleName}: $json")
    return null // Placeholder
}

/**
 * Reified type parameter for filtering collections
 */
inline fun <reified T> List<*>.filterIsInstance(): List<T> {
    return this.filterIsInstance<T>()
}

// ================================
// Star Projections and Type Projections
// ================================

/**
 * Working with star projections (*)
 */
fun processUnknownBox(box: Box<*>) {
    // Can only call methods that don't depend on the generic type
    println("Box is empty: ${box.isEmpty()}")
    println("Box content: ${box.getValue()}") // Returns Any?
}

/**
 * Type projections with bounds
 */
fun processNumberBox(box: Box<out Number>) {
    val value: Number? = box.getValue() // Can safely get Number or subtype
    println("Number value: $value")
}

fun fillBoxWithNumbers(box: Box<in Int>) {
    // Can safely put Int or supertypes
    // val value = box.getValue() // Cannot get value (would return Any?)
}

// ================================
// Advanced Generic Patterns
// ================================

/**
 * Generic builder pattern
 */
class QueryBuilder<T> {
    private val conditions = mutableListOf<String>()
    private var orderBy: String? = null
    private var limit: Int? = null
    
    fun where(condition: String): QueryBuilder<T> {
        conditions.add(condition)
        return this
    }
    
    fun orderBy(field: String): QueryBuilder<T> {
        this.orderBy = field
        return this
    }
    
    fun limit(count: Int): QueryBuilder<T> {
        this.limit = count
        return this
    }
    
    fun build(): String {
        val query = StringBuilder("SELECT * FROM ${T::class.simpleName}")
        
        if (conditions.isNotEmpty()) {
            query.append(" WHERE ${conditions.joinToString(" AND ")}")
        }
        
        orderBy?.let { query.append(" ORDER BY $it") }
        limit?.let { query.append(" LIMIT $it") }
        
        return query.toString()
    }
}

/**
 * Generic cache with expiration
 */
class Cache<K, V> {
    private data class CacheEntry<V>(
        val value: V,
        val timestamp: Long,
        val ttl: Long
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() > timestamp + ttl
    }
    
    private val storage = mutableMapOf<K, CacheEntry<V>>()
    
    fun put(key: K, value: V, ttlMillis: Long = 60000) {
        storage[key] = CacheEntry(value, System.currentTimeMillis(), ttlMillis)
    }
    
    fun get(key: K): V? {
        val entry = storage[key] ?: return null
        
        return if (entry.isExpired()) {
            storage.remove(key)
            null
        } else {
            entry.value
        }
    }
    
    fun cleanup() {
        val expiredKeys = storage.filterValues { it.isExpired() }.keys
        expiredKeys.forEach { storage.remove(it) }
    }
}

/**
 * Generic Result type for error handling
 */
sealed class Result<out T, out E> {
    data class Success<out T>(val value: T) : Result<T, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()
    
    fun <R> map(transform: (T) -> R): Result<R, E> {
        return when (this) {
            is Success -> Success(transform(value))
            is Error -> Error(error)
        }
    }
    
    fun <R> flatMap(transform: (T) -> Result<R, E>): Result<R, E> {
        return when (this) {
            is Success -> transform(value)
            is Error -> Error(error)
        }
    }
    
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Error -> null
    }
    
    fun getOrElse(default: (E) -> T): T = when (this) {
        is Success -> value
        is Error -> default(error)
    }
}

// ================================
// Real-World Examples
// ================================

/**
 * Generic DAO (Data Access Object) pattern
 */
interface Entity {
    val id: String
}

data class User(override val id: String, val name: String, val email: String) : Entity
data class Product(override val id: String, val name: String, val price: Double) : Entity

abstract class BaseDao<T : Entity> {
    protected val storage = mutableMapOf<String, T>()
    
    open fun save(entity: T): T {
        storage[entity.id] = entity
        return entity
    }
    
    open fun findById(id: String): T? = storage[id]
    
    open fun findAll(): List<T> = storage.values.toList()
    
    open fun delete(id: String): Boolean {
        return storage.remove(id) != null
    }
    
    open fun update(entity: T): T? {
        return if (storage.containsKey(entity.id)) {
            storage[entity.id] = entity
            entity
        } else {
            null
        }
    }
}

class UserDao : BaseDao<User>() {
    fun findByEmail(email: String): User? {
        return storage.values.find { it.email == email }
    }
    
    fun findByNamePattern(pattern: String): List<User> {
        return storage.values.filter { it.name.contains(pattern, ignoreCase = true) }
    }
}

/**
 * Generic validation framework
 */
interface Validator<T> {
    fun validate(value: T): List<String>
}

class ValidationResult<T>(
    val value: T,
    val errors: List<String>
) {
    val isValid: Boolean get() = errors.isEmpty()
}

class ValidatorChain<T> {
    private val validators = mutableListOf<Validator<T>>()
    
    fun add(validator: Validator<T>): ValidatorChain<T> {
        validators.add(validator)
        return this
    }
    
    fun validate(value: T): ValidationResult<T> {
        val allErrors = validators.flatMap { it.validate(value) }
        return ValidationResult(value, allErrors)
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateBasicGenerics() {
    println("=== Basic Generics ===")
    
    val stringBox = Box("Hello, World!")
    val intBox = Box(42)
    val nullBox = Box<String?>(null)
    
    println("String box: $stringBox, empty: ${stringBox.isEmpty()}")
    println("Int box: $intBox, empty: ${intBox.isEmpty()}")
    println("Null box: $nullBox, empty: ${nullBox.isEmpty()}")
    
    val swapped = swap(Pair("first", "second"))
    println("Swapped pair: $swapped")
    
    val numbers = listOf(1, 2, 3, 4, 5)
    val evenSquares = mapAndFilter(numbers, { it * it }, { it % 2 == 0 })
    println("Even squares: $evenSquares")
}

fun demonstrateVariance() {
    println("\n=== Variance ===")
    
    val userProducer: EventProducer<UserEvent> = EventProducer()
    val eventProducer: EventProducer<Event> = userProducer // Covariance works
    
    val eventHandler: EventHandler<Event> = EventHandler()
    val userHandler: EventHandler<UserEvent> = eventHandler // Contravariance works
    
    println("Variance demonstration completed")
}

fun demonstrateConstraints() {
    println("\n=== Type Constraints ===")
    
    val numbers = listOf(1, 2, 3, 4, 5)
    val average = calculateAverage(numbers)
    println("Average of $numbers: $average")
    
    val max = findMax(listOf("apple", "banana", "cherry"))
    println("Max string: $max")
}

fun demonstrateReifiedTypes() {
    println("\n=== Reified Types ===")
    
    println("Is 'hello' a String? ${isInstanceOf<String>("hello")}")
    println("Is 42 a String? ${isInstanceOf<String>(42)}")
    
    val mixedList = listOf("hello", 42, "world", 3.14, "kotlin")
    val strings = mixedList.filterIsInstance<String>()
    println("Strings from mixed list: $strings")
}

fun demonstrateAdvancedPatterns() {
    println("\n=== Advanced Patterns ===")
    
    // Query Builder
    val query = QueryBuilder<User>()
        .where("age > 18")
        .where("status = 'active'")
        .orderBy("name")
        .limit(10)
        .build()
    println("Generated query: $query")
    
    // Cache
    val cache = Cache<String, User>()
    val user = User("1", "John Doe", "john@example.com")
    cache.put("user1", user, 5000) // 5 second TTL
    
    println("Cached user: ${cache.get("user1")}")
    
    // Result type
    val successResult: Result<String, String> = Result.Success("Operation completed")
    val errorResult: Result<String, String> = Result.Error("Something went wrong")
    
    val mapped = successResult.map { it.uppercase() }
    println("Mapped result: ${mapped.getOrNull()}")
    println("Error result: ${errorResult.getOrElse { "Default value" }}")
}

fun demonstrateDao() {
    println("\n=== Generic DAO Pattern ===")
    
    val userDao = UserDao()
    
    val user1 = User("1", "Alice Johnson", "alice@example.com")
    val user2 = User("2", "Bob Smith", "bob@example.com")
    
    userDao.save(user1)
    userDao.save(user2)
    
    println("All users: ${userDao.findAll()}")
    println("User by email: ${userDao.findByEmail("alice@example.com")}")
    println("Users with 'o' in name: ${userDao.findByNamePattern("o")}")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a generic `Stack<T>` class with push, pop, peek, isEmpty, and size operations
 * 2. Implement a generic `Either<L, R>` type similar to Result but for two valid types
 * 3. Create a generic `Tree<T>` class with insert, search, and traversal methods
 * 4. Implement a generic `Observable<T>` class that can notify observers of value changes
 * 5. Create a generic `Pool<T>` class for object pooling with acquire and release methods
 * 6. Implement a generic `Lazy<T>` class that computes a value only once when first accessed
 * 7. Create a generic `StateManager<T>` class for managing application state with history
 * 8. Implement a generic `Serializer<T>` interface with implementations for different formats
 * 9. Create a generic `Cache<K, V>` with LRU eviction policy
 * 10. Implement a generic `Pipeline<T>` class for chaining transformations
 */

fun main() {
    demonstrateBasicGenerics()
    demonstrateVariance()
    demonstrateConstraints()
    demonstrateReifiedTypes()
    demonstrateAdvancedPatterns()
    demonstrateDao()
    
    println("\n=== Generics Summary ===")
    println("✓ Generic classes and interfaces for type-safe containers")
    println("✓ Generic functions with type parameters and constraints")
    println("✓ Variance (covariance, contravariance) for flexible type relationships")
    println("✓ Reified type parameters for runtime type access")
    println("✓ Advanced patterns: Builder, Cache, Result, DAO")
    println("✓ Real-world applications in frameworks and libraries")
}