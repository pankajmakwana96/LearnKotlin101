package com.kotlinmastery.basics.functions

/**
 * # Higher-Order Functions in Kotlin
 * 
 * Higher-order functions are functions that take other functions as parameters or return functions.
 * This enables powerful functional programming patterns and creates more reusable, composable code.
 * 
 * ## Learning Objectives
 * - Understand function types and lambda expressions
 * - Master higher-order function patterns
 * - Use closures and variable capture
 * - Apply functional programming concepts
 * - Build reusable function compositions
 * 
 * ## Prerequisites: Function basics and basic collections
 * ## Estimated Time: 4 hours
 */

fun main() {
    println("=== Kotlin Higher-Order Functions Demo ===\n")
    
    functionTypes()
    lambdaExpressions()
    higherOrderFunctionBasics()
    closuresAndCapture()
    functionComposition()
    practicalPatterns()
    standardLibraryExamples()
    realWorldApplications()
}

/**
 * ## Function Types
 * 
 * Function types define the signature of functions that can be passed as parameters.
 */
fun functionTypes() {
    println("--- Function Types ---")
    
    // Basic function type declarations
    val simpleFunction: (Int) -> Int = { x -> x * 2 }
    val addFunction: (Int, Int) -> Int = { a, b -> a + b }
    val stringProcessor: (String) -> String = { it.uppercase() }
    val predicate: (Int) -> Boolean = { it > 0 }
    
    println("Simple function (double 5): ${simpleFunction(5)}")
    println("Add function (3 + 7): ${addFunction(3, 7)}")
    println("String processor ('hello'): ${stringProcessor("hello")}")
    println("Predicate (is 5 > 0): ${predicate(5)}")
    
    // Function types with no parameters
    val noParamFunction: () -> String = { "Hello from no-param function!" }
    val currentTime: () -> Long = { System.currentTimeMillis() }
    
    println("No param function: ${noParamFunction()}")
    println("Current time: ${currentTime()}")
    
    // Function types with multiple parameters
    val calculator: (Double, Double, String) -> Double = { a, b, operation ->
        when (operation) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0.0) a / b else Double.NaN
            else -> Double.NaN
        }
    }
    
    println("Calculator (10.0, 3.0, '+'): ${calculator(10.0, 3.0, "+")}")
    println("Calculator (10.0, 3.0, '*'): ${calculator(10.0, 3.0, "*")}")
    
    // Function types with nullable parameters and return types
    val nullableProcessor: (String?) -> String? = { input ->
        input?.let { it.trim().takeIf { trimmed -> trimmed.isNotEmpty() } }
    }
    
    println("Nullable processor (null): ${nullableProcessor(null)}")
    println("Nullable processor ('  hello  '): ${nullableProcessor("  hello  ")}")
    println("Nullable processor ('   '): ${nullableProcessor("   ")}")
    
    // Higher-order function type (function that returns a function)
    val functionFactory: (String) -> (String) -> String = { prefix ->
        { text -> "$prefix: $text" }
    }
    
    val errorLogger = functionFactory("ERROR")
    val infoLogger = functionFactory("INFO")
    
    println("Error logger: ${errorLogger("Something went wrong")}")
    println("Info logger: ${infoLogger("Operation completed")}")
    
    // Function type with receiver (extension function type)
    val stringExtension: String.(Int) -> String = { times ->
        this.repeat(times)
    }
    
    println("String extension ('Ha'.repeat(3)): ${"Ha".stringExtension(3)}")
    
    // Storing function references in variables
    fun multiply(a: Int, b: Int): Int = a * b
    fun divide(a: Int, b: Int): Double = a.toDouble() / b
    
    val multiplyRef: (Int, Int) -> Int = ::multiply
    val divideRef: (Int, Int) -> Double = ::divide
    
    println("Multiply reference (4, 5): ${multiplyRef(4, 5)}")
    println("Divide reference (10, 3): ${divideRef(10, 3)}")
    
    println()
}

/**
 * ## Lambda Expressions
 * 
 * Lambda expressions provide a concise way to define anonymous functions.
 */
