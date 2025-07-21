package com.kotlinmastery.basics.syntax

/**
 * # Null Safety in Kotlin
 * 
 * Kotlin's type system distinguishes between references that can hold null and those that cannot.
 * This helps eliminate the NullPointerException that's common in many programming languages.
 * 
 * ## Learning Objectives
 * - Understand nullable vs non-nullable types
 * - Master safe call operator (?.) and Elvis operator (?:)
 * - Use not-null assertion (!!) appropriately
 * - Handle nullable types in various scenarios
 * - Understand platform types and Java interop
 * 
 * ## Prerequisites: Basic variable declarations and types
 * ## Estimated Time: 3 hours
 */

fun main() {
    println("=== Kotlin Null Safety Demo ===\n")
    
    nullableVsNonNullable()
    safeCallOperator()
    elvisOperator()
    notNullAssertion()
    letFunction()
    nullableCollections()
    platformTypes()
    realWorldScenarios()
}

/**
 * ## Nullable vs Non-Nullable Types
 * 
 * In Kotlin, the type system distinguishes between references that can hold null (nullable)
 * and those that cannot (non-nullable). This distinction is made at compile time.
 */
fun nullableVsNonNullable() {
    println("--- Nullable vs Non-Nullable Types ---")
    
    // Non-nullable types (default)
    val nonNullableString: String = "Hello, Kotlin!"
    val nonNullableInt: Int = 42
    
    // nonNullableString = null  // ❌ Compilation error
    // nonNullableInt = null     // ❌ Compilation error
    
    println("Non-nullable string: $nonNullableString")
    println("Non-nullable int: $nonNullableInt")
    
    // Nullable types (explicitly marked with ?)
    val nullableString: String? = "Hello, Nullable Kotlin!"
    val nullableInt: Int? = 42
    val explicitlyNull: String? = null
    
    println("Nullable string: $nullableString")
    println("Nullable int: $nullableInt")
    println("Explicitly null: $explicitlyNull")
    
    // Checking for null
    if (nullableString != null) {
        // Smart cast: nullableString is automatically cast to String (non-nullable)
        println("Smart cast - string length: ${nullableString.length}")
        println("Smart cast - uppercase: ${nullableString.uppercase()}")
    }
    
    if (explicitlyNull != null) {
        println("This won't execute because explicitlyNull is null")
    } else {
        println("explicitlyNull is indeed null")
    }
    
    // Nullable parameters and return types
    fun greetPerson(name: String?): String {
        return if (name != null) {
            "Hello, $name!"
        } else {
            "Hello, Anonymous!"
        }
    }
    
    println("Greeting with name: ${greetPerson("Alice")}")
    println("Greeting without name: ${greetPerson(null)}")
    
    println()
}

/**
 * ## Safe Call Operator (?.)
 * 
 * The safe call operator performs a call only if the receiver is not null.
 * If the receiver is null, the result is null.
 */
fun safeCallOperator() {
    println("--- Safe Call Operator (?.) ---")
    
    val nullableString: String? = "Kotlin"
    val explicitlyNull: String? = null
    
    // Safe call on non-null value
    val lengthOfString = nullableString?.length
    val uppercaseString = nullableString?.uppercase()
    
    // Safe call on null value
    val lengthOfNull = explicitlyNull?.length
    val uppercaseNull = explicitlyNull?.uppercase()
    
    println("Length of '$nullableString': $lengthOfString")
    println("Uppercase of '$nullableString': $uppercaseString")
    println("Length of null: $lengthOfNull")
    println("Uppercase of null: $uppercaseNull")
    
    // Chaining safe calls
    data class Person(val name: String?, val address: Address?)
    data class Address(val street: String?, val city: String?)
    
    val person1 = Person("Alice", Address("123 Main St", "Springfield"))
    val person2 = Person("Bob", null)
    val person3 = Person(null, Address("456 Oak Ave", null))
    
    // Safe call chaining
    val city1 = person1.address?.city
    val city2 = person2.address?.city
    val city3 = person3.address?.city
    
    println("\nChaining safe calls:")
    println("Person1's city: $city1")
    println("Person2's city: $city2")
    println("Person3's city: $city3")
    
    // Safe calls with method invocations
    val street1Length = person1.address?.street?.length
    val street2Length = person2.address?.street?.length
    
    println("Person1's street length: $street1Length")
    println("Person2's street length: $street2Length")
    
    // Safe calls in collections
    val people = listOf(person1, person2, person3)
    val cities = people.mapNotNull { it.address?.city }
    
    println("All cities: $cities")
    
    println()
}

