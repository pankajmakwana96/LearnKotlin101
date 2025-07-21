/**
 * Kotlin Koans - Introduction Exercises
 * 
 * These exercises are inspired by the official Kotlin Koans and provide
 * hands-on practice with Kotlin fundamentals. Each exercise includes:
 * - A clear problem statement
 * - Step-by-step guidance
 * - Expected output
 * - Solution explanation
 * - Progressive difficulty
 * 
 * Complete each exercise by implementing the required functions.
 * Run the tests to verify your solutions.
 */

// ================================
// Koan 1: Hello, World!
// ================================

/**
 * TASK: Make the function return "Hello, world!"
 * 
 * This is your first Kotlin function. Functions in Kotlin are declared
 * with the 'fun' keyword.
 */
fun helloWorld(): String {
    // TODO: Return "Hello, world!"
    return "Hello, world!"
}

// ================================
// Koan 2: Named Arguments
// ================================

/**
 * TASK: Make the function call compile by using named arguments
 * 
 * Kotlin supports named arguments, which make function calls more readable
 * and allow you to pass arguments in any order.
 */
fun joinOptions(options: Collection<String>) = options.joinToString(prefix = "[", postfix = "]")

fun namedArguments(): String {
    // TODO: Fix the function call using named arguments
    // The joinToString function has parameters: separator, prefix, postfix, limit, truncated, transform
    return listOf(1, 2, 3).joinToString(postfix = "!", prefix = "(", separator = " # ")
}

// ================================  
// Koan 3: Default Arguments
// ================================

/**
 * TASK: Implement the function with default arguments
 * 
 * Default arguments allow you to call functions with fewer parameters.
 */
fun defaultArguments(name: String, greeting: String = "Hello", punctuation: String = "!"): String {
    // TODO: Implement the function to return a greeting
    return "$greeting $name$punctuation"
}

// Test the default arguments
fun testDefaultArguments() {
    println(defaultArguments("World"))                    // Should print: Hello World!
    println(defaultArguments("Kotlin", "Hi"))             // Should print: Hi Kotlin!
    println(defaultArguments("Everyone", "Greetings", ".")) // Should print: Greetings Everyone.
}

// ================================
// Koan 4: Triple-Quoted Strings
// ================================

/**
 * TASK: Use triple-quoted strings to create a multi-line string
 * 
 * Triple-quoted strings allow you to create strings that span multiple lines
 * without escape characters.
 */
fun tripleQuotedStrings(): String {
    val question = "life, the universe, and everything"
    val answer = 42
    
    // TODO: Create a multi-line string using triple quotes
    return """
        |The question about $question
        |is answered by the number $answer.
    """.trimMargin()
}

// ================================
// Koan 5: String Templates
// ================================

/**
 * TASK: Use string templates to format the output
 * 
 * String templates allow you to embed expressions in strings using ${}
 */
fun stringTemplates(name: String, age: Int, city: String): String {
    // TODO: Create a formatted string using string templates
    return "Hello, my name is $name. I am $age years old and I live in $city."
}

fun advancedStringTemplates(): String {
    val numbers = listOf(1, 2, 3, 4, 5)
    // TODO: Use string templates with expressions
    return "The list has ${numbers.size} elements: ${numbers.joinToString()}"
}

// ================================
// Koan 6: Nullable Types
// ================================

/**
 * TASK: Handle nullable types safely
 * 
 * Kotlin's null safety prevents NullPointerExceptions by making nullability
 * explicit in the type system.
 */
fun nullableTypes(str: String?): String {
    // TODO: Handle the nullable string safely and return its length or "null" if it's null
    return if (str != null) {
        "String length: ${str.length}"
    } else {
        "String is null"
    }
}

fun safeCallOperator(str: String?): Int {
    // TODO: Use the safe call operator (?.) to get the string length
    return str?.length ?: 0
}

fun elvisOperator(str: String?): String {
    // TODO: Use the Elvis operator (?:) to provide a default value
    return str ?: "default"
}

// ================================
// Koan 7: Smart Casts
// ================================

/**
 * TASK: Use smart casts to work with Any type
 * 
 * Smart casts automatically cast variables after null or type checks.
 */
fun smartCasts(obj: Any): String {
    // TODO: Use smart casts to handle different types
    return when (obj) {
        is String -> "String: $obj"
        is Int -> "Integer: $obj"
        is Double -> "Double: $obj"
        is Boolean -> "Boolean: $obj"
        else -> "Unknown type: ${obj::class.simpleName}"
    }
}

// ================================
// Koan 8: Extension Functions
// ================================

/**
 * TASK: Create extension functions
 * 
 * Extension functions allow you to add new functions to existing classes.
 */
