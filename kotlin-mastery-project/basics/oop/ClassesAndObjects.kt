package com.kotlinmastery.basics.oop

/**
 * # Classes and Objects in Kotlin
 * 
 * Object-oriented programming in Kotlin provides a clean and expressive syntax for creating classes,
 * objects, and managing state. This module covers class declaration, constructors, properties, and
 * fundamental OOP concepts.
 * 
 * ## Learning Objectives
 * - Understand class declaration and instantiation
 * - Master primary and secondary constructors
 * - Work with properties, getters, and setters
 * - Use visibility modifiers effectively
 * - Apply object-oriented design principles
 * 
 * ## Prerequisites: Functions and basic programming concepts
 * ## Estimated Time: 5 hours
 */

fun main() {
    println("=== Kotlin Classes and Objects Demo ===\n")
    
    basicClasses()
    constructors()
    properties()
    visibilityModifiers()
    methods()
    companionObjects()
    objectExpressions()
    realWorldExamples()
}

/**
 * ## Basic Classes
 * 
 * Classes in Kotlin are declared using the `class` keyword and can be instantiated without `new`.
 */
fun basicClasses() {
    println("--- Basic Classes ---")
    
    // Simple class with no properties
    class EmptyClass
    
    val emptyInstance = EmptyClass()
    println("Created empty class instance: ${emptyInstance.javaClass.simpleName}")
    
    // Class with primary constructor
    class Person(name: String, age: Int) {
        // Properties must be declared if you want to access them
        val personName = name
        val personAge = age
        
        fun introduce() {
            println("Hi, I'm $personName and I'm $personAge years old")
        }
    }
    
    val person1 = Person("Alice", 30)
    person1.introduce()
    
    // Class with property declarations in constructor
    class Student(val name: String, val age: Int, val studentId: String) {
        fun getInfo(): String {
            return "Student: $name (ID: $studentId), Age: $age"
        }
    }
    
    val student1 = Student("Bob", 20, "STU001")
    println(student1.getInfo())
    println("Student name: ${student1.name}")
    println("Student ID: ${student1.studentId}")
    
    // Class with mutable properties
    class Counter(var count: Int = 0) {
        fun increment() {
            count++
        }
        
        fun decrement() {
            count--
        }
        
        fun reset() {
            count = 0
        }
        
        override fun toString(): String {
            return "Counter(count=$count)"
        }
    }
    
    val counter = Counter(5)
    println("Initial counter: $counter")
    counter.increment()
    counter.increment()
    println("After 2 increments: $counter")
    counter.decrement()
    println("After 1 decrement: $counter")
    counter.reset()
    println("After reset: $counter")
    
    // Class with computed properties
    class Rectangle(val width: Double, val height: Double) {
        val area: Double
            get() = width * height
        
        val perimeter: Double
            get() = 2 * (width + height)
        
        val isSquare: Boolean
            get() = width == height
        
        fun describe(): String {
            val shape = if (isSquare) "square" else "rectangle"
            return "This $shape has area $area and perimeter $perimeter"
        }
    }
    
    val rectangle = Rectangle(5.0, 3.0)
    val square = Rectangle(4.0, 4.0)
    
    println("\n${rectangle.describe()}")
    println(square.describe())
    
    // Class inheritance preview
    open class Vehicle(val brand: String, val model: String) {
        open fun start() {
            println("$brand $model is starting...")
        }
        
        open fun stop() {
            println("$brand $model is stopping...")
        }
    }
    
    class Car(brand: String, model: String, val doors: Int) : Vehicle(brand, model) {
        override fun start() {
            println("Car $brand $model with $doors doors is starting the engine...")
        }
    }
    
    val vehicle = Vehicle("Generic", "Vehicle")
    val car = Car("Toyota", "Camry", 4)
    
    println("\nVehicle operations:")
    vehicle.start()
    vehicle.stop()
    
    println("\nCar operations:")
    car.start()
    car.stop()
    
    println()
}

/**
 * ## Constructors
 * 
 * Kotlin supports primary constructors (in class header) and secondary constructors.
 */