/**
 * ## Elvis Operator (?:)
 * 
 * The Elvis operator provides a default value when the left-hand side is null.
 * It's called "Elvis" because it looks like Elvis Presley's hair when tilted.
 */
fun elvisOperator() {
    println("--- Elvis Operator (?:) ---")
    
    val nullableString: String? = null
    val nonNullableString: String? = "Kotlin"
    
    // Basic Elvis operator usage
    val result1 = nullableString ?: "Default Value"
    val result2 = nonNullableString ?: "Default Value"
    
    println("Null string with Elvis: $result1")
    println("Non-null string with Elvis: $result2")
    
    // Elvis with expressions
    val length1 = nullableString?.length ?: 0
    val length2 = nonNullableString?.length ?: 0
    
    println("Length of null string (default 0): $length1")
    println("Length of non-null string: $length2")
    
    // Elvis with function calls
    fun getDefaultName() = "Anonymous"
    
    val name1 = nullableString ?: getDefaultName()
    val name2 = nonNullableString ?: getDefaultName()
    
    println("Name1 (with default function): $name1")
    println("Name2 (original value): $name2")
    
    // Elvis with early return
    fun processName(name: String?): String {
        val validName = name ?: return "No name provided"
        return "Processing: $validName"
    }
    
    println("Processing null name: ${processName(null)}")
    println("Processing valid name: ${processName("Alice")}")
    
    // Elvis with throw
    fun requireName(name: String?): String {
        return name ?: throw IllegalArgumentException("Name cannot be null")
    }
    
    try {
        println("Required name: ${requireName("Bob")}")
        println("Required null name: ${requireName(null)}")
    } catch (e: IllegalArgumentException) {
        println("Caught exception: ${e.message}")
    }
    
    // Complex Elvis expressions
    data class User(val firstName: String?, val lastName: String?, val username: String?)
    
    fun getDisplayName(user: User): String {
        return user.firstName?.let { first ->
            user.lastName?.let { last ->
                "$first $last"
            }
        } ?: user.username ?: "Unknown User"
    }
    
    val user1 = User("John", "Doe", "johndoe")
    val user2 = User(null, null, "janedoe")
    val user3 = User(null, null, null)
    
    println("\nDisplay names:")
    println("User1: ${getDisplayName(user1)}")
    println("User2: ${getDisplayName(user2)}")
    println("User3: ${getDisplayName(user3)}")
    
    println()
}

/**
 * ## Not-Null Assertion (!!)
 * 
 * The not-null assertion operator converts any nullable type to non-nullable.
 * Use it carefully - it will throw KotlinNullPointerException if the value is null.
 */
fun notNullAssertion() {
    println("--- Not-Null Assertion (!!) ---")
    
    val nullableString: String? = "Kotlin"
    val explicitlyNull: String? = null
    
    // Safe usage - when you're certain the value is not null
    val definitelyNotNull = nullableString!!
    println("Definitely not null: $definitelyNotNull")
    println("Its length: ${definitelyNotNull.length}")
    
    // Direct usage without intermediate variable
    println("Direct usage: ${nullableString!!.uppercase()}")
    
    // Dangerous usage - will throw exception
    try {
        val thisWillFail = explicitlyNull!!
        println("This line won't execute: $thisWillFail")
    } catch (e: KotlinNullPointerException) {
        println("Caught KotlinNullPointerException: ${e.message}")
    }
    
    // When to use !! (rarely!)
    // 1. After explicit null check
    fun processStringLength(input: String?): Int {
        if (input != null) {
            return input!!.length  // Better to use smart cast, but this works
        }
        return 0
    }
    
    // 2. With platform types (Java interop) when you know the contract
    // val javaString = JavaClass.getNonNullString()  // Platform type String!
    // val kotlinString = javaString!!  // Convert to non-nullable
    
    // 3. In testing when setting up known conditions
    fun testExample() {
        val testData: String? = "test"
        val result = processTestData(testData!!)  // Known to be non-null in test
        println("Test result: $result")
    }
    
    fun processTestData(data: String): String = "Processed: $data"
    
    testExample()
    
    // Better alternatives to !!
    println("\nBetter alternatives to !!:")
    
    // Instead of: explicitlyNull!!.length
    // Use safe call: explicitlyNull?.length
    println("Safe call result: ${explicitlyNull?.length}")
    
    // Instead of: explicitlyNull!!.length
    // Use Elvis: explicitlyNull?.length ?: 0
    println("Elvis result: ${explicitlyNull?.length ?: 0}")
    
    // Instead of: explicitlyNull!!.uppercase()
    // Use let: explicitlyNull?.let { it.uppercase() }
    println("Let result: ${explicitlyNull?.let { it.uppercase() }}")
    
    println()
}

