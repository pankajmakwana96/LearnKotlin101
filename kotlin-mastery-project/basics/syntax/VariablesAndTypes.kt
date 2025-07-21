package com.kotlinmastery.basics.syntax

/**
 * # Variables and Types in Kotlin
 * 
 * This module covers the fundamental concepts of variable declarations and type system in Kotlin.
 * You'll learn about val vs var, type inference, explicit typing, and primitive types.
 * 
 * ## Learning Objectives
 * - Understand the difference between val and var
 * - Master type inference and explicit typing
 * - Work with primitive types and their representations
 * - Use type conversions and casting
 * 
 * ## Prerequisites: Basic programming knowledge
 * ## Estimated Time: 2 hours
 */

fun main() {
    println("=== Kotlin Variables and Types Demo ===\n")
    
    variableDeclarations()
    typeInference()
    primitiveTypes()
    typeConversions()
    stringTypes()
    arrays()
}

/**
 * ## Variable Declarations: val vs var
 * 
 * Kotlin has two keywords for variable declarations:
 * - `val` (value): immutable reference, similar to `final` in Java
 * - `var` (variable): mutable reference
 * 
 * **Best Practice**: Prefer `val` over `var` for immutable data and better code safety.
 */
fun variableDeclarations() {
    println("--- Variable Declarations ---")
    
    // val: immutable reference (read-only)
    val immutableName = "Kotlin"
    println("Immutable name: $immutableName")
    
    // var: mutable reference
    var mutableAge = 25
    println("Initial age: $mutableAge")
    mutableAge = 26  // This is allowed
    println("Updated age: $mutableAge")
    
    // immutableName = "Java"  // ❌ Compilation error - val cannot be reassigned
    
    // val with mutable content
    val mutableList = mutableListOf("Apple", "Banana")
    println("Initial list: $mutableList")
    mutableList.add("Cherry")  // ✅ Content can be modified
    println("Modified list: $mutableList")
    
    // Late initialization
    val lateInitValue: String
    if (mutableAge > 20) {
        lateInitValue = "Adult"
    } else {
        lateInitValue = "Young"
    }
    println("Late initialized: $lateInitValue")
    
    println()
}

/**
 * ## Type Inference
 * 
 * Kotlin has powerful type inference - the compiler can deduce types from context.
 * You can choose between explicit typing and letting the compiler infer types.
 */
fun typeInference() {
    println("--- Type Inference ---")
    
    // Type inferred from literal
    val inferredInt = 42                    // Type: Int
    val inferredDouble = 3.14               // Type: Double
    val inferredString = "Hello"            // Type: String
    val inferredBoolean = true              // Type: Boolean
    
    // Explicit type declaration
    val explicitInt: Int = 42
    val explicitDouble: Double = 3.14
    val explicitString: String = "Hello"
    val explicitBoolean: Boolean = true
    
    println("Inferred types work the same as explicit:")
    println("Inferred int: $inferredInt, Explicit int: $explicitInt")
    
    // When explicit typing is necessary
    val explicitLong: Long = 42             // Without explicit type, would be Int
    val explicitFloat: Float = 3.14f        // Float requires 'f' suffix or explicit type
    
    // Function return type inference
    fun getMultipliedValue() = inferredInt * 2  // Return type inferred as Int
    println("Function with inferred return type: ${getMultipliedValue()}")
    
    // Collection type inference
    val inferredList = listOf(1, 2, 3)         // Type: List<Int>
    val inferredMap = mapOf("key" to "value")   // Type: Map<String, String>
    
    println("Inferred list type contains: $inferredList")
    println("Inferred map type contains: $inferredMap")
    
    println()
}

/**
 * ## Primitive Types
 * 
 * Kotlin has the same primitive types as Java, but they're represented as objects.
 * The compiler optimizes them to Java primitives when possible.
 */