fun constructors() {
    println("--- Constructors ---")
    
    // Primary constructor with default values
    class User(
        val username: String,
        val email: String,
        val isActive: Boolean = true,
        val role: String = "user"
    ) {
        init {
            // Primary constructor initialization block
            println("Creating user: $username")
            require(username.isNotBlank()) { "Username cannot be blank" }
            require(email.contains("@")) { "Email must contain @" }
        }
        
        fun displayInfo() {
            val status = if (isActive) "active" else "inactive"
            println("User: $username ($email) - Role: $role, Status: $status")
        }
    }
    
    val user1 = User("alice", "alice@example.com")
    val user2 = User("bob", "bob@test.com", false, "admin")
    
    user1.displayInfo()
    user2.displayInfo()
    
    // Secondary constructors
    class Product {
        val name: String
        val price: Double
        val category: String
        val description: String
        
        // Primary constructor
        constructor(name: String, price: Double, category: String) {
            this.name = name
            this.price = price
            this.category = category
            this.description = "No description available"
            println("Product created: $name")
        }
        
        // Secondary constructor calling primary
        constructor(name: String, price: Double, category: String, description: String) 
            : this(name, price, category) {
            // Cannot modify val properties in secondary constructor body
            // this.description = description  // This would cause an error
            println("Description added: $description")
        }
        
        fun getInfo(): String {
            return "$name - $$price ($category): $description"
        }
    }
    
    // Better approach: class with primary constructor and default parameter
    class BetterProduct(
        val name: String,
        val price: Double,
        val category: String,
        val description: String = "No description available"
    ) {
        init {
            println("Better product created: $name")
        }
        
        // Secondary constructor for legacy compatibility
        constructor(name: String, price: Double) : this(name, price, "General", "No description")
        
        fun getInfo(): String {
            return "$name - $$price ($category): $description"
        }
    }
    
    println("\nProduct creation:")
    val product1 = BetterProduct("Laptop", 999.99, "Electronics")
    val product2 = BetterProduct("Book", 29.99, "Education", "Programming guide")
    val product3 = BetterProduct("Gadget", 49.99)  // Using secondary constructor
    
    println(product1.getInfo())
    println(product2.getInfo())
    println(product3.getInfo())
    
    // Class with complex initialization
    class DatabaseConnection(
        val host: String,
        val port: Int,
        val database: String,
        username: String,
        password: String
    ) {
        private val connectionString: String
        val isConnected: Boolean
        
        init {
            // Complex initialization logic
            connectionString = buildConnectionString(host, port, database, username, password)
            isConnected = attemptConnection()
            
            if (isConnected) {
                println("✅ Connected to database: $database@$host:$port")
            } else {
                println("❌ Failed to connect to database: $database@$host:$port")
            }
        }
        
        private fun buildConnectionString(host: String, port: Int, db: String, user: String, pass: String): String {
            // Simulate connection string building (don't log password!)
            return "jdbc:postgresql://$host:$port/$db?user=$user&password=***"
        }
        
        private fun attemptConnection(): Boolean {
            // Simulate connection attempt
            return host.isNotBlank() && port > 0 && database.isNotBlank()
        }
        
        fun executeQuery(sql: String): String {
            return if (isConnected) {
                "Executing: $sql"
            } else {
                "Error: Not connected to database"
            }
        }
    }
    
    println("\nDatabase connections:")
    val validDb = DatabaseConnection("localhost", 5432, "myapp", "user", "password")
    val invalidDb = DatabaseConnection("", 0, "", "", "")
    
    println(validDb.executeQuery("SELECT * FROM users"))
    println(invalidDb.executeQuery("SELECT * FROM users"))
    
    println()
}

/**
 * ## Properties
 * 
 * Properties in Kotlin can have custom getters and setters, backing fields, and delegation.
 */
