/**
 * Delegation Patterns in Kotlin
 * 
 * This module covers various delegation patterns in Kotlin:
 * - Class delegation with 'by' keyword
 * - Property delegation patterns
 * - Built-in delegates (lazy, observable, vetoable)
 * - Custom delegate implementation
 * - Delegated properties best practices
 * - Interface delegation vs composition
 * - Advanced delegation use cases
 */

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// ================================
// Class Delegation
// ================================

/**
 * Interface for demonstration
 */
interface DataProcessor {
    fun process(data: String): String
    fun validate(data: String): Boolean
    fun getStats(): Map<String, Int>
}

/**
 * Concrete implementation
 */
class DefaultDataProcessor : DataProcessor {
    private var processedCount = 0
    private var validatedCount = 0
    
    override fun process(data: String): String {
        processedCount++
        return data.uppercase().trim()
    }
    
    override fun validate(data: String): Boolean {
        validatedCount++
        return data.isNotBlank() && data.length <= 100
    }
    
    override fun getStats(): Map<String, Int> {
        return mapOf(
            "processed" to processedCount,
            "validated" to validatedCount
        )
    }
}

/**
 * Class using delegation to implement interface
 */
class EnhancedDataProcessor(
    private val delegate: DataProcessor = DefaultDataProcessor()
) : DataProcessor by delegate {
    
    private var enhancementCount = 0
    
    // Override specific methods to add enhancement
    override fun process(data: String): String {
        enhancementCount++
        val processed = delegate.process(data)
        
        // Add enhancement: remove special characters
        return processed.filter { it.isLetterOrDigit() || it.isWhitespace() }
    }
    
    // Add new functionality
    fun getEnhancementCount(): Int = enhancementCount
    
    override fun getStats(): Map<String, Int> {
        val originalStats = delegate.getStats()
        return originalStats + ("enhanced" to enhancementCount)
    }
}

/**
 * Multiple interface delegation
 */
interface Logger {
    fun log(message: String)
    fun logError(message: String, throwable: Throwable?)
}

interface Validator {
    fun validate(input: String): Boolean
    fun getValidationRules(): List<String>
}

class ConsoleLogger : Logger {
    override fun log(message: String) {
        println("[LOG] $message")
    }
    
    override fun logError(message: String, throwable: Throwable?) {
        println("[ERROR] $message")
        throwable?.printStackTrace()
    }
}

class StringValidator : Validator {
    override fun validate(input: String): Boolean {
        return input.isNotBlank() && 
               input.length >= 3 && 
               input.all { it.isLetterOrDigit() || it.isWhitespace() }
    }
    
    override fun getValidationRules(): List<String> {
        return listOf(
            "Must not be blank",
            "Must be at least 3 characters",
            "Must contain only alphanumeric characters and spaces"
        )
    }
}

/**
 * Service combining multiple delegated interfaces
 */
class ValidationService(
    logger: Logger = ConsoleLogger(),
    validator: Validator = StringValidator()
) : Logger by logger, Validator by validator {
    
    fun processWithLogging(input: String): String? {
        log("Processing input: $input")
        
        return if (validate(input)) {
            log("Input validation successful")
            input.trim().lowercase()
        } else {
            val rules = getValidationRules().joinToString(", ")
            logError("Validation failed. Rules: $rules", null)
            null
        }
    }
}

// ================================
// Property Delegation
// ================================

/**
 * Built-in delegates demonstration
 */
class UserPreferences {
    // Lazy initialization
    val expensiveValue: String by lazy {
        println("Computing expensive value...")
        Thread.sleep(100) // Simulate expensive computation
        "Computed value: ${System.currentTimeMillis()}"
    }
    
    // Observable property
    var userName: String by Delegates.observable("default") { property, oldValue, newValue ->
        println("${property.name} changed from '$oldValue' to '$newValue'")
    }
    
    // Vetoable property (with validation)
    var userAge: Int by Delegates.vetoable(0) { property, oldValue, newValue ->
        println("Attempting to change ${property.name} from $oldValue to $newValue")
        newValue in 1..150 // Only allow valid ages
    }
    
