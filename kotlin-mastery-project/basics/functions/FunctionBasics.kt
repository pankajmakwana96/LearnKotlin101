package com.kotlinmastery.basics.functions

/**
 * # Function Basics in Kotlin
 * 
 * Functions are first-class citizens in Kotlin, supporting both procedural and functional programming styles.
 * This module covers function declarations, expressions, parameters, and fundamental concepts.
 * 
 * ## Learning Objectives
 * - Understand function declarations and expressions
 * - Master parameter handling (default, named, vararg)
 * - Use local functions and scope
 * - Apply function types and references
 * - Handle return types and unit functions
 * 
 * ## Prerequisites: Variables, types, and control flow
 * ## Estimated Time: 4 hours
 */

fun main() {
    println("=== Kotlin Function Basics Demo ===\n")
    
    functionDeclarations()
    functionExpressions()
    functionParameters()
    returnTypes()
    localFunctions()
    functionReferences()
    functionOverloading()
    realWorldExamples()
}

/**
 * ## Function Declarations
 * 
 * Kotlin functions are declared using the `fun` keyword and can have various forms.
 */
fun functionDeclarations() {
    println("--- Function Declarations ---")
    
    // Basic function declaration
    fun greet(name: String) {
        println("Hello, $name!")
    }
    
    greet("Kotlin")
    
    // Function with return value
    fun add(a: Int, b: Int): Int {
        return a + b
    }
    
    val sum = add(5, 3)
    println("5 + 3 = $sum")
    
    // Function with multiple parameters
    fun calculateArea(length: Double, width: Double): Double {
        return length * width
    }
    
    val area = calculateArea(5.0, 3.0)
    println("Area of 5.0 x 3.0 rectangle: $area")
    
    // Function with no parameters
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    println("Current timestamp: ${getCurrentTimestamp()}")
    
    // Function returning Unit (void equivalent)
    fun printInfo(message: String): Unit {
        println("Info: $message")
    }
    
    printInfo("This function returns Unit")
    
    // Unit can be omitted
    fun printWarning(message: String) {
        println("Warning: $message")
    }
    
    printWarning("Unit return type is optional")
    
    // Function with conditional return
    fun getGrade(score: Int): String {
        if (score >= 90) return "A"
        if (score >= 80) return "B"
        if (score >= 70) return "C"
        if (score >= 60) return "D"
        return "F"
    }
    
    val scores = listOf(95, 87, 72, 58, 91)
    scores.forEach { score ->
        println("Score $score gets grade: ${getGrade(score)}")
    }
    
    // Function with early return
    fun validateAge(age: Int): String {
        if (age < 0) return "Invalid age: negative value"
        if (age > 150) return "Invalid age: too high"
        if (age < 18) return "Minor"
        if (age >= 65) return "Senior"
        return "Adult"
    }
    
    val ages = listOf(-1, 5, 17, 25, 70, 200)
    ages.forEach { age ->
        println("Age $age: ${validateAge(age)}")
    }
    
    println()
}

/**
 * ## Function Expressions
 * 
 * Functions can be written as single expressions for concise code.
 */
