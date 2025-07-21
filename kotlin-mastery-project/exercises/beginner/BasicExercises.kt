/**
 * Beginner Level Exercises - Kotlin Fundamentals
 * 
 * This module contains hands-on exercises for practicing basic Kotlin concepts:
 * - Variables and types
 * - Basic operations and expressions
 * - String manipulation
 * - Null safety
 * - Control flow (if/else, when, loops)
 * - Functions (basic)
 * - Collections (basic operations)
 * 
 * Instructions:
 * 1. Read each exercise description carefully
 * 2. Implement the function to solve the problem
 * 3. Test your solution with the provided test cases
 * 4. Run the main function to see all results
 */

// ================================
// Variables and Types Exercises
// ================================

/**
 * Exercise 1: Variable Declaration and Type Inference
 * 
 * Task: Create variables of different types and demonstrate type inference
 * - Create an integer variable with value 42
 * - Create a double variable with value 3.14
 * - Create a string variable with your name
 * - Create a boolean variable with value true
 * - Create a nullable string variable with null value
 */
fun exercise1_Variables(): String {
    // TODO: Implement the variables as described above
    // Return a formatted string showing all variable values and their types
    
    val integer = 42
    val decimal = 3.14
    val name = "Kotlin Learner"
    val isActive = true
    val nullableString: String? = null
    
    return """
        Integer: $integer (${integer::class.simpleName})
        Decimal: $decimal (${decimal::class.simpleName})
        Name: $name (${name::class.simpleName})
        Boolean: $isActive (${isActive::class.simpleName})
        Nullable: $nullableString (${nullableString?.let { it::class.simpleName } ?: "null"})
    """.trimIndent()
}

/**
 * Exercise 2: Type Conversion
 * 
 * Task: Convert between different numeric types
 * Given a string representation of a number, convert it to Int, Double, and Float
 * Handle conversion errors gracefully
 */
fun exercise2_TypeConversion(numberString: String): Map<String, Any?> {
    // TODO: Convert the string to different numeric types
    // Return a map with keys: "int", "double", "float", "errors"
    
    val results = mutableMapOf<String, Any?>()
    val errors = mutableListOf<String>()
    
    try {
        results["int"] = numberString.toInt()
    } catch (e: NumberFormatException) {
        errors.add("Cannot convert to Int: ${e.message}")
    }
    
    try {
        results["double"] = numberString.toDouble()
    } catch (e: NumberFormatException) {
        errors.add("Cannot convert to Double: ${e.message}")
    }
    
    try {
        results["float"] = numberString.toFloat()
    } catch (e: NumberFormatException) {
        errors.add("Cannot convert to Float: ${e.message}")
    }
    
    results["errors"] = errors
    return results
}

// ================================
// String Manipulation Exercises
// ================================

/**
 * Exercise 3: String Operations
 * 
 * Task: Perform various string operations
 * - Count vowels in the string
 * - Reverse the string
 * - Convert to title case
 * - Remove all spaces
 */
fun exercise3_StringOperations(input: String): Map<String, Any> {
    // TODO: Implement all the string operations
    
    val vowels = "aeiouAEIOU"
    val vowelCount = input.count { it in vowels }
    val reversed = input.reversed()
    val titleCase = input.lowercase().split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
    val noSpaces = input.replace(" ", "")
    
    return mapOf(
        "original" to input,
        "vowelCount" to vowelCount,
        "reversed" to reversed,
        "titleCase" to titleCase,
        "noSpaces" to noSpaces
    )
}

/**
 * Exercise 4: String Template and Formatting
 * 
 * Task: Create formatted strings using string templates
 * Generate a user profile string with the given information
 */
fun exercise4_StringTemplate(
    name: String,
    age: Int,
    city: String,
    salary: Double?
): String {
    // TODO: Create a nicely formatted user profile using string templates
    // Handle the nullable salary appropriately
    
    val salaryText = salary?.let { "${"%.2f".format(it)}" } ?: "Not specified"
    
    return """
        ═══════════════════════════════
                USER PROFILE
        ═══════════════════════════════
        Name: $name
        Age: $age years old
        City: $city
        Salary: $salaryText
        Profile Created: ${java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
        ═══════════════════════════════
    """.trimIndent()
}

