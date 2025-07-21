/**
 * Intermediate Level Exercises - Advanced Kotlin Concepts
 * 
 * This module contains hands-on exercises for practicing intermediate Kotlin concepts:
 * - Higher-order functions and lambdas
 * - Collections with functional operations
 * - Sequences and lazy evaluation
 * - Generics and type constraints
 * - Extension functions and properties
 * - Data classes and destructuring
 * - Sealed classes and pattern matching
 * - Scope functions
 * - Delegation patterns
 * 
 * Instructions:
 * 1. Each exercise focuses on specific intermediate concepts
 * 2. Implement the required functionality
 * 3. Pay attention to functional programming patterns
 * 4. Test your solutions with the provided test cases
 */

// ================================
// Higher-Order Functions Exercises
// ================================

/**
 * Exercise 1: Function Composition
 * 
 * Task: Implement function composition utilities and use them to create data processing pipelines
 */

// Helper functions for composition
infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C = { a -> this(f(a)) }
infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C = { a -> g(this(a)) }

fun exercise1_FunctionComposition(): Map<String, Any> {
    // TODO: Create a data processing pipeline using function composition
    // Steps: trim string -> convert to lowercase -> split into words -> count words -> multiply by 2
    
    val trimString: (String) -> String = { it.trim() }
    val toLowerCase: (String) -> String = { it.lowercase() }
    val splitToWords: (String) -> List<String> = { it.split("\\s+".toRegex()) }
    val countWords: (List<String>) -> Int = { it.size }
    val multiplyByTwo: (Int) -> Int = { it * 2 }
    
    // Compose the pipeline
    val pipeline = trimString andThen toLowerCase andThen splitToWords andThen countWords andThen multiplyByTwo
    
    val testString = "  Hello World Kotlin Programming  "
    val result = pipeline(testString)
    
    return mapOf(
        "original" to testString,
        "result" to result,
        "pipeline" to "trim -> lowercase -> split -> count -> multiply"
    )
}

/**
 * Exercise 2: Custom Filter and Transform Functions
 * 
 * Task: Create reusable higher-order functions for data processing
 */
fun <T> List<T>.filterAndTransform(
    predicate: (T) -> Boolean,
    transform: (T) -> T
): List<T> {
    // TODO: Implement a function that filters and transforms in one pass
    return this.filter(predicate).map(transform)
}

fun <T, R> List<T>.mapNotNull(transform: (T) -> R?): List<R> {
    // TODO: Implement mapNotNull - transform and filter out nulls
    return this.map(transform).filterNotNull()
}

fun exercise2_HigherOrderFunctions(): Map<String, Any> {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    // Filter even numbers and square them
    val evenSquares = numbers.filterAndTransform(
        predicate = { it % 2 == 0 },
        transform = { it * it }
    )
    
    // Convert numbers to strings, but only positive ones
    val positiveStrings = numbers.mapNotNull { num ->
        if (num > 0) "Number: $num" else null
    }
    
    return mapOf(
        "original" to numbers,
        "evenSquares" to evenSquares,
        "positiveStrings" to positiveStrings
    )
}

// ================================
// Advanced Collections Exercises
// ================================

/**
 * Exercise 3: Complex Data Processing
 * 
 * Task: Process a collection of sales data using functional operations
 */
data class SaleRecord(
    val id: String,
    val product: String,
    val category: String,
    val amount: Double,
    val date: String,
    val salesPerson: String
)