fun functionExpressions() {
    println("--- Function Expressions ---")
    
    // Single expression function
    fun double(x: Int): Int = x * 2
    
    println("Double of 7: ${double(7)}")
    
    // Expression function with type inference
    fun triple(x: Int) = x * 3  // Return type inferred as Int
    
    println("Triple of 5: ${triple(5)}")
    
    // Expression function with when
    fun getAbsoluteValue(x: Int) = if (x >= 0) x else -x
    
    val numbers = listOf(-5, 0, 3, -10, 8)
    numbers.forEach { num ->
        println("Absolute value of $num: ${getAbsoluteValue(num)}")
    }
    
    // Expression function with when expression
    fun getDayType(dayNumber: Int) = when (dayNumber) {
        1, 7 -> "Weekend"
        in 2..6 -> "Weekday"
        else -> "Invalid day"
    }
    
    (1..8).forEach { day ->
        println("Day $day: ${getDayType(day)}")
    }
    
    // Complex expression function
    fun calculateDiscount(price: Double, customerType: String, quantity: Int) = when {
        customerType == "VIP" -> price * 0.8 * quantity
        quantity >= 10 -> price * 0.9 * quantity
        quantity >= 5 -> price * 0.95 * quantity
        else -> price * quantity
    }
    
    val scenarios = listOf(
        Triple(100.0, "VIP", 3),
        Triple(50.0, "Regular", 12),
        Triple(25.0, "Regular", 7),
        Triple(75.0, "Regular", 2)
    )
    
    scenarios.forEach { (price, type, qty) ->
        val total = calculateDiscount(price, type, qty)
        println("$type customer, $qty items at $$price each: Total $${String.format("%.2f", total)}")
    }
    
    // Recursive expression function
    fun factorial(n: Int): Long = if (n <= 1) 1 else n * factorial(n - 1)
    
    (1..10).forEach { n ->
        println("Factorial of $n: ${factorial(n)}")
    }
    
    // Expression function with string operations
    fun formatName(firstName: String, lastName: String) = 
        "${firstName.replaceFirstChar { it.uppercase() }} ${lastName.uppercase()}"
    
    val names = listOf(
        "john" to "doe",
        "jane" to "smith",
        "bob" to "johnson"
    )
    
    names.forEach { (first, last) ->
        println("Formatted name: ${formatName(first, last)}")
    }
    
    println()
}

/**
 * ## Function Parameters
 * 
 * Kotlin provides flexible parameter handling with default values, named arguments, and vararg.
 */
fun functionParameters() {
    println("--- Function Parameters ---")
    
    // Default parameters
    fun greetUser(name: String, greeting: String = "Hello", punctuation: String = "!") {
        println("$greeting, $name$punctuation")
    }
    
    greetUser("Alice")                                    // Uses defaults
    greetUser("Bob", "Hi")                               // Custom greeting
    greetUser("Charlie", "Good morning", ".")            // All custom
    
    // Named arguments
    greetUser(name = "Diana", punctuation = "!!!")       // Skip middle parameter
    greetUser(punctuation = "?", name = "Eve", greeting = "Hey")  // Any order
    
    // Function with many default parameters
    fun createUser(
        name: String,
        email: String = "",
        age: Int = 0,
        isActive: Boolean = true,
        role: String = "user",
        department: String = "general"
    ): String {
        return "User: $name, Email: $email, Age: $age, Active: $isActive, Role: $role, Dept: $department"
    }
    
    println("\nUser creation examples:")
    println(createUser("John"))
    println(createUser("Jane", email = "jane@example.com"))
    println(createUser("Bob", age = 30, role = "admin"))
    println(createUser(
        name = "Alice",
        email = "alice@company.com",
        age = 28,
        role = "manager",
        department = "engineering"
    ))
    
    // Vararg parameters
    fun calculateSum(vararg numbers: Int): Int {
        var sum = 0
        for (number in numbers) {
            sum += number
        }
        return sum
    }
    
    println("\nVararg examples:")
    println("Sum of 1, 2, 3: ${calculateSum(1, 2, 3)}")
    println("Sum of 5, 10: ${calculateSum(5, 10)}")
    println("Sum of 1, 2, 3, 4, 5, 6: ${calculateSum(1, 2, 3, 4, 5, 6)}")
    
    // Spreading arrays to vararg
    val numbersArray = intArrayOf(2, 4, 6, 8)
    println("Sum from array: ${calculateSum(*numbersArray)}")  // Spread operator
    
    // Vararg with other parameters
    fun logMessage(level: String, vararg messages: String) {
        val timestamp = System.currentTimeMillis()
        val combinedMessage = messages.joinToString(" ")
        println("[$timestamp] [$level] $combinedMessage")
    }
    
    logMessage("INFO", "User", "logged", "in", "successfully")
    logMessage("ERROR", "Database", "connection", "failed")
    
    // Vararg with named parameters
    fun formatReport(
        title: String,
        author: String = "Unknown",
        vararg sections: String,
        includeTimestamp: Boolean = true
    ): String {
        val timestamp = if (includeTimestamp) " [${System.currentTimeMillis()}]" else ""
        val sectionList = sections.joinToString("\n- ", prefix = "\n- ")
        return "Report: $title by $author$timestamp$sectionList"
    }
    
    println("\nReport formatting:")
    println(formatReport(
        "Monthly Sales",
        "John Doe",
        "Executive Summary",
        "Sales Figures",
        "Regional Analysis",
        "Recommendations",
        includeTimestamp = false
    ))
    
    // Function with lambda parameters (preview)
    fun processNumbers(vararg numbers: Int, operation: (Int) -> Int): List<Int> {
        return numbers.map(operation)
    }
    
    val results = processNumbers(1, 2, 3, 4, 5) { it * it }  // Square each number
    println("Squared numbers: $results")
    
    println()
}