fun properties() {
    println("--- Properties ---")
    
    // Properties with custom getters and setters
    class Temperature {
        var celsius: Double = 0.0
            set(value) {
                if (value < -273.15) {
                    throw IllegalArgumentException("Temperature cannot be below absolute zero")
                }
                field = value
            }
        
        var fahrenheit: Double
            get() = celsius * 9.0 / 5.0 + 32.0
            set(value) {
                celsius = (value - 32.0) * 5.0 / 9.0
            }
        
        val kelvin: Double
            get() = celsius + 273.15
        
        val description: String
            get() = when {
                celsius < 0 -> "Freezing"
                celsius < 10 -> "Cold"
                celsius < 20 -> "Cool"
                celsius < 30 -> "Warm"
                else -> "Hot"
            }
    }
    
    val temp = Temperature()
    temp.celsius = 25.0
    println("Temperature: ${temp.celsius}°C = ${temp.fahrenheit}°F = ${temp.kelvin}K (${temp.description})")
    
    temp.fahrenheit = 68.0
    println("After setting Fahrenheit: ${temp.celsius}°C = ${temp.fahrenheit}°F")
    
    // Properties with backing fields
    class BankAccount(initialBalance: Double) {
        var balance: Double = initialBalance
            private set  // Only class can modify balance
        
        private var _transactionHistory = mutableListOf<String>()
        
        val transactionHistory: List<String>
            get() = _transactionHistory.toList()  // Return immutable copy
        
        fun deposit(amount: Double) {
            require(amount > 0) { "Deposit amount must be positive" }
            balance += amount
            _transactionHistory.add("Deposited: $$amount")
        }
        
        fun withdraw(amount: Double): Boolean {
            require(amount > 0) { "Withdrawal amount must be positive" }
            
            return if (balance >= amount) {
                balance -= amount
                _transactionHistory.add("Withdrew: $$amount")
                true
            } else {
                _transactionHistory.add("Failed withdrawal: $$amount (insufficient funds)")
                false
            }
        }
        
        val accountSummary: String
            get() = "Balance: $$balance, Transactions: ${_transactionHistory.size}"
    }
    
    val account = BankAccount(1000.0)
    println("\nBank account operations:")
    println("Initial: ${account.accountSummary}")
    
    account.deposit(500.0)
    println("After deposit: ${account.accountSummary}")
    
    val withdrawalSuccess = account.withdraw(200.0)
    println("Withdrawal success: $withdrawalSuccess, ${account.accountSummary}")
    
    val failedWithdrawal = account.withdraw(2000.0)
    println("Failed withdrawal: $failedWithdrawal, ${account.accountSummary}")
    
    println("Transaction history:")
    account.transactionHistory.forEach { transaction ->
        println("  - $transaction")
    }
    
    // Late-initialized properties
    class DataProcessor {
        lateinit var configuration: Map<String, String>
        
        fun configure(config: Map<String, String>) {
            configuration = config
            println("Configuration loaded with ${config.size} settings")
        }
        
        fun process(data: String): String {
            // Check if configuration is initialized
            if (!::configuration.isInitialized) {
                throw IllegalStateException("Configuration not loaded")
            }
            
            val prefix = configuration["prefix"] ?: "PROCESSED"
            val suffix = configuration["suffix"] ?: ""
            
            return "$prefix: $data $suffix".trim()
        }
        
        val isConfigured: Boolean
            get() = ::configuration.isInitialized
    }
    
    val processor = DataProcessor()
    println("\nData processor:")
    println("Is configured: ${processor.isConfigured}")
    
    try {
        processor.process("test data")
    } catch (e: IllegalStateException) {
        println("Error: ${e.message}")
    }
    
    processor.configure(mapOf("prefix" to "RESULT", "suffix" to "[DONE]"))
    println("Is configured: ${processor.isConfigured}")
    println("Processed: ${processor.process("test data")}")
    
    // Lazy properties
    class ExpensiveResource {
        val expensiveProperty: String by lazy {
            println("Computing expensive property...")
            Thread.sleep(1000)  // Simulate expensive computation
            "Expensive result computed at ${System.currentTimeMillis()}"
        }
        
        val anotherExpensiveProperty: List<Int> by lazy {
            println("Computing another expensive property...")
            (1..1000000).toList()
        }
    }
    
    val resource = ExpensiveResource()
    println("\nLazy properties:")
    println("Resource created (lazy properties not computed yet)")
    
    println("Accessing expensive property:")
    println(resource.expensiveProperty)
    
    println("Accessing expensive property again (cached):")
    println(resource.expensiveProperty)
    
    println("List size: ${resource.anotherExpensiveProperty.size}")
    
    println()
}

/**
 * ## Visibility Modifiers
 * 
 * Kotlin provides four visibility modifiers: public, private, protected, and internal.
 */
fun visibilityModifiers() {
    println("--- Visibility Modifiers ---")
    
    // Class demonstrating different visibility levels
    open class BaseService {
        public val publicProperty = "Everyone can see this"
        private val privateProperty = "Only this class can see this"
        protected val protectedProperty = "This class and subclasses can see this"
        internal val internalProperty = "Same module can see this"
        
        public fun publicMethod() = "Public method result"
        private fun privateMethod() = "Private method result"
        protected fun protectedMethod() = "Protected method result"
        internal fun internalMethod() = "Internal method result"
        
        fun demonstrateAccess() {
            println("From BaseService:")
            println("  Public: $publicProperty")
            println("  Private: $privateProperty")
            println("  Protected: $protectedProperty")
            println("  Internal: $internalProperty")
            println("  Private method: ${privateMethod()}")
        }
    }
    
    class DerivedService : BaseService() {
        fun demonstrateDerivedAccess() {
            println("From DerivedService:")
            println("  Public: $publicProperty")
            // println("  Private: $privateProperty")  // ❌ Not accessible
            println("  Protected: $protectedProperty")  // ✅ Accessible in subclass
            println("  Internal: $internalProperty")
            println("  Protected method: ${protectedMethod()}")
            // println("  Private method: ${privateMethod()}")  // ❌ Not accessible
        }
    }
    
    val baseService = BaseService()
    val derivedService = DerivedService()
    
    baseService.demonstrateAccess()
    derivedService.demonstrateDerivedAccess()
    
    // External access
    println("\nExternal access to BaseService:")
    println("  Public: ${baseService.publicProperty}")
    // println("  Private: ${baseService.privateProperty}")    // ❌ Not accessible
    // println("  Protected: ${baseService.protectedProperty}") // ❌ Not accessible
    println("  Internal: ${baseService.internalProperty}")     // ✅ Same module
    
    // Practical example: API with controlled access
    class UserManager {
        private val users = mutableMapOf<String, UserData>()
        private var nextId = 1
        
        private data class UserData(
            val id: Int,
            val username: String,
            val email: String,
            var isActive: Boolean = true
        )
        
        fun createUser(username: String, email: String): Int {
            val userId = nextId++
            val userData = UserData(userId, username, email)
            users[username] = userData
            return userId
        }
        
        fun getUserInfo(username: String): String? {
            val user = users[username]
            return user?.let { "User: ${it.username} (${it.email}) - Active: ${it.isActive}" }
        }
        
        fun deactivateUser(username: String): Boolean {
            val user = users[username]
            return if (user != null) {
                user.isActive = false
                true
            } else {
                false
            }
        }
        
        internal fun getAllUsers(): List<String> {
            // Internal method for administrative purposes
            return users.values.map { "${it.username} (ID: ${it.id})" }
        }
        
        private fun validateEmail(email: String): Boolean {
            return email.contains("@") && email.contains(".")
        }
        
        val userCount: Int
            get() = users.size
    }
    
    val userManager = UserManager()
    
    println("\nUser management:")
    val userId1 = userManager.createUser("alice", "alice@example.com")
    val userId2 = userManager.createUser("bob", "bob@test.com")
    
    println("Created user Alice with ID: $userId1")
    println("Created user Bob with ID: $userId2")
    println("Total users: ${userManager.userCount}")
    
    println("Alice info: ${userManager.getUserInfo("alice")}")
    
    userManager.deactivateUser("alice")
    println("Alice after deactivation: ${userManager.getUserInfo("alice")}")
    
    // Internal access (same module)
    println("All users: ${userManager.getAllUsers()}")
    
    println()
}