/**
 * ## The let Function
 * 
 * The let function is particularly useful for working with nullable values.
 * It executes the given block only if the receiver is not null.
 */
fun letFunction() {
    println("--- The let Function ---")
    
    val nullableString: String? = "Kotlin"
    val explicitlyNull: String? = null
    
    // Basic let usage
    nullableString?.let { value ->
        println("Processing non-null value: $value")
        println("Its length is: ${value.length}")
        println("Uppercase: ${value.uppercase()}")
    }
    
    explicitlyNull?.let { value ->
        println("This won't execute because value is null")
    }
    
    // let with return value
    val result1 = nullableString?.let { it.length * 2 }
    val result2 = explicitlyNull?.let { it.length * 2 }
    
    println("Result1 (doubled length): $result1")
    println("Result2 (null): $result2")
    
    // let for transforming nullable values
    data class Person(val name: String?, val age: Int?)
    
    val person1 = Person("Alice", 30)
    val person2 = Person(null, 25)
    
    // Transform if name is not null
    val greeting1 = person1.name?.let { name ->
        "Hello, $name! You are ${person1.age ?: "unknown"} years old."
    }
    
    val greeting2 = person2.name?.let { name ->
        "Hello, $name! You are ${person2.age ?: "unknown"} years old."
    }
    
    println("Greeting1: $greeting1")
    println("Greeting2: $greeting2")
    
    // let for avoiding repeated null checks
    fun processUser(user: Person) {
        user.name?.let { name ->
            println("Processing user: $name")
            
            // Within this block, 'name' is guaranteed to be non-null
            if (name.length > 5) {
                println("User has a long name")
            }
            
            user.age?.let { age ->
                // Nested let for multiple nullable properties
                println("User $name is $age years old")
                if (age >= 18) {
                    println("User is an adult")
                }
            }
        }
    }
    
    println("\nProcessing users:")
    processUser(person1)
    processUser(person2)
    
    // let vs if-not-null check
    println("\nComparison: let vs if-not-null")
    
    // Using if-not-null
    if (nullableString != null) {
        val processed = nullableString.uppercase().replace("KOTLIN", "AMAZING")
        println("If-not-null result: $processed")
    }
    
    // Using let (more concise for simple transformations)
    val letResult = nullableString?.let { 
        it.uppercase().replace("KOTLIN", "AMAZING") 
    }
    println("Let result: $letResult")
    
    println()
}

/**
 * ## Nullable Collections
 * 
 * Collections themselves can be nullable, and they can contain nullable elements.
 * Understanding the difference is crucial for safe collection handling.
 */
