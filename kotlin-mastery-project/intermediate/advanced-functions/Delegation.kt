/**
 * Delegation in Kotlin
 * 
 * This module covers Kotlin's delegation features, including:
 * - Class delegation (by keyword)
 * - Property delegation (by keyword for properties)
 * - Built-in delegates (lazy, observable, vetoable, notNull)
 * - Custom delegates
 * - Delegated properties patterns
 * - Real-world applications and best practices
 */

// ================================
// Class Delegation
// ================================

/**
 * Interface for demonstrating class delegation
 */
interface Printer {
    fun print(message: String)
    fun printWithTimestamp(message: String)
}

/**
 * Concrete implementation of Printer
 */
class ConsolePrinter : Printer {
    override fun print(message: String) {
        println(message)
    }
    
    override fun printWithTimestamp(message: String) {
        println("[${System.currentTimeMillis()}] $message")
    }
}

/**
 * Class that delegates to Printer implementation
 * Uses 'by' keyword to delegate all Printer methods to the provided instance
 */
class Logger(printer: Printer) : Printer by printer {
    private var logCount = 0
    
    // Can still override delegated methods if needed
    override fun print(message: String) {
        logCount++
        printer.print("[$logCount] $message")
    }
    
    fun getLogCount(): Int = logCount
}

/**
 * Multiple interface delegation
 */
interface Readable {
    fun read(): String
}

interface Writable {
    fun write(data: String)
}

class FileHandler : Readable, Writable {
    private val content = mutableListOf<String>()
    
    override fun read(): String = content.joinToString("\n")
    
    override fun write(data: String) {
        content.add(data)
    }
}

/**
 * Class delegating to multiple interfaces
 */
class DocumentProcessor(
    private val reader: Readable,
    private val writer: Writable
) : Readable by reader, Writable by writer {
    
    fun process() {
        val data = read()
        val processed = data.uppercase()
        write("PROCESSED: $processed")
    }
}

// ================================
// Property Delegation
// ================================

/**
 * Built-in Delegates: lazy
 * Lazy initialization - computed only on first access
 */
class ExpensiveResource {
    // Thread-safe lazy initialization
    val expensiveProperty: String by lazy {
        println("Computing expensive property...")
        Thread.sleep(1000) // Simulate expensive computation
        "Expensive result computed at ${System.currentTimeMillis()}"
    }
    
    // Lazy with explicit mode
    val threadSafeProperty: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        "Thread-safe lazy property"
    }
    
    // Non-thread-safe lazy (faster but only safe for single-threaded use)
    val nonThreadSafeProperty: String by lazy(LazyThreadSafetyMode.NONE) {
        "Non-thread-safe lazy property"
    }
}

/**
 * Built-in Delegates: observable
 * Observes property changes
 */
class UserProfile {
    var name: String by Delegates.observable("Unknown") { property, oldValue, newValue ->
        println("Property ${property.name} changed from '$oldValue' to '$newValue'")
    }
    
    var email: String by Delegates.observable("") { _, old, new ->
        if (old.isNotEmpty()) {
            println("Email updated from $old to $new")
            // Could trigger email verification here
        }
    }
}

/**
 * Built-in Delegates: vetoable
 * Can veto (reject) property changes based on conditions
 */
class BankAccount {
    var balance: Double by Delegates.vetoable(0.0) { _, oldValue, newValue ->
        // Veto negative balances
        val allowed = newValue >= 0
        if (!allowed) {
            println("Transaction rejected: Cannot have negative balance")
        }
        allowed
    }
    
    fun withdraw(amount: Double): Boolean {
        val newBalance = balance - amount
        balance = newBalance // This will be vetoed if negative
        return balance == newBalance
    }
    
    fun deposit(amount: Double) {
        balance += amount
    }
}

/**
 * Built-in Delegates: notNull
 * For non-null properties that are initialized later
 */
class Configuration {
    var databaseUrl: String by Delegates.notNull()
    var apiKey: String by Delegates.notNull()
    
    fun initialize() {
        databaseUrl = "jdbc:postgresql://localhost:5432/mydb"
        apiKey = "sk-1234567890abcdef"
    }
}

// ================================
// Custom Delegates
// ================================

/**
 * Custom delegate that implements ReadOnlyProperty interface
 */
class PreferenceDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val preferences: MutableMap<String, Any> = mutableMapOf()
) : ReadOnlyProperty<Any?, T> {
    
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return preferences[key] as? T ?: defaultValue
    }
}

/**
 * Custom delegate that implements ReadWriteProperty interface
 */
class PreferenceMutableDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val preferences: MutableMap<String, Any> = mutableMapOf()
) : ReadWriteProperty<Any?, T> {
    
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return preferences[key] as? T ?: defaultValue
    }
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value != null) {
            preferences[key] = value as Any
        } else {
            preferences.remove(key)
        }
    }
}

/**
 * Custom delegate for validation
 */