fun lambdaExpressions() {
    println("--- Lambda Expressions ---")
    
    // Basic lambda syntax
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    // Lambda with explicit parameter
    val doubled = numbers.map { number -> number * 2 }
    println("Doubled numbers: $doubled")
    
    // Lambda with implicit parameter (it)
    val tripled = numbers.map { it * 3 }
    println("Tripled numbers: $tripled")
    
    // Multi-parameter lambda
    val indexedNumbers = numbers.mapIndexed { index, value -> "[$index]: $value" }
    println("Indexed numbers: $indexedNumbers")
    
    // Lambda with multiple statements
    val processed = numbers.map { number ->
        val squared = number * number
        val description = if (squared > 25) "large" else "small"
        "$number² = $squared ($description)"
    }
    println("Processed numbers: $processed")
    
    // Lambda returning lambda
    val createMultiplier = { factor: Int ->
        { value: Int -> value * factor }
    }
    
    val doubler = createMultiplier(2)
    val tripler = createMultiplier(3)
    
    println("Doubler(7): ${doubler(7)}")
    println("Tripler(4): ${tripler(4)}")
    
    // Lambda with early return (using labels)
    fun processItems(items: List<String>, processor: (String) -> String): List<String> {
        return items.map { item ->
            processor(item)
        }
    }
    
    val items = listOf("apple", "banana", "", "cherry", "date")
    val result = processItems(items) { item ->
        if (item.isEmpty()) return@processItems emptyList()  // Early return from outer function
        item.uppercase()
    }
    println("Processed items with early return: $result")
    
    // Lambda with no early return (using local return)
    val filteredAndProcessed = items.mapNotNull { item ->
        if (item.isEmpty()) return@mapNotNull null  // Local return from lambda
        "Processed: ${item.uppercase()}"
    }
    println("Filtered and processed: $filteredAndProcessed")
    
    // Destructuring in lambdas
    val pairs = listOf(Pair("a", 1), Pair("b", 2), Pair("c", 3))
    val destructured = pairs.map { (letter, number) -> "$letter$number" }
    println("Destructured pairs: $destructured")
    
    // Lambda with underscore for unused parameters
    val coordinates = listOf(Triple(1, 2, 3), Triple(4, 5, 6), Triple(7, 8, 9))
    val xValues = coordinates.map { (x, _, _) -> x }  // Only use x, ignore y and z
    println("X values only: $xValues")
    
    // Type annotations in lambdas
    val typedLambda: (String) -> Int = { text: String -> text.length }
    println("Typed lambda ('Kotlin'): ${typedLambda("Kotlin")}")
    
    // Lambda as the last parameter (trailing lambda syntax)
    fun customFilter(items: List<Int>, predicate: (Int) -> Boolean): List<Int> {
        return items.filter(predicate)
    }
    
    // Regular syntax
    val evenNumbers1 = customFilter(numbers, { it % 2 == 0 })
    
    // Trailing lambda syntax
    val evenNumbers2 = customFilter(numbers) { it % 2 == 0 }
    
    println("Even numbers (regular syntax): $evenNumbers1")
    println("Even numbers (trailing lambda): $evenNumbers2")
    
    println()
}

/**
 * ## Higher-Order Function Basics
 * 
 * Functions that accept other functions as parameters or return functions.
 */