fun nullableCollections() {
    println("--- Nullable Collections ---")
    
    // Different types of nullable collections
    val nonNullableList: List<String> = listOf("A", "B", "C")
    val nullableList: List<String>? = listOf("A", "B", "C")
    val listOfNullables: List<String?> = listOf("A", null, "C")
    val nullableListOfNullables: List<String?>? = listOf("A", null, "C")
    
    println("Non-nullable list: $nonNullableList")
    println("Nullable list: $nullableList")
    println("List of nullables: $listOfNullables")
    println("Nullable list of nullables: $nullableListOfNullables")
    
    // Working with nullable collections
    val size1 = nullableList?.size ?: 0
    println("Size of nullable list: $size1")
    
    // Filtering out nulls
    val filteredList = listOfNullables.filterNotNull()
    println("Filtered list (nulls removed): $filteredList")
    
    // Safe iteration
    nullableList?.forEach { item ->
        println("Processing item: $item")
    }
    
    listOfNullables.forEach { item ->
        item?.let { nonNullItem ->
            println("Processing non-null item: $nonNullItem")
        }
    }
    
    // Map operations with nullables
    val lengths = listOfNullables.map { it?.length }
    val safeLengths = listOfNullables.mapNotNull { it?.length }
    
    println("Lengths (with nulls): $lengths")
    println("Safe lengths (nulls filtered): $safeLengths")
    
    // Working with maps and nullable values
    val userAges: Map<String, Int?> = mapOf(
        "Alice" to 30,
        "Bob" to null,
        "Charlie" to 25
    )
    
    println("\nUser ages map:")
    userAges.forEach { (name, age) ->
        val ageText = age?.toString() ?: "unknown"
        println("$name: $ageText years old")
    }
    
    // Safe map access
    val aliceAge = userAges["Alice"]
    val davidAge = userAges["David"]  // Key doesn't exist, returns null
    
    println("Alice's age: ${aliceAge ?: "unknown"}")
    println("David's age: ${davidAge ?: "unknown"}")
    
    println()
}

/**
 * ## Platform Types and Java Interop
 * 
 * When working with Java code, Kotlin uses platform types (denoted as Type!)
 * to represent types whose nullability is not known.
 */
fun platformTypes() {
    println("--- Platform Types and Java Interop ---")
    
    // Simulating platform types (normally these come from Java)
    // In real scenarios, these would be returned from Java methods
    
    // Example of what you might get from Java
    // String javaString = getString();  // Could be null or not null
    
    // In Kotlin, this would be a platform type String!
    // You can treat it as nullable or non-nullable
    
    println("Platform types are encountered when calling Java code.")
    println("Java methods don't specify nullability, so Kotlin uses platform types.")
    
    // Strategies for handling platform types:
    
    // 1. Treat as nullable (safe approach)
    fun handleAsNullable(platformString: String?): String {
        return platformString?.uppercase() ?: "DEFAULT"
    }
    
    // 2. Treat as non-nullable (risky but sometimes necessary)
    fun handleAsNonNullable(platformString: String): String {
        return platformString.uppercase()
    }
    
    // 3. Use assertions when you're confident
    fun handleWithAssertion(platformString: String?): String {
        return platformString!!.uppercase()
    }
    
    // Best practices for platform types:
    println("\nBest practices for platform types:")
    println("1. Check Java documentation for nullability contracts")
    println("2. Use @Nullable/@NonNull annotations in Java code")
    println("3. Treat as nullable by default for safety")
    println("4. Add null checks when calling Java methods")
    
    // Example of defensive programming with platform types
    fun safeJavaInterop(javaResult: String?): String {
        return when {
            javaResult == null -> "Java returned null"
            javaResult.isEmpty() -> "Java returned empty string"
            else -> "Java returned: $javaResult"
        }
    }
    
    println("Safe Java interop examples:")
    println(safeJavaInterop(null))
    println(safeJavaInterop(""))
    println(safeJavaInterop("Hello from Java"))
    
    println()
}

/**
 * ## Real-World Scenarios
 * 
 * Practical examples of null safety in common programming scenarios.
 */