class ValidatedDelegate<T>(
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
 * Custom delegate for caching expensive computations
 */
class CachedDelegate<T>(
    private val ttlMillis: Long,
    private val computation: () -> T
) : ReadOnlyProperty<Any?, T> {
    
    private var cachedValue: T? = null
    private var lastComputed: Long = 0
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val now = System.currentTimeMillis()
        
        if (cachedValue == null || (now - lastComputed) > ttlMillis) {
            cachedValue = computation()
            lastComputed = now
        }
        
        return cachedValue!!
    }
}

/**
 * Custom delegate for thread-local storage
 */
class ThreadLocalDelegate<T>(private val initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private val threadLocal = ThreadLocal.withInitial(initializer)
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return threadLocal.get()
    }
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        threadLocal.set(value)
    }
}

// ================================
// Real-World Applications
// ================================

/**
 * Settings class using various delegates
 */
class AppSettings {
    // Using lazy for expensive initialization
    val theme: String by lazy {
        loadThemeFromFile() // Expensive operation
    }
    
    // Using observable to track changes
    var language: String by Delegates.observable("en") { _, oldValue, newValue ->
        if (oldValue != newValue) {
            println("Language changed from $oldValue to $newValue")
            notifyLanguageChange(newValue)
        }
    }
    
    // Using vetoable for validation
    var fontSize: Int by Delegates.vetoable(12) { _, _, newValue ->
        newValue in 8..72 // Only allow reasonable font sizes
    }
    
    // Using custom validation delegate
    var username: String by ValidatedDelegate("user") { value ->
        value.length >= 3 && value.all { it.isLetterOrDigit() || it == '_' }
    }
    
    // Using custom preference delegate
    private val prefs = mutableMapOf<String, Any>()
    val serverUrl: String by PreferenceMutableDelegate("server_url", "http://localhost:8080", prefs)
    
    private fun loadThemeFromFile(): String {
        // Simulate file reading
        Thread.sleep(100)
        return "dark"
    }
    
    private fun notifyLanguageChange(newLanguage: String) {
        println("Notifying components about language change to: $newLanguage")
    }
}

/**
 * Database connection using delegates
 */
class DatabaseConnection {
    // Lazy connection establishment
    private val connection: String by lazy {
        println("Establishing database connection...")
        "Connection established at ${System.currentTimeMillis()}"
    }
    
    // Connection pool size with validation
    var poolSize: Int by ValidatedDelegate(10) { it in 1..100 }
    
    // Cached expensive query result
    val schemaVersion: String by CachedDelegate(30000) { // 30 second cache
        println("Fetching schema version from database...")
        Thread.sleep(500) // Simulate database query
        "v2.1.4"
    }
    
    fun connect(): String = connection
    
    fun getSchema(): String = schemaVersion
}

/**
 * HTTP client configuration using delegates
 */
class HttpClientConfig {
    var timeout: Int by Delegates.vetoable(30) { _, _, newValue ->
        newValue > 0 // Timeout must be positive
    }
    
    var maxRetries: Int by ValidatedDelegate(3) { it in 0..10 }
    
    val userAgent: String by lazy { "MyApp/1.0 (Kotlin)" }
    
    // Thread-local request ID
    var requestId: String by ThreadLocalDelegate { generateRequestId() }
    
    private fun generateRequestId(): String {
        return "req_${System.currentTimeMillis()}_${Thread.currentThread().id}"
    }
}

/**
 * Map-based property delegation
 */
class JsonObject(private val map: MutableMap<String, Any?>) {
    var name: String by map
    var age: Int by map
    var email: String? by map
    
    constructor() : this(mutableMapOf())
    
    fun toMap(): Map<String, Any?> = map.toMap()
}

/**
 * Delegate provider pattern
 */
class LoggingDelegate<T>(private val initialValue: T) : ReadWriteProperty<Any?, T> {
    private var value = initialValue
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("Getting ${property.name}: $value")
        return value
    }
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("Setting ${property.name}: ${this.value} -> $value")
        this.value = value
    }
}

fun <T> logged(initialValue: T) = LoggingDelegate(initialValue)

/**
 * Class using delegate provider
 */
class TrackedObject {
    var property1: String by logged("initial1")
    var property2: Int by logged(42)
    var property3: Boolean by logged(false)
}

// ================================
// Advanced Delegation Patterns
// ================================

/**
 * Composite delegate that combines multiple behaviors
 */
class CompositeDelegate<T>(
    private var value: T,
    private val validators: List<(T) -> Boolean> = emptyList(),
    private val observers: List<(T, T) -> Unit> = emptyList()
) : ReadWriteProperty<Any?, T> {
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        // Validate with all validators
        if (validators.all { it(value) }) {
            val oldValue = this.value
            this.value = value
            
            // Notify all observers
            observers.forEach { it(oldValue, value) }
        } else {
            throw IllegalArgumentException("Validation failed for value: $value")
        }
    }
}

/**
 * Delegate factory for common patterns
 */
object Delegates {
    fun <T> cached(ttlMillis: Long, computation: () -> T) = 
        CachedDelegate(ttlMillis, computation)
    
    fun <T> validated(initialValue: T, validator: (T) -> Boolean, errorMessage: String = "Invalid value") =
        ValidatedDelegate(initialValue, validator, errorMessage)
    