fun exercise3_DataProcessing(): Map<String, Any> {
    val salesData = listOf(
        SaleRecord("1", "Laptop", "Electronics", 999.99, "2023-01-15", "John"),
        SaleRecord("2", "Mouse", "Electronics", 29.99, "2023-01-16", "Alice"),
        SaleRecord("3", "Desk", "Furniture", 299.99, "2023-01-17", "Bob"),
        SaleRecord("4", "Phone", "Electronics", 699.99, "2023-01-18", "John"),
        SaleRecord("5", "Chair", "Furniture", 149.99, "2023-01-19", "Alice"),
        SaleRecord("6", "Tablet", "Electronics", 399.99, "2023-01-20", "Bob")
    )
    
    // TODO: Implement the following analyses:
    
    // 1. Total sales by category
    val salesByCategory = salesData
        .groupBy { it.category }
        .mapValues { (_, records) -> records.sumOf { it.amount } }
    
    // 2. Top performing sales person by total sales
    val topSalesPerson = salesData
        .groupBy { it.salesPerson }
        .mapValues { (_, records) -> records.sumOf { it.amount } }
        .maxByOrNull { it.value }
    
    // 3. Average sale amount by category
    val avgSaleByCategory = salesData
        .groupBy { it.category }
        .mapValues { (_, records) -> records.map { it.amount }.average() }
    
    // 4. Products sold for more than $300
    val expensiveProducts = salesData
        .filter { it.amount > 300 }
        .map { it.product }
    
    // 5. Sales count by sales person
    val salesCountByPerson = salesData
        .groupingBy { it.salesPerson }
        .eachCount()
    
    return mapOf(
        "totalSalesByCategory" to salesByCategory,
        "topSalesPerson" to topSalesPerson,
        "avgSaleByCategory" to avgSaleByCategory,
        "expensiveProducts" to expensiveProducts,
        "salesCountByPerson" to salesCountByPerson
    )
}

/**
 * Exercise 4: Sequence Operations for Large Datasets
 * 
 * Task: Use sequences for efficient processing of large datasets
 */
fun exercise4_SequenceOperations(): Map<String, Any> {
    // TODO: Create a sequence that generates numbers and processes them efficiently
    
    // Generate first 1000 even numbers, square them, filter those divisible by 8, take first 10
    val result = generateSequence(2) { it + 2 }
        .take(1000)
        .map { it * it }
        .filter { it % 8 == 0 }
        .take(10)
        .toList()
    
    // Create a sequence from text processing
    val text = "The quick brown fox jumps over the lazy dog. The dog was sleeping."
    val wordAnalysis = text.split(" ")
        .asSequence()
        .map { it.lowercase().replace("[^a-z]".toRegex(), "") }
        .filter { it.isNotEmpty() }
        .groupingBy { it }
        .eachCount()
    
    return mapOf(
        "processedNumbers" to result,
        "wordFrequency" to wordAnalysis,
        "uniqueWords" to wordAnalysis.size
    )
}

// ================================
// Generics Exercises
// ================================

/**
 * Exercise 5: Generic Data Structures
 * 
 * Task: Implement a generic stack with type constraints
 */
class Stack<T> {
    private val items = mutableListOf<T>()
    
    fun push(item: T) {
        // TODO: Add item to the stack
        items.add(item)
    }
    
    fun pop(): T? {
        // TODO: Remove and return the top item, or null if empty
        return if (items.isNotEmpty()) items.removeAt(items.size - 1) else null
    }
    
    fun peek(): T? {
        // TODO: Return the top item without removing it
        return items.lastOrNull()
    }
    
    fun isEmpty(): Boolean = items.isEmpty()
    
    fun size(): Int = items.size
    
    override fun toString(): String = "Stack(${items.reversed()})"
}

/**
 * Generic function with type constraints
 */
fun <T : Comparable<T>> findMinMax(items: List<T>): Pair<T?, T?> {
    // TODO: Find minimum and maximum values in the list
    if (items.isEmpty()) return null to null
    
    var min = items[0]
    var max = items[0]
    
    for (item in items) {
        if (item < min) min = item
        if (item > max) max = item
    }
    
    return min to max
}

fun exercise5_Generics(): Map<String, Any> {
    // Test generic stack
    val stringStack = Stack<String>()
    stringStack.push("first")
    stringStack.push("second")
    stringStack.push("third")
    
    val poppedItems = mutableListOf<String>()
    while (!stringStack.isEmpty()) {
        stringStack.pop()?.let { poppedItems.add(it) }
    }
    
    // Test generic function
    val numbers = listOf(5, 2, 8, 1, 9, 3)
    val (min, max) = findMinMax(numbers)
    
    val strings = listOf("zebra", "apple", "banana", "cherry")
    val (minStr, maxStr) = findMinMax(strings)
    
    return mapOf(
        "stackOperations" to poppedItems,
        "numberMinMax" to (min to max),
        "stringMinMax" to (minStr to maxStr)
    )
}

// ================================
// Extension Functions Exercises
// ================================