// ================================
// Null Safety Exercises
// ================================

/**
 * Exercise 5: Null Safety Operations
 * 
 * Task: Work with nullable values safely
 * - Use safe call operator (?.)
 * - Use Elvis operator (?:)
 * - Use let function for null checks
 */
fun exercise5_NullSafety(
    nullableString: String?,
    nullableNumber: Int?,
    nullableList: List<String>?
): Map<String, Any> {
    // TODO: Safely handle all nullable values and return information about them
    
    val stringLength = nullableString?.length ?: 0
    val numberSquared = nullableNumber?.let { it * it } ?: -1
    val listSize = nullableList?.size ?: 0
    val firstItem = nullableList?.firstOrNull() ?: "No items"
    
    return mapOf(
        "stringLength" to stringLength,
        "numberSquared" to numberSquared,
        "listSize" to listSize,
        "firstItem" to firstItem,
        "hasString" to (nullableString != null),
        "hasNumber" to (nullableNumber != null),
        "hasList" to (nullableList != null)
    )
}

/**
 * Exercise 6: Safe Navigation Chain
 * 
 * Task: Navigate through a chain of nullable objects safely
 */
data class Address(val street: String?, val city: String?, val zipCode: String?)
data class Person(val name: String?, val address: Address?)
data class Company(val name: String?, val ceo: Person?)

fun exercise6_SafeNavigation(company: Company?): String {
    // TODO: Safely extract the CEO's address information
    // Return a formatted string with available information
    
    val companyName = company?.name ?: "Unknown Company"
    val ceoName = company?.ceo?.name ?: "Unknown CEO"
    val street = company?.ceo?.address?.street ?: "Unknown Street"
    val city = company?.ceo?.address?.city ?: "Unknown City"
    val zipCode = company?.ceo?.address?.zipCode ?: "Unknown ZIP"
    
    return """
        Company: $companyName
        CEO: $ceoName
        Address: $street, $city $zipCode
    """.trimIndent()
}

// ================================
// Control Flow Exercises
// ================================

/**
 * Exercise 7: Conditional Logic
 * 
 * Task: Implement a grade calculator
 * - 90-100: A
 * - 80-89: B
 * - 70-79: C
 * - 60-69: D
 * - Below 60: F
 * - Handle invalid scores (negative or > 100)
 */
fun exercise7_GradeCalculator(score: Int): String {
    // TODO: Implement grade calculation logic
    
    return when {
        score < 0 || score > 100 -> "Invalid score: $score"
        score >= 90 -> "A"
        score >= 80 -> "B"
        score >= 70 -> "C"
        score >= 60 -> "D"
        else -> "F"
    }
}

/**
 * Exercise 8: When Expression Advanced
 * 
 * Task: Implement a calculator that handles different operations
 * Support: +, -, *, /, %, ^(power)
 * Handle division by zero and invalid operations
 */
fun exercise8_Calculator(num1: Double, operator: String, num2: Double): String {
    // TODO: Implement calculator logic using when expression
    
    return when (operator) {
        "+" -> (num1 + num2).toString()
        "-" -> (num1 - num2).toString()
        "*" -> (num1 * num2).toString()
        "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Error: Division by zero"
        "%" -> if (num2 != 0.0) (num1 % num2).toString() else "Error: Division by zero"
        "^" -> kotlin.math.pow(num1, num2).toString()
        else -> "Error: Invalid operator '$operator'"
    }
}

// ================================
// Loop Exercises
// ================================

/**
 * Exercise 9: For Loop Variations
 * 
 * Task: Practice different for loop patterns
 * - Count from 1 to n
 * - Count from n down to 1
 * - Count even numbers from 2 to n
 * - Sum of squares from 1 to n
 */