// TODO: Create an extension function for Int that returns whether the number is even
fun Int.isEven(): Boolean {
    return this % 2 == 0
}

// TODO: Create an extension function for String that counts vowels
fun String.countVowels(): Int {
    return count { it.lowercaseChar() in "aeiou" }
}

// TODO: Create an extension function for List<Int> that returns only even numbers
fun List<Int>.evenNumbers(): List<Int> {
    return filter { it.isEven() }
}

// ================================
// Koan 9: Data Classes
// ================================

/**
 * TASK: Create a data class and use its features
 * 
 * Data classes automatically provide equals(), hashCode(), toString(), copy(), and destructuring.
 */
// TODO: Create a data class Person with name and age properties
data class Person(val name: String, val age: Int)

fun dataClassFeatures() {
    val person1 = Person("Alice", 30)
    val person2 = Person("Bob", 25)
    val person3 = person1.copy(age = 31) // Copy with modification
    
    println("Person 1: $person1")
    println("Person 2: $person2")
    println("Person 3 (copy of 1 with age 31): $person3")
    
    // TODO: Use destructuring declaration
    val (name, age) = person1
    println("Destructured - Name: $name, Age: $age")
    
    // TODO: Compare data classes
    println("person1 == person2: ${person1 == person2}")
    println("person1 == person3: ${person1 == person3}")
}

// ================================
// Koan 10: Sealed Classes
// ================================

/**
 * TASK: Create a sealed class hierarchy
 * 
 * Sealed classes represent restricted class hierarchies where all subclasses
 * are known at compile time.
 */
// TODO: Create a sealed class for mathematical expressions
sealed class Expr
data class Num(val value: Int) : Expr()
data class Sum(val left: Expr, val right: Expr) : Expr()
data class Multiply(val left: Expr, val right: Expr) : Expr()

// TODO: Implement evaluation function using when expression
fun eval(expr: Expr): Int = when (expr) {
    is Num -> expr.value
    is Sum -> eval(expr.left) + eval(expr.right)
    is Multiply -> eval(expr.left) * eval(expr.right)
}

// ================================
// Koan 11: Object Expressions and Declarations
// ================================

/**
 * TASK: Use object expressions and declarations
 * 
 * Objects provide a way to create single instances and anonymous objects.
 */
// TODO: Create an object declaration for a counter
object Counter {
    private var count = 0
    
    fun increment() {
        count++
    }
    
    fun getCount(): Int = count
    
    fun reset() {
        count = 0
    }
}

// TODO: Create a function that returns an object expression
fun createComparator(): Comparator<String> {
    return object : Comparator<String> {
        override fun compare(a: String, b: String): Int {
            return a.length.compareTo(b.length)
        }
    }
}

// ================================
// Koan 12: SAM Conversions
// ================================

/**
 * TASK: Use SAM (Single Abstract Method) conversions
 * 
 * SAM conversions allow you to use lambda expressions for Java interfaces
 * with a single abstract method.
 */
// TODO: Use lambda instead of anonymous object for Runnable
fun createRunnable(message: String): Runnable {
    return Runnable { println("Running: $message") }
}

// TODO: Use lambda for Comparator
fun createStringComparator(): Comparator<String> {
    return Comparator { a, b -> a.compareTo(b, ignoreCase = true) }
}

// ================================
// Koan 13: Scope Functions
// ================================

/**
 * TASK: Use scope functions appropriately
 * 
 * Scope functions (let, run, with, apply, also) provide different ways
 * to execute code blocks in the context of an object.
 */
data class Student(var name: String = "", var grade: Int = 0, var active: Boolean = true)

fun scopeFunctions(): String {
    val result = mutableListOf<String>()
    
    // TODO: Use 'let' to handle nullable values
    val nullableString: String? = "Hello"
    nullableString?.let { 
        result.add("Using let: $it")
    }
    
    // TODO: Use 'run' for scoped computations
    val computation = run {
        val x = 10
        val y = 20
        x * y
    }
    result.add("Using run: $computation")
    
    // TODO: Use 'with' to operate on an object
    val student = Student()
    with(student) {
        name = "Alice"
        grade = 95
        active = true
    }
    result.add("Using with: $student")
    
    // TODO: Use 'apply' to configure an object
    val configuredStudent = Student().apply {
        name = "Bob"
        grade = 87
        active = true
    }
    result.add("Using apply: $configuredStudent")
    
    // TODO: Use 'also' for side effects
    val processedStudent = configuredStudent.also { student ->
        println("Processing student: ${student.name}")
        // Side effect: logging
    }
    result.add("Using also: processed ${processedStudent.name}")
    
    return result.joinToString("\n")
}