/**
 * ## Return Types
 * 
 * Understanding different return types and their implications.
 */
fun returnTypes() {
    println("--- Return Types ---")
    
    // Explicit return types
    fun getStringLength(text: String): Int {
        return text.length
    }
    
    fun isEven(number: Int): Boolean {
        return number % 2 == 0
    }
    
    fun getFirstCharacter(text: String): Char? {
        return if (text.isNotEmpty()) text[0] else null
    }
    
    println("String length of 'Kotlin': ${getStringLength("Kotlin")}")
    println("Is 42 even? ${isEven(42)}")
    println("First character of 'Hello': ${getFirstCharacter("Hello")}")
    println("First character of empty string: ${getFirstCharacter("")}")
    
    // Function returning collections
    fun getEvenNumbers(max: Int): List<Int> {
        val result = mutableListOf<Int>()
        for (i in 2..max step 2) {
            result.add(i)
        }
        return result
    }
    
    fun getWordCounts(text: String): Map<String, Int> {
        val words = text.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }
        val counts = mutableMapOf<String, Int>()
        for (word in words) {
            counts[word] = counts.getOrDefault(word, 0) + 1
        }
        return counts
    }
    
    println("\nEven numbers up to 10: ${getEvenNumbers(10)}")
    
    val sampleText = "The quick brown fox jumps over the lazy dog. The dog was lazy."
    val wordCounts = getWordCounts(sampleText)
    println("Word counts: $wordCounts")
    
    // Function returning pairs and data classes
    data class Coordinates(val x: Double, val y: Double)
    
    fun getDivisionResult(dividend: Int, divisor: Int): Pair<Int, Int>? {
        return if (divisor != 0) {
            Pair(dividend / divisor, dividend % divisor)  // quotient, remainder
        } else {
            null
        }
    }
    
    fun parseCoordinates(input: String): Coordinates? {
        val parts = input.split(",")
        return if (parts.size == 2) {
            val x = parts[0].trim().toDoubleOrNull()
            val y = parts[1].trim().toDoubleOrNull()
            if (x != null && y != null) Coordinates(x, y) else null
        } else {
            null
        }
    }
    
    val divisionCases = listOf(Pair(10, 3), Pair(15, 5), Pair(7, 0))
    divisionCases.forEach { (dividend, divisor) ->
        val result = getDivisionResult(dividend, divisor)
        if (result != null) {
            println("$dividend ÷ $divisor = ${result.first} remainder ${result.second}")
        } else {
            println("Cannot divide $dividend by $divisor")
        }
    }
    
    val coordinateInputs = listOf("3.5,4.2", "invalid", "1.0, 2.0", "1,2,3")
    coordinateInputs.forEach { input ->
        val coords = parseCoordinates(input)
        if (coords != null) {
            println("Parsed '$input' as: (${coords.x}, ${coords.y})")
        } else {
            println("Failed to parse '$input' as coordinates")
        }
    }
    
    // Nothing return type (functions that never return)
    fun throwError(message: String): Nothing {
        throw IllegalArgumentException(message)
    }
    
    fun processValue(value: Int): String {
        return when {
            value > 0 -> "Positive: $value"
            value == 0 -> "Zero"
            else -> throwError("Negative values not supported: $value")
        }
    }
    
    try {
        println(processValue(5))
        println(processValue(0))
        println(processValue(-3))  // This will throw an exception
    } catch (e: IllegalArgumentException) {
        println("Caught exception: ${e.message}")
    }
    
    println()
}