    // NotNull delegate
    var requiredSetting: String by Delegates.notNull()
}

/**
 * Map-backed properties
 */
class Configuration(private val config: MutableMap<String, Any>) {
    var host: String by config
    var port: Int by config  
    var ssl: Boolean by config
    var timeout: Long by config
    
    // Additional helper methods
    fun toMap(): Map<String, Any> = config.toMap()
    
    fun reset() {
        config.clear()
        // Set defaults
        host = "localhost"
        port = 8080
        ssl = false
        timeout = 5000L
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>): Configuration {
            return Configuration(map.toMutableMap())
        }
        
        fun default(): Configuration {
            return Configuration(mutableMapOf(
                "host" to "localhost",
                "port" to 8080,
                "ssl" to false,
                "timeout" to 5000L
            ))
        }
    }
}

// ================================
// Custom Delegates
// ================================

/**
 * Custom delegate for caching values with expiration
 */
class ExpiringCache<T>(
    private val expirationMillis: Long,
    private val valueProvider: () -> T
) : ReadOnlyProperty<Any?, T> {
    
    private var cachedValue: T? = null
    private var lastUpdated: Long = 0
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val currentTime = System.currentTimeMillis()
        
        if (cachedValue == null || currentTime - lastUpdated > expirationMillis) {
            println("Cache miss for ${property.name}, refreshing...")
            cachedValue = valueProvider()
            lastUpdated = currentTime
        } else {
            println("Cache hit for ${property.name}")
        }
        
        return cachedValue!!
    }
}

/**
 * Custom delegate for validated properties
 */
class Validated<T>(
    private var value: T,
    private val validator: (T) -> Boolean,
    private val errorMessage: String = "Invalid value"
) : ReadWriteProperty<Any?, T> {
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (validator(value)) {
            this.value = value
        } else {
            throw IllegalArgumentException("$errorMessage: $value")
        }
    }
}

/**
 * Custom delegate for thread-safe properties
 */
class ThreadSafe<T>(initialValue: T) : ReadWriteProperty<Any?, T> {
    private var value: T = initialValue
    private val lock = Any()
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        synchronized(lock) {
            return value
        }
    }
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(lock) {
            this.value = value
        }
    }
}

/**
 * Custom delegate for logging property access
 */
class LoggedProperty<T>(
    private var value: T,
    private val logger: (String) -> Unit = ::println
) : ReadWriteProperty<Any?, T> {
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        logger("Reading property ${property.name}: $value")
        return value
    }
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        logger("Setting property ${property.name} from ${this.value} to $value")
        this.value = value
    }
}

// ================================
// Advanced Delegation Patterns
// ================================

/**
 * Delegation with transformation
 */
class TransformingDelegate<T, R>(
    private var backingValue: T,
    private val transform: (T) -> R,
    private val reverseTransform: (R) -> T
) : ReadWriteProperty<Any?, R> {
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): R {
        return transform(backingValue)
    }
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
        backingValue = reverseTransform(value)
    }
}

/**
 * Delegate with history tracking
 */
class HistoryTracking<T>(initialValue: T) : ReadWriteProperty<Any?, T> {
    private var currentValue: T = initialValue
    private val history = mutableListOf<Pair<T, Long>>()
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = currentValue
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        history.add(currentValue to System.currentTimeMillis())
        currentValue = value
    }
    
    fun getHistory(): List<Pair<T, Long>> = history.toList()
    
    fun rollback(): Boolean {
        return if (history.isNotEmpty()) {
            val (previousValue, _) = history.removeLastOrNull() ?: return false
            currentValue = previousValue
            true
        } else {
            false
        }
    }
}

/**
 * Conditional delegate (only updates if condition is met)
 */
class ConditionalDelegate<T>(
    private var value: T,
    private val condition: (oldValue: T, newValue: T) -> Boolean
) : ReadWriteProperty<Any?, T> {
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (condition(this.value, value)) {
            this.value = value
        }
    }
}