// ================================
// Koan 14: Collections
// ================================

/**
 * TASK: Work with collections using functional operations
 * 
 * Kotlin provides rich collection APIs with functional programming support.
 */
fun collectionsIntroduction(): String {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val result = mutableListOf<String>()
    
    // TODO: Filter even numbers
    val evenNumbers = numbers.filter { it % 2 == 0 }
    result.add("Even numbers: $evenNumbers")
    
    // TODO: Map to squares
    val squares = numbers.map { it * it }
    result.add("Squares: $squares")
    
    // TODO: Find the sum
    val sum = numbers.sum()
    result.add("Sum: $sum")
    
    // TODO: Group by even/odd
    val grouped = numbers.groupBy { if (it % 2 == 0) "even" else "odd" }
    result.add("Grouped: $grouped")
    
    // TODO: Find first element greater than 5
    val firstGreaterThanFive = numbers.first { it > 5 }
    result.add("First > 5: $firstGreaterThanFive")
    
    // TODO: Check if all numbers are positive
    val allPositive = numbers.all { it > 0 }
    result.add("All positive: $allPositive")
    
    return result.joinToString("\n")
}

// ================================
// Koan 15: Lambdas
// ================================

/**
 * TASK: Work with lambda expressions
 * 
 * Lambda expressions provide a concise way to represent functions.
 */
fun lambdas(): String {
    val result = mutableListOf<String>()
    
    // TODO: Simple lambda
    val square: (Int) -> Int = { x -> x * x }
    result.add("Square of 5: ${square(5)}")
    
    // TODO: Lambda with multiple parameters
    val add: (Int, Int) -> Int = { a, b -> a + b }
    result.add("Add 3 and 4: ${add(3, 4)}")
    
    // TODO: Lambda with it parameter
    val double: (Int) -> Int = { it * 2 }
    result.add("Double 7: ${double(7)}")
    
    // TODO: Higher-order function
    fun operateOnNumbers(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
        return operation(a, b)
    }
    
    val multiplication = operateOnNumbers(6, 7) { x, y -> x * y }
    result.add("6 * 7 = $multiplication")
    
    // TODO: Function reference
    fun isPositive(x: Int): Boolean = x > 0
    val numbers = listOf(-2, -1, 0, 1, 2)
    val positiveNumbers = numbers.filter(::isPositive)
    result.add("Positive numbers: $positiveNumbers")
    
    return result.joinToString("\n")
}

// ================================
// Test Runner
// ================================

/**
 * Test runner to validate solutions
 */
fun runKoanTests() {
    println("🎯 Running Kotlin Koans Tests")
    println("=" * 40)
    
    // Test Koan 1: Hello World
    assert(helloWorld() == "Hello, world!") { "Koan 1 failed" }
    println("✅ Koan 1: Hello World - PASSED")
    
    // Test Koan 2: Named Arguments  
    assert(namedArguments() == "(1 # 2 # 3)!") { "Koan 2 failed" }
    println("✅ Koan 2: Named Arguments - PASSED")
    
    // Test Koan 3: Default Arguments
    assert(defaultArguments("World") == "Hello World!") { "Koan 3 failed" }
    println("✅ Koan 3: Default Arguments - PASSED")
    
    // Test Koan 4: Triple-Quoted Strings
    val expected = "The question about life, the universe, and everything\nis answered by the number 42."
    assert(tripleQuotedStrings().trim() == expected) { "Koan 4 failed" }
    println("✅ Koan 4: Triple-Quoted Strings - PASSED")
    
    // Test Koan 5: String Templates
    assert(stringTemplates("Alice", 25, "NYC") == "Hello, my name is Alice. I am 25 years old and I live in NYC.") { "Koan 5 failed" }
    println("✅ Koan 5: String Templates - PASSED")
    
    // Test Koan 6: Nullable Types
    assert(nullableTypes("hello") == "String length: 5") { "Koan 6a failed" }
    assert(nullableTypes(null) == "String is null") { "Koan 6b failed" }
    assert(safeCallOperator(null) == 0) { "Koan 6c failed" }
    assert(elvisOperator(null) == "default") { "Koan 6d failed" }
    println("✅ Koan 6: Nullable Types - PASSED")
    
    // Test Koan 7: Smart Casts
    assert(smartCasts("hello") == "String: hello") { "Koan 7a failed" }
    assert(smartCasts(42) == "Integer: 42") { "Koan 7b failed" }
    println("✅ Koan 7: Smart Casts - PASSED")
    
    // Test Koan 8: Extension Functions
    assert(4.isEven() == true) { "Koan 8a failed" }
    assert("hello".countVowels() == 2) { "Koan 8b failed" }
    assert(listOf(1, 2, 3, 4, 5).evenNumbers() == listOf(2, 4)) { "Koan 8c failed" }
    println("✅ Koan 8: Extension Functions - PASSED")
    
    // Test Koan 10: Sealed Classes
    val expr = Sum(Num(5), Multiply(Num(2), Num(3)))
    assert(eval(expr) == 11) { "Koan 10 failed" }
    println("✅ Koan 10: Sealed Classes - PASSED")
    
    println("\n🎉 All Kotlin Koans Tests Passed!")
    println("Great job! You've mastered the Kotlin fundamentals.")
}