/**
 * ## Local Functions
 * 
 * Functions can be defined inside other functions, providing encapsulation and access to outer scope.
 */
fun localFunctions() {
    println("--- Local Functions ---")
    
    // Basic local function
    fun processOrder(items: List<String>, customerType: String): String {
        fun calculateDiscount(): Double {
            return when (customerType) {
                "VIP" -> 0.2
                "Premium" -> 0.1
                "Regular" -> 0.05
                else -> 0.0
            }
        }
        
        fun formatItemList(): String {
            return items.joinToString(", ")
        }
        
        val discount = calculateDiscount()
        val itemList = formatItemList()
        return "Order for $customerType customer: $itemList (${(discount * 100).toInt()}% discount)"
    }
    
    val order1 = processOrder(listOf("Laptop", "Mouse", "Keyboard"), "VIP")
    val order2 = processOrder(listOf("Book", "Pen"), "Regular")
    println(order1)
    println(order2)
    
    // Local function accessing outer parameters
    fun calculateTax(income: Double, country: String): Double {
        fun getBaseTaxRate(): Double {
            return when (country) {
                "US" -> 0.22
                "UK" -> 0.20
                "Germany" -> 0.25
                "Japan" -> 0.18
                else -> 0.15
            }
        }
        
        fun calculateProgressiveTax(): Double {
            val baseTax = income * getBaseTaxRate()
            return when {
                income > 100000 -> baseTax * 1.2  // High earner surcharge
                income > 50000 -> baseTax * 1.1   // Medium earner adjustment
                else -> baseTax
            }
        }
        
        return calculateProgressiveTax()
    }
    
    val taxCases = listOf(
        Triple(45000.0, "US", "Middle income US"),
        Triple(75000.0, "UK", "High-middle income UK"),
        Triple(120000.0, "Germany", "High income Germany")
    )
    
    taxCases.forEach { (income, country, description) ->
        val tax = calculateTax(income, country)
        println("$description: Income $$income, Tax: $${String.format("%.2f", tax)}")
    }
    
    // Local function with recursion
    fun findFactorsOf(number: Int): List<Int> {
        val factors = mutableListOf<Int>()
        
        fun checkFactor(candidate: Int) {
            if (candidate > number) return
            
            if (number % candidate == 0) {
                factors.add(candidate)
            }
            
            checkFactor(candidate + 1)
        }
        
        checkFactor(1)
        return factors
    }
    
    val numbersToFactor = listOf(12, 15, 28)
    numbersToFactor.forEach { num ->
        val factors = findFactorsOf(num)
        println("Factors of $num: $factors")
    }
    
    // Local extension functions
    fun analyzeText(text: String): Map<String, Any> {
        fun String.wordCount(): Int = this.split(Regex("\\W+")).filter { it.isNotBlank() }.size
        
        fun String.averageWordLength(): Double {
            val words = this.split(Regex("\\W+")).filter { it.isNotBlank() }
            return if (words.isNotEmpty()) words.map { it.length }.average() else 0.0
        }
        
        fun String.sentenceCount(): Int = this.split(Regex("[.!?]+")).filter { it.isNotBlank() }.size
        
        return mapOf(
            "wordCount" to text.wordCount(),
            "averageWordLength" to text.averageWordLength(),
            "sentenceCount" to text.sentenceCount(),
            "characterCount" to text.length
        )
    }
    
    val sampleText = "This is a sample text. It contains multiple sentences! How interesting?"
    val analysis = analyzeText(sampleText)
    println("\nText analysis for: \"$sampleText\"")
    analysis.forEach { (key, value) ->
        println("  $key: $value")
    }
    
    // Local function with validation
    fun registerUser(name: String, email: String, age: Int): String {
        fun validateName(): String? {
            return when {
                name.isBlank() -> "Name cannot be empty"
                name.length < 2 -> "Name must be at least 2 characters"
                name.length > 50 -> "Name must be less than 50 characters"
                !name.all { it.isLetter() || it.isWhitespace() } -> "Name can only contain letters and spaces"
                else -> null
            }
        }
        
        fun validateEmail(): String? {
            return when {
                email.isBlank() -> "Email cannot be empty"
                !email.contains("@") -> "Email must contain @"
                !email.contains(".") -> "Email must contain a domain"
                email.count { it == '@' } != 1 -> "Email must contain exactly one @"
                else -> null
            }
        }
        
        fun validateAge(): String? {
            return when {
                age < 13 -> "Must be at least 13 years old"
                age > 120 -> "Age must be realistic"
                else -> null
            }
        }
        
        val errors = listOfNotNull(validateName(), validateEmail(), validateAge())
        
        return if (errors.isEmpty()) {
            "User $name registered successfully with email $email"
        } else {
            "Registration failed: ${errors.joinToString(", ")}"
        }
    }
    
    val registrationAttempts = listOf(
        Triple("John Doe", "john@example.com", 25),
        Triple("", "invalid", 10),
        Triple("Alice123", "alice@test", 30),
        Triple("Bob", "bob@site.com", 150)
    )
    
    println("\nUser registration attempts:")
    registrationAttempts.forEach { (name, email, age) ->
        val result = registerUser(name, email, age)
        println(result)
    }
    
    println()
}