fun higherOrderFunctionBasics() {
    println("--- Higher-Order Function Basics ---")
    
    // Function taking another function as parameter
    fun applyOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
        println("Applying operation to $a and $b")
        return operation(a, b)
    }
    
    val add = { x: Int, y: Int -> x + y }
    val multiply = { x: Int, y: Int -> x * y }
    val max = { x: Int, y: Int -> if (x > y) x else y }
    
    println("Add: ${applyOperation(5, 3, add)}")
    println("Multiply: ${applyOperation(5, 3, multiply)}")
    println("Max: ${applyOperation(5, 3, max)}")
    
    // Function returning a function
    fun createValidator(minLength: Int): (String) -> Boolean {
        return { input -> input.length >= minLength }
    }
    
    val passwordValidator = createValidator(8)
    val usernameValidator = createValidator(3)
    
    val testPasswords = listOf("123", "password123", "abc")
    testPasswords.forEach { password ->
        val isValid = passwordValidator(password)
        println("Password '$password' is valid: $isValid")
    }
    
    val testUsernames = listOf("ab", "alice", "x")
    testUsernames.forEach { username ->
        val isValid = usernameValidator(username)
        println("Username '$username' is valid: $isValid")
    }
    
    // Function with multiple function parameters
    fun processData(
        data: List<Int>,
        filter: (Int) -> Boolean,
        transform: (Int) -> String,
        reduce: (String, String) -> String
    ): String {
        return data
            .filter(filter)
            .map(transform)
            .reduce(reduce)
    }
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val result = processData(
        data = numbers,
        filter = { it % 2 == 0 },  // Even numbers only
        transform = { "[$it]" },   // Wrap in brackets
        reduce = { acc, item -> "$acc $item" }  // Join with spaces
    )
    println("Processed data: $result")
    
    // Higher-order function with default function parameters
    fun analyzeNumbers(
        numbers: List<Int>,
        processor: (Int) -> Int = { it },  // Default: identity function
        aggregator: (List<Int>) -> Double = { it.average() }  // Default: average
    ): Double {
        val processed = numbers.map(processor)
        return aggregator(processed)
    }
    
    val testNumbers = listOf(1, 2, 3, 4, 5)
    println("Default analysis: ${analyzeNumbers(testNumbers)}")
    println("Squared analysis: ${analyzeNumbers(testNumbers, { it * it })}")
    println("Sum of squares: ${analyzeNumbers(testNumbers, { it * it }, { it.sum().toDouble() })}")
    
    // Conditional function execution
    fun conditionalExecute(
        condition: Boolean,
        onTrue: () -> String,
        onFalse: () -> String = { "Default false action" }
    ): String {
        return if (condition) onTrue() else onFalse()
    }
    
    val isWorkday = true
    val result1 = conditionalExecute(
        condition = isWorkday,
        onTrue = { "Go to work" },
        onFalse = { "Stay home and relax" }
    )
    println("Workday action: $result1")
    
    val isWeekend = false
    val result2 = conditionalExecute(isWeekend, { "Sleep in" })
    println("Weekend action: $result2")
    
    // Builder pattern with higher-order functions
    class QueryBuilder {
        private var table: String = ""
        private var conditions: MutableList<String> = mutableListOf()
        private var orderBy: String = ""
        
        fun from(tableName: String): QueryBuilder {
            table = tableName
            return this
        }
        
        fun where(condition: String): QueryBuilder {
            conditions.add(condition)
            return this
        }
        
        fun orderBy(column: String): QueryBuilder {
            orderBy = column
            return this
        }
        
        fun build(): String {
            val whereClause = if (conditions.isNotEmpty()) " WHERE ${conditions.joinToString(" AND ")}" else ""
            val orderClause = if (orderBy.isNotEmpty()) " ORDER BY $orderBy" else ""
            return "SELECT * FROM $table$whereClause$orderClause"
        }
    }
    
    fun buildQuery(builder: QueryBuilder.() -> Unit): String {
        return QueryBuilder().apply(builder).build()
    }
    
    val query1 = buildQuery {
        from("users")
        where("age > 18")
        where("active = true")
        orderBy("name")
    }
    println("Generated query: $query1")
    
    println()
}

/**
 * ## Closures and Variable Capture
 * 
 * Lambdas can capture variables from their surrounding scope, creating closures.
 */
fun closuresAndCapture() {
    println("--- Closures and Variable Capture ---")
    
    // Basic variable capture
    var counter = 0
    val increment = {
        counter++
        println("Counter incremented to: $counter")
    }
    
    increment()
    increment()
    increment()
    println("Final counter value: $counter")
    
    // Capturing immutable variables
    val multiplier = 5
    val multiplyBy5 = { value: Int ->
        value * multiplier  // Captures immutable multiplier
    }
    
    println("Multiply 7 by captured value: ${multiplyBy5(7)}")
    
    // Creating multiple closures with different captured values
    fun createAccumulator(initial: Int): () -> Int {
        var current = initial
        return {
            current++
            current
        }
    }
    
    val acc1 = createAccumulator(0)
    val acc2 = createAccumulator(100)
    
    println("Accumulator 1: ${acc1()}, ${acc1()}, ${acc1()}")
    println("Accumulator 2: ${acc2()}, ${acc2()}, ${acc2()}")
    
    // Closure with mutable collection
    val items = mutableListOf<String>()
    val addItem = { item: String ->
        items.add(item)
        println("Added '$item'. Total items: ${items.size}")
    }
    
    addItem("Apple")
    addItem("Banana")
    addItem("Cherry")
    println("All items: $items")
    
    // Complex closure example: Event handler system
    class EventHandler {
        private val listeners = mutableMapOf<String, MutableList<(String) -> Unit>>()
        
        fun on(event: String, handler: (String) -> Unit) {
            listeners.getOrPut(event) { mutableListOf() }.add(handler)
        }
        
        fun emit(event: String, data: String) {
            listeners[event]?.forEach { handler -> handler(data) }
        }
    }
    
    val eventHandler = EventHandler()
    var loginCount = 0
    var errorCount = 0
    
    // Closures capturing different variables
    eventHandler.on("login") { username ->
        loginCount++
        println("User logged in: $username (Total logins: $loginCount)")
    }
    
    eventHandler.on("error") { message ->
        errorCount++
        println("Error occurred: $message (Total errors: $errorCount)")
    }
    
    eventHandler.on("login") { username ->
        println("Welcome back, $username!")
    }
    
    eventHandler.emit("login", "alice")
    eventHandler.emit("error", "Database connection failed")
    eventHandler.emit("login", "bob")
    eventHandler.emit("error", "Invalid password")
    
    // Closure with conditional capture
    fun createConditionalLogger(enabled: Boolean): (String) -> Unit {
        return if (enabled) {
            { message -> println("[LOG] $message") }
        } else {
            { _ -> /* Do nothing */ }
        }
    }
    
    val logger = createConditionalLogger(true)
    val silentLogger = createConditionalLogger(false)
    
    logger("This will be logged")
    silentLogger("This will not be logged")
    
    // Closure with resource management
    fun createFileProcessor(logErrors: Boolean): (String) -> String {
        val errorLog = mutableListOf<String>()
        
        return { filename ->
            try {
                // Simulate file processing
                if (filename.contains("error")) {
                    throw RuntimeException("File processing failed")
                }
                "Processed: $filename"
            } catch (e: Exception) {
                if (logErrors) {
                    errorLog.add("Error processing $filename: ${e.message}")
                    println("Logged error. Total errors: ${errorLog.size}")
                }
                "Error processing: $filename"
            }
        }
    }
    
    val processor = createFileProcessor(true)
    val files = listOf("document.txt", "error_file.txt", "image.png", "bad_error.pdf")
    
    files.forEach { file ->
        val result = processor(file)
        println(result)
    }
    
    println()
}