fun primitiveTypes() {
    println("--- Primitive Types ---")
    
    // Integer types
    val byteValue: Byte = 127               // 8-bit signed (-128 to 127)
    val shortValue: Short = 32767           // 16-bit signed (-32,768 to 32,767)
    val intValue: Int = 2147483647          // 32-bit signed
    val longValue: Long = 9223372036854775807L  // 64-bit signed, 'L' suffix
    
    // Floating-point types
    val floatValue: Float = 3.14f           // 32-bit IEEE 754, 'f' or 'F' suffix
    val doubleValue: Double = 3.14159265359 // 64-bit IEEE 754
    
    // Character and Boolean
    val charValue: Char = 'K'               // 16-bit Unicode character
    val booleanValue: Boolean = true        // true or false
    
    // Unsigned types (Kotlin 1.3+)
    val unsignedByte: UByte = 255u
    val unsignedInt: UInt = 4294967295u
    val unsignedLong: ULong = 18446744073709551615uL
    
    println("Byte range: $byteValue")
    println("Short range: $shortValue")
    println("Int value: $intValue")
    println("Long value: $longValue")
    println("Float precision: $floatValue")
    println("Double precision: $doubleValue")
    println("Character: $charValue")
    println("Boolean: $booleanValue")
    println("Unsigned byte: $unsignedByte")
    
    // Number formatting and properties
    println("\nNumber properties:")
    println("Int max value: ${Int.MAX_VALUE}")
    println("Int min value: ${Int.MIN_VALUE}")
    println("Double positive infinity: ${Double.POSITIVE_INFINITY}")
    println("Double NaN: ${Double.NaN}")
    
    // Binary, octal, and hexadecimal literals
    val binaryNumber = 0b1010               // Binary: 10 in decimal
    val hexNumber = 0xFF                    // Hexadecimal: 255 in decimal
    val decimalNumber = 123                 // Decimal
    
    println("\nNumber systems:")
    println("Binary 0b1010 = $binaryNumber")
    println("Hex 0xFF = $hexNumber")
    println("Decimal 123 = $decimalNumber")
    
    // Underscores in numeric literals for readability
    val million = 1_000_000
    val creditCardNumber = 1234_5678_9012_3456L
    val bytes = 0xFF_EC_DE_5E
    
    println("\nReadable numbers:")
    println("Million: $million")
    println("Credit card: $creditCardNumber")
    println("Bytes: ${bytes.toString(16).uppercase()}")
    
    println()
}

/**
 * ## Type Conversions
 * 
 * Kotlin doesn't perform implicit type conversions. Every type conversion must be explicit.
 * This prevents unexpected behavior and makes code more predictable.
 */
fun typeConversions() {
    println("--- Type Conversions ---")
    
    val intValue = 42
    
    // Explicit type conversions
    val longFromInt = intValue.toLong()
    val doubleFromInt = intValue.toDouble()
    val stringFromInt = intValue.toString()
    val charFromInt = intValue.toChar()     // Converts to Unicode character
    
    println("Original int: $intValue")
    println("To Long: $longFromInt")
    println("To Double: $doubleFromInt")
    println("To String: $stringFromInt")
    println("To Char (Unicode): $charFromInt")
    
    // String to number conversions
    val numberString = "123"
    val stringToInt = numberString.toInt()
    val stringToDouble = numberString.toDouble()
    
    println("\nString to number:")
    println("String '$numberString' to Int: $stringToInt")
    println("String '$numberString' to Double: $stringToDouble")
    
    // Safe conversions with null handling
    val invalidString = "abc"
    val safeIntConversion = invalidString.toIntOrNull()
    val safeDoubleConversion = invalidString.toDoubleOrNull()
    
    println("\nSafe conversions:")
    println("'$invalidString' to Int safely: $safeIntConversion")
    println("'$invalidString' to Double safely: $safeDoubleConversion")
    
    // Type checking and smart casting
    val value: Any = "Hello, Kotlin!"
    
    if (value is String) {
        // Smart cast: value is automatically cast to String
        println("\nSmart cast demo:")
        println("Value is String: ${value.length} characters")
        println("Uppercase: ${value.uppercase()}")
    }
    
    // Safe casting with 'as?'
    val safeCast = value as? Int            // Returns null if cast fails
    val unsafeCast = value as? String       // Returns the string if cast succeeds
    
    println("\nSafe casting:")
    println("Value as Int?: $safeCast")
    println("Value as String?: $unsafeCast")
    
    println()
}

/**
 * ## String Types and Operations
 * 
 * Strings in Kotlin are immutable and provide rich functionality for text processing.
 */
fun stringTypes() {
    println("--- String Types ---")
    
    // Basic string declaration
    val simpleString = "Hello, World!"
    val emptyString = ""
    val singleChar = "K"
    
    // Multi-line strings (raw strings)
    val multilineString = """
        This is a multi-line string.
        It preserves formatting.
        No need to escape "quotes" here.
    """.trimIndent()
    
    // String templates and interpolation
    val name = "Kotlin"
    val version = 1.9
    val templatedString = "Welcome to $name version $version!"
    val expressionInTemplate = "Next version will be ${version + 0.1}"
    
    println("Simple string: $simpleString")
    println("Empty string length: ${emptyString.length}")
    println("Multi-line string:\n$multilineString")
    println("Templated string: $templatedString")
    println("Expression in template: $expressionInTemplate")
    
    // String properties and methods
    println("\nString operations:")
    println("Length: ${simpleString.length}")
    println("Uppercase: ${simpleString.uppercase()}")
    println("Lowercase: ${simpleString.lowercase()}")
    println("Contains 'World': ${simpleString.contains("World")}")
    println("Starts with 'Hello': ${simpleString.startsWith("Hello")}")
    println("Ends with '!': ${simpleString.endsWith("!")}")
    
    // String indexing and substrings
    println("\nString indexing:")
    println("First character: ${simpleString[0]}")
    println("Last character: ${simpleString[simpleString.length - 1]}")
    println("Substring (0..4): ${simpleString.substring(0..4)}")
    println("Substring (7 until end): ${simpleString.substring(7)}")
    
    // String comparison
    val string1 = "Hello"
    val string2 = "hello"
    val string3 = "Hello"
    
    println("\nString comparison:")
    println("'$string1' == '$string3': ${string1 == string3}")           // Structural equality
    println("'$string1' === '$string3': ${string1 === string3}")         // Referential equality
    println("'$string1' == '$string2': ${string1 == string2}")
    println("'$string1' equals '$string2' ignore case: ${string1.equals(string2, ignoreCase = true)}")
    
    println()
}