/**
 * ## Methods
 * 
 * Methods in classes can be instance methods, extension methods, or have special behaviors.
 */
fun methods() {
    println("--- Methods ---")
    
    // Class with various method types
    class Calculator {
        private var memory: Double = 0.0
        
        // Basic methods
        fun add(a: Double, b: Double): Double = a + b
        fun subtract(a: Double, b: Double): Double = a - b
        fun multiply(a: Double, b: Double): Double = a * b
        
        fun divide(a: Double, b: Double): Double {
            require(b != 0.0) { "Division by zero is not allowed" }
            return a / b
        }
        
        // Methods with default parameters
        fun power(base: Double, exponent: Double = 2.0): Double {
            return Math.pow(base, exponent)
        }
        
        // Method with vararg
        fun sum(vararg numbers: Double): Double {
            return numbers.sum()
        }
        
        // Methods working with instance state
        fun storeInMemory(value: Double) {
            memory = value
            println("Stored $value in memory")
        }
        
        fun recallFromMemory(): Double {
            println("Recalled $memory from memory")
            return memory
        }
        
        fun clearMemory() {
            memory = 0.0
            println("Memory cleared")
        }
        
        // Method returning multiple values using data class
        data class DivisionResult(val quotient: Double, val remainder: Double)
        
        fun divideWithRemainder(dividend: Double, divisor: Double): DivisionResult {
            require(divisor != 0.0) { "Division by zero is not allowed" }
            val quotient = dividend / divisor
            val remainder = dividend % divisor
            return DivisionResult(quotient, remainder)
        }
        
        // Method with function parameter
        fun applyOperation(a: Double, b: Double, operation: (Double, Double) -> Double): Double {
            return operation(a, b)
        }
    }
    
    val calc = Calculator()
    
    println("Basic operations:")
    println("5 + 3 = ${calc.add(5.0, 3.0)}")
    println("5 - 3 = ${calc.subtract(5.0, 3.0)}")
    println("5 * 3 = ${calc.multiply(5.0, 3.0)}")
    println("5 / 3 = ${calc.divide(5.0, 3.0)}")
    
    println("\nPower operations:")
    println("2^3 = ${calc.power(2.0, 3.0)}")
    println("5^2 (default) = ${calc.power(5.0)}")
    
    println("\nSum with varargs:")
    println("Sum of 1,2,3,4,5 = ${calc.sum(1.0, 2.0, 3.0, 4.0, 5.0)}")
    
    println("\nMemory operations:")
    calc.storeInMemory(42.0)
    val memoryValue = calc.recallFromMemory()
    calc.clearMemory()
    
    println("\nDivision with remainder:")
    val result = calc.divideWithRemainder(17.0, 5.0)
    println("17 ÷ 5 = ${result.quotient} remainder ${result.remainder}")
    
    println("\nFunction parameter:")
    val customOp = calc.applyOperation(8.0, 3.0) { a, b -> (a + b) * (a - b) }
    println("(8+3) * (8-3) = $customOp")
    
    // Method overloading
    class Formatter {
        fun format(value: Int): String = "Integer: $value"
        fun format(value: Double): String = "Double: ${String.format("%.2f", value)}"
        fun format(value: String): String = "String: '$value'"
        fun format(value: Boolean): String = "Boolean: $value"
        
        // Method with different parameter counts
        fun format(value: Int, prefix: String): String = "$prefix: $value"
        fun format(value: Int, prefix: String, suffix: String): String = "$prefix: $value $suffix"
    }
    
    val formatter = Formatter()
    
    println("\nMethod overloading:")
    println(formatter.format(42))
    println(formatter.format(3.14159))
    println(formatter.format("Hello"))
    println(formatter.format(true))
    println(formatter.format(100, "Count"))
    println(formatter.format(100, "Count", "(items)"))
    
    // Infix methods
    class Point(val x: Double, val y: Double) {
        infix fun distanceTo(other: Point): Double {
            val dx = x - other.x
            val dy = y - other.y
            return Math.sqrt(dx * dx + dy * dy)
        }
        
        infix fun isCloseTo(other: Point): Boolean {
            return (this distanceTo other) < 1.0
        }
        
        override fun toString(): String = "($x, $y)"
    }
    
    val point1 = Point(0.0, 0.0)
    val point2 = Point(3.0, 4.0)
    val point3 = Point(0.5, 0.5)
    
    println("\nInfix methods:")
    println("Distance from $point1 to $point2: ${point1 distanceTo point2}")
    println("Is $point1 close to $point3? ${point1 isCloseTo point3}")
    println("Is $point1 close to $point2? ${point1 isCloseTo point2}")
    
    println()
}