/**
 * ## Function Composition
 * 
 * Combining simple functions to create more complex behavior.
 */
fun functionComposition() {
    println("--- Function Composition ---")
    
    // Basic function composition
    fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
        return { x -> f(g(x)) }
    }
    
    val addOne = { x: Int -> x + 1 }
    val multiplyByTwo = { x: Int -> x * 2 }
    val square = { x: Int -> x * x }
    
    val addOneThenMultiplyByTwo = compose(multiplyByTwo, addOne)
    val squareThenAddOne = compose(addOne, square)
    
    println("Compose add1 then *2 (5): ${addOneThenMultiplyByTwo(5)}")  // (5+1)*2 = 12
    println("Compose square then add1 (5): ${squareThenAddOne(5)}")      // 5²+1 = 26
    
    // Pipe function (reverse composition)
    infix fun <A, B, C> ((A) -> B).pipe(f: (B) -> C): (A) -> C {
        return { x -> f(this(x)) }
    }
    
    val addOnePipeMultiplyByTwo = addOne pipe multiplyByTwo
    println("Pipe add1 then *2 (5): ${addOnePipeMultiplyByTwo(5)}")
    
    // String processing pipeline
    val trim = { s: String -> s.trim() }
    val lowercase = { s: String -> s.lowercase() }
    val removeSpaces = { s: String -> s.replace(" ", "") }
    val addPrefix = { s: String -> "processed_$s" }
    
    val stringProcessor = trim pipe lowercase pipe removeSpaces pipe addPrefix
    
    val testString = "  Hello World  "
    println("String processing pipeline: '${testString}' -> '${stringProcessor(testString)}'")
    
    // Number processing pipeline
    val isPositive = { x: Int -> x > 0 }
    val isEven = { x: Int -> x % 2 == 0 }
    val toString = { x: Int -> x.toString() }
    
    fun <T> createFilter(predicate: (T) -> Boolean): (List<T>) -> List<T> {
        return { list -> list.filter(predicate) }
    }
    
    fun <T, R> createMapper(transform: (T) -> R): (List<T>) -> List<R> {
        return { list -> list.map(transform) }
    }
    
    val numbers = listOf(-3, -1, 0, 2, 4, 5, 7, 8)
    
    val positiveNumbers = createFilter(isPositive)
    val evenNumbers = createFilter(isEven)
    val numbersToStrings = createMapper(toString)
    
    val processedNumbers = positiveNumbers(numbers)
    val evenPositiveNumbers = evenNumbers(processedNumbers)
    val stringResults = numbersToStrings(evenPositiveNumbers)
    
    println("Original numbers: $numbers")
    println("Positive numbers: $processedNumbers")
    println("Even positive numbers: $evenPositiveNumbers")
    println("As strings: $stringResults")
    
    // Validation pipeline
    data class User(val name: String, val email: String, val age: Int)
    
    fun createValidator<T>(predicate: (T) -> Boolean, error: String): (T) -> Pair<T, List<String>> {
        return { value ->
            if (predicate(value)) {
                Pair(value, emptyList())
            } else {
                Pair(value, listOf(error))
            }
        }
    }
    
    fun <T> combineValidators(vararg validators: (T) -> Pair<T, List<String>>): (T) -> Pair<T, List<String>> {
        return { value ->
            val allErrors = validators.flatMap { validator -> validator(value).second }
            Pair(value, allErrors)
        }
    }
    
    val nameValidator = createValidator<User>({ it.name.isNotBlank() }, "Name cannot be blank")
    val emailValidator = createValidator<User>({ it.email.contains("@") }, "Email must contain @")
    val ageValidator = createValidator<User>({ it.age >= 18 }, "Must be 18 or older")
    
    val userValidator = combineValidators(nameValidator, emailValidator, ageValidator)
    
    val testUsers = listOf(
        User("Alice", "alice@example.com", 25),
        User("", "bob@example.com", 30),
        User("Charlie", "invalid-email", 17),
        User("", "bad-email", 15)
    )
    
    println("\nUser validation:")
    testUsers.forEach { user ->
        val (_, errors) = userValidator(user)
        if (errors.isEmpty()) {
            println("✅ ${user.name}: Valid")
        } else {
            println("❌ ${user.name}: ${errors.joinToString(", ")}")
        }
    }
    
    // Functional curry (partial application)
    fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C {
        return { a -> { b -> f(a, b) } }
    }
    
    val add = { a: Int, b: Int -> a + b }
    val curriedAdd = curry(add)
    val add5 = curriedAdd(5)
    
    println("\nCurried functions:")
    println("Add 5 to 3: ${add5(3)}")
    println("Add 5 to 7: ${add5(7)}")
    
    println()
}

