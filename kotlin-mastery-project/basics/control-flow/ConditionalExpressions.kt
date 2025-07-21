package com.kotlinmastery.basics.controlflow

/**
 * # Conditional Expressions in Kotlin
 * 
 * Kotlin treats conditionals as expressions that return values, making code more concise and functional.
 * This module covers if expressions, when expressions, and various conditional patterns.
 * 
 * ## Learning Objectives
 * - Understand if as both statement and expression
 * - Master when expressions and pattern matching
 * - Use range checks and smart casts in conditionals
 * - Apply conditional expressions in real-world scenarios
 * - Handle complex conditional logic elegantly
 * 
 * ## Prerequisites: Variables, types, and basic syntax
 * ## Estimated Time: 3 hours
 */

fun main() {
    println("=== Kotlin Conditional Expressions Demo ===\n")
    
    ifExpressions()
    whenExpressions()
    whenWithRanges()
    whenWithTypes()
    smartCasts()
    complexConditionals()
    realWorldExamples()
}

/**
 * ## If Expressions
 * 
 * In Kotlin, if is an expression that returns a value, not just a statement.
 * This makes code more concise and enables functional programming patterns.
 */
fun ifExpressions() {
    println("--- If Expressions ---")
    
    // Traditional if statement
    val age = 25
    if (age >= 18) {
        println("$age years old - You are an adult")
    } else {
        println("$age years old - You are a minor")
    }
    
    // If as expression - returns value
    val status = if (age >= 18) "Adult" else "Minor"
    println("Status: $status")
    
    // If expression with blocks
    val score = 85
    val grade = if (score >= 90) {
        println("Excellent performance!")
        "A"
    } else if (score >= 80) {
        println("Good performance!")
        "B"
    } else if (score >= 70) {
        println("Satisfactory performance!")
        "C"
    } else {
        println("Needs improvement!")
        "F"
    }
    println("Grade: $grade")
    
    // If expression in function return
    fun getMaxValue(a: Int, b: Int) = if (a > b) a else b
    fun getMinValue(a: Int, b: Int) = if (a < b) a else b
    
    val x = 10
    val y = 20
    println("Max of $x and $y: ${getMaxValue(x, y)}")
    println("Min of $x and $y: ${getMinValue(x, y)}")
    
    // If expression with nullable values
    val nullableNumber: Int? = 42
    val result = if (nullableNumber != null) {
        "Number is $nullableNumber"
    } else {
        "Number is null"
    }
    println("Nullable check result: $result")
    
    // Nested if expressions
    val temperature = 22
    val humidity = 65
    val comfort = if (temperature >= 20) {
        if (temperature <= 25) {
            if (humidity < 70) "Comfortable" else "Humid"
        } else {
            "Too hot"
        }
    } else {
        "Too cold"
    }
    println("Weather comfort: $comfort")
    
    // If expression with function calls
    fun isEven(number: Int) = number % 2 == 0
    fun isPositive(number: Int) = number > 0
    
    val number = 8
    val description = if (isPositive(number)) {
        if (isEven(number)) "Positive and even" else "Positive and odd"
    } else {
        if (isEven(number)) "Negative and even" else "Negative and odd"
    }
    println("Number $number is: $description")
    
    // If expression with collections
    val numbers = listOf(1, 2, 3, 4, 5)
    val summary = if (numbers.isNotEmpty()) {
        "List has ${numbers.size} elements, first: ${numbers.first()}, last: ${numbers.last()}"
    } else {
        "List is empty"
    }
    println("Collection summary: $summary")
    
    println()
}

/**
 * ## When Expressions
 * 
 * When expressions are Kotlin's replacement for switch statements, but much more powerful.
 * They can match values, ranges, types, and arbitrary conditions.
 */