/**
 * ## Companion Objects
 * 
 * Companion objects provide a way to create static-like members in Kotlin classes.
 */
fun companionObjects() {
    println("--- Companion Objects ---")
    
    // Basic companion object
    class MathUtils {
        companion object {
            const val PI = 3.14159
            const val E = 2.71828
            
            fun max(a: Int, b: Int): Int = if (a > b) a else b
            fun min(a: Int, b: Int): Int = if (a < b) a else b
            
            fun factorial(n: Int): Long {
                require(n >= 0) { "Factorial is not defined for negative numbers" }
                return if (n <= 1) 1 else n * factorial(n - 1)
            }
        }
    }
    
    // Access companion object members without creating an instance
    println("Math constants:")
    println("PI = ${MathUtils.PI}")
    println("E = ${MathUtils.E}")
    
    println("\nMath operations:")
    println("Max of 5 and 8: ${MathUtils.max(5, 8)}")
    println("Min of 5 and 8: ${MathUtils.min(5, 8)}")
    println("Factorial of 5: ${MathUtils.factorial(5)}")
    
    // Named companion object
    class Logger {
        companion object Factory {
            private val loggers = mutableMapOf<String, Logger>()
            
            fun getLogger(name: String): Logger {
                return loggers.getOrPut(name) { Logger(name) }
            }
            
            fun getAllLoggers(): List<String> {
                return loggers.keys.toList()
            }
        }
        
        private val name: String
        private val messages = mutableListOf<String>()
        
        private constructor(name: String) {
            this.name = name
        }
        
        fun log(message: String) {
            val timestamp = System.currentTimeMillis()
            val logEntry = "[$timestamp] [$name] $message"
            messages.add(logEntry)
            println(logEntry)
        }
        
        fun getHistory(): List<String> = messages.toList()
    }
    
    println("\nLogger factory:")
    val appLogger = Logger.getLogger("APP")
    val dbLogger = Logger.getLogger("DATABASE")
    val sameAppLogger = Logger.getLogger("APP")  // Should return the same instance
    
    appLogger.log("Application started")
    dbLogger.log("Database connected")
    sameAppLogger.log("User logged in")  // Same as appLogger
    
    println("All logger names: ${Logger.getAllLoggers()}")
    println("App logger history size: ${appLogger.getHistory().size}")
    
    // Companion object with interface implementation
    interface JsonSerializable {
        fun toJson(): String
    }
    
    class User(val id: Int, val name: String, val email: String) : JsonSerializable {
        override fun toJson(): String {
            return """{"id":$id,"name":"$name","email":"$email"}"""
        }
        
        companion object : JsonSerializable {
            val ANONYMOUS = User(0, "Anonymous", "anonymous@example.com")
            
            fun fromJson(json: String): User {
                // Simplified JSON parsing (in real code, use a proper JSON library)
                val id = Regex("\"id\":(\\d+)").find(json)?.groupValues?.get(1)?.toInt() ?: 0
                val name = Regex("\"name\":\"([^\"]+)\"").find(json)?.groupValues?.get(1) ?: ""
                val email = Regex("\"email\":\"([^\"]+)\"").find(json)?.groupValues?.get(1) ?: ""
                return User(id, name, email)
            }
            
            override fun toJson(): String {
                return """{"class":"User","version":"1.0"}"""
            }
        }
    }
    
    println("\nUser serialization:")
    val user = User(1, "Alice", "alice@example.com")
    val json = user.toJson()
    println("User JSON: $json")
    
    val deserializedUser = User.fromJson(json)
    println("Deserialized: ${deserializedUser.name} (${deserializedUser.email})")
    
    println("Anonymous user: ${User.ANONYMOUS.name}")
    println("Class metadata: ${User.toJson()}")
    
    // Companion object with factory methods
    class DatabaseConnection private constructor(
        private val host: String,
        private val port: Int,
        private val database: String
    ) {
        companion object {
            fun createLocal(database: String): DatabaseConnection {
                return DatabaseConnection("localhost", 5432, database)
            }
            
            fun createRemote(host: String, port: Int, database: String): DatabaseConnection {
                require(host.isNotBlank()) { "Host cannot be blank" }
                require(port in 1..65535) { "Port must be between 1 and 65535" }
                return DatabaseConnection(host, port, database)
            }
            
            fun createFromUrl(url: String): DatabaseConnection {
                // Simplified URL parsing
                val regex = Regex("jdbc:postgresql://([^:]+):(\\d+)/(.+)")
                val match = regex.find(url) ?: throw IllegalArgumentException("Invalid URL format")
                
                val host = match.groupValues[1]
                val port = match.groupValues[2].toInt()
                val database = match.groupValues[3]
                
                return DatabaseConnection(host, port, database)
            }
        }
        
        fun getConnectionInfo(): String {
            return "Connected to $database at $host:$port"
        }
    }
    
    println("\nDatabase connection factory:")
    val localDb = DatabaseConnection.createLocal("myapp")
    val remoteDb = DatabaseConnection.createRemote("db.example.com", 5432, "production")
    val urlDb = DatabaseConnection.createFromUrl("jdbc:postgresql://staging.db:5432/staging_db")
    
    println(localDb.getConnectionInfo())
    println(remoteDb.getConnectionInfo())
    println(urlDb.getConnectionInfo())
    
    println()
}