fun exercise9_ForLoops(n: Int): Map<String, Any> {
    // TODO: Implement all the loop variations
    
    val countUp = mutableListOf<Int>()
    for (i in 1..n) {
        countUp.add(i)
    }
    
    val countDown = mutableListOf<Int>()
    for (i in n downTo 1) {
        countDown.add(i)
    }
    
    val evenNumbers = mutableListOf<Int>()
    for (i in 2..n step 2) {
        evenNumbers.add(i)
    }
    
    var sumOfSquares = 0
    for (i in 1..n) {
        sumOfSquares += i * i
    }
    
    return mapOf(
        "countUp" to countUp,
        "countDown" to countDown,
        "evenNumbers" to evenNumbers,
        "sumOfSquares" to sumOfSquares
    )
}

/**
 * Exercise 10: While Loop with Conditions
 * 
 * Task: Find the first n numbers of the Fibonacci sequence
 * Use while loop and stop when you have n numbers
 */
fun exercise10_FibonacciWhile(n: Int): List<Long> {
    // TODO: Generate Fibonacci sequence using while loop
    
    if (n <= 0) return emptyList()
    if (n == 1) return listOf(0L)
    if (n == 2) return listOf(0L, 1L)
    
    val fibonacci = mutableListOf(0L, 1L)
    var count = 2
    
    while (count < n) {
        val next = fibonacci[count - 1] + fibonacci[count - 2]
        fibonacci.add(next)
        count++
    }
    
    return fibonacci
}

// ================================
// Basic Function Exercises
// ================================

/**
 * Exercise 11: Function with Default Parameters
 * 
 * Task: Create a function to format a person's full name
 * Parameters: firstName, lastName, middleName (optional), title (optional)
 * Default title should be empty, middleName should be empty
 */
fun exercise11_FormatName(
    firstName: String,
    lastName: String,
    middleName: String = "",
    title: String = ""
): String {
    // TODO: Format the full name properly
    
    val parts = mutableListOf<String>()
    
    if (title.isNotEmpty()) parts.add(title)
    parts.add(firstName)
    if (middleName.isNotEmpty()) parts.add(middleName)
    parts.add(lastName)
    
    return parts.joinToString(" ")
}

/**
 * Exercise 12: Function with Vararg
 * 
 * Task: Create a function that calculates statistics for a variable number of integers
 * Return: min, max, average, count
 */
fun exercise12_Statistics(vararg numbers: Int): Map<String, Double> {
    // TODO: Calculate statistics for the provided numbers
    
    if (numbers.isEmpty()) {
        return mapOf(
            "min" to 0.0,
            "max" to 0.0,
            "average" to 0.0,
            "count" to 0.0
        )
    }
    
    return mapOf(
        "min" to numbers.minOrNull()!!.toDouble(),
        "max" to numbers.maxOrNull()!!.toDouble(),
        "average" to numbers.average(),
        "count" to numbers.size.toDouble()
    )
}

// ================================
// Basic Collection Exercises
// ================================

/**
 * Exercise 13: List Operations
 * 
 * Task: Perform basic operations on a list of integers
 * - Filter even numbers
 * - Double all numbers
 * - Find numbers greater than a threshold
 * - Sort in descending order
 */
fun exercise13_ListOperations(numbers: List<Int>, threshold: Int): Map<String, List<Int>> {
    // TODO: Implement all list operations
    
    return mapOf(
        "original" to numbers,
        "even" to numbers.filter { it % 2 == 0 },
        "doubled" to numbers.map { it * 2 },
        "aboveThreshold" to numbers.filter { it > threshold },
        "sortedDesc" to numbers.sortedDescending()
    )
}

/**
 * Exercise 14: String List Processing
 * 
 * Task: Process a list of names
 * - Convert all to uppercase
 * - Filter names starting with a specific letter
 * - Find the longest name
 * - Count names with more than n characters
 */
