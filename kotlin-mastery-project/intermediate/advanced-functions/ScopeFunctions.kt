package com.kotlinmastery.intermediate.advancedfunctions

/**
 * # Scope Functions in Kotlin
 * 
 * Scope functions (let, run, with, apply, also) provide a concise way to execute code blocks
 * in the context of an object. Each has specific use cases and return behaviors that make
 * code more readable and expressive when used appropriately.
 * 
 * ## Learning Objectives
 * - Understand the purpose and differences between scope functions
 * - Master when to use let, run, with, apply, and also
 * - Apply scope functions for null safety and object initialization
 * - Combine scope functions for complex operations
 * - Avoid common scope function anti-patterns
 * 
 * ## Prerequisites: Higher-order functions and lambdas
 * ## Estimated Time: 4 hours
 */

fun main() {
    println("=== Kotlin Scope Functions Demo ===\n")
    
    letFunction()
    runFunction()
    withFunction()
    applyFunction()
    alsoFunction()
    choosingTheBestScopeFunction()
    realWorldExamples()
    scopeFunctionPatterns()
}

/**
 * ## let Function
 * 
 * let executes a block and returns the result. The object is available as 'it' (or named parameter).
 * Primary use: null safety and transformations.
 */
fun letFunction() {
    println("--- let Function ---")
    
    // Basic let usage
    val name = "Kotlin"
    val result = name.let {
        println("Processing: $it")
        it.uppercase()
    }
    println("Result: $result")
    
    // let with nullable objects (most common use case)
    val nullableName: String? = "Alice"
    val processedName = nullableName?.let { name ->
        println("Name is not null: $name")
        "Hello, $name!"
    }
    println("Processed: $processedName")
    
    val nullValue: String? = null
    val processedNull = nullValue?.let { name ->
        println("This won't execute")
        "Hello, $name!"
    }
    println("Processed null: $processedNull")
    
    // let for transformations
    val numbers = listOf(1, 2, 3, 4, 5)
    val evenSquares = numbers
        .filter { it % 2 == 0 }
        .let { evens ->
            println("Even numbers: $evens")
            evens.map { it * it }
        }
    println("Even squares: $evenSquares")
    
    // let with complex nullable chains
    data class Person(val name: String?, val address: Address?)
    data class Address(val street: String?, val city: String?)
    
    val person = Person("John", Address("123 Main St", "Springfield"))
    val cityInfo = person.address?.city?.let { city ->
        "City: $city (${city.length} characters)"
    } ?: "No city available"
    println("City info: $cityInfo")
    
    // let for avoiding repeated null checks
    fun processUser(user: Person?) {
        user?.let { u ->
            println("Processing user: ${u.name}")
            u.address?.let { addr ->
                println("Address: ${addr.street}, ${addr.city}")
            }
            // Within this block, 'u' is guaranteed non-null
            val summary = buildString {
                append("User: ${u.name}")
                u.address?.let { append(", City: ${it.city}") }
            }
            println("Summary: $summary")
        } ?: println("User is null")
    }
    
    processUser(person)
    processUser(null)
    
    // let with different return types
    val stringNumber = "123"
    val calculation = stringNumber.let { str ->
        val num = str.toIntOrNull()
        num?.let { it * 2 } ?: 0
    }
    println("Calculation: $calculation")
    
    // let for temporary variables
    fun calculateArea(length: Double, width: Double): String {
        return (length * width).let { area ->
            when {
                area < 10 -> "Small area: $area"
                area < 100 -> "Medium area: $area"
                else -> "Large area: $area"
            }
        }
    }
    
    println("Area description: ${calculateArea(5.0, 15.0)}")
    
    // let with destructuring
    val pair = Pair("key", 42)
    val message = pair.let { (key, value) ->
        "Key: $key, Value: $value"
    }
    println("Destructured: $message")
    
    println()
}

/**
 * ## run Function
 * 
 * run executes a block and returns the result. The object is available as 'this'.
 * Primary use: object configuration and computed properties.
 */
fun runFunction() {
    println("--- run Function ---")
    
    // Basic run usage (extension function)
    val name = "Kotlin"
    val result = name.run {
        println("Processing: $this")
        uppercase() + " Programming"
    }
    println("Result: $result")
    
    // run without receiver (standalone function)
    val computation = run {
        val a = 10
        val b = 20
        println("Computing: $a + $b")
        a + b
    }
    println("Computation result: $computation")
    
    // run for object initialization and configuration
    data class ConfigBuilder(
        var host: String = "localhost",
        var port: Int = 8080,
        var ssl: Boolean = false,
        var timeout: Int = 30
    ) {
        fun validate(): Boolean = host.isNotEmpty() && port > 0
        fun build(): String = "$host:$port (SSL: $ssl, Timeout: ${timeout}s)"
    }
    
    val config = ConfigBuilder().run {
        host = "api.example.com"
        port = 443
        ssl = true
        timeout = 60
        
        if (validate()) {
            build()
        } else {
            "Invalid configuration"
        }
    }
    println("Configuration: $config")
    
    // run for complex calculations
    data class Rectangle(val width: Double, val height: Double)
    
    val rectangle = Rectangle(5.0, 3.0)
    val analysis = rectangle.run {
        val area = width * height
        val perimeter = 2 * (width + height)
        val diagonal = kotlin.math.sqrt(width * width + height * height)
        
        """
        Rectangle Analysis:
        - Dimensions: ${width} x ${height}
        - Area: $area
        - Perimeter: $perimeter
        - Diagonal: ${"%.2f".format(diagonal)}
        - Is Square: ${width == height}
        """.trimIndent()
    }
    println(analysis)
    
    // run with nullable objects
    val nullableString: String? = "Hello World"
    val processedString = nullableString?.run {
        println("String length: $length")
        split(" ").joinToString("-") { it.lowercase() }
    }
    println("Processed string: $processedString")
    
    // run for scoped operations
    fun processFile(filename: String): String {
        return filename.run {
            println("Processing file: $this")
            
            // Simulate file processing
            val extension = substringAfterLast(".")
            val baseName = substringBeforeLast(".")
            val size = length * 1024 // Mock file size
            
            when (extension) {
                "txt" -> "Text file: $baseName ($size bytes)"
                "jpg", "png" -> "Image file: $baseName ($size bytes)"
                "pdf" -> "Document: $baseName ($size bytes)"
                else -> "Unknown file type: $this"
            }
        }
    }
    
    println("File processing: ${processFile("document.pdf")}")
    
    // run for database-like operations
    class DatabaseConnection {
        fun connect(): Boolean = true
        fun query(sql: String): List<String> = listOf("Result1", "Result2")
        fun close() = println("Connection closed")
    }
    
    val queryResult = DatabaseConnection().run {
        if (connect()) {
            val results = query("SELECT * FROM users")
            close()
            "Query successful: ${results.size} results"
        } else {
            "Connection failed"
        }
    }
    println("Database operation: $queryResult")
    
    // Nested run functions
    val nestedResult = "input".run {
        println("Outer run: $this")
        uppercase().run {
            println("Inner run: $this")
            "$this processed"
        }
    }
    println("Nested result: $nestedResult")
    
    println()
}