/**
 * ## Function References
 * 
 * Kotlin allows you to reference functions as values and pass them around.
 */
fun functionReferences() {
    println("--- Function References ---")
    
    // Top-level function references
    fun square(x: Int): Int = x * x
    fun cube(x: Int): Int = x * x * x
    fun double(x: Int): Int = x * 2
    
    // Function reference syntax
    val squareRef = ::square
    val cubeRef = ::cube
    val doubleRef = ::double
    
    println("Using function references:")
    val number = 5
    println("$number squared: ${squareRef(number)}")
    println("$number cubed: ${cubeRef(number)}")
    println("$number doubled: ${doubleRef(number)}")
    
    // Using function references in higher-order functions
    val numbers = listOf(1, 2, 3, 4, 5)
    
    println("\nApplying functions to list $numbers:")
    println("Squared: ${numbers.map(::square)}")
    println("Cubed: ${numbers.map(::cube)}")
    println("Doubled: ${numbers.map(::double)}")
    
    // Method references on instances
    data class Calculator(val multiplier: Int) {
        fun multiply(x: Int): Int = x * multiplier
        fun add(x: Int): Int = x + multiplier
    }
    
    val calc = Calculator(10)
    val multiplyRef = calc::multiply
    val addRef = calc::add
    
    println("\nMethod references on Calculator instance:")
    println("Numbers multiplied by 10: ${numbers.map(multiplyRef)}")
    println("Numbers plus 10: ${numbers.map(addRef)}")
    
    // Constructor references
    data class Person(val name: String, val age: Int)
    
    val personConstructor = ::Person
    val people = listOf(
        "Alice" to 30,
        "Bob" to 25,
        "Charlie" to 35
    ).map { (name, age) -> personConstructor(name, age) }
    
    println("\nCreated people using constructor reference:")
    people.forEach { person ->
        println("${person.name} is ${person.age} years old")
    }
    
    // Property references
    val nameRef = Person::name
    val ageRef = Person::age
    
    println("\nUsing property references:")
    println("Names: ${people.map(nameRef)}")
    println("Ages: ${people.map(ageRef)}")
    
    // Function composition using references
    fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int {
        return { x -> f(g(x)) }
    }
    
    val squareThenDouble = compose(::double, ::square)
    val doubleThenSquare = compose(::square, ::double)
    
    println("\nFunction composition:")
    val testValue = 3
    println("Square then double $testValue: ${squareThenDouble(testValue)}")
    println("Double then square $testValue: ${doubleThenSquare(testValue)}")
    
    // Using function references for filtering and sorting
    fun isEven(x: Int): Boolean = x % 2 == 0
    fun isPositive(x: Int): Boolean = x > 0
    
    val mixedNumbers = listOf(-3, -1, 0, 2, 4, 5, 7, 8)
    
    println("\nFiltering with function references:")
    println("Original: $mixedNumbers")
    println("Even numbers: ${mixedNumbers.filter(::isEven)}")
    println("Positive numbers: ${mixedNumbers.filter(::isPositive)}")
    println("Even AND positive: ${mixedNumbers.filter(::isEven).filter(::isPositive)}")
    
    // Standard library function references
    val strings = listOf("  hello  ", "  world  ", "  kotlin  ")
    
    println("\nUsing standard library function references:")
    println("Original: $strings")
    println("Trimmed: ${strings.map(String::trim)}")
    println("Uppercase: ${strings.map(String::trim).map(String::uppercase)}")
    println("Lengths: ${strings.map(String::trim).map(String::length)}")
    
    println()
}