// ================================
// Interactive Koan Runner
// ================================

/**
 * Interactive runner for exploring each koan
 */
fun runInteractiveKoans() {
    println("🎓 Interactive Kotlin Koans Explorer")
    println("=" * 40)
    
    val koans = mapOf(
        "1" to "Hello World" to { println(helloWorld()) },
        "2" to "Named Arguments" to { println(namedArguments()) },
        "3" to "Default Arguments" to { testDefaultArguments() },
        "4" to "Triple-Quoted Strings" to { println(tripleQuotedStrings()) },
        "5" to "String Templates" to { 
            println(stringTemplates("John", 30, "Boston"))
            println(advancedStringTemplates())
        },
        "6" to "Nullable Types" to {
            println(nullableTypes("Kotlin"))
            println("Safe call result: ${safeCallOperator(null)}")
            println("Elvis result: ${elvisOperator(null)}")
        },
        "7" to "Smart Casts" to {
            println(smartCasts("Hello"))
            println(smartCasts(42))
            println(smartCasts(3.14))
            println(smartCasts(true))
        },
        "8" to "Extension Functions" to {
            println("4 is even: ${4.isEven()}")
            println("5 is even: ${5.isEven()}")
            println("Vowels in 'hello': ${"hello".countVowels()}")
            println("Even numbers from [1,2,3,4,5,6]: ${listOf(1,2,3,4,5,6).evenNumbers()}")
        },
        "9" to "Data Classes" to { dataClassFeatures() },
        "10" to "Sealed Classes" to {
            val expr1 = Sum(Num(1), Num(2))
            val expr2 = Multiply(Num(3), Num(4))
            val expr3 = Sum(expr1, expr2)
            println("Evaluating (1 + 2) + (3 * 4) = ${eval(expr3)}")
        },
        "11" to "Objects" to {
            Counter.increment()
            Counter.increment()
            println("Counter: ${Counter.getCount()}")
            Counter.reset()
            println("After reset: ${Counter.getCount()}")
        },
        "13" to "Scope Functions" to { println(scopeFunctions()) },
        "14" to "Collections" to { println(collectionsIntroduction()) },
        "15" to "Lambdas" to { println(lambdas()) }
    )
    
    while (true) {
        println("\nAvailable Koans:")
        koans.forEach { (key, title) ->
            println("  $key. ${title.first}")
        }
        println("  test - Run all tests")
        println("  exit - Exit")
        
        print("\nSelect a koan (number): ")
        val input = readlnOrNull()?.trim()
        
        when {
            input == "exit" -> {
                println("Happy coding with Kotlin! 🚀")
                break
            }
            input == "test" -> runKoanTests()
            koans.containsKey(input) -> {
                val (title, demo) = koans[input]!!
                println("\n🎯 Koan $input: $title")
                println("-" * 30)
                demo()
            }
            else -> println("❌ Invalid selection. Please try again.")
        }
    }
}

// ================================
// Main Function
// ================================

fun main() {
    println("Welcome to Kotlin Koans! 🎉")
    println()
    
    print("Would you like to (r)un tests or (e)xplore interactively? [r/e]: ")
    val choice = readlnOrNull()?.trim()?.lowercase()
    
    when (choice) {
        "r", "run", "test" -> runKoanTests()
        "e", "explore", "interactive" -> runInteractiveKoans()
        else -> {
            println("Running tests by default...")
            runKoanTests()
        }
    }
}

/**
 * 🎯 Learning Objectives Covered:
 * 
 * ✅ Basic syntax and function declarations
 * ✅ Named and default parameters
 * ✅ String handling and templates
 * ✅ Null safety and smart casts
 * ✅ Extension functions
 * ✅ Data classes and their features
 * ✅ Sealed classes for type safety
 * ✅ Object expressions and declarations
 * ✅ Scope functions usage
 * ✅ Collection operations
 * ✅ Lambda expressions and higher-order functions
 * 
 * 🚀 Next Steps:
 * - Complete more advanced Kotlin Koans
 * - Practice with real-world projects
 * - Explore coroutines and advanced features
 * - Build applications using these concepts
 */