/**
 * ## with Function
 * 
 * with takes an object as parameter and executes a block. The object is available as 'this'.
 * Primary use: calling multiple functions on the same object.
 */
fun withFunction() {
    println("--- with Function ---")
    
    // Basic with usage
    val stringBuilder = StringBuilder()
    val result = with(stringBuilder) {
        append("Hello")
        append(" ")
        append("World")
        append("!")
        toString()
    }
    println("StringBuilder result: $result")
    
    // with for multiple operations on an object
    data class Car(var brand: String = "", var model: String = "", var year: Int = 0) {
        fun start() = println("$brand $model started")
        fun stop() = println("$brand $model stopped")
        fun getInfo() = "$year $brand $model"
    }
    
    val car = Car()
    val carInfo = with(car) {
        brand = "Toyota"
        model = "Camry"
        year = 2023
        
        start()
        stop()
        getInfo()
    }
    println("Car info: $carInfo")
    
    // with for configuration objects
    data class EmailConfig(
        var smtpServer: String = "",
        var port: Int = 0,
        var username: String = "",
        var password: String = "",
        var encryption: String = ""
    ) {
        fun isValid() = smtpServer.isNotEmpty() && port > 0 && username.isNotEmpty()
        override fun toString() = "EmailConfig(server=$smtpServer:$port, user=$username, encryption=$encryption)"
    }
    
    val emailConfig = EmailConfig()
    val configResult = with(emailConfig) {
        smtpServer = "smtp.gmail.com"
        port = 587
        username = "user@gmail.com"
        password = "secretpassword"
        encryption = "TLS"
        
        if (isValid()) {
            "Configuration is valid: $this"
        } else {
            "Configuration is invalid"
        }
    }
    println("Email config: $configResult")
    
    // with for canvas-like operations
    class Canvas {
        fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) = 
            println("Drawing line from ($x1,$y1) to ($x2,$y2)")
        fun drawRect(x: Int, y: Int, width: Int, height: Int) = 
            println("Drawing rectangle at ($x,$y) with size ${width}x$height")
        fun drawCircle(x: Int, y: Int, radius: Int) = 
            println("Drawing circle at ($x,$y) with radius $radius")
        fun setColor(color: String) = println("Setting color to $color")
    }
    
    val canvas = Canvas()
    with(canvas) {
        setColor("red")
        drawLine(0, 0, 100, 100)
        setColor("blue")
        drawRect(50, 50, 200, 150)
        setColor("green")
        drawCircle(100, 100, 50)
    }
    
    // with for mathematical operations
    data class Vector3D(val x: Double, val y: Double, val z: Double) {
        fun magnitude() = kotlin.math.sqrt(x * x + y * y + z * z)
        fun normalize(): Vector3D {
            val mag = magnitude()
            return Vector3D(x / mag, y / mag, z / mag)
        }
        operator fun plus(other: Vector3D) = Vector3D(x + other.x, y + other.y, z + other.z)
        operator fun times(scalar: Double) = Vector3D(x * scalar, y * scalar, z * scalar)
    }
    
    val vector = Vector3D(3.0, 4.0, 5.0)
    val vectorAnalysis = with(vector) {
        val mag = magnitude()
        val normalized = normalize()
        val doubled = this * 2.0
        
        """
        Vector Analysis:
        - Original: ($x, $y, $z)
        - Magnitude: ${"%.2f".format(mag)}
        - Normalized: (${"%.2f".format(normalized.x)}, ${"%.2f".format(normalized.y)}, ${"%.2f".format(normalized.z)})
        - Doubled: (${"%.1f".format(doubled.x)}, ${"%.1f".format(doubled.y)}, ${"%.1f".format(doubled.z)})
        """.trimIndent()
    }
    println(vectorAnalysis)
    
    // with for collections
    val numbers = mutableListOf<Int>()
    val listResult = with(numbers) {
        add(1)
        add(2)
        add(3)
        addAll(listOf(4, 5))
        
        "List contains ${size} elements: $this"
    }
    println("List result: $listResult")
    
    // with vs other scope functions
    println("\nwith vs other scope functions:")
    val person = "Alice"
    
    // These are equivalent:
    val withResult = with(person) { "Hello, $this!" }
    val runResult = person.run { "Hello, $this!" }
    val letResult = person.let { "Hello, $it!" }
    
    println("with: $withResult")
    println("run: $runResult")  
    println("let: $letResult")
    
    println()
}