/**
 * ## Arrays
 * 
 * Arrays in Kotlin are represented by the Array class and have fixed size.
 * They provide type-safe access to elements.
 */
fun arrays() {
    println("--- Arrays ---")
    
    // Array creation methods
    val arrayFromElements = arrayOf(1, 2, 3, 4, 5)
    val stringArray = arrayOf("Apple", "Banana", "Cherry")
    val mixedArray = arrayOf(1, "Two", 3.0, true)          // Array<Any>
    
    // Typed arrays
    val intArray = intArrayOf(1, 2, 3, 4, 5)               // Primitive int array
    val doubleArray = doubleArrayOf(1.1, 2.2, 3.3)
    val booleanArray = booleanArrayOf(true, false, true)
    
    // Array creation with size and initialization
    val zeroArray = Array(5) { 0 }                         // [0, 0, 0, 0, 0]
    val squareArray = Array(5) { i -> i * i }              // [0, 1, 4, 9, 16]
    val nullableArray = arrayOfNulls<String>(3)            // [null, null, null]
    
    println("Array from elements: ${arrayFromElements.contentToString()}")
    println("String array: ${stringArray.contentToString()}")
    println("Mixed array: ${mixedArray.contentToString()}")
    println("Int array: ${intArray.contentToString()}")
    println("Double array: ${doubleArray.contentToString()}")
    println("Boolean array: ${booleanArray.contentToString()}")
    println("Zero array: ${zeroArray.contentToString()}")
    println("Square array: ${squareArray.contentToString()}")
    println("Nullable array: ${nullableArray.contentToString()}")
    
    // Array operations
    println("\nArray operations:")
    println("Array size: ${arrayFromElements.size}")
    println("First element: ${arrayFromElements[0]}")
    println("Last element: ${arrayFromElements[arrayFromElements.size - 1]}")
    println("Contains 3: ${arrayFromElements.contains(3)}")
    println("Index of 3: ${arrayFromElements.indexOf(3)}")
    
    // Array modification
    val mutableArray = arrayFromElements.copyOf()
    mutableArray[0] = 10
    println("Modified array: ${mutableArray.contentToString()}")
    
    // Array iteration
    println("\nArray iteration:")
    print("Elements: ")
    for (element in arrayFromElements) {
        print("$element ")
    }
    println()
    
    print("With indices: ")
    for ((index, element) in arrayFromElements.withIndex()) {
        print("[$index:$element] ")
    }
    println()
    
    // Functional operations on arrays
    println("\nFunctional operations:")
    val doubled = arrayFromElements.map { it * 2 }
    val filtered = arrayFromElements.filter { it > 2 }
    val sum = arrayFromElements.sum()
    
    println("Original: ${arrayFromElements.contentToString()}")
    println("Doubled: $doubled")
    println("Filtered (> 2): $filtered")
    println("Sum: $sum")
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice variable declarations and types:
 * 
 * 1. Create a val variable for your birth year and a var for your current age
 * 2. Convert a String "42" to an Int safely, handling potential errors
 * 3. Create a multi-line string with your favorite quote and format it nicely
 * 4. Create an array of your favorite colors and print them with indices
 * 5. Experiment with different number bases (binary, hex) for the same value
 */

// TODO: Exercise 1 - Personal information
fun exercise1() {
    // TODO: Declare birth year (val) and current age (var)
    // TODO: Calculate and print your age next year
}

// TODO: Exercise 2 - Safe string conversion
fun exercise2() {
    // TODO: Try converting "42", "abc", and "3.14" to Int safely
    // TODO: Print appropriate messages for each conversion result
}

// TODO: Exercise 3 - Multi-line string formatting
fun exercise3() {
    // TODO: Create a multi-line string with a quote
    // TODO: Include the author's name using string templates
    // TODO: Format it nicely with proper indentation
}

// TODO: Exercise 4 - Array operations
fun exercise4() {
    // TODO: Create an array of your favorite colors
    // TODO: Print each color with its index
    // TODO: Find the longest color name
}

// TODO: Exercise 5 - Number bases
fun exercise5() {
    // TODO: Represent the number 255 in decimal, binary, and hexadecimal
    // TODO: Convert and print them in different formats
}