fun whenExpressions() {
    println("--- When Expressions ---")
    
    // Basic when expression
    val dayOfWeek = 3
    val dayName = when (dayOfWeek) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "Invalid day"
    }
    println("Day $dayOfWeek is: $dayName")
    
    // When with multiple values
    val month = 6
    val season = when (month) {
        12, 1, 2 -> "Winter"
        3, 4, 5 -> "Spring"
        6, 7, 8 -> "Summer"
        9, 10, 11 -> "Fall"
        else -> "Invalid month"
    }
    println("Month $month is in: $season")
    
    // When with expressions and blocks
    val grade = 'B'
    val gpa = when (grade) {
        'A' -> {
            println("Excellent grade!")
            4.0
        }
        'B' -> {
            println("Good grade!")
            3.0
        }
        'C' -> {
            println("Average grade!")
            2.0
        }
        'D' -> {
            println("Below average grade!")
            1.0
        }
        'F' -> {
            println("Failing grade!")
            0.0
        }
        else -> {
            println("Invalid grade!")
            -1.0
        }
    }
    println("Grade $grade has GPA: $gpa")
    
    // When without argument (replaces if-else chains)
    val temperature = 30
    val humidity = 80
    val weatherAdvice = when {
        temperature > 35 -> "Stay indoors, it's too hot!"
        temperature < 0 -> "Wear warm clothes, it's freezing!"
        humidity > 70 && temperature > 25 -> "It's humid and warm, stay hydrated!"
        humidity < 30 -> "It's dry, use moisturizer!"
        temperature in 20..25 && humidity in 40..60 -> "Perfect weather!"
        else -> "Weather is okay"
    }
    println("Weather advice: $weatherAdvice")
    
    // When with function return
    fun getHttpStatusMessage(code: Int) = when (code) {
        200 -> "OK"
        201 -> "Created"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        500 -> "Internal Server Error"
        in 200..299 -> "Success"
        in 400..499 -> "Client Error"
        in 500..599 -> "Server Error"
        else -> "Unknown Status"
    }
    
    val statusCodes = listOf(200, 404, 500, 201, 999)
    statusCodes.forEach { code ->
        println("HTTP $code: ${getHttpStatusMessage(code)}")
    }
    
    // When with sealed classes (preview - covered later)
    enum class Priority { LOW, MEDIUM, HIGH, CRITICAL }
    
    val taskPriority = Priority.HIGH
    val urgencyLevel = when (taskPriority) {
        Priority.LOW -> "Take your time"
        Priority.MEDIUM -> "Normal timeline"
        Priority.HIGH -> "ASAP"
        Priority.CRITICAL -> "DROP EVERYTHING!"
    }
    println("Task priority $taskPriority: $urgencyLevel")
    
    println()
}

/**
 * ## When with Ranges and Collections
 * 
 * When expressions can match ranges, check membership in collections, and use complex conditions.
 */
fun whenWithRanges() {
    println("--- When with Ranges and Collections ---")
    
    // When with ranges
    val score = 87
    val letterGrade = when (score) {
        in 90..100 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        in 60..69 -> "D"
        in 0..59 -> "F"
        else -> "Invalid score"
    }
    println("Score $score gets grade: $letterGrade")
    
    // When with collection membership
    val favoriteColors = setOf("blue", "green", "purple")
    val color = "blue"
    val preference = when (color) {
        in favoriteColors -> "I love this color!"
        "red", "orange", "yellow" -> "Warm color, nice!"
        "black", "white", "gray" -> "Neutral color"
        else -> "Not sure about this color"
    }
    println("Color $color: $preference")
    
    // When with string patterns
    val fileName = "document.pdf"
    val fileType = when {
        fileName.endsWith(".pdf") -> "PDF Document"
        fileName.endsWith(".doc") || fileName.endsWith(".docx") -> "Word Document"
        fileName.endsWith(".txt") -> "Text File"
        fileName.endsWith(".jpg") || fileName.endsWith(".png") -> "Image File"
        fileName.contains("temp") -> "Temporary File"
        else -> "Unknown File Type"
    }
    println("File $fileName is: $fileType")
    
    // When with regex patterns
    val input = "user@example.com"
    val inputType = when {
        Regex("""^\d+$""").matches(input) -> "Number"
        Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""").matches(input) -> "Email"
        Regex("""^\+?[\d\s\-\(\)]+$""").matches(input) -> "Phone Number"
        Regex("""^https?://.*""").matches(input) -> "URL"
        else -> "Unknown Format"
    }
    println("Input '$input' is: $inputType")
    
    // When with custom conditions
    val age = 25
    val hasLicense = true
    val hasInsurance = true
    val canDrive = when {
        age < 16 -> false
        age in 16..17 && hasLicense -> true
        age >= 18 && hasLicense && hasInsurance -> true
        else -> false
    }
    println("Can drive (age: $age, license: $hasLicense, insurance: $hasInsurance): $canDrive")
    
    // When with complex range operations
    val time = 14 // 24-hour format
    val timeOfDay = when (time) {
        in 0..5 -> "Late night"
        in 6..11 -> "Morning"
        in 12..13 -> "Noon"
        in 14..17 -> "Afternoon"
        in 18..20 -> "Evening"
        in 21..23 -> "Night"
        else -> "Invalid time"
    }
    println("Time $time:00 is: $timeOfDay")
    
    // When with downTo and step
    val number = 15
    val divisibilityInfo = when {
        number % 15 == 0 -> "Divisible by 15 (and 3 and 5)"
        number % 5 == 0 -> "Divisible by 5"
        number % 3 == 0 -> "Divisible by 3"
        number % 2 == 0 -> "Even number"
        else -> "Odd number"
    }
    println("Number $number: $divisibilityInfo")
    
    println()
}