/**
 * ## apply Function
 * 
 * apply executes a block and returns the receiver object. The object is available as 'this'.
 * Primary use: object initialization and configuration.
 */
fun applyFunction() {
    println("--- apply Function ---")
    
    // Basic apply usage - object initialization
    data class Person(var name: String = "", var age: Int = 0, var email: String = "") {
        fun introduce() = println("Hi, I'm $name, $age years old. Email: $email")
    }
    
    val person = Person().apply {
        name = "Alice"
        age = 30
        email = "alice@example.com"
    }
    
    println("Person created: $person")
    person.introduce()
    
    // apply for mutable collections
    val numbers = mutableListOf<Int>().apply {
        add(1)
        add(2)
        add(3)
        addAll(listOf(4, 5))
    }
    println("Numbers: $numbers")
    
    // apply for view configuration (Android-style)
    class TextView {
        var text: String = ""
        var textSize: Int = 14
        var textColor: String = "black"
        var visible: Boolean = true
        
        fun show() = println("Showing TextView: '$text' (size: $textSize, color: $textColor)")
    }
    
    val textView = TextView().apply {
        text = "Hello, World!"
        textSize = 18
        textColor = "blue"
        visible = true
    }
    textView.show()
    
    // apply for builder pattern
    class HttpRequest {
        private var url: String = ""
        private var method: String = "GET"
        private var headers: MutableMap<String, String> = mutableMapOf()
        private var body: String? = null
        
        fun url(url: String): HttpRequest {
            this.url = url
            return this
        }
        
        fun method(method: String): HttpRequest {
            this.method = method
            return this
        }
        
        fun header(key: String, value: String): HttpRequest {
            headers[key] = value
            return this
        }
        
        fun body(body: String): HttpRequest {
            this.body = body
            return this
        }
        
        fun execute(): String {
            return "Executing $method request to $url with headers $headers" + 
                   if (body != null) " and body: $body" else ""
        }
    }
    
    val request = HttpRequest().apply {
        url("https://api.example.com/users")
        method("POST")
        header("Content-Type", "application/json")
        header("Authorization", "Bearer token123")
        body("""{"name": "John", "age": 25}""")
    }
    
    println("HTTP Request: ${request.execute()}")
    
    // apply for conditional initialization
    data class DatabaseConfig(
        var host: String = "localhost",
        var port: Int = 5432,
        var database: String = "",
        var username: String = "",
        var password: String = "",
        var sslEnabled: Boolean = false
    ) {
        override fun toString() = "$username@$host:$port/$database (SSL: $sslEnabled)"
    }
    
    fun createDatabaseConfig(environment: String) = DatabaseConfig().apply {
        when (environment) {
            "development" -> {
                host = "localhost"
                database = "dev_db"
                username = "dev_user"
                password = "dev_pass"
                sslEnabled = false
            }
            "production" -> {
                host = "prod.db.example.com"
                port = 5432
                database = "prod_db"
                username = "prod_user"
                password = "secure_pass"
                sslEnabled = true
            }
            "test" -> {
                host = "test.db.example.com"
                database = "test_db"
                username = "test_user"
                password = "test_pass"
                sslEnabled = false
            }
        }
    }
    
    val devConfig = createDatabaseConfig("development")
    val prodConfig = createDatabaseConfig("production")
    
    println("Dev config: $devConfig")
    println("Prod config: $prodConfig")
    
    // apply with validation
    class User {
        var name: String = ""
        var email: String = ""
        var age: Int = 0
        
        fun validate(): Boolean {
            return name.isNotEmpty() && email.contains("@") && age >= 0
        }
        
        override fun toString() = "User(name='$name', email='$email', age=$age)"
    }
    
    fun createValidUser(name: String, email: String, age: Int): User? {
        return User().apply {
            this.name = name
            this.email = email  
            this.age = age
        }.takeIf { it.validate() }
    }
    
    val validUser = createValidUser("Bob", "bob@example.com", 25)
    val invalidUser = createValidUser("", "invalid", -1)
    
    println("Valid user: $validUser")
    println("Invalid user: $invalidUser")
    
    // apply for file/resource operations
    class FileWriter(private val filename: String) {
        private val content = StringBuilder()
        
        fun writeLine(line: String): FileWriter {
            content.append(line).append("\n")
            return this
        }
        
        fun writeData(data: Any): FileWriter {
            content.append(data.toString()).append("\n")
            return this
        }
        
        fun save(): String {
            // Simulate saving to file
            return "Saved ${content.length} characters to $filename:\n$content"
        }
    }
    
    val fileOutput = FileWriter("output.txt").apply {
        writeLine("Header: Data Export")
        writeLine("Date: ${java.time.LocalDate.now()}")
        writeLine("---")
        writeData("Record 1: Important data")
        writeData("Record 2: More data")
        writeLine("End of file")
    }.save()
    
    println("File operation result: $fileOutput")
    
    // apply vs other scope functions for initialization
    println("\napply vs other scope functions for initialization:")
    
    // apply returns the object (best for initialization)
    val person1 = Person().apply {
        name = "Alice"
        age = 30
    }
    
    // run returns the last expression (not the object)
    val person2String = Person().run {
        name = "Bob"
        age = 25
        "$name is $age years old"  // Returns string, not Person
    }
    
    // with requires passing object as parameter
    val person3 = Person()
    with(person3) {
        name = "Charlie"
        age = 35
    }
    // person3 is modified, but with returns Unit
    
    println("apply result (Person object): $person1")
    println("run result (String): $person2String")  
    println("with result (Person object): $person3")
    
    println()
}