/**
 * ## Practical Patterns
 * 
 * Common higher-order function patterns used in real applications.
 */
fun practicalPatterns() {
    println("--- Practical Patterns ---")
    
    // 1. Strategy Pattern
    interface PaymentStrategy {
        fun pay(amount: Double): String
    }
    
    class PaymentProcessor {
        fun processPayment(amount: Double, strategy: (Double) -> String): String {
            return "Processing payment: ${strategy(amount)}"
        }
    }
    
    val creditCardPayment = { amount: Double -> "Charged $$amount to credit card" }
    val paypalPayment = { amount: Double -> "Transferred $$amount via PayPal" }
    val bankTransfer = { amount: Double -> "Transferred $$amount via bank" }
    
    val processor = PaymentProcessor()
    
    println("Payment strategies:")
    println(processor.processPayment(100.0, creditCardPayment))
    println(processor.processPayment(50.0, paypalPayment))
    println(processor.processPayment(200.0, bankTransfer))
    
    // 2. Observer Pattern
    class EventPublisher<T> {
        private val listeners = mutableListOf<(T) -> Unit>()
        
        fun subscribe(listener: (T) -> Unit) {
            listeners.add(listener)
        }
        
        fun publish(event: T) {
            listeners.forEach { it(event) }
        }
    }
    
    val orderEvents = EventPublisher<String>()
    
    orderEvents.subscribe { order -> println("Email notification: Order $order confirmed") }
    orderEvents.subscribe { order -> println("SMS notification: Order $order shipped") }
    orderEvents.subscribe { order -> println("Analytics: Order $order processed") }
    
    orderEvents.publish("ORD-123")
    
    // 3. Command Pattern
    class CommandProcessor {
        private val commands = mutableListOf<() -> Unit>()
        
        fun addCommand(command: () -> Unit) {
            commands.add(command)
        }
        
        fun executeAll() {
            commands.forEach { it() }
            commands.clear()
        }
    }
    
    val processor2 = CommandProcessor()
    
    processor2.addCommand { println("Backing up database") }
    processor2.addCommand { println("Clearing cache") }
    processor2.addCommand { println("Sending reports") }
    
    println("\nExecuting commands:")
    processor2.executeAll()
    
    // 4. Memoization Pattern
    fun <T, R> memoize(function: (T) -> R): (T) -> R {
        val cache = mutableMapOf<T, R>()
        return { input ->
            cache.getOrPut(input) { function(input) }
        }
    }
    
    val expensiveCalculation = { n: Int ->
        println("Computing expensive calculation for $n")
        Thread.sleep(100)  // Simulate expensive operation
        n * n * n
    }
    
    val memoizedCalculation = memoize(expensiveCalculation)
    
    println("\nMemoization demo:")
    println("First call: ${memoizedCalculation(5)}")   // Will compute
    println("Second call: ${memoizedCalculation(5)}")  // Will use cache
    println("Third call: ${memoizedCalculation(3)}")   // Will compute
    println("Fourth call: ${memoizedCalculation(5)}")  // Will use cache
    
    // 5. Retry Pattern
    fun <T> retry(maxAttempts: Int, operation: (Int) -> T): T? {
        repeat(maxAttempts) { attempt ->
            try {
                return operation(attempt + 1)
            } catch (e: Exception) {
                println("Attempt ${attempt + 1} failed: ${e.message}")
                if (attempt == maxAttempts - 1) {
                    println("All attempts failed")
                }
            }
        }
        return null
    }
    
    val unreliableOperation = { attempt: Int ->
        if (attempt < 3) {
            throw RuntimeException("Operation failed")
        } else {
            "Success on attempt $attempt"
        }
    }
    
    println("\nRetry pattern demo:")
    val result = retry(5, unreliableOperation)
    println("Final result: $result")
    
    // 6. Throttling Pattern
    fun <T> throttle(windowMs: Long, action: (T) -> Unit): (T) -> Unit {
        var lastExecuted = 0L
        return { input ->
            val now = System.currentTimeMillis()
            if (now - lastExecuted >= windowMs) {
                action(input)
                lastExecuted = now
            } else {
                println("Throttled: skipping execution")
            }
        }
    }
    
    val logAction = { message: String -> println("LOG: $message at ${System.currentTimeMillis()}") }
    val throttledLog = throttle(1000L, logAction)
    
    println("\nThrottling demo:")
    throttledLog("Message 1")
    Thread.sleep(500)
    throttledLog("Message 2")  // Should be throttled
    Thread.sleep(600)
    throttledLog("Message 3")  // Should execute
    
    println()
}