/**
 * ## When with Types and Smart Casts
 * 
 * When expressions can match types and automatically cast variables (smart casting).
 */
fun whenWithTypes() {
    println("--- When with Types and Smart Casts ---")
    
    // When with type checking
    val value: Any = "Hello, Kotlin!"
    val result = when (value) {
        is String -> "String with length ${value.length}: $value"
        is Int -> "Integer with value: $value"
        is Double -> "Double with value: $value"
        is Boolean -> "Boolean with value: $value"
        is List<*> -> "List with ${value.size} elements"
        else -> "Unknown type: ${value.javaClass.simpleName}"
    }
    println("Value analysis: $result")
    
    // Function with type matching
    fun processValue(input: Any): String = when (input) {
        is String -> {
            if (input.isEmpty()) {
                "Empty string"
            } else {
                "String: '${input.uppercase()}'"
            }
        }
        is Int -> {
            if (input < 0) {
                "Negative integer: $input"
            } else {
                "Positive integer: $input"
            }
        }
        is List<*> -> {
            when {
                input.isEmpty() -> "Empty list"
                input.size == 1 -> "Single item list: ${input[0]}"
                else -> "List with ${input.size} items: ${input.take(3)}..."
            }
        }
        is Map<*, *> -> "Map with ${input.size} entries"
        null -> "Null value"
        else -> "Unsupported type: ${input.javaClass.simpleName}"
    }
    
    val testValues: List<Any?> = listOf(
        "Kotlin",
        42,
        -10,
        listOf("a", "b", "c"),
        listOf<String>(),
        mapOf("key" to "value"),
        null,
        3.14
    )
    
    testValues.forEach { testValue ->
        println("Processing: ${processValue(testValue)}")
    }
    
    // Nested type checking
    fun analyzeCollection(collection: Any): String = when (collection) {
        is List<*> -> when {
            collection.isEmpty() -> "Empty list"
            collection.all { it is String } -> "List of strings: $collection"
            collection.all { it is Int } -> "List of integers: $collection"
            collection.all { it is Number } -> "List of numbers: $collection"
            else -> "Mixed list: $collection"
        }
        is Set<*> -> "Set with ${collection.size} unique elements"
        is Map<*, *> -> when {
            collection.isEmpty() -> "Empty map"
            collection.keys.all { it is String } -> "String-keyed map"
            else -> "Map with mixed keys"
        }
        else -> "Not a collection"
    }
    
    val collections = listOf(
        listOf("a", "b", "c"),
        listOf(1, 2, 3),
        listOf(1, "a", 3.14),
        setOf(1, 2, 3),
        mapOf("a" to 1, "b" to 2),
        mapOf(1 to "a", 2 to "b"),
        "not a collection"
    )
    
    println("\nCollection analysis:")
    collections.forEach { coll ->
        println("${coll.toString().take(20)}... -> ${analyzeCollection(coll)}")
    }
    
    // When with generic types and constraints
    fun <T> processGeneric(value: T): String = when (value) {
        is String -> "String: ${value.length} characters"
        is Number -> "Number: ${value.toDouble()}"
        is Collection<*> -> "Collection: ${value.size} elements"
        else -> "Other: ${value.toString()}"
    }
    
    println("\nGeneric processing:")
    println(processGeneric("Hello"))
    println(processGeneric(42))
    println(processGeneric(3.14))
    println(processGeneric(listOf(1, 2, 3)))
    
    println()
}