    fun <T> threadLocal(initializer: () -> T) = 
        ThreadLocalDelegate(initializer)
    
    fun <T> composite(
        initialValue: T,
        validators: List<(T) -> Boolean> = emptyList(),
        observers: List<(T, T) -> Unit> = emptyList()
    ) = CompositeDelegate(initialValue, validators, observers)
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateClassDelegation() {
    println("=== Class Delegation ===")
    
    val printer = ConsolePrinter()
    val logger = Logger(printer)
    
    logger.print("Hello, World!")
    logger.printWithTimestamp("Important message")
    println("Log count: ${logger.getLogCount()}")
    
    // Multiple delegation
    val fileHandler = FileHandler()
    val processor = DocumentProcessor(fileHandler, fileHandler)
    
    processor.write("Hello, Kotlin!")
    processor.process()
    println("Final content: ${processor.read()}")
}

fun demonstrateBuiltInDelegates() {
    println("\n=== Built-in Delegates ===")
    
    // Lazy delegate
    val resource = ExpensiveResource()
    println("Resource created")
    println("Accessing expensive property...")
    println(resource.expensiveProperty)
    println("Accessing again (should be cached)...")
    println(resource.expensiveProperty)
    
    // Observable delegate
    val profile = UserProfile()
    profile.name = "Alice"
    profile.email = "alice@example.com"
    profile.email = "alice.smith@example.com"
    
    // Vetoable delegate
    val account = BankAccount()
    account.deposit(100.0)
    println("Balance: ${account.balance}")
    
    account.withdraw(50.0)
    println("Balance after withdrawal: ${account.balance}")
    
    account.withdraw(100.0) // Should be rejected
    println("Balance after rejected withdrawal: ${account.balance}")
}

fun demonstrateCustomDelegates() {
    println("\n=== Custom Delegates ===")
    
    // App settings with various delegates
    val settings = AppSettings()
    
    println("Theme: ${settings.theme}")
    settings.language = "es"
    
    settings.fontSize = 14
    println("Font size: ${settings.fontSize}")
    
    try {
        settings.fontSize = 100 // Should be rejected
    } catch (e: Exception) {
        println("Font size change rejected")
    }
    
    try {
        settings.username = "alice_123"
        println("Username set to: ${settings.username}")
        
        settings.username = "ab" // Should be rejected
    } catch (e: IllegalArgumentException) {
        println("Username validation failed: ${e.message}")
    }
    
    // Database connection
    val db = DatabaseConnection()
    println("Database: ${db.connect()}")
    println("Schema version: ${db.getSchema()}")
    println("Schema version (cached): ${db.getSchema()}")
}

fun demonstrateAdvancedPatterns() {
    println("\n=== Advanced Patterns ===")
    
    // JSON object with map delegation
    val json = JsonObject().apply {
        name = "John Doe"
        age = 30
        email = "john@example.com"
    }
    
    println("JSON object: ${json.toMap()}")
    
    // Tracked object with logging delegate
    val tracked = TrackedObject()
    tracked.property1 = "new value"
    tracked.property2 = 100
    tracked.property3 = true
    
    // Composite delegate
    val validator1: (Int) -> Boolean = { it > 0 }
    val validator2: (Int) -> Boolean = { it < 1000 }
    val observer: (Int, Int) -> Unit = { old, new -> 
        println("Value changed from $old to $new") 
    }
    
    class TestClass {
        var value: Int by Delegates.composite(
            initialValue = 10,
            validators = listOf(validator1, validator2),
            observers = listOf(observer)
        )
    }
    
    val test = TestClass()
    test.value = 50
    
    try {
        test.value = -5 // Should fail validation
    } catch (e: IllegalArgumentException) {
        println("Composite validation failed: ${e.message}")
    }
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a delegate that automatically persists property values to a file
 * 2. Implement a delegate that maintains a history of property changes
 * 3. Create a delegate that encrypts/decrypts property values
 * 4. Implement a delegate for property synchronization across threads
 * 5. Create a delegate that implements rate limiting for property changes
 * 6. Implement a delegate that validates property values against a schema
 * 7. Create a delegate that automatically converts between different units
 * 8. Implement a delegate for property change rollback functionality
 * 9. Create a delegate that integrates with external configuration systems
 * 10. Implement a delegate for property binding between different objects
 */

fun main() {
    demonstrateClassDelegation()
    demonstrateBuiltInDelegates()
    demonstrateCustomDelegates()
    demonstrateAdvancedPatterns()
    
    println("\n=== Delegation Summary ===")
    println("✓ Class delegation with 'by' keyword for composition over inheritance")
    println("✓ Property delegation for lazy initialization, observation, and validation")
    println("✓ Built-in delegates: lazy, observable, vetoable, notNull")
    println("✓ Custom delegates implementing ReadOnlyProperty/ReadWriteProperty")
    println("✓ Advanced patterns: composite delegates, caching, thread-local storage")
    println("✓ Real-world applications: settings, database connections, validation")
}