fun exercise14_StringListProcessing(names: List<String>, startLetter: Char, minLength: Int): Map<String, Any> {
    // TODO: Process the string list as specified
    
    return mapOf(
        "uppercase" to names.map { it.uppercase() },
        "startsWith" to names.filter { it.startsWith(startLetter, ignoreCase = true) },
        "longest" to (names.maxByOrNull { it.length } ?: ""),
        "longNames" to names.count { it.length > minLength }
    )
}

/**
 * Exercise 15: Map Operations
 * 
 * Task: Work with a map of student grades
 * - Calculate average grade
 * - Find students with grade above average
 * - Find the student with highest grade
 * - Count students by grade letter (A, B, C, D, F)
 */
fun exercise15_MapOperations(studentGrades: Map<String, Int>): Map<String, Any> {
    // TODO: Process the map of student grades
    
    if (studentGrades.isEmpty()) {
        return mapOf(
            "average" to 0.0,
            "aboveAverage" to emptyList<String>(),
            "topStudent" to "",
            "gradeDistribution" to emptyMap<String, Int>()
        )
    }
    
    val average = studentGrades.values.average()
    val aboveAverage = studentGrades.filter { it.value > average }.keys.toList()
    val topStudent = studentGrades.maxByOrNull { it.value }?.key ?: ""
    
    val gradeDistribution = studentGrades.values.groupingBy { score ->
        when {
            score >= 90 -> "A"
            score >= 80 -> "B"
            score >= 70 -> "C"
            score >= 60 -> "D"
            else -> "F"
        }
    }.eachCount()
    
    return mapOf(
        "average" to average,
        "aboveAverage" to aboveAverage,
        "topStudent" to topStudent,
        "gradeDistribution" to gradeDistribution
    )
}

// ================================
// Test Cases and Main Function
// ================================

fun runBeginnerExercises() {
    println("=".repeat(50))
    println("BEGINNER EXERCISES - KOTLIN FUNDAMENTALS")
    println("=".repeat(50))
    
    // Test Exercise 1
    println("\n1. Variables and Types:")
    println(exercise1_Variables())
    
    // Test Exercise 2
    println("\n2. Type Conversion:")
    println("Valid number: ${exercise2_TypeConversion("123")}")
    println("Invalid number: ${exercise2_TypeConversion("abc")}")
    
    // Test Exercise 3
    println("\n3. String Operations:")
    val stringOps = exercise3_StringOperations("Hello World Kotlin")
    stringOps.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // Test Exercise 4
    println("\n4. String Template:")
    println(exercise4_StringTemplate("Alice Johnson", 28, "New York", 75000.50))
    println(exercise4_StringTemplate("Bob Smith", 35, "Boston", null))
    
    // Test Exercise 5
    println("\n5. Null Safety:")
    val nullSafety = exercise5_NullSafety("Hello", 42, listOf("a", "b", "c"))
    nullSafety.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // Test Exercise 6
    println("\n6. Safe Navigation:")
    val company = Company(
        "Tech Corp",
        Person("John Doe", Address("123 Main St", "Springfield", "12345"))
    )
    println(exercise6_SafeNavigation(company))
    println("With null company:")
    println(exercise6_SafeNavigation(null))
    
    // Test Exercise 7
    println("\n7. Grade Calculator:")
    listOf(95, 85, 75, 65, 55, -10, 105).forEach { score ->
        println("Score $score: ${exercise7_GradeCalculator(score)}")
    }
    
    // Test Exercise 8
    println("\n8. Calculator:")
    listOf(
        Triple(10.0, "+", 5.0),
        Triple(10.0, "-", 3.0),
        Triple(6.0, "*", 7.0),
        Triple(15.0, "/", 3.0),
        Triple(10.0, "/", 0.0),
        Triple(2.0, "^", 3.0),
        Triple(10.0, "x", 5.0)
    ).forEach { (a, op, b) ->
        println("$a $op $b = ${exercise8_Calculator(a, op, b)}")
    }
    
    // Test Exercise 9
    println("\n9. For Loops (n=5):")
    val loopResults = exercise9_ForLoops(5)
    loopResults.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // Test Exercise 10
    println("\n10. Fibonacci (first 10):")
    println(exercise10_FibonacciWhile(10))
    
    // Test Exercise 11
    println("\n11. Format Name:")
    println(exercise11_FormatName("John", "Doe"))
    println(exercise11_FormatName("Jane", "Smith", "Marie"))
    println(exercise11_FormatName("Bob", "Johnson", title = "Dr."))
    println(exercise11_FormatName("Alice", "Brown", "Kay", "Prof."))
    
    // Test Exercise 12
    println("\n12. Statistics:")
    val stats = exercise12_Statistics(1, 5, 3, 9, 2, 8, 4, 7, 6)
    stats.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // Test Exercise 13
    println("\n13. List Operations:")
    val listOps = exercise13_ListOperations(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5)
    listOps.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // Test Exercise 14
    println("\n14. String List Processing:")
    val names = listOf("Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona")
    val stringListOps = exercise14_StringListProcessing(names, 'D', 5)
    stringListOps.forEach { (key, value) ->
        println("$key: $value")
    }
    
    // Test Exercise 15
    println("\n15. Map Operations:")
    val grades = mapOf(
        "Alice" to 95,
        "Bob" to 87,
        "Charlie" to 92,
        "Diana" to 78,
        "Edward" to 84,
        "Fiona" to 96
    )
    val mapOps = exercise15_MapOperations(grades)
    mapOps.forEach { (key, value) ->
        println("$key: $value")
    }
    
    println("\n" + "=".repeat(50))
    println("BEGINNER EXERCISES COMPLETED!")
    println("=".repeat(50))
}