/**
 * ## Standard Library Examples
 * 
 * Exploring higher-order functions in Kotlin's standard library.
 */
fun standardLibraryExamples() {
    println("--- Standard Library Examples ---")
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val words = listOf("apple", "banana", "cherry", "date", "elderberry")
    
    // Collection transformations
    println("Original numbers: $numbers")
    println("Squared: ${numbers.map { it * it }}")
    println("Even numbers: ${numbers.filter { it % 2 == 0 }}")
    println("Cumulative sum: ${numbers.scan(0) { acc, n -> acc + n }}")
    
    // Aggregations
    println("\nAggregations:")
    println("Sum: ${numbers.sum()}")
    println("Reduce (product): ${numbers.reduce { acc, n -> acc * n }}")
    println("Fold (concatenate): ${numbers.fold("") { acc, n -> "$acc$n" }}")
    
    // Grouping and partitioning
    println("\nGrouping:")
    val grouped = numbers.groupBy { if (it % 2 == 0) "even" else "odd" }
    println("Grouped by even/odd: $grouped")
    
    val (evens, odds) = numbers.partition { it % 2 == 0 }
    println("Partitioned - Evens: $evens, Odds: $odds")
    
    // String operations
    println("\nString operations:")
    println("Words: $words")
    println("Joined: ${words.joinToString(" | ") { it.uppercase() }}")
    println("Longest word: ${words.maxByOrNull { it.length }}")
    println("Words by length: ${words.sortedBy { it.length }}")
    
    // Any, all, none
    println("\nBoolean operations:")
    println("Any word longer than 5: ${words.any { it.length > 5 }}")
    println("All words start with vowel: ${words.all { it[0] in "aeiou" }}")
    println("No words contain 'z': ${words.none { 'z' in it }}")
    
    // Take and drop operations
    println("\nTake/Drop operations:")
    println("Take while < 5: ${numbers.takeWhile { it < 5 }}")
    println("Drop while < 5: ${numbers.dropWhile { it < 5 }}")
    println("Take last 3: ${numbers.takeLast(3)}")
    
    // Zip and associate operations
    println("\nZip and associate:")
    val letters = listOf("a", "b", "c", "d", "e")
    val zipped = letters.zip(numbers) { letter, number -> "$letter$number" }
    println("Zipped: $zipped")
    
    val associated = words.associate { it to it.length }
    println("Word lengths: $associated")
    
    // Flat operations
    val nestedNumbers = listOf(listOf(1, 2), listOf(3, 4, 5), listOf(6))
    println("\nFlat operations:")
    println("Nested: $nestedNumbers")
    println("Flattened: ${nestedNumbers.flatten()}")
    println("Flat mapped (*2): ${nestedNumbers.flatMap { it.map { n -> n * 2 } }}")
    
    // Window operations
    println("\nWindow operations:")
    println("Windowed (size 3): ${numbers.windowed(3)}")
    println("Windowed with transform: ${numbers.windowed(3) { it.sum() }}")
    println("Chunked (size 3): ${numbers.chunked(3)}")
    
    // Advanced filtering
    println("\nAdvanced filtering:")
    val nullableNumbers = listOf(1, null, 3, null, 5)
    println("Filter not null: ${nullableNumbers.filterNotNull()}")
    println("Filter indexed (even indices): ${numbers.filterIndexed { index, _ -> index % 2 == 0 }}")
    
    // Distinct operations
    val duplicates = listOf(1, 2, 2, 3, 3, 3, 4, 4, 5)
    println("\nDistinct operations:")
    println("With duplicates: $duplicates")
    println("Distinct: ${duplicates.distinct()}")
    println("Distinct by (mod 3): ${numbers.distinctBy { it % 3 }}")
    
    println()
}