/**
 * ## Smart Casts
 * 
 * Kotlin automatically casts variables after type checks, eliminating the need for explicit casting.
 */
fun smartCasts() {
    println("--- Smart Casts ---")
    
    // Basic smart cast with nullable types
    val nullableString: String? = "Kotlin"
    
    if (nullableString != null) {
        // Smart cast: nullableString is automatically cast to String
        println("String length: ${nullableString.length}")
        println("Uppercase: ${nullableString.uppercase()}")
    }
    
    // Smart cast with Any type
    val unknownValue: Any = 42
    
    if (unknownValue is Int) {
        // Smart cast: unknownValue is automatically cast to Int
        println("Integer operations: ${unknownValue + 10}")
        println("Is even: ${unknownValue % 2 == 0}")
    }
    
    // Smart cast in when expressions
    fun describeValue(value: Any) = when (value) {
        is String -> "String with ${value.length} characters: '$value'"
        is Int -> "Integer: $value (${if (value % 2 == 0) "even" else "odd"})"
        is Double -> "Double: $value (formatted: ${"%.2f".format(value)})"
        is List<*> -> "List with ${value.size} elements: ${value.take(3)}"
        else -> "Unknown type"
    }
    
    println("Value descriptions:")
    println(describeValue("Hello"))
    println(describeValue(42))
    println(describeValue(3.14159))
    println(describeValue(listOf(1, 2, 3, 4, 5)))
    
    // Smart cast with multiple conditions
    fun processStringOrNumber(input: Any): String {
        return when {
            input is String && input.isNotEmpty() -> {
                // input is smart cast to String
                "Non-empty string: ${input.uppercase()}"
            }
            input is String && input.isEmpty() -> {
                // input is smart cast to String
                "Empty string"
            }
            input is Int && input > 0 -> {
                // input is smart cast to Int
                "Positive integer: $input"
            }
            input is Int && input < 0 -> {
                // input is smart cast to Int
                "Negative integer: $input"
            }
            input is Int -> {
                // input is smart cast to Int
                "Zero"
            }
            else -> "Unsupported type"
        }
    }
    
    val testInputs = listOf("Hello", "", 42, -10, 0, 3.14)
    testInputs.forEach { input ->
        println("${input.toString().padEnd(10)} -> ${processStringOrNumber(input)}")
    }
    
    // Smart cast limitations and workarounds
    println("\nSmart cast limitations:")
    
    // Mutable property - smart cast not allowed
    var mutableValue: Any? = "Hello"
    
    // This would not compile:
    // if (mutableValue is String) {
    //     println(mutableValue.length)  // Smart cast to 'String' is impossible
    // }
    
    // Workaround 1: Local variable
    val localValue = mutableValue
    if (localValue is String) {
        println("Local variable smart cast: ${localValue.length}")
    }
    
    // Workaround 2: Explicit cast
    if (mutableValue is String) {
        val castedValue = mutableValue as String
        println("Explicit cast: ${castedValue.length}")
    }
    
    // Safe cast with smart cast
    val safelyCasted = mutableValue as? String
    if (safelyCasted != null) {
        println("Safe cast result: ${safelyCasted.length}")
    }
    
    println()
}

/**
 * ## Complex Conditionals
 * 
 * Combining multiple conditional patterns for sophisticated logic handling.
 */