/**
 * Exercise 6: Useful Extension Functions
 * 
 * Task: Create practical extension functions for common operations
 */

// String extensions
fun String.isPalindrome(): Boolean {
    // TODO: Check if string is a palindrome
    val cleaned = this.lowercase().replace(Regex("[^a-z0-9]"), "")
    return cleaned == cleaned.reversed()
}

fun String.wordCount(): Int {
    // TODO: Count words in string
    return this.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
}

fun String.capitalizeWords(): String {
    // TODO: Capitalize first letter of each word
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

// Collection extensions
fun <T> List<T>.second(): T? {
    // TODO: Get second element safely
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.penultimate(): T? {
    // TODO: Get second-to-last element
    return if (this.size >= 2) this[this.size - 2] else null
}

fun <T> List<T>.chunked(size: Int): List<List<T>> {
    // TODO: Split list into chunks of specified size
    return this.windowed(size, size, partialWindows = true) { it.toList() }
}

fun exercise6_ExtensionFunctions(): Map<String, Any> {
    val testString = "A man a plan a canal Panama"
    val testList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    return mapOf(
        "isPalindrome" to testString.isPalindrome(),
        "wordCount" to testString.wordCount(),
        "capitalized" to testString.capitalizeWords(),
        "secondElement" to testList.second(),
        "penultimate" to testList.penultimate(),
        "chunked" to testList.chunked(3)
    )
}

// ================================
// Data Classes and Destructuring
// ================================

/**
 * Exercise 7: Advanced Data Class Usage
 * 
 * Task: Work with data classes, destructuring, and component functions
 */
data class Employee(
    val id: String,
    val name: String,
    val department: String,
    val salary: Double,
    val yearsOfService: Int
) {
    // TODO: Add computed properties
    val annualBonus: Double
        get() = salary * (yearsOfService * 0.01)
    
    val isEligibleForPromotion: Boolean
        get() = yearsOfService >= 2 && salary < 100000
    
    // TODO: Add custom component functions for destructuring beyond the first 5
    operator fun component6(): Double = annualBonus
    operator fun component7(): Boolean = isEligibleForPromotion
}

fun exercise7_DataClassesDestructuring(): Map<String, Any> {
    val employees = listOf(
        Employee("1", "John Doe", "Engineering", 75000.0, 3),
        Employee("2", "Jane Smith", "Marketing", 65000.0, 5),
        Employee("3", "Bob Johnson", "Engineering", 95000.0, 1),
        Employee("4", "Alice Brown", "Sales", 70000.0, 4)
    )
    
    // TODO: Use destructuring to process employees
    val engineeringData = employees
        .filter { it.department == "Engineering" }
        .map { employee ->
            val (id, name, _, salary, years, bonus, eligible) = employee
            mapOf(
                "id" to id,
                "name" to name,
                "salary" to salary,
                "years" to years,
                "bonus" to bonus,
                "promotionEligible" to eligible
            )
        }
    
    // Department salary analysis
    val departmentAnalysis = employees
        .groupBy { it.department }
        .mapValues { (_, empList) ->
            mapOf(
                "count" to empList.size,
                "avgSalary" to empList.map { it.salary }.average(),
                "totalBonus" to empList.sumOf { it.annualBonus },
                "eligibleForPromotion" to empList.count { it.isEligibleForPromotion }
            )
        }
    
    return mapOf(
        "engineeringEmployees" to engineeringData,
        "departmentAnalysis" to departmentAnalysis
    )
}

// ================================
// Sealed Classes Exercises
// ================================

/**
 * Exercise 8: State Management with Sealed Classes
 * 
 * Task: Model application states using sealed classes
 */
sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<out T>(val data: T) : LoadingState<T>()
    data class Error(val exception: Throwable) : LoadingState<Nothing>()
    
    // TODO: Add utility methods
    fun isLoading(): Boolean = this is Loading
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun <R> map(transform: (T) -> R): LoadingState<R> = when (this) {
        is Loading -> Loading
        is Success -> Success(transform(data))
        is Error -> Error(exception)
    }
}

sealed class NetworkResult {
    data class Success(val data: String) : NetworkResult()
    data class HttpError(val code: Int, val message: String) : NetworkResult()
    object NetworkError : NetworkResult()
    object TimeoutError : NetworkResult()
}

fun exercise8_SealedClasses(): Map<String, Any> {
    // TODO: Simulate different loading states
    val states = listOf<LoadingState<String>>(
        LoadingState.Loading,
        LoadingState.Success("Data loaded successfully"),
        LoadingState.Error(RuntimeException("Network error"))
    )
    
    val stateResults = states.map { state ->
        when (state) {
            is LoadingState.Loading -> "Currently loading..."
            is LoadingState.Success -> "Success: ${state.data}"
            is LoadingState.Error -> "Error: ${state.exception.message}"
        }
    }
    
    // Network result processing
    val networkResults = listOf(
        NetworkResult.Success("User data"),
        NetworkResult.HttpError(404, "Not Found"),
        NetworkResult.NetworkError,
        NetworkResult.TimeoutError
    )
    
    val processedResults = networkResults.map { result ->
        when (result) {
            is NetworkResult.Success -> "Data: ${result.data}"
            is NetworkResult.HttpError -> "HTTP ${result.code}: ${result.message}"
            is NetworkResult.NetworkError -> "Network connection failed"
            is NetworkResult.TimeoutError -> "Request timed out"
        }
    }
    
    return mapOf(
        "loadingStates" to stateResults,
        "networkResults" to processedResults
    )
}

// ================================
// Scope Functions Exercises
// ================================

/**
 * Exercise 9: Scope Function Patterns
 * 
 * Task: Use scope functions appropriately for different scenarios
 */
data class UserProfile(
    var name: String = "",
    var email: String = "",
    var age: Int = 0,
    var preferences: MutableMap<String, String> = mutableMapOf()
)

fun exercise9_ScopeFunctions(): Map<String, Any> {
    // TODO: Use different scope functions appropriately
    
    // Use 'apply' to configure an object
    val user1 = UserProfile().apply {
        name = "John Doe"
        email = "john@example.com"
        age = 30
        preferences["theme"] = "dark"
        preferences["language"] = "en"
    }
    
    // Use 'let' for null-safe operations and transformations
    val email: String? = "alice@example.com"
    val emailValidation = email?.let { emailStr ->
        when {
            emailStr.contains("@") && emailStr.contains(".") -> "Valid email"
            else -> "Invalid email"
        }
    } ?: "Email is null"
    
    // Use 'run' for scoped computations
    val userSummary = user1.run {
        "User: $name ($age years old)\nEmail: $email\nPreferences: ${preferences.size} items"
    }
    
    // Use 'with' to operate on an object without returning it
    val preferencesReport = with(user1.preferences) {
        put("notifications", "enabled")
        put("privacy", "high")
        "Preferences configured: ${keys.joinToString()}"
    }
    
    // Use 'also' for side effects
    val user2 = UserProfile().also { user ->
        user.name = "Jane Smith"
        user.email = "jane@example.com"
        println("Created user: ${user.name}") // Side effect
    }.also { user ->
        user.preferences["theme"] = "light"
        println("Configured preferences for: ${user.name}") // Another side effect
    }
    
    return mapOf(
        "configuredUser" to user1,
        "emailValidation" to emailValidation,
        "userSummary" to userSummary,
        "preferencesReport" to preferencesReport,
        "userWithSideEffects" to user2
    )
}

// ================================
// Delegation Exercises
// ================================

/**
 * Exercise 10: Property Delegation Patterns
 * 
 * Task: Implement and use various property delegation patterns
 */
class ObservableProperty<T>(initialValue: T, private val onChange: (T) -> Unit) {
    private var value: T = initialValue
    
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val oldValue = this.value
        this.value = value
        if (oldValue != value) {
            onChange(value)
        }
    }
}