// ================================
// Usage Examples
// ================================

/**
 * Class demonstrating custom delegates
 */
class SmartConfiguration {
    // Expiring cache - refreshes every 5 seconds
    val currentTime: String by ExpiringCache(5000) {
        "Current time: ${java.time.LocalDateTime.now()}"
    }
    
    // Validated property - must be positive
    var positiveNumber: Int by Validated(1, { it > 0 }, "Number must be positive")
    
    // Thread-safe property
    var sharedCounter: Int by ThreadSafe(0)
    
    // Logged property
    var importantValue: String by LoggedProperty("initial")
    
    // Transforming delegate - stores as uppercase, returns as lowercase
    var transformedText: String by TransformingDelegate(
        backingValue = "",
        transform = { it.lowercase() },
        reverseTransform = { it.uppercase() }
    )
    
    // History tracking
    var trackedValue: String by HistoryTracking("initial")
    
    // Conditional updates - only allows longer strings
    var growingString: String by ConditionalDelegate("") { old, new ->
        new.length > old.length
    }
    
    fun getTrackedHistory(): List<Pair<String, Long>> {
        val delegate = this::trackedValue.getDelegate()
        return if (delegate is HistoryTracking<*>) {
            @Suppress("UNCHECKED_CAST")
            (delegate as HistoryTracking<String>).getHistory()
        } else {
            emptyList()
        }
    }
    
    fun rollbackTrackedValue(): Boolean {
        val delegate = this::trackedValue.getDelegate()
        return if (delegate is HistoryTracking<*>) {
            @Suppress("UNCHECKED_CAST")
            (delegate as HistoryTracking<String>).rollback()
        } else {
            false
        }
    }
}

/**
 * Repository pattern with delegation
 */
interface Repository<T, ID> {
    suspend fun save(entity: T): T
    suspend fun findById(id: ID): T?
    suspend fun findAll(): List<T>
    suspend fun delete(id: ID): Boolean
}