/**
 * ## Real-World Applications
 * 
 * Practical examples of higher-order functions in real applications.
 */
fun realWorldApplications() {
    println("--- Real-World Applications ---")
    
    // 1. Data Processing Pipeline
    data class Sale(val product: String, val amount: Double, val region: String, val date: String)
    
    val sales = listOf(
        Sale("Laptop", 1200.0, "North", "2023-01-15"),
        Sale("Mouse", 25.0, "South", "2023-01-16"),
        Sale("Keyboard", 80.0, "North", "2023-01-17"),
        Sale("Monitor", 350.0, "East", "2023-01-18"),
        Sale("Laptop", 1100.0, "West", "2023-01-19"),
        Sale("Mouse", 30.0, "North", "2023-01-20")
    )
    
    // Pipeline for sales analysis
    fun analyzeSales(
        sales: List<Sale>,
        regionFilter: (String) -> Boolean = { true },
        amountThreshold: Double = 0.0,
        groupBy: (Sale) -> String,
        aggregator: (List<Sale>) -> Double
    ): Map<String, Double> {
        return sales
            .filter { regionFilter(it.region) }
            .filter { it.amount >= amountThreshold }
            .groupBy(groupBy)
            .mapValues(aggregator)
    }
    
    println("Sales Analysis:")
    
    // Total sales by product
    val salesByProduct = analyzeSales(
        sales = sales,
        groupBy = { it.product },
        aggregator = { it.sumOf { sale -> sale.amount } }
    )
    println("Sales by product: $salesByProduct")
    
    // Average sales by region (high-value items only)
    val avgHighValueByRegion = analyzeSales(
        sales = sales,
        amountThreshold = 100.0,
        groupBy = { it.region },
        aggregator = { it.map { sale -> sale.amount }.average() }
    )
    println("Avg high-value sales by region: $avgHighValueByRegion")
    
    // 2. Configuration Builder with Validation
    data class ServerConfig(
        val host: String,
        val port: Int,
        val ssl: Boolean,
        val maxConnections: Int
    )
    
    class ConfigBuilder {
        private var host = "localhost"
        private var port = 8080
        private var ssl = false
        private var maxConnections = 100
        private val validators = mutableListOf<(ServerConfig) -> List<String>>()
        
        fun host(value: String) = apply { host = value }
        fun port(value: Int) = apply { port = value }
        fun ssl(enabled: Boolean) = apply { ssl = enabled }
        fun maxConnections(value: Int) = apply { maxConnections = value }
        
        fun validate(validator: (ServerConfig) -> List<String>) = apply {
            validators.add(validator)
        }
        
        fun build(): Result<ServerConfig> {
            val config = ServerConfig(host, port, ssl, maxConnections)
            val errors = validators.flatMap { it(config) }
            
            return if (errors.isEmpty()) {
                Result.success(config)
            } else {
                Result.failure(IllegalArgumentException(errors.joinToString(", ")))
            }
        }
    }
    
    fun buildServerConfig(builder: ConfigBuilder.() -> Unit): Result<ServerConfig> {
        return ConfigBuilder().apply(builder).build()
    }
    
    // Validation functions
    val hostValidator = { config: ServerConfig ->
        if (config.host.isBlank()) listOf("Host cannot be blank") else emptyList()
    }
    
    val portValidator = { config: ServerConfig ->
        when {
            config.port < 1 -> listOf("Port must be positive")
            config.port > 65535 -> listOf("Port must be less than 65536")
            config.ssl && config.port == 80 -> listOf("SSL should use port 443, not 80")
            else -> emptyList()
        }
    }
    
    val connectionValidator = { config: ServerConfig ->
        if (config.maxConnections < 1) listOf("Max connections must be positive") else emptyList()
    }
    
    println("\nConfiguration Building:")
    
    val validConfig = buildServerConfig {
        host("api.example.com")
        port(443)
        ssl(true)
        maxConnections(200)
        validate(hostValidator)
        validate(portValidator)
        validate(connectionValidator)
    }
    
    when {
        validConfig.isSuccess -> println("✅ Valid config: ${validConfig.getOrNull()}")
        validConfig.isFailure -> println("❌ Invalid config: ${validConfig.exceptionOrNull()?.message}")
    }
    
    val invalidConfig = buildServerConfig {
        host("")
        port(80)
        ssl(true)
        maxConnections(-5)
        validate(hostValidator)
        validate(portValidator)
        validate(connectionValidator)
    }
    
    when {
        invalidConfig.isSuccess -> println("✅ Valid config: ${invalidConfig.getOrNull()}")
        invalidConfig.isFailure -> println("❌ Invalid config: ${invalidConfig.exceptionOrNull()?.message}")
    }
    
    // 3. Event Processing System
    sealed class Event {
        data class UserLogin(val userId: String, val timestamp: Long) : Event()
        data class UserLogout(val userId: String, val timestamp: Long) : Event()
        data class PageView(val userId: String, val page: String, val timestamp: Long) : Event()
        data class Purchase(val userId: String, val amount: Double, val timestamp: Long) : Event()
    }
    
    class EventProcessor {
        private val handlers = mutableMapOf<Class<out Event>, MutableList<(Event) -> Unit>>()
        
        inline fun <reified T : Event> on(noinline handler: (T) -> Unit) {
            handlers.getOrPut(T::class.java) { mutableListOf() }
                .add { event -> handler(event as T) }
        }
        
        fun process(event: Event) {
            handlers[event::class.java]?.forEach { handler -> handler(event) }
        }
    }
    
    val eventProcessor = EventProcessor()
    val userSessions = mutableMapOf<String, Long>()
    val pageViews = mutableMapOf<String, Int>()
    
    eventProcessor.on<Event.UserLogin> { event ->
        userSessions[event.userId] = event.timestamp
        println("User ${event.userId} logged in")
    }
    
    eventProcessor.on<Event.UserLogout> { event ->
        val loginTime = userSessions[event.userId]
        if (loginTime != null) {
            val sessionDuration = event.timestamp - loginTime
            println("User ${event.userId} logged out (session: ${sessionDuration}ms)")
            userSessions.remove(event.userId)
        }
    }
    
    eventProcessor.on<Event.PageView> { event ->
        pageViews[event.page] = pageViews.getOrDefault(event.page, 0) + 1
        println("Page view: ${event.page} by ${event.userId}")
    }
    
    eventProcessor.on<Event.Purchase> { event ->
        println("Purchase: $${event.amount} by ${event.userId}")
    }
    
    println("\nEvent Processing:")
    val currentTime = System.currentTimeMillis()
    
    eventProcessor.process(Event.UserLogin("user1", currentTime))
    eventProcessor.process(Event.PageView("user1", "/home", currentTime + 1000))
    eventProcessor.process(Event.PageView("user1", "/products", currentTime + 2000))
    eventProcessor.process(Event.Purchase("user1", 99.99, currentTime + 3000))
    eventProcessor.process(Event.UserLogout("user1", currentTime + 4000))
    
    println("Page view stats: $pageViews")
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice higher-order functions:
 * 
 * 1. Create a flexible sorting system using function parameters
 * 2. Build a data transformation pipeline with validation
 * 3. Implement a caching decorator using higher-order functions
 * 4. Create a rate limiter using closures
 * 5. Build a query builder using function composition
 */

// TODO: Exercise 1 - Flexible sorting system
fun <T> flexibleSort(
    items: List<T>,
    // TODO: Add parameters for:
    // - Key selector function
    // - Comparator function
    // - Direction (ascending/descending)
    // - Secondary sort criteria
): List<T> {
    // TODO: Implement flexible sorting logic
    return items
}

// TODO: Exercise 2 - Data transformation pipeline
class DataPipeline<T> {
    // TODO: Implement a pipeline that supports:
    // - Multiple transformation steps
    // - Validation at each step
    // - Error handling and reporting
    // - Conditional execution
    
    fun transform(input: T): Result<T> {
        // TODO: Apply all transformations and validations
        return Result.success(input)
    }
}

// TODO: Exercise 3 - Caching decorator
fun <T, R> cached(
    keyGenerator: (T) -> String,
    expiration: Long = Long.MAX_VALUE,
    function: (T) -> R
): (T) -> R {
    // TODO: Create a caching wrapper that:
    // - Generates cache keys using keyGenerator
    // - Respects expiration times
    // - Thread-safe operation
    // - Cache statistics
    return function
}

// TODO: Exercise 4 - Rate limiter
class RateLimiter {
    // TODO: Implement rate limiting that:
    // - Supports different time windows
    // - Tracks requests per key/user
    // - Configurable limits
    // - Graceful degradation
    
    fun <T> limit(
        key: String,
        requestsPerWindow: Int,
        windowMs: Long,
        action: () -> T
    ): T? {
        // TODO: Check rate limits and execute if allowed
        return null
    }
}

// TODO: Exercise 5 - Query builder with composition
class QueryBuilder {
    // TODO: Build a query system that:
    // - Composes multiple conditions using functions
    // - Supports different data sources
    // - Type-safe operations
    // - Optimizable execution plans
    
    fun <T> build(): (List<T>) -> List<T> {
        // TODO: Return composed query function
        return { it }
    }
}