/**
 * ## also Function
 * 
 * also executes a block and returns the receiver object. The object is available as 'it'.
 * Primary use: additional actions without changing the object.
 */
fun alsoFunction() {
    println("--- also Function ---")
    
    // Basic also usage - side effects
    val numbers = listOf(1, 2, 3, 4, 5)
        .also { println("Original list: $it") }
        .filter { it % 2 == 0 }
        .also { println("Filtered list: $it") }
        .map { it * it }
        .also { println("Squared list: $it") }
    
    println("Final result: $numbers")
    
    // also for logging and debugging
    data class User(val name: String, val email: String)
    
    fun createUser(name: String, email: String): User {
        return User(name, email).also {
            println("Created user: ${it.name} with email ${it.email}")
            // Could log to file, send analytics, etc.
        }
    }
    
    val user = createUser("Alice", "alice@example.com")
    println("User object: $user")
    
    // also for validation and side effects
    fun validateAndProcess(value: String): String {
        return value
            .also { 
                if (it.isBlank()) throw IllegalArgumentException("Value cannot be blank")
                println("Validating: '$it'")
            }
            .trim()
            .also { println("Trimmed: '$it'") }
            .lowercase()
            .also { println("Lowercased: '$it'") }
    }
    
    val processed = validateAndProcess("  HELLO WORLD  ")
    println("Final processed value: '$processed'")
    
    // also for adding to collections
    val userList = mutableListOf<User>()
    val newUser = User("Bob", "bob@example.com")
        .also { userList.add(it) }
        .also { println("Added user to list. List size: ${userList.size}") }
    
    println("New user: $newUser")
    println("User list: $userList")
    
    // also for caching/memoization
    class ExpensiveCalculator {
        private val cache = mutableMapOf<Int, Int>()
        
        fun calculate(input: Int): Int {
            return cache.getOrPut(input) {
                // Expensive calculation
                Thread.sleep(100)
                input * input
            }.also {
                println("Calculated result for $input: $it (cached: ${input in cache})")
            }
        }
    }
    
    val calculator = ExpensiveCalculator()
    println("First calculation: ${calculator.calculate(5)}")
    println("Second calculation: ${calculator.calculate(5)}")  // From cache
    
    // also for initialization with side effects
    class DatabaseConnection(private val url: String) {
        var isConnected: Boolean = false
            private set
            
        fun connect(): DatabaseConnection {
            // Simulate connection
            isConnected = true
            return this
        }
        
        fun query(sql: String): List<String> {
            if (!isConnected) throw IllegalStateException("Not connected")
            return listOf("Result1", "Result2")
        }
        
        override fun toString() = "DatabaseConnection(url='$url', connected=$isConnected)"
    }
    
    val dbConnection = DatabaseConnection("jdbc:postgresql://localhost:5432/mydb")
        .also { println("Created connection: $it") }
        .connect()
        .also { println("Connected: $it") }
    
    val queryResults = dbConnection.query("SELECT * FROM users")
        .also { println("Query returned ${it.size} results") }
    
    println("Final results: $queryResults")
    
    // also for audit trails
    data class BankAccount(val accountNumber: String, var balance: Double) {
        fun withdraw(amount: Double): BankAccount {
            if (amount > balance) throw IllegalArgumentException("Insufficient funds")
            balance -= amount
            return this
        }
        
        fun deposit(amount: Double): BankAccount {
            balance += amount
            return this
        }
    }
    
    val account = BankAccount("ACC123", 1000.0)
        .also { println("Initial account: $it") }
        .withdraw(200.0)
        .also { println("After withdrawal: $it") }
        .deposit(50.0)
        .also { println("After deposit: $it") }
    
    println("Final account state: $account")
    
    // also for performance measurement
    fun performanceTest(data: List<Int>): List<Int> {
        val startTime = System.nanoTime()
        
        return data
            .filter { it > 0 }
            .also { println("Filtering took: ${(System.nanoTime() - startTime) / 1_000_000}ms") }
            .map { it * 2 }
            .also { println("Mapping took: ${(System.nanoTime() - startTime) / 1_000_000}ms total") }
            .sorted()
            .also { println("Sorting took: ${(System.nanoTime() - startTime) / 1_000_000}ms total") }
    }
    
    val testData = listOf(5, -2, 8, -1, 3, 7, -4, 6)
    val result = performanceTest(testData)
    println("Performance test result: $result")
    
    // also vs apply comparison
    println("\nalso vs apply comparison:")
    
    data class Config(var name: String = "", var value: String = "")
    
    // apply modifies the object (object as 'this')
    val config1 = Config().apply {
        name = "setting1"  // 'this' is implicit
        value = "value1"
    }
    
    // also performs side effects (object as 'it')  
    val config2 = Config().also {
        it.name = "setting2"  // 'it' must be explicit
        it.value = "value2"
        println("Configured: $it")  // Side effect
    }
    
    println("Config1 (apply): $config1")
    println("Config2 (also): $config2")
    
    println()
}

/**
 * ## Choosing the Best Scope Function
 * 
 * Guidelines for selecting the most appropriate scope function for each scenario.
 */