/**
 * ## Function Overloading
 * 
 * Multiple functions with the same name but different parameter signatures.
 */
fun functionOverloading() {
    println("--- Function Overloading ---")
    
    // Basic function overloading
    fun format(value: Int): String = "Integer: $value"
    fun format(value: Double): String = "Double: ${String.format("%.2f", value)}"
    fun format(value: String): String = "String: '$value'"
    fun format(value: Boolean): String = "Boolean: $value"
    
    println("Function overloading examples:")
    println(format(42))
    println(format(3.14159))
    println(format("Hello"))
    println(format(true))
    
    // Overloading with different parameter counts
    fun calculateArea(radius: Double): Double = Math.PI * radius * radius  // Circle
    fun calculateArea(length: Double, width: Double): Double = length * width  // Rectangle
    fun calculateArea(a: Double, b: Double, c: Double): Double {  // Triangle (Heron's formula)
        val s = (a + b + c) / 2
        return Math.sqrt(s * (s - a) * (s - b) * (s - c))
    }
    
    println("\nArea calculations:")
    println("Circle (radius 5): ${String.format("%.2f", calculateArea(5.0))}")
    println("Rectangle (4x6): ${String.format("%.2f", calculateArea(4.0, 6.0))}")
    println("Triangle (3,4,5): ${String.format("%.2f", calculateArea(3.0, 4.0, 5.0))}")
    
    // Overloading with default parameters
    fun createMessage(text: String): String = "Message: $text"
    fun createMessage(text: String, priority: String): String = "[$priority] Message: $text"
    fun createMessage(text: String, priority: String, timestamp: Long): String = 
        "[$timestamp] [$priority] Message: $text"
    
    println("\nMessage creation:")
    println(createMessage("Hello"))
    println(createMessage("Important update", "HIGH"))
    println(createMessage("System notification", "INFO", System.currentTimeMillis()))
    
    // Overloading with collections
    fun processData(item: String): String = "Processing single item: $item"
    fun processData(items: List<String>): String = "Processing ${items.size} items: ${items.joinToString(", ")}"
    fun processData(items: Array<String>): String = "Processing array of ${items.size} items: ${items.joinToString(", ")}"
    
    println("\nData processing:")
    println(processData("single"))
    println(processData(listOf("one", "two", "three")))
    println(processData(arrayOf("a", "b", "c", "d")))
    
    // Overloading resolution examples
    fun demonstrate(value: Any): String = "Any: $value"
    fun demonstrate(value: Number): String = "Number: $value"
    fun demonstrate(value: Int): String = "Int: $value"
    
    println("\nOverloading resolution (most specific type wins):")
    println(demonstrate(42))          // Calls Int version
    println(demonstrate(3.14))        // Calls Number version
    println(demonstrate("text"))      // Calls Any version
    
    // Careful with nullable overloads
    fun handleValue(value: String): String = "Non-null string: $value"
    fun handleValue(value: String?): String = if (value != null) "Nullable string: $value" else "Null value"
    
    val nonNullString: String = "test"
    val nullableString: String? = "test"
    val nullValue: String? = null
    
    println("\nNullable overloading:")
    println(handleValue(nonNullString))    // Calls non-null version
    println(handleValue(nullableString))   // Calls nullable version
    println(handleValue(nullValue))        // Calls nullable version
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of functions in common programming scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. Input Validation and Sanitization
    fun sanitizeInput(input: String, maxLength: Int = 100): String {
        fun removeHtmlTags(text: String): String = text.replace(Regex("<[^>]*>"), "")
        fun normalizeWhitespace(text: String): String = text.replace(Regex("\\s+"), " ")
        fun truncateIfNeeded(text: String, max: Int): String = 
            if (text.length > max) "${text.substring(0, max - 3)}..." else text
        
        return input
            .trim()
            .let(::removeHtmlTags)
            .let(::normalizeWhitespace)
            .let { truncateIfNeeded(it, maxLength) }
    }
    
    val userInputs = listOf(
        "<script>alert('xss')</script>Hello World",
        "This    has     multiple    spaces",
        "A".repeat(150)  // Very long string
    )
    
    println("Input sanitization:")
    userInputs.forEach { input ->
        val sanitized = sanitizeInput(input)
        println("Original: ${input.take(50)}${if (input.length > 50) "..." else ""}")
        println("Sanitized: $sanitized\n")
    }
    
    // 2. Configuration Management
    data class DatabaseConfig(
        val host: String,
        val port: Int,
        val database: String,
        val username: String,
        val password: String,
        val maxConnections: Int = 10,
        val timeout: Int = 30
    )
    
    fun createDatabaseConfig(
        environment: String,
        host: String = "localhost",
        port: Int = 5432,
        database: String = "app_db",
        username: String = "app_user",
        password: String,
        maxConnections: Int = when (environment) {
            "production" -> 50
            "staging" -> 20
            else -> 10
        },
        timeout: Int = when (environment) {
            "production" -> 60
            else -> 30
        }
    ): DatabaseConfig {
        fun validateConfig(config: DatabaseConfig): DatabaseConfig {
            require(config.host.isNotBlank()) { "Host cannot be blank" }
            require(config.port in 1..65535) { "Port must be between 1 and 65535" }
            require(config.database.isNotBlank()) { "Database name cannot be blank" }
            require(config.username.isNotBlank()) { "Username cannot be blank" }
            require(config.password.isNotBlank()) { "Password cannot be blank" }
            require(config.maxConnections > 0) { "Max connections must be positive" }
            require(config.timeout > 0) { "Timeout must be positive" }
            return config
        }
        
        val config = DatabaseConfig(host, port, database, username, password, maxConnections, timeout)
        return validateConfig(config)
    }
    
    println("Database configuration:")
    try {
        val prodConfig = createDatabaseConfig("production", password = "secure_prod_password")
        val devConfig = createDatabaseConfig("development", password = "dev_password")
        
        println("Production config: $prodConfig")
        println("Development config: $devConfig")
    } catch (e: IllegalArgumentException) {
        println("Configuration error: ${e.message}")
    }
    
    // 3. Data Processing Pipeline
    data class Transaction(val id: String, val amount: Double, val type: String, val timestamp: Long)
    
    fun processTransactions(
        transactions: List<Transaction>,
        minAmount: Double = 0.0,
        allowedTypes: Set<String> = setOf("credit", "debit", "transfer")
    ): Map<String, Any> {
        
        fun validateTransaction(transaction: Transaction): Boolean {
            return transaction.amount >= minAmount && 
                   transaction.type in allowedTypes &&
                   transaction.id.isNotBlank()
        }
        
        fun calculateStats(validTransactions: List<Transaction>): Map<String, Double> {
            return mapOf(
                "total" to validTransactions.sumOf { it.amount },
                "average" to (validTransactions.map { it.amount }.average().takeIf { !it.isNaN() } ?: 0.0),
                "max" to (validTransactions.maxOfOrNull { it.amount } ?: 0.0),
                "min" to (validTransactions.minOfOrNull { it.amount } ?: 0.0)
            )
        }
        
        fun groupByType(validTransactions: List<Transaction>): Map<String, List<Transaction>> {
            return validTransactions.groupBy { it.type }
        }
        
        val validTransactions = transactions.filter(::validateTransaction)
        val invalidCount = transactions.size - validTransactions.size
        
        return mapOf(
            "validCount" to validTransactions.size,
            "invalidCount" to invalidCount,
            "statistics" to calculateStats(validTransactions),
            "byType" to groupByType(validTransactions).mapValues { it.value.size }
        )
    }
    
    val sampleTransactions = listOf(
        Transaction("T001", 100.50, "credit", System.currentTimeMillis()),
        Transaction("T002", -50.0, "debit", System.currentTimeMillis()),  // Invalid: negative amount
        Transaction("T003", 250.75, "credit", System.currentTimeMillis()),
        Transaction("T004", 75.25, "transfer", System.currentTimeMillis()),
        Transaction("", 100.0, "credit", System.currentTimeMillis()),     // Invalid: empty ID
        Transaction("T005", 25.0, "withdrawal", System.currentTimeMillis()) // Invalid: type not allowed
    )
    
    println("\nTransaction processing:")
    val results = processTransactions(sampleTransactions)
    results.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // 4. API Response Builder
    fun buildApiResponse(
        success: Boolean,
        data: Any? = null,
        message: String? = null,
        errorCode: String? = null,
        metadata: Map<String, Any> = emptyMap()
    ): Map<String, Any?> {
        
        fun addTimestamp(response: MutableMap<String, Any?>) {
            response["timestamp"] = System.currentTimeMillis()
        }
        
        fun addPagination(response: MutableMap<String, Any?>, data: Any?) {
            if (data is List<*>) {
                response["pagination"] = mapOf(
                    "total" to data.size,
                    "page" to 1,
                    "pageSize" to data.size
                )
            }
        }
        
        val response = mutableMapOf<String, Any?>(
            "success" to success,
            "data" to data
        )
        
        if (message != null) response["message"] = message
        if (errorCode != null) response["errorCode"] = errorCode
        if (metadata.isNotEmpty()) response["metadata"] = metadata
        
        addTimestamp(response)
        addPagination(response, data)
        
        return response
    }
    
    println("\nAPI Response examples:")
    
    // Success response with data
    val successResponse = buildApiResponse(
        success = true,
        data = listOf("item1", "item2", "item3"),
        message = "Data retrieved successfully"
    )
    println("Success response: $successResponse")
    
    // Error response
    val errorResponse = buildApiResponse(
        success = false,
        message = "User not found",
        errorCode = "USER_NOT_FOUND_404",
        metadata = mapOf("requestId" to "req_123", "retryAfter" to 60)
    )
    println("Error response: $errorResponse")
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice function basics:
 * 
 * 1. Create a calculator with overloaded functions for different operations
 * 2. Build a text formatter with various formatting options using default parameters
 * 3. Implement a validation framework using local functions
 * 4. Create a logging system with different log levels and formatting
 * 5. Build a data transformer with function references and composition
 */

// TODO: Exercise 1 - Calculator with overloads
object Calculator {
    // TODO: Implement overloaded functions for add, subtract, multiply, divide
    // Support Int, Double, and BigDecimal operations
    // Handle division by zero appropriately
}

// TODO: Exercise 2 - Text formatter
fun formatText(
    text: String,
    // TODO: Add default parameters for:
    // - maxWidth (default 80)
    // - alignment (left, center, right - default left)
    // - padding character (default space)
    // - case conversion (none, upper, lower, title - default none)
): String {
    // TODO: Implement text formatting logic
    return ""
}

// TODO: Exercise 3 - Validation framework
class ValidationResult(val isValid: Boolean, val errors: List<String>)

fun validateUserData(
    name: String,
    email: String,
    age: Int,
    password: String
): ValidationResult {
    // TODO: Use local functions to validate each field
    // Return combined validation result
    return ValidationResult(false, emptyList())
}

// TODO: Exercise 4 - Logging system
enum class LogLevel { DEBUG, INFO, WARN, ERROR }

fun log(level: LogLevel, message: String, vararg tags: String) {
    // TODO: Implement logging with:
    // - Timestamp formatting
    // - Level-based filtering
    // - Tag support
    // - Different output formats
}

// TODO: Exercise 5 - Data transformer
fun <T, R> transform(
    data: List<T>,
    transformer: (T) -> R,
    filter: (T) -> Boolean = { true },
    validator: (R) -> Boolean = { true }
): List<R> {
    // TODO: Implement transformation pipeline
    // Apply filter, transform, then validate
    return emptyList()
}