class ValidatedProperty<T>(
    initialValue: T,
    private val validator: (T) -> Boolean
) {
    private var value: T = initialValue
    
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (validator(value)) {
            this.value = value
        } else {
            throw IllegalArgumentException("Invalid value: $value")
        }
    }
}

// Custom property delegates
fun <T> observable(initialValue: T, onChange: (T) -> Unit) = ObservableProperty(initialValue, onChange)
fun <T> validated(initialValue: T, validator: (T) -> Boolean) = ValidatedProperty(initialValue, validator)

class UserSettings {
    // TODO: Use different delegation patterns
    
    // Observable property
    var username: String by observable("") { newValue ->
        println("Username changed to: $newValue")
    }
    
    // Validated property
    var age: Int by validated(0) { it in 0..150 }
    
    // Lazy property
    val initializationTime: String by lazy {
        println("Computing initialization time...")
        java.time.LocalDateTime.now().toString()
    }
    
    // Map delegation
    private val preferences = mutableMapOf<String, Any>()
    var theme: String by preferences
    var language: String by preferences
    var fontSize: Int by preferences
}

fun exercise10_PropertyDelegation(): Map<String, Any> {
    val settings = UserSettings()
    val changeLog = mutableListOf<String>()
    
    // Test observable property
    settings.username = "john_doe"
    settings.username = "jane_doe"
    
    // Test validated property
    settings.age = 25
    try {
        settings.age = 200 // Should fail validation
    } catch (e: IllegalArgumentException) {
        changeLog.add("Age validation failed: ${e.message}")
    }
    
    // Test lazy property
    val initTime1 = settings.initializationTime
    val initTime2 = settings.initializationTime // Should use cached value
    
    // Test map delegation
    settings.theme = "dark"
    settings.language = "en"
    settings.fontSize = 14
    
    return mapOf(
        "currentSettings" to mapOf(
            "username" to settings.username,
            "age" to settings.age,
            "theme" to settings.theme,
            "language" to settings.language,
            "fontSize" to settings.fontSize
        ),
        "initTimes" to listOf(initTime1, initTime2),
        "sameInitTime" to (initTime1 == initTime2),
        "changeLog" to changeLog
    )
}