fun complexConditionals() {
    println("--- Complex Conditionals ---")
    
    // Nested when expressions
    fun categorizeTemperature(temp: Double, unit: String): String {
        val celsius = when (unit.uppercase()) {
            "F" -> (temp - 32) * 5 / 9
            "K" -> temp - 273.15
            "C" -> temp
            else -> return "Invalid unit"
        }
        
        return when {
            celsius < -40 -> "Extremely cold"
            celsius < 0 -> "Freezing"
            celsius < 10 -> "Cold"
            celsius < 20 -> "Cool"
            celsius < 30 -> "Warm"
            celsius < 40 -> "Hot"
            else -> "Extremely hot"
        }
    }
    
    val temperatures = listOf(
        32.0 to "F",
        0.0 to "C",
        273.15 to "K",
        100.0 to "F",
        -10.0 to "C"
    )
    
    temperatures.forEach { (temp, unit) ->
        println("${temp}°$unit is: ${categorizeTemperature(temp, unit)}")
    }
    
    // Conditional chains with different patterns
    data class User(val name: String, val age: Int, val email: String?, val isActive: Boolean)
    
    fun validateUser(user: User): List<String> {
        val errors = mutableListOf<String>()
        
        // Name validation
        when {
            user.name.isBlank() -> errors.add("Name cannot be blank")
            user.name.length < 2 -> errors.add("Name must be at least 2 characters")
            user.name.length > 50 -> errors.add("Name must be less than 50 characters")
        }
        
        // Age validation
        when (user.age) {
            in Int.MIN_VALUE..0 -> errors.add("Age must be positive")
            in 1..12 -> errors.add("User must be at least 13 years old")
            in 120..Int.MAX_VALUE -> errors.add("Age seems unrealistic")
        }
        
        // Email validation
        user.email?.let { email ->
            when {
                email.isBlank() -> errors.add("Email cannot be blank if provided")
                !email.contains("@") -> errors.add("Email must contain @")
                !email.contains(".") -> errors.add("Email must contain a domain")
                email.length > 100 -> errors.add("Email is too long")
            }
        }
        
        // Activity status
        if (!user.isActive) {
            errors.add("User account is inactive")
        }
        
        return errors
    }
    
    val users = listOf(
        User("Alice", 25, "alice@example.com", true),
        User("", 30, "bob@example.com", true),
        User("Charlie", 10, "charlie@example.com", true),
        User("Diana", 150, "invalid-email", false),
        User("Eve", 22, null, true)
    )
    
    println("\nUser validation:")
    users.forEach { user ->
        val errors = validateUser(user)
        if (errors.isEmpty()) {
            println("✅ ${user.name}: Valid user")
        } else {
            println("❌ ${user.name}: ${errors.joinToString(", ")}")
        }
    }
    
    // Complex business logic
    enum class MembershipTier { BRONZE, SILVER, GOLD, PLATINUM }
    data class Purchase(val amount: Double, val category: String)
    data class Customer(val tier: MembershipTier, val purchases: List<Purchase>)
    
    fun calculateDiscount(customer: Customer, newPurchase: Purchase): Double {
        val baseDiscount = when (customer.tier) {
            MembershipTier.BRONZE -> 0.02
            MembershipTier.SILVER -> 0.05
            MembershipTier.GOLD -> 0.08
            MembershipTier.PLATINUM -> 0.12
        }
        
        val categoryMultiplier = when (newPurchase.category.lowercase()) {
            "electronics" -> 1.5
            "books" -> 2.0
            "clothing" -> 1.2
            else -> 1.0
        }
        
        val volumeBonus = when {
            newPurchase.amount >= 1000 -> 0.05
            newPurchase.amount >= 500 -> 0.03
            newPurchase.amount >= 200 -> 0.01
            else -> 0.0
        }
        
        val loyaltyBonus = when {
            customer.purchases.size >= 20 -> 0.03
            customer.purchases.size >= 10 -> 0.02
            customer.purchases.size >= 5 -> 0.01
            else -> 0.0
        }
        
        return (baseDiscount * categoryMultiplier + volumeBonus + loyaltyBonus).coerceAtMost(0.3)
    }
    
    val customer = Customer(
        tier = MembershipTier.GOLD,
        purchases = List(15) { Purchase(100.0, "misc") }
    )
    
    val testPurchases = listOf(
        Purchase(150.0, "books"),
        Purchase(800.0, "electronics"),
        Purchase(50.0, "food")
    )
    
    println("\nDiscount calculation:")
    testPurchases.forEach { purchase ->
        val discount = calculateDiscount(customer, purchase)
        val discountAmount = purchase.amount * discount
        println("${purchase.category} purchase of $${purchase.amount}: ${(discount * 100).toInt()}% discount ($${String.format("%.2f", discountAmount)} off)")
    }
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of conditional expressions in common programming scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. HTTP Status Code Handler
    fun handleHttpResponse(statusCode: Int, body: String?): String {
        return when (statusCode) {
            in 200..299 -> when (statusCode) {
                200 -> "Success: ${body ?: "No content"}"
                201 -> "Created successfully"
                204 -> "Success: No content to return"
                else -> "Success with status $statusCode"
            }
            in 300..399 -> "Redirection required (Status: $statusCode)"
            in 400..499 -> when (statusCode) {
                400 -> "Bad Request: Check your input"
                401 -> "Unauthorized: Authentication required"
                403 -> "Forbidden: Access denied"
                404 -> "Not Found: Resource doesn't exist"
                else -> "Client error (Status: $statusCode)"
            }
            in 500..599 -> "Server error (Status: $statusCode): Please try again later"
            else -> "Unknown status code: $statusCode"
        }
    }
    
    val responses = listOf(
        200 to "User data",
        201 to null,
        404 to null,
        500 to "Internal error",
        999 to null
    )
    
    println("HTTP Response Handling:")
    responses.forEach { (status, body) ->
        println("$status -> ${handleHttpResponse(status, body)}")
    }
    
    // 2. File Processing Logic
    data class FileInfo(val name: String, val size: Long, val extension: String)
    
    fun processFile(file: FileInfo): String {
        val sizeCategory = when {
            file.size < 1024 -> "Small"
            file.size < 1024 * 1024 -> "Medium"
            file.size < 1024 * 1024 * 1024 -> "Large"
            else -> "Very Large"
        }
        
        val processingMethod = when (file.extension.lowercase()) {
            "txt", "md" -> "Text processing"
            "jpg", "png", "gif" -> when {
                file.size > 10 * 1024 * 1024 -> "Image compression required"
                else -> "Image optimization"
            }
            "pdf" -> "PDF parsing"
            "zip", "rar" -> "Archive extraction"
            "exe", "dmg" -> "Security scan required"
            else -> "Generic file handling"
        }
        
        return "$sizeCategory file (${file.name}): $processingMethod"
    }
    
    val files = listOf(
        FileInfo("document.txt", 2048, "txt"),
        FileInfo("photo.jpg", 15 * 1024 * 1024, "jpg"),
        FileInfo("archive.zip", 100 * 1024, "zip"),
        FileInfo("installer.exe", 50 * 1024 * 1024, "exe")
    )
    
    println("\nFile Processing:")
    files.forEach { file ->
        println(processFile(file))
    }
    
    // 3. Configuration Validation
    data class AppConfig(
        val environment: String,
        val port: Int?,
        val database: String?,
        val debugMode: Boolean?
    )
    
    fun validateConfiguration(config: AppConfig): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        
        // Environment validation
        when (config.environment.lowercase()) {
            "development", "dev" -> {
                // Development-specific validations
                if (config.debugMode != true) {
                    errors.add("Debug mode should be enabled in development")
                }
            }
            "production", "prod" -> {
                // Production-specific validations
                if (config.debugMode == true) {
                    errors.add("Debug mode must be disabled in production")
                }
                if (config.database == null) {
                    errors.add("Database configuration is required in production")
                }
            }
            "testing", "test" -> {
                // Testing-specific validations
                if (config.database != null && !config.database.contains("test")) {
                    errors.add("Test environment should use test database")
                }
            }
            else -> errors.add("Invalid environment: ${config.environment}")
        }
        
        // Port validation
        config.port?.let { port ->
            when {
                port < 1 -> errors.add("Port must be positive")
                port < 1024 && config.environment == "production" -> 
                    errors.add("Production should not use privileged ports")
                port > 65535 -> errors.add("Port must be less than 65536")
            }
        }
        
        return Pair(errors.isEmpty(), errors)
    }
    
    val configurations = listOf(
        AppConfig("development", 3000, "dev_db", true),
        AppConfig("production", 80, null, false),
        AppConfig("production", 8080, "prod_db", true),
        AppConfig("testing", 3001, "main_db", false)
    )
    
    println("\nConfiguration Validation:")
    configurations.forEach { config ->
        val (isValid, errors) = validateConfiguration(config)
        if (isValid) {
            println("✅ ${config.environment}: Valid configuration")
        } else {
            println("❌ ${config.environment}: ${errors.joinToString(", ")}")
        }
    }
    
    // 4. Game Logic Example
    enum class GameState { MENU, PLAYING, PAUSED, GAME_OVER }
    data class Player(val level: Int, val health: Int, val score: Int)
    
    fun getGameAction(state: GameState, player: Player, input: String): String {
        return when (state) {
            GameState.MENU -> when (input.lowercase()) {
                "start", "play" -> "Starting new game..."
                "quit", "exit" -> "Goodbye!"
                "help" -> "Available commands: start, quit, help"
                else -> "Unknown command. Type 'help' for available commands."
            }
            
            GameState.PLAYING -> when {
                input.lowercase() == "pause" -> "Game paused"
                input.lowercase() == "quit" -> "Returning to menu..."
                player.health <= 0 -> "Game Over! Final score: ${player.score}"
                input.lowercase().startsWith("move") -> {
                    val direction = input.split(" ").getOrNull(1)
                    when (direction?.lowercase()) {
                        "up", "down", "left", "right" -> "Moving $direction..."
                        else -> "Invalid direction. Use: up, down, left, right"
                    }
                }
                input.lowercase() == "attack" -> when {
                    player.level < 5 -> "Basic attack!"
                    player.level < 10 -> "Power attack!"
                    else -> "Ultimate attack!"
                }
                else -> "Unknown action. Available: move [direction], attack, pause, quit"
            }
            
            GameState.PAUSED -> when (input.lowercase()) {
                "resume", "continue" -> "Resuming game..."
                "quit" -> "Returning to menu..."
                "save" -> "Game saved!"
                else -> "Paused. Available: resume, save, quit"
            }
            
            GameState.GAME_OVER -> when (input.lowercase()) {
                "restart", "again" -> "Starting new game..."
                "menu" -> "Returning to menu..."
                "quit" -> "Thanks for playing!"
                else -> "Game Over. Available: restart, menu, quit"
            }
        }
    }
    
    val gameScenarios = listOf(
        Triple(GameState.MENU, Player(1, 100, 0), "start"),
        Triple(GameState.PLAYING, Player(3, 80, 1500), "move up"),
        Triple(GameState.PLAYING, Player(8, 50, 5000), "attack"),
        Triple(GameState.PAUSED, Player(5, 30, 2000), "save"),
        Triple(GameState.GAME_OVER, Player(10, 0, 10000), "restart")
    )
    
    println("\nGame Logic:")
    gameScenarios.forEach { (state, player, input) ->
        val action = getGameAction(state, player, input)
        println("[$state] Input: '$input' -> $action")
    }
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice conditional expressions:
 * 
 * 1. Create a grade calculator that converts numeric scores to letter grades with +/- modifiers
 * 2. Build a password strength validator with detailed feedback
 * 3. Implement a smart file organizer based on file type and size
 * 4. Create a weather advisory system based on multiple conditions
 * 5. Build a simple chatbot command processor
 */