class CachingRepository<T, ID>(
    private val delegate: Repository<T, ID>
) : Repository<T, ID> by delegate {
    
    private val cache = mutableMapOf<ID, T>()
    
    override suspend fun findById(id: ID): T? {
        return cache[id] ?: delegate.findById(id)?.also { cache[id] = it }
    }
    
    override suspend fun save(entity: T): T {
        val saved = delegate.save(entity)
        // Assume entity has an id property for caching
        cache[getEntityId(saved)] = saved
        return saved
    }
    
    override suspend fun delete(id: ID): Boolean {
        val deleted = delegate.delete(id)
        if (deleted) {
            cache.remove(id)
        }
        return deleted
    }
    
    private fun getEntityId(entity: T): ID {
        // In a real implementation, you'd extract the ID from the entity
        @Suppress("UNCHECKED_CAST")
        return (entity as Any).hashCode() as ID
    }
    
    fun clearCache() {
        cache.clear()
    }
    
    fun getCacheSize(): Int = cache.size
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateClassDelegation() {
    println("=== Class Delegation Demo ===")
    
    val processor = EnhancedDataProcessor()
    
    val input = "Hello, World! @#$%"
    val processed = processor.process(input)
    val isValid = processor.validate(processed)
    
    println("Input: $input")
    println("Processed: $processed")
    println("Valid: $isValid")
    println("Enhancement count: ${processor.getEnhancementCount()}")
    println("Stats: ${processor.getStats()}")
    
    println("\nMultiple Interface Delegation:")
    val service = ValidationService()
    val result = service.processWithLogging("Hello World 123")
    println("Result: $result")
}

fun demonstratePropertyDelegation() {
    println("\n=== Property Delegation Demo ===")
    
    val prefs = UserPreferences()
    
    // Lazy evaluation
    println("First access to expensiveValue:")
    println(prefs.expensiveValue)
    println("Second access to expensiveValue:")
    println(prefs.expensiveValue)
    
    // Observable property
    prefs.userName = "Alice"
    prefs.userName = "Bob"
    
    // Vetoable property
    prefs.userAge = 25  // Valid
    prefs.userAge = -5  // Invalid, won't change
    prefs.userAge = 200 // Invalid, won't change
    println("Final age: ${prefs.userAge}")
    
    // NotNull delegate
    prefs.requiredSetting = "important value"
    println("Required setting: ${prefs.requiredSetting}")
}

fun demonstrateMapDelegation() {
    println("\n=== Map Delegation Demo ===")
    
    val config = Configuration.default()
    println("Default config: ${config.toMap()}")
    
    config.host = "example.com"
    config.port = 443
    config.ssl = true
    println("Modified config: ${config.toMap()}")
    
    val fromMap = Configuration.fromMap(mapOf(
        "host" to "api.service.com",
        "port" to 9090,
        "ssl" to false,
        "timeout" to 10000L
    ))
    println("From map: ${fromMap.toMap()}")
}

fun demonstrateCustomDelegates() {
    println("\n=== Custom Delegates Demo ===")
    
    val smartConfig = SmartConfiguration()
    
    // Expiring cache
    println("First time access:")
    println(smartConfig.currentTime)
    println("Second time access (cached):")
    println(smartConfig.currentTime)
    
    // Wait and access again to see cache expiration
    Thread.sleep(1000)
    println("After wait:")
    println(smartConfig.currentTime)
    
    // Validated property
    try {
        smartConfig.positiveNumber = 10
        println("Positive number set to: ${smartConfig.positiveNumber}")
        smartConfig.positiveNumber = -5 // Should throw exception
    } catch (e: IllegalArgumentException) {
        println("Validation error: ${e.message}")
    }
    
    // Logged property
    smartConfig.importantValue = "new value"
    val _ = smartConfig.importantValue // Trigger read log
    
    // Transforming delegate
    smartConfig.transformedText = "Hello World"
    println("Transformed text (stored as uppercase, returned as lowercase): ${smartConfig.transformedText}")
    
    // History tracking
    smartConfig.trackedValue = "first"
    smartConfig.trackedValue = "second" 
    smartConfig.trackedValue = "third"
    println("Current tracked value: ${smartConfig.trackedValue}")
    println("History: ${smartConfig.getTrackedHistory()}")
    
    smartConfig.rollbackTrackedValue()
    println("After rollback: ${smartConfig.trackedValue}")
    
    // Conditional delegate
    smartConfig.growingString = "abc"
    println("Growing string: ${smartConfig.growingString}")
    
    smartConfig.growingString = "ab" // Won't change (shorter)
    println("After attempting shorter: ${smartConfig.growingString}")
    
    smartConfig.growingString = "abcdef" // Will change (longer)
    println("After setting longer: ${smartConfig.growingString}")
}

fun main() {
    demonstrateClassDelegation()
    demonstratePropertyDelegation()
    demonstrateMapDelegation()
    demonstrateCustomDelegates()
    
    println("\n=== Delegation Best Practices ===")
    println("✓ Use class delegation to compose behavior")
    println("✓ Leverage built-in property delegates (lazy, observable, etc.)")
    println("✓ Create custom delegates for reusable property behavior")
    println("✓ Prefer delegation over inheritance when appropriate")
    println("✓ Use map delegation for flexible configuration objects")
    println("✓ Consider thread safety with custom delegates")
    println("✓ Document custom delegate behavior clearly")
}

/**
 * TODO: Advanced Delegation Exercises
 * 
 * 1. Create a delegate that persists property values to disk
 * 2. Implement a delegate with automatic serialization/deserialization
 * 3. Build a delegate that notifies listeners of property changes
 * 4. Create a delegate with built-in validation and error reporting
 * 5. Implement a delegate with automatic retry logic for network operations
 * 6. Build a caching delegate with TTL and size limits
 * 7. Create a delegate that maintains property change audit logs
 * 8. Implement a delegate with automatic type conversion
 * 9. Build a delegate that handles property migrations/upgrades
 * 10. Create a delegate with conditional read/write access control
 */