// ================================
// Test Runner
// ================================

fun runIntermediateExercises() {
    println("=".repeat(60))
    println("INTERMEDIATE EXERCISES - ADVANCED KOTLIN CONCEPTS")
    println("=".repeat(60))
    
    // Exercise 1: Function Composition
    println("\n1. Function Composition:")
    val comp = exercise1_FunctionComposition()
    comp.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 2: Higher-Order Functions
    println("\n2. Higher-Order Functions:")
    val hof = exercise2_HigherOrderFunctions()
    hof.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 3: Data Processing
    println("\n3. Complex Data Processing:")
    val dataProc = exercise3_DataProcessing()
    dataProc.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 4: Sequence Operations
    println("\n4. Sequence Operations:")
    val seqOps = exercise4_SequenceOperations()
    seqOps.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 5: Generics
    println("\n5. Generic Data Structures:")
    val generics = exercise5_Generics()
    generics.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 6: Extension Functions
    println("\n6. Extension Functions:")
    val extensions = exercise6_ExtensionFunctions()
    extensions.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 7: Data Classes
    println("\n7. Data Classes and Destructuring:")
    val dataClasses = exercise7_DataClassesDestructuring()
    dataClasses.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 8: Sealed Classes
    println("\n8. Sealed Classes:")
    val sealed = exercise8_SealedClasses()
    sealed.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 9: Scope Functions
    println("\n9. Scope Functions:")
    val scope = exercise9_ScopeFunctions()
    scope.forEach { (key, value) -> println("$key: $value") }
    
    // Exercise 10: Property Delegation
    println("\n10. Property Delegation:")
    val delegation = exercise10_PropertyDelegation()
    delegation.forEach { (key, value) -> println("$key: $value") }
    
    println("\n" + "=".repeat(60))
    println("INTERMEDIATE EXERCISES COMPLETED!")
    println("=".repeat(60))
}

fun main() {
    runIntermediateExercises()
}

/**
 * Additional Challenge Projects for Intermediate Level:
 * 
 * 1. JSON Parser: Build a simple JSON parser using functional programming
 * 2. Query Builder: Create a type-safe SQL query builder using generics
 * 3. State Machine: Implement a finite state machine using sealed classes
 * 4. Cache System: Build a generic caching system with different eviction policies
 * 5. Validation Framework: Create a composable validation framework
 * 6. Event System: Build a type-safe event bus with filtering capabilities
 * 7. Configuration DSL: Create a domain-specific language for configuration
 * 8. Data Pipeline: Build a streaming data processing pipeline
 * 9. Plugin System: Design a plugin architecture using interfaces and delegation
 * 10. Testing Framework: Create a simple testing framework using higher-order functions
 */