// TODO: Exercise 1 - Grade calculator with modifiers
fun calculateDetailedGrade(score: Int): String {
    // TODO: Return grades with + and - modifiers
    // A: 93-100, A-: 90-92, B+: 87-89, B: 83-86, B-: 80-82, etc.
    // Handle invalid scores (negative or > 100)
    return ""
}

// TODO: Exercise 2 - Password strength validator
fun validatePassword(password: String): Pair<String, List<String>> {
    // TODO: Return strength level and list of improvements
    // Check: length, uppercase, lowercase, digits, special chars
    // Strength levels: Weak, Fair, Good, Strong, Very Strong
    return Pair("", emptyList())
}

// TODO: Exercise 3 - File organizer
data class FileItem(val name: String, val sizeBytes: Long, val lastModified: Long)

fun organizeFile(file: FileItem): String {
    // TODO: Suggest folder based on file type and characteristics
    // Consider: file extension, size, age, name patterns
    // Return folder path like "Documents/PDFs" or "Archive/Old Images"
    return ""
}

// TODO: Exercise 4 - Weather advisory system
data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: Double,
    val visibility: Double
)

fun generateWeatherAdvisory(weather: WeatherData): List<String> {
    // TODO: Generate list of weather advisories
    // Consider combinations of conditions for advisories
    // Return specific advice like "Umbrella recommended", "Road conditions hazardous"
    return emptyList()
}

// TODO: Exercise 5 - Chatbot command processor
enum class BotContext { GENERAL, SHOPPING, SUPPORT, SETTINGS }

fun processChatCommand(context: BotContext, message: String, userHistory: List<String>): String {
    // TODO: Process user message based on context and history
    // Handle different command types in each context
    // Consider user's previous interactions for personalized responses
    return ""
}