fun choosingTheBestScopeFunction() {
    println("--- Choosing the Best Scope Function ---")
    
    println("Scope Function Selection Guide:")
    println()
    
    println("┌─────────────┬─────────────┬─────────────┬─────────────────────────────────────────┐")
    println("│ Function    │ Object Ref  │ Return      │ Use Case                                │")
    println("├─────────────┼─────────────┼─────────────┼─────────────────────────────────────────┤")
    println("│ let         │ it          │ Lambda      │ Null safety, transformations            │")
    println("│ run         │ this        │ Lambda      │ Object config + computation             │")
    println("│ with        │ this        │ Lambda      │ Multiple operations on object           │")
    println("│ apply       │ this        │ Object      │ Object initialization                   │")
    println("│ also        │ it          │ Object      │ Side effects, debugging                 │")
    println("└─────────────┴─────────────┴─────────────┴─────────────────────────────────────────┘")
    
    println("\nDecision Tree:")
    println("1. Do you need to return the object itself?")
    println("   Yes → use apply (initialization) or also (side effects)")
    println("   No  → use let (null safety), run (computation), or with (operations)")
    
    println("\n2. Is the object nullable?")
    println("   Yes → use let or run with safe call (?.)")
    println("   No  → any function works")
    
    println("\n3. Do you need 'this' or 'it' reference?")
    println("   'this' (implicit) → run, with, apply")
    println("   'it' (explicit)   → let, also")
    
    // Practical examples of choosing the right function
    println("\nPractical Examples:")
    
    data class Person(var name: String = "", var age: Int = 0, var email: String = "")
    
    // Use case 1: Null safety transformation
    val nullableName: String? = "Alice"
    val greeting = nullableName?.let { "Hello, $it!" } ?: "Hello, stranger!"
    println("1. Null safety (let): $greeting")
    
    // Use case 2: Object initialization  
    val person = Person().apply {
        name = "Bob"
        age = 25
        email = "bob@example.com"
    }
    println("2. Initialization (apply): $person")
    
    // Use case 3: Multiple operations on existing object
    val stringBuilder = StringBuilder()
    val result = with(stringBuilder) {
        append("Hello")
        append(" ")
        append("World")
        toString()
    }
    println("3. Multiple operations (with): $result")
    
    // Use case 4: Computation with object context
    val rectangle = Rectangle(5.0, 3.0)
    val area = rectangle.run {
        val area = width * height
        "Area of ${width}x${height} rectangle is $area"
    }
    println("4. Computation (run): $area")
    
    data class Rectangle(val width: Double, val height: Double)
    
    // Use case 5: Side effects while returning object
    val numbers = mutableListOf(1, 2, 3)
        .also { println("5. Side effects (also): Original list $it") }
        .apply { add(4) }
        .also { println("   After adding: $it") }
    
    // Common mistakes and better alternatives
    println("\nCommon Mistakes and Better Alternatives:")
    
    // Mistake 1: Using let when apply would be better
    println("\n❌ Don't use let for initialization:")
    val badPerson = Person().let {
        it.name = "Charlie"
        it.age = 30
        it  // Need to return the object
    }
    
    println("✅ Use apply instead:")
    val goodPerson = Person().apply {
        name = "Charlie"
        age = 30
        // Object automatically returned
    }
    
    // Mistake 2: Using apply when you need the result
    println("\n❌ Don't use apply when you need computation result:")
    // val badCalculation = someObject.apply { 
    //     complexCalculation() // Result is lost, object returned instead
    // }
    
    println("✅ Use run instead:")
    val calculation = "123".run {
        toInt() * 2  // Computation result is returned
    }
    println("Calculation result: $calculation")
    
    // Mistake 3: Unnecessary scope functions
    println("\n❌ Don't use scope functions unnecessarily:")
    val unnecessary = "Hello".let { it.uppercase() }  // Just use: "Hello".uppercase()
    
    println("✅ Use scope functions only when they add value:")
    val necessary = "Hello".let { greeting ->
        println("Processing: $greeting")
        greeting.uppercase()
    }
    println("Necessary usage: $necessary")
    
    // Performance considerations
    println("\nPerformance Considerations:")
    println("• Scope functions have minimal overhead")
    println("• Inline functions - no performance cost")
    println("• Choose based on readability, not performance")
    println("• Avoid deep nesting of scope functions")
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of scope functions in real development scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. Android View Configuration (simulated)
    class View {
        var visibility: String = "VISIBLE"
        var backgroundColor: String = "white"
        var padding: Int = 0
        var margin: Int = 0
        
        fun setOnClickListener(listener: () -> Unit) {
            println("Click listener set")
        }
        
        override fun toString() = "View(visibility=$visibility, bg=$backgroundColor, padding=$padding)"
    }
    
    fun setupView(): View {
        return View().apply {
            visibility = "VISIBLE"
            backgroundColor = "blue"
            padding = 16
            margin = 8
            setOnClickListener { println("View clicked!") }
        }
    }
    
    val view = setupView()
    println("1. View setup: $view")
    
    // 2. HTTP Client Configuration
    data class HttpClient(
        var baseUrl: String = "",
        var timeout: Int = 30,
        var retries: Int = 3,
        val headers: MutableMap<String, String> = mutableMapOf()
    ) {
        fun addHeader(key: String, value: String) = headers.put(key, value)
        fun get(path: String): String = "GET $baseUrl$path with headers $headers"
    }
    
    val client = HttpClient().apply {
        baseUrl = "https://api.example.com"
        timeout = 60
        retries = 5
        addHeader("Authorization", "Bearer token123")
        addHeader("Content-Type", "application/json")
    }
    
    val response = client.get("/users")
    println("2. HTTP Client: $response")
    
    // 3. Database Transaction Management
    class DatabaseTransaction {
        private var isActive = false
        private val operations = mutableListOf<String>()
        
        fun begin(): DatabaseTransaction {
            isActive = true
            println("Transaction started")
            return this
        }
        
        fun execute(sql: String): DatabaseTransaction {
            if (!isActive) throw IllegalStateException("Transaction not active")
            operations.add(sql)
            println("Executed: $sql")
            return this
        }
        
        fun commit() {
            if (!isActive) throw IllegalStateException("Transaction not active")
            println("Committed ${operations.size} operations")
            isActive = false
        }
        
        fun rollback() {
            if (!isActive) throw IllegalStateException("Transaction not active")
            println("Rolled back ${operations.size} operations")
            operations.clear()
            isActive = false
        }
    }
    
    val transaction = DatabaseTransaction()
        .apply { begin() }
        .also { println("3. Transaction management:") }
        .apply {
            execute("INSERT INTO users (name) VALUES ('Alice')")
            execute("UPDATE users SET email = 'alice@example.com' WHERE name = 'Alice'")
        }
        .also { it.commit() }
    
    // 4. Configuration File Loading
    data class AppConfig(
        var databaseUrl: String = "",
        var apiKey: String = "",
        var logLevel: String = "INFO",
        var maxConnections: Int = 10
    )
    
    fun loadConfiguration(environment: String): AppConfig? {
        return when (environment) {
            "development" -> AppConfig().apply {
                databaseUrl = "jdbc:h2:mem:devdb"
                apiKey = "dev-api-key"
                logLevel = "DEBUG"
                maxConnections = 5
            }
            "production" -> AppConfig().apply {
                databaseUrl = "jdbc:postgresql://prod-db:5432/app"
                apiKey = System.getenv("PROD_API_KEY") ?: "missing-key"
                logLevel = "WARN"
                maxConnections = 50
            }
            else -> null
        }?.also { config ->
            println("4. Loaded $environment config: $config")
            // Could validate configuration here
        }
    }
    
    val config = loadConfiguration("production")
    
    // 5. Builder Pattern with Validation
    class EmailMessage {
        var to: String = ""
        var from: String = ""
        var subject: String = ""
        var body: String = ""
        val attachments = mutableListOf<String>()
        
        fun addAttachment(filename: String) = attachments.add(filename)
        
        fun validate(): List<String> {
            val errors = mutableListOf<String>()
            if (to.isBlank()) errors.add("'to' is required")
            if (from.isBlank()) errors.add("'from' is required")
            if (subject.isBlank()) errors.add("'subject' is required")
            if (!to.contains("@")) errors.add("'to' must be valid email")
            if (!from.contains("@")) errors.add("'from' must be valid email")
            return errors
        }
        
        fun send(): String {
            return "Email sent from $from to $to: '$subject' with ${attachments.size} attachments"
        }
    }
    
    fun createAndSendEmail(): String? {
        return EmailMessage().apply {
            to = "recipient@example.com"
            from = "sender@example.com" 
            subject = "Important Update"
            body = "This is an important message."
            addAttachment("document.pdf")
            addAttachment("image.jpg")
        }.let { email ->
            val errors = email.validate()
            if (errors.isEmpty()) {
                email.send().also { result ->
                    println("5. Email result: $result")
                }
            } else {
                println("5. Email validation failed: $errors")
                null
            }
        }
    }
    
    createAndSendEmail()
    
    // 6. Caching with Statistics
    class CacheManager<K, V> {
        private val cache = mutableMapOf<K, V>()
        private var hits = 0
        private var misses = 0
        
        fun get(key: K): V? {
            return cache[key]?.also {
                hits++
                println("Cache hit for key: $key")
            } ?: run {
                misses++
                println("Cache miss for key: $key")
                null
            }
        }
        
        fun put(key: K, value: V): V {
            return value.also { 
                cache[key] = it
                println("Cached: $key -> $value")
            }
        }
        
        fun getStats(): String = with(this) {
            val total = hits + misses
            val hitRate = if (total > 0) hits * 100.0 / total else 0.0
            "Cache stats - Hits: $hits, Misses: $misses, Hit rate: ${"%.1f".format(hitRate)}%"
        }
    }
    
    val cache = CacheManager<String, String>()
    
    // Simulate cache operations
    listOf("user:1", "user:2", "user:1", "user:3", "user:1").forEach { key ->
        cache.get(key) ?: cache.put(key, "User data for $key")
    }
    
    println("6. ${cache.getStats()}")
    
    // 7. Fluent API with Scope Functions
    class QueryBuilder {
        private var table: String = ""
        private var fields: MutableList<String> = mutableListOf()
        private var conditions: MutableList<String> = mutableListOf()
        private var orderBy: String = ""
        private var limit: Int? = null
        
        fun select(vararg fields: String) = apply { 
            this.fields.addAll(fields)
        }
        
        fun from(table: String) = apply { 
            this.table = table 
        }
        
        fun where(condition: String) = apply { 
            conditions.add(condition)
        }
        
        fun orderBy(field: String) = apply { 
            this.orderBy = field
        }
        
        fun limit(count: Int) = apply { 
            this.limit = count
        }
        
        fun build(): String = run {
            val fieldList = if (fields.isEmpty()) "*" else fields.joinToString(", ")
            val whereClause = if (conditions.isEmpty()) "" else " WHERE ${conditions.joinToString(" AND ")}"
            val orderClause = if (orderBy.isEmpty()) "" else " ORDER BY $orderBy"
            val limitClause = limit?.let { " LIMIT $it" } ?: ""
            
            "SELECT $fieldList FROM $table$whereClause$orderClause$limitClause"
        }
    }
    
    val query = QueryBuilder()
        .select("name", "email", "age")
        .from("users")
        .where("age > 18")
        .where("active = true")
        .orderBy("name")
        .limit(10)
        .build()
        .also { println("7. Generated query: $it") }
    
    println()
}