/**
 * ## Object Expressions
 * 
 * Object expressions create anonymous objects that implement interfaces or extend classes.
 */
fun objectExpressions() {
    println("--- Object Expressions ---")
    
    // Anonymous object implementing interface
    interface EventListener {
        fun onEvent(event: String)
    }
    
    class EventPublisher {
        private val listeners = mutableListOf<EventListener>()
        
        fun addEventListener(listener: EventListener) {
            listeners.add(listener)
        }
        
        fun publishEvent(event: String) {
            listeners.forEach { it.onEvent(event) }
        }
    }
    
    val publisher = EventPublisher()
    
    // Object expression implementing interface
    val consoleListener = object : EventListener {
        override fun onEvent(event: String) {
            println("Console: Received event '$event'")
        }
    }
    
    val fileListener = object : EventListener {
        private val logFile = mutableListOf<String>()
        
        override fun onEvent(event: String) {
            val timestamp = System.currentTimeMillis()
            val logEntry = "[$timestamp] $event"
            logFile.add(logEntry)
            println("File: Logged event to file (${logFile.size} entries)")
        }
        
        fun getLogEntries(): List<String> = logFile.toList()
    }
    
    publisher.addEventListener(consoleListener)
    publisher.addEventListener(fileListener)
    
    publisher.publishEvent("User logged in")
    publisher.publishEvent("Data saved")
    
    // Accessing additional methods of object expression
    println("File log entries: ${fileListener.getLogEntries().size}")
    
    // Anonymous object extending class
    open class Task(val name: String) {
        open fun execute() {
            println("Executing task: $name")
        }
    }
    
    val customTask = object : Task("Custom Processing") {
        private var progress = 0
        
        override fun execute() {
            println("Starting custom task: $name")
            repeat(3) { step ->
                progress = (step + 1) * 33
                println("Progress: $progress%")
                Thread.sleep(100)  // Simulate work
            }
            println("Custom task completed!")
        }
        
        fun getProgress(): Int = progress
    }
    
    println("\nCustom task execution:")
    customTask.execute()
    println("Final progress: ${customTask.getProgress()}%")
    
    // Anonymous object with properties and methods
    val calculator = object {
        val name = "Anonymous Calculator"
        private var lastResult = 0.0
        
        fun add(a: Double, b: Double): Double {
            lastResult = a + b
            return lastResult
        }
        
        fun multiply(a: Double, b: Double): Double {
            lastResult = a * b
            return lastResult
        }
        
        fun getLastResult(): Double = lastResult
        
        fun reset() {
            lastResult = 0.0
        }
    }
    
    println("\nAnonymous calculator:")
    println("Calculator name: ${calculator.name}")
    println("5 + 3 = ${calculator.add(5.0, 3.0)}")
    println("4 * 6 = ${calculator.multiply(4.0, 6.0)}")
    println("Last result: ${calculator.getLastResult()}")
    
    // Object expression with multiple interfaces
    interface Drawable {
        fun draw()
    }
    
    interface Clickable {
        fun click()
    }
    
    val button = object : Drawable, Clickable {
        private var isPressed = false
        
        override fun draw() {
            val state = if (isPressed) "pressed" else "normal"
            println("Drawing button in $state state")
        }
        
        override fun click() {
            isPressed = !isPressed
            println("Button clicked! Now ${if (isPressed) "pressed" else "released"}")
            draw()
        }
    }
    
    println("\nMulti-interface object:")
    button.draw()
    button.click()
    button.click()
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of classes and objects in real scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. Shopping Cart System
    data class Product(val id: String, val name: String, val price: Double, val category: String)
    
    class ShoppingCart {
        private val items = mutableMapOf<String, Pair<Product, Int>>()  // productId to (product, quantity)
        
        fun addItem(product: Product, quantity: Int = 1) {
            require(quantity > 0) { "Quantity must be positive" }
            
            val currentItem = items[product.id]
            if (currentItem != null) {
                val newQuantity = currentItem.second + quantity
                items[product.id] = Pair(product, newQuantity)
                println("Updated ${product.name}: quantity now $newQuantity")
            } else {
                items[product.id] = Pair(product, quantity)
                println("Added ${product.name} to cart (quantity: $quantity)")
            }
        }
        
        fun removeItem(productId: String) {
            val removed = items.remove(productId)
            if (removed != null) {
                println("Removed ${removed.first.name} from cart")
            } else {
                println("Product not found in cart")
            }
        }
        
        fun updateQuantity(productId: String, newQuantity: Int) {
            require(newQuantity >= 0) { "Quantity cannot be negative" }
            
            val item = items[productId]
            if (item != null) {
                if (newQuantity == 0) {
                    removeItem(productId)
                } else {
                    items[productId] = Pair(item.first, newQuantity)
                    println("Updated ${item.first.name} quantity to $newQuantity")
                }
            } else {
                println("Product not found in cart")
            }
        }
        
        fun getTotal(): Double {
            return items.values.sumOf { (product, quantity) ->
                product.price * quantity
            }
        }
        
        fun getItemCount(): Int {
            return items.values.sumOf { it.second }
        }
        
        fun getItems(): List<Triple<Product, Int, Double>> {
            return items.values.map { (product, quantity) ->
                Triple(product, quantity, product.price * quantity)
            }
        }
        
        fun clear() {
            items.clear()
            println("Cart cleared")
        }
        
        fun checkout(): String {
            if (items.isEmpty()) {
                return "Cannot checkout: cart is empty"
            }
            
            val receipt = buildString {
                appendLine("=== RECEIPT ===")
                getItems().forEach { (product, quantity, subtotal) ->
                    appendLine("${product.name} x$quantity @ $${product.price} = $${"%.2f".format(subtotal)}")
                }
                appendLine("================")
                appendLine("Total: $${"%.2f".format(getTotal())}")
                appendLine("Items: ${getItemCount()}")
            }
            
            clear()
            return receipt
        }
    }
    
    // Shopping cart demonstration
    val laptop = Product("P001", "Gaming Laptop", 1299.99, "Electronics")
    val mouse = Product("P002", "Wireless Mouse", 49.99, "Electronics")
    val book = Product("P003", "Kotlin Programming", 39.99, "Books")
    
    val cart = ShoppingCart()
    
    println("Shopping Cart Demo:")
    cart.addItem(laptop)
    cart.addItem(mouse, 2)
    cart.addItem(book)
    cart.addItem(mouse)  // Should update quantity
    
    println("\nCart summary:")
    println("Total: $${cart.getTotal()}")
    println("Item count: ${cart.getItemCount()}")
    
    cart.updateQuantity("P002", 1)  // Reduce mouse quantity
    
    println("\nFinal receipt:")
    println(cart.checkout())
    
    // 2. User Management System
    enum class UserRole { ADMIN, MODERATOR, USER, GUEST }
    
    class User private constructor(
        val id: String,
        val username: String,
        val email: String,
        var role: UserRole,
        private var _isActive: Boolean
    ) {
        val isActive: Boolean get() = _isActive
        private val loginHistory = mutableListOf<Long>()
        private var lastLoginTime: Long? = null
        
        companion object {
            private var nextId = 1
            private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
            
            fun create(username: String, email: String, role: UserRole = UserRole.USER): User {
                require(username.isNotBlank()) { "Username cannot be blank" }
                require(username.length >= 3) { "Username must be at least 3 characters" }
                require(emailRegex.matches(email)) { "Invalid email format" }
                
                val id = "USR${nextId.toString().padStart(4, '0')}"
                nextId++
                
                return User(id, username, email, role, true)
            }
        }
        
        fun login(): Boolean {
            if (!_isActive) {
                println("Login failed: Account is inactive")
                return false
            }
            
            val currentTime = System.currentTimeMillis()
            loginHistory.add(currentTime)
            lastLoginTime = currentTime
            println("User $username logged in successfully")
            return true
        }
        
        fun deactivate() {
            _isActive = false
            println("User $username has been deactivated")
        }
        
        fun activate() {
            _isActive = true
            println("User $username has been activated")
        }
        
        fun changeRole(newRole: UserRole, authorizedBy: User) {
            require(authorizedBy.role == UserRole.ADMIN) { "Only admins can change user roles" }
            require(authorizedBy.isActive) { "Authorizing admin must be active" }
            
            val oldRole = role
            role = newRole
            println("User $username role changed from $oldRole to $newRole by ${authorizedBy.username}")
        }
        
        fun getLoginStats(): String {
            val totalLogins = loginHistory.size
            val lastLogin = lastLoginTime?.let { "Last login: $it" } ?: "Never logged in"
            return "Total logins: $totalLogins, $lastLogin"
        }
        
        override fun toString(): String {
            val status = if (_isActive) "Active" else "Inactive"
            return "User(id=$id, username=$username, email=$email, role=$role, status=$status)"
        }
    }
    
    class UserManager {
        private val users = mutableMapOf<String, User>()
        
        fun registerUser(username: String, email: String, role: UserRole = UserRole.USER): User? {
            return try {
                // Check if username or email already exists
                val existingUser = users.values.find { 
                    it.username == username || it.email == email 
                }
                
                if (existingUser != null) {
                    println("Registration failed: Username or email already exists")
                    null
                } else {
                    val user = User.create(username, email, role)
                    users[user.id] = user
                    println("User registered: $user")
                    user
                }
            } catch (e: IllegalArgumentException) {
                println("Registration failed: ${e.message}")
                null
            }
        }
        
        fun findUser(identifier: String): User? {
            // Search by ID, username, or email
            return users.values.find { 
                it.id == identifier || it.username == identifier || it.email == identifier 
            }
        }
        
        fun getAllUsers(): List<User> = users.values.toList()
        
        fun getActiveUsers(): List<User> = users.values.filter { it.isActive }
        
        fun getUsersByRole(role: UserRole): List<User> = users.values.filter { it.role == role }
        
        fun generateReport(): String {
            val total = users.size
            val active = getActiveUsers().size
            val byRole = UserRole.values().associate { role ->
                role to getUsersByRole(role).size
            }
            
            return buildString {
                appendLine("=== USER REPORT ===")
                appendLine("Total users: $total")
                appendLine("Active users: $active")
                appendLine("Inactive users: ${total - active}")
                appendLine("By role:")
                byRole.forEach { (role, count) ->
                    appendLine("  $role: $count")
                }
            }
        }
    }
    
    // User management demonstration
    println("\nUser Management Demo:")
    val userManager = UserManager()
    
    // Register users
    val admin = userManager.registerUser("admin", "admin@company.com", UserRole.ADMIN)
    val alice = userManager.registerUser("alice", "alice@example.com", UserRole.USER)
    val bob = userManager.registerUser("bob", "bob@example.com", UserRole.MODERATOR)
    
    // Try to register duplicate
    userManager.registerUser("alice", "alice2@example.com")  // Should fail
    
    // User operations
    alice?.login()
    bob?.login()
    
    // Role changes
    admin?.let { adminUser ->
        alice?.changeRole(UserRole.MODERATOR, adminUser)
    }
    
    // Deactivate user
    bob?.deactivate()
    
    println("\nUser statistics:")
    alice?.let { println("Alice stats: ${it.getLoginStats()}") }
    bob?.let { println("Bob stats: ${it.getLoginStats()}") }
    
    println("\nSystem report:")
    println(userManager.generateReport())
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice classes and objects:
 * 
 * 1. Create a library management system with books, members, and borrowing
 * 2. Build a simple banking system with accounts, transactions, and interest
 * 3. Implement a task management system with projects, tasks, and assignments
 * 4. Create a inventory management system with products, stock, and suppliers
 * 5. Build a course enrollment system with students, courses, and grades
 */

// TODO: Exercise 1 - Library Management System
class Book(
    // TODO: Add properties: id, title, author, isbn, isAvailable
    // TODO: Add methods for checking out and returning
) {
    companion object {
        // TODO: Add factory methods for creating books
    }
}

class LibraryMember(
    // TODO: Add properties: id, name, email, maxBorrowLimit
    // TODO: Track borrowed books and borrowing history
) {
    // TODO: Add methods for borrowing and returning books
}

class Library {
    // TODO: Manage books and members
    // TODO: Handle borrowing/returning operations
    // TODO: Generate reports (overdue books, popular books, etc.)
}

// TODO: Exercise 2 - Banking System
sealed class TransactionType {
    // TODO: Define transaction types (deposit, withdrawal, transfer, etc.)
}

class BankAccount(
    // TODO: Account number, holder name, balance, account type
    // TODO: Transaction history
    // TODO: Interest calculation for savings accounts
) {
    // TODO: Implement deposit, withdraw, transfer methods
    // TODO: Generate account statements
}

class Bank {
    // TODO: Manage multiple accounts
    // TODO: Handle transfers between accounts
    // TODO: Calculate and apply interest
}

// TODO: Exercise 3 - Task Management System
enum class TaskStatus {
    // TODO: Define task statuses
}

enum class Priority {
    // TODO: Define priority levels
}

class Task(
    // TODO: Task properties and methods
) {
    // TODO: Methods for updating status, assigning users, etc.
}

class Project(
    // TODO: Project properties and task management
) {
    // TODO: Methods for adding tasks, tracking progress, etc.
}

// TODO: Exercise 4 - Inventory Management
class Product(
    // TODO: Product details, stock levels, pricing
) {
    // TODO: Methods for updating stock, pricing, etc.
}

class Supplier(
    // TODO: Supplier information and product catalog
) {
    // TODO: Methods for managing supplier relationships
}

class InventoryManager {
    // TODO: Manage products, stock levels, suppliers
    // TODO: Handle stock alerts, reordering, etc.
}

// TODO: Exercise 5 - Course Enrollment System
class Student(
    // TODO: Student information and enrollment history
) {
    // TODO: Methods for enrolling in courses, viewing grades
}

class Course(
    // TODO: Course details, capacity, enrolled students
) {
    // TODO: Methods for managing enrollment, grades
}

class GradeBook {
    // TODO: Manage grades for students in courses
    // TODO: Calculate GPAs, generate transcripts
}