/**
 * Additional Challenge Exercises for Beginners
 * 
 * Try these additional challenges to test your understanding:
 * 
 * Challenge 1: Palindrome Checker
 * Write a function that checks if a string is a palindrome (reads same forwards and backwards)
 * 
 * Challenge 2: Prime Number Checker
 * Write a function that determines if a number is prime
 * 
 * Challenge 3: Word Frequency Counter
 * Count the frequency of each word in a sentence
 * 
 * Challenge 4: Simple Encryption
 * Implement a Caesar cipher (shift each letter by n positions)
 * 
 * Challenge 5: Number Guessing Game
 * Implement logic for a number guessing game with hints
 */

// Challenge implementations (optional)
fun challengePalindromeChecker(text: String): Boolean {
    val cleaned = text.lowercase().replace(Regex("[^a-z0-9]"), "")
    return cleaned == cleaned.reversed()
}

fun challengePrimeChecker(n: Int): Boolean {
    if (n < 2) return false
    if (n == 2) return true
    if (n % 2 == 0) return false
    
    for (i in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
        if (n % i == 0) return false
    }
    return true
}

fun challengeWordFrequency(sentence: String): Map<String, Int> {
    return sentence.lowercase()
        .replace(Regex("[^a-z\\s]"), "")
        .split("\\s+".toRegex())
        .filter { it.isNotEmpty() }
        .groupingBy { it }
        .eachCount()
}

fun challengeCaesarCipher(text: String, shift: Int): String {
    return text.map { char ->
        when {
            char.isLetter() -> {
                val base = if (char.isLowerCase()) 'a' else 'A'
                val shifted = (char - base + shift) % 26
                (base + shifted).toChar()
            }
            else -> char
        }
    }.joinToString("")
}

fun main() {
    runBeginnerExercises()
    
    // Run challenge exercises
    println("\n" + "=".repeat(30))
    println("BONUS CHALLENGES")
    println("=".repeat(30))
    
    println("Palindrome 'racecar': ${challengePalindromeChecker("racecar")}")
    println("Palindrome 'hello': ${challengePalindromeChecker("hello")}")
    
    println("Prime 17: ${challengePrimeChecker(17)}")
    println("Prime 18: ${challengePrimeChecker(18)}")
    
    println("Word frequency: ${challengeWordFrequency("hello world hello kotlin")}")
    
    println("Caesar cipher 'hello' shift 3: ${challengeCaesarCipher("hello", 3)}")
}