/**
 * ## Scope Function Patterns
 * 
 * Common patterns and advanced techniques using scope functions.
 */
fun scopeFunctionPatterns() {
    println("--- Scope Function Patterns ---")
    
    // Pattern 1: Safe Navigation with Multiple Operations
    data class Address(val street: String?, val city: String?, val country: String?)
    data class Person(val name: String?, val address: Address?)
    
    val person: Person? = Person("Alice", Address("123 Main St", "Springfield", "USA"))
    
    val addressInfo = person?.address?.let { address ->
        buildString {
            address.street?.let { append("Street: $it\n") }
            address.city?.let { append("City: $it\n") }
            address.country?.let { append("Country: $it\n") }
        }.takeIf { it.isNotEmpty() } ?: "No address information available"
    } ?: "Person not available"
    
    println("1. Safe Navigation Pattern:")
    println(addressInfo)
    
    // Pattern 2: Conditional Initialization
    data class ServerConfig(
        var host: String = "localhost",
        var port: Int = 8080,
        var ssl: Boolean = false,
        var timeout: Int = 30000
    )
    
    fun createServerConfig(environment: String, useSSL: Boolean = false) = 
        ServerConfig().apply {
            when (environment) {
                "production" -> {
                    host = "prod.example.com"
                    port = if (useSSL) 443 else 80
                    ssl = useSSL
                    timeout = 60000
                }
                "staging" -> {
                    host = "staging.example.com"
                    port = if (useSSL) 443 else 8080
                    ssl = useSSL
                    timeout = 45000
                }
                "development" -> {
                    // Use defaults
                    ssl = false
                    timeout = 10000
                }
            }
        }.also {
            println("2. Conditional Initialization: Created $environment config - $it")
        }
    
    val prodConfig = createServerConfig("production", true)
    
    // Pattern 3: Try-Catch with Scope Functions
    fun safeOperation(input: String): String? {
        return try {
            input.toInt()
        } catch (e: NumberFormatException) {
            null
        }?.let { number ->
            (number * 2).toString()
        }?.also { result ->
            println("3. Safe Operation: '$input' -> '$result'")
        }
    }
    
    listOf("123", "abc", "456").forEach { input ->
        safeOperation(input) ?: println("3. Safe Operation: '$input' -> failed")
    }
    
    // Pattern 4: Resource Management
    class Resource(private val name: String) {
        fun open() = println("Opening resource: $name")
        fun process(): String = "Processed $name"
        fun close() = println("Closing resource: $name")
    }
    
    fun useResource(name: String): String? {
        return Resource(name).let { resource ->
            try {
                resource.open()
                resource.process()
            } catch (e: Exception) {
                println("Error processing resource: ${e.message}")
                null
            } finally {
                resource.close()
            }
        }
    }
    
    val resourceResult = useResource("database-connection")
    println("4. Resource Management result: $resourceResult")
    
    // Pattern 5: Validation Chain
    data class User(val name: String, val email: String, val age: Int)
    
    fun validateUser(name: String, email: String, age: Int): User? {
        return name.takeIf { it.isNotBlank() }?.let { validName ->
            email.takeIf { it.contains("@") }?.let { validEmail ->
                age.takeIf { it >= 18 }?.let { validAge ->
                    User(validName, validEmail, validAge)
                }
            }
        }.also { user ->
            if (user != null) {
                println("5. Validation Chain: Created valid user $user")
            } else {
                println("5. Validation Chain: Validation failed for $name, $email, $age")
            }
        }
    }
    
    validateUser("Alice", "alice@example.com", 25)
    validateUser("", "invalid", 15)
    
    // Pattern 6: State Machine with Scope Functions
    sealed class State {
        object Initial : State()
        data class Processing(val data: String) : State()
        data class Completed(val result: String) : State()
        data class Error(val message: String) : State()
    }
    
    class StateMachine(private var currentState: State = State.Initial) {
        fun process(input: String): StateMachine {
            currentState = when (currentState) {
                is State.Initial -> State.Processing(input)
                is State.Processing -> {
                    if (input.isNotEmpty()) {
                        State.Completed("Processed: ${currentState.data} + $input")
                    } else {
                        State.Error("Empty input")
                    }
                }
                is State.Completed -> State.Error("Already completed")
                is State.Error -> currentState // Stay in error state
            }
            return this
        }
        
        fun getResult(): String = when (val state = currentState) {
            is State.Initial -> "Not started"
            is State.Processing -> "Processing: ${state.data}"
            is State.Completed -> state.result
            is State.Error -> "Error: ${state.message}"
        }
    }
    
    val stateMachine = StateMachine()
        .apply { process("input1") }
        .also { println("6. State Machine: ${it.getResult()}") }
        .apply { process("input2") }
        .also { println("6. State Machine: ${it.getResult()}") }
    
    // Pattern 7: Fluent Configuration DSL
    class DatabaseConnectionBuilder {
        private var host: String = ""
        private var port: Int = 0
        private var database: String = ""
        private var credentials: Pair<String, String>? = null
        private var poolSize: Int = 10
        
        fun host(host: String) = apply { this.host = host }
        fun port(port: Int) = apply { this.port = port }
        fun database(db: String) = apply { this.database = db }
        fun credentials(username: String, password: String) = apply { 
            this.credentials = username to password 
        }
        fun poolSize(size: Int) = apply { this.poolSize = size }
        
        fun build(): String = run {
            val auth = credentials?.let { "${it.first}:${it.second}@" } ?: ""
            "jdbc:postgresql://$auth$host:$port/$database?pool=$poolSize"
        }
    }
    
    fun database(init: DatabaseConnectionBuilder.() -> Unit): String {
        return DatabaseConnectionBuilder().apply(init).build()
    }
    
    val connectionString = database {
        host("localhost")
        port(5432)
        database("myapp")
        credentials("user", "pass")
        poolSize(20)
    }.also { println("7. Fluent DSL: $it") }
    
    // Pattern 8: Error Accumulation
    data class ValidationResult(val errors: List<String>, val value: String?)
    
    fun validateInput(input: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        input.takeIf { it.isNotBlank() } ?: run {
            errors.add("Input cannot be blank")
        }
        
        input.takeIf { it.length >= 3 } ?: run {
            errors.add("Input must be at least 3 characters")
        }
        
        input.takeIf { it.matches(Regex("[a-zA-Z]+")) } ?: run {
            errors.add("Input must contain only letters")
        }
        
        return ValidationResult(
            errors = errors,
            value = if (errors.isEmpty()) input.uppercase() else null
        ).also { result ->
            if (result.errors.isEmpty()) {
                println("8. Error Accumulation: '$input' -> '${result.value}'")
            } else {
                println("8. Error Accumulation: '$input' -> errors: ${result.errors}")
            }
        }
    }
    
    listOf("hello", "hi", "hello123", "").forEach { validateInput(it) }
    
    // Pattern 9: Memoization with Scope Functions
    class MemoizedFunction<T, R>(private val function: (T) -> R) {
        private val cache = mutableMapOf<T, R>()
        
        operator fun invoke(input: T): R {
            return cache.getOrPut(input) {
                function(input)
            }.also {
                println("9. Memoization: f($input) = $it ${if (input in cache) "(cached)" else "(computed)"}")
            }
        }
    }
    
    val fibonacci = MemoizedFunction<Int, Long> { n ->
        when (n) {
            0, 1 -> n.toLong()
            else -> fibonacci(n - 1) + fibonacci(n - 2)
        }
    }
    
    listOf(5, 3, 7, 5).forEach { fibonacci(it) }
    
    println("\nScope Function Patterns Complete!")
    println("These patterns demonstrate the power and flexibility of scope functions")
    println("when combined with other Kotlin features and design patterns.")
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice scope functions:
 * 
 * 1. Create a configuration builder using appropriate scope functions
 * 2. Implement a validation system with error accumulation
 * 3. Build a fluent API for creating SQL queries
 * 4. Create a resource manager with proper cleanup
 * 5. Implement a caching decorator using scope functions
 */

// TODO: Exercise 1 - Configuration Builder
data class WebServerConfig(
    var host: String = "localhost",
    var port: Int = 8080,
    var maxThreads: Int = 100,
    var enableSsl: Boolean = false,
    var sslPort: Int = 8443,
    val routes: MutableList<String> = mutableListOf()
)

fun configureWebServer(init: WebServerConfig.() -> Unit): WebServerConfig {
    // TODO: Use appropriate scope functions to create and configure WebServerConfig
    // Apply the init function and validate the configuration
    // Return the configured instance
    return WebServerConfig()
}

// TODO: Exercise 2 - Validation System
data class UserRegistration(
    val username: String,
    val email: String,
    val password: String,
    val age: Int
)

data class ValidationError(val field: String, val message: String)

class UserValidator {
    // TODO: Implement validation methods using scope functions
    // Use let for null checks, run for complex validations
    // Use also for logging validation steps
    
    fun validate(registration: UserRegistration): Pair<Boolean, List<ValidationError>> {
        // TODO: Validate all fields and accumulate errors
        // Return success flag and list of errors
        return false to emptyList()
    }
    
    private fun validateUsername(username: String): List<ValidationError> {
        // TODO: Validate username rules using scope functions
        return emptyList()
    }
    
    private fun validateEmail(email: String): List<ValidationError> {
        // TODO: Validate email format using scope functions
        return emptyList()
    }
    
    private fun validatePassword(password: String): List<ValidationError> {
        // TODO: Validate password strength using scope functions
        return emptyList()
    }
}

// TODO: Exercise 3 - Fluent SQL Query API
class SqlQueryBuilder {
    // TODO: Build a fluent API for SQL queries using apply/also patterns
    // Support SELECT, FROM, WHERE, JOIN, ORDER BY, LIMIT
    
    fun select(vararg columns: String): SqlQueryBuilder {
        // TODO: Implement using apply
        return this
    }
    
    fun from(table: String): SqlQueryBuilder {
        // TODO: Implement using apply
        return this
    }
    
    fun where(condition: String): SqlQueryBuilder {
        // TODO: Implement using apply
        return this
    }
    
    fun join(table: String, condition: String): SqlQueryBuilder {
        // TODO: Implement using apply
        return this
    }
    
    fun orderBy(column: String, direction: String = "ASC"): SqlQueryBuilder {
        // TODO: Implement using apply
        return this
    }
    
    fun limit(count: Int): SqlQueryBuilder {
        // TODO: Implement using apply
        return this
    }
    
    fun build(): String {
        // TODO: Use run to build the final SQL string
        return ""
    }
}

// TODO: Exercise 4 - Resource Manager
interface Resource {
    fun open()
    fun process(): String
    fun close()
}

class ResourceManager {
    // TODO: Implement resource management using scope functions
    // Use let for null checks, run for processing, also for logging
    
    fun <T> useResource(resource: Resource?, operation: (Resource) -> T): T? {
        // TODO: Safely use resource with proper cleanup
        // Handle null resources and exceptions
        // Ensure resource is always closed
        return null
    }
    
    fun processMultipleResources(resources: List<Resource>): List<String> {
        // TODO: Process multiple resources safely
        // Use appropriate scope functions for batch operations
        return emptyList()
    }
}

// TODO: Exercise 5 - Caching Decorator
class CacheDecorator<K, V>(
    private val loader: (K) -> V,
    private val maxSize: Int = 100
) {
    // TODO: Implement caching with statistics using scope functions
    // Use let for cache hits, run for miss handling, also for statistics
    
    fun get(key: K): V {
        // TODO: Check cache first, load if not found
        // Update statistics using also
        return loader(key)
    }
    
    fun getStats(): Map<String, Any> {
        // TODO: Return cache statistics using with or run
        return emptyMap()
    }
    
    fun clear() {
        // TODO: Clear cache and reset statistics using apply
    }
}