fun realWorldScenarios() {
    println("--- Real-World Scenarios ---")
    
    // Scenario 1: User input validation
    fun validateEmail(email: String?): String? {
        return email?.let { 
            if (it.contains("@") && it.contains(".")) {
                it.lowercase()
            } else {
                null
            }
        }
    }
    
    val emails = listOf("user@example.com", null, "invalid-email", "ADMIN@SITE.COM")
    
    println("Email validation:")
    emails.forEach { email ->
        val validated = validateEmail(email)
        println("Input: $email -> Validated: $validated")
    }
    
    // Scenario 2: Configuration with defaults
    data class AppConfig(
        val serverUrl: String?,
        val timeout: Int?,
        val retries: Int?,
        val debugMode: Boolean?
    ) {
        fun getEffectiveConfig(): EffectiveConfig {
            return EffectiveConfig(
                serverUrl = this.serverUrl ?: "https://api.default.com",
                timeout = this.timeout ?: 30,
                retries = this.retries ?: 3,
                debugMode = this.debugMode ?: false
            )
        }
    }
    
    data class EffectiveConfig(
        val serverUrl: String,
        val timeout: Int,
        val retries: Int,
        val debugMode: Boolean
    )
    
    val userConfig = AppConfig("https://custom.api.com", null, 5, null)
    val effectiveConfig = userConfig.getEffectiveConfig()
    
    println("\nConfiguration with defaults:")
    println("User config: $userConfig")
    println("Effective config: $effectiveConfig")
    
    // Scenario 3: Safe data processing pipeline
    data class Product(val id: String?, val name: String?, val price: Double?)
    
    fun processProducts(products: List<Product>): List<String> {
        return products
            .filter { it.id != null && it.name != null && it.price != null }
            .mapNotNull { product ->
                product.name?.let { name ->
                    product.price?.let { price ->
                        if (price > 0) "$name: $${String.format("%.2f", price)}" else null
                    }
                }
            }
    }
    
    val products = listOf(
        Product("1", "Laptop", 999.99),
        Product(null, "Mouse", 25.50),
        Product("3", null, 15.00),
        Product("4", "Keyboard", -10.0),
        Product("5", "Monitor", 350.00)
    )
    
    val processedProducts = processProducts(products)
    
    println("\nProduct processing:")
    println("Original products: $products")
    println("Processed products: $processedProducts")
    
    // Scenario 4: Network response handling
    data class ApiResponse<T>(val data: T?, val error: String?)
    
    fun handleApiResponse(response: ApiResponse<String>): String {
        return response.data?.let { data ->
            "Success: $data"
        } ?: response.error?.let { error ->
            "Error: $error"
        } ?: "Unknown response state"
    }
    
    val successResponse = ApiResponse("User data loaded", null)
    val errorResponse = ApiResponse<String>(null, "Network timeout")
    val unknownResponse = ApiResponse<String>(null, null)
    
    println("\nAPI response handling:")
    println(handleApiResponse(successResponse))
    println(handleApiResponse(errorResponse))
    println(handleApiResponse(unknownResponse))
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice null safety concepts:
 * 
 * 1. Create a function that safely extracts the domain from an email address
 * 2. Implement a user profile merger that handles nullable fields gracefully
 * 3. Build a configuration loader with fallback values for missing settings
 * 4. Create a safe data validator for user registration form
 * 5. Implement a nullable list processor with multiple transformation steps
 */

// TODO: Exercise 1 - Email domain extraction
fun extractEmailDomain(email: String?): String? {
    // TODO: Extract domain part from email (after @)
    // TODO: Return null if email is null or invalid format
    // TODO: Handle edge cases like multiple @ symbols
    return null
}

// TODO: Exercise 2 - Profile merger
data class UserProfile(
    val name: String?,
    val email: String?,
    val phone: String?,
    val address: String?
)

fun mergeProfiles(primary: UserProfile?, secondary: UserProfile?): UserProfile? {
    // TODO: Merge two profiles, preferring primary when both have values
    // TODO: Return null if both profiles are null
    // TODO: Use secondary values only when primary values are null
    return null
}

// TODO: Exercise 3 - Configuration loader
data class DatabaseConfig(
    val host: String?,
    val port: Int?,
    val database: String?,
    val username: String?,
    val password: String?
)

fun loadDatabaseConfig(config: DatabaseConfig?): DatabaseConfig {
    // TODO: Provide default values for null configurations
    // TODO: Default host: "localhost", port: 5432, database: "app", username: "user"
    // TODO: Throw exception if password is null (required field)
    return DatabaseConfig(null, null, null, null, null)
}

// TODO: Exercise 4 - Registration validator
data class RegistrationForm(
    val username: String?,
    val email: String?,
    val password: String?,
    val confirmPassword: String?
)

fun validateRegistration(form: RegistrationForm?): List<String> {
    // TODO: Return list of validation errors
    // TODO: Check for null/empty fields, valid email format, password match
    // TODO: Return empty list if all validations pass
    return emptyList()
}

// TODO: Exercise 5 - List processor
fun processNullableList(input: List<String?>?): List<String> {
    // TODO: Filter out nulls, trim whitespace, convert to uppercase
    // TODO: Remove empty strings after trimming
    // TODO: Return empty list if input is null
    return emptyList()
}