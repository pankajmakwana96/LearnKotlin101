package com.kotlinmastery.basics.controlflow

/**
 * # Loops and Iteration in Kotlin
 * 
 * Kotlin provides powerful and expressive loop constructs for iteration. This module covers
 * for loops, while loops, ranges, and advanced iteration patterns with collections.
 * 
 * ## Learning Objectives
 * - Master for loops with ranges and collections
 * - Use while and do-while loops effectively
 * - Control loop execution with break and continue
 * - Work with labels for nested loop control
 * - Apply functional iteration patterns
 * 
 * ## Prerequisites: Variables, conditionals, and basic collections
 * ## Estimated Time: 3 hours
 */

fun main() {
    println("=== Kotlin Loops and Iteration Demo ===\n")
    
    forLoopsWithRanges()
    forLoopsWithCollections()
    whileLoops()
    loopControl()
    nestedLoopsAndLabels()
    functionalIteration()
    iteratorPatterns()
    realWorldExamples()
}

/**
 * ## For Loops with Ranges
 * 
 * Kotlin's for loops work with any iterable, with ranges being one of the most common uses.
 */
fun forLoopsWithRanges() {
    println("--- For Loops with Ranges ---")
    
    // Basic range iteration
    print("Numbers 1 to 5: ")
    for (i in 1..5) {
        print("$i ")
    }
    println()
    
    // Range with until (exclusive end)
    print("Numbers 0 until 5: ")
    for (i in 0 until 5) {
        print("$i ")
    }
    println()
    
    // Descending range
    print("Countdown from 5: ")
    for (i in 5 downTo 1) {
        print("$i ")
    }
    println()
    
    // Range with step
    print("Even numbers 0 to 10: ")
    for (i in 0..10 step 2) {
        print("$i ")
    }
    println()
    
    // Descending with step
    print("Odd numbers 9 down to 1: ")
    for (i in 9 downTo 1 step 2) {
        print("$i ")
    }
    println()
    
    // Character ranges
    print("Letters a to e: ")
    for (char in 'a'..'e') {
        print("$char ")
    }
    println()
    
    // Practical examples with ranges
    println("\nPractical range examples:")
    
    // Multiplication table
    val number = 7
    println("Multiplication table for $number:")
    for (i in 1..10) {
        println("$number × $i = ${number * i}")
    }
    
    // Sum calculation
    var sum = 0
    for (i in 1..100) {
        sum += i
    }
    println("Sum of numbers 1 to 100: $sum")
    
    // Factorial calculation
    fun factorial(n: Int): Long {
        var result = 1L
        for (i in 1..n) {
            result *= i
        }
        return result
    }
    
    println("Factorial of 5: ${factorial(5)}")
    println("Factorial of 10: ${factorial(10)}")
    
    // Pattern printing
    println("\nPattern printing:")
    for (i in 1..5) {
        repeat(i) { print("* ") }
        println()
    }
    
    // Reverse pattern
    for (i in 5 downTo 1) {
        repeat(i) { print("# ") }
        println()
    }
    
    println()
}

/**
 * ## For Loops with Collections
 * 
 * For loops can iterate over any collection, providing access to elements and indices.
 */
fun forLoopsWithCollections() {
    println("--- For Loops with Collections ---")
    
    val fruits = listOf("Apple", "Banana", "Cherry", "Date", "Elderberry")
    val numbers = arrayOf(10, 20, 30, 40, 50)
    val grades = mapOf("Alice" to 95, "Bob" to 87, "Charlie" to 92)
    
    // Basic collection iteration
    println("Fruits:")
    for (fruit in fruits) {
        println("- $fruit")
    }
    
    // Array iteration
    println("\nNumbers from array:")
    for (number in numbers) {
        print("$number ")
    }
    println()
    
    // Map iteration
    println("\nGrades:")
    for ((name, grade) in grades) {
        println("$name: $grade")
    }
    
    // Iteration with indices
    println("\nFruits with indices:")
    for (i in fruits.indices) {
        println("[$i] ${fruits[i]}")
    }
    
    // Using withIndex() for index and value
    println("\nNumbers with indices using withIndex():")
    for ((index, value) in numbers.withIndex()) {
        println("Index $index: $value")
    }
    
    // String iteration
    val text = "Kotlin"
    print("Characters in '$text': ")
    for (char in text) {
        print("$char ")
    }
    println()
    
    // Range of indices
    println("\nReverse iteration using indices:")
    for (i in fruits.indices.reversed()) {
        println("${fruits[i]}")
    }
    
    // Nested collection iteration
    val matrix = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    
    println("\nMatrix iteration:")
    for (row in matrix) {
        for (element in row) {
            print("$element ")
        }
        println()
    }
    
    // Matrix with indices
    println("Matrix with coordinates:")
    for ((rowIndex, row) in matrix.withIndex()) {
        for ((colIndex, element) in row.withIndex()) {
            println("[$rowIndex,$colIndex] = $element")
        }
    }
    
    // Set iteration (unordered)
    val uniqueNumbers = setOf(3, 1, 4, 1, 5, 9, 2, 6)
    println("\nSet iteration (note: order not guaranteed):")
    for (num in uniqueNumbers) {
        print("$num ")
    }
    println()
    
    // Custom data class iteration
    data class Person(val name: String, val age: Int, val city: String)
    val people = listOf(
        Person("Alice", 30, "New York"),
        Person("Bob", 25, "London"),
        Person("Charlie", 35, "Tokyo")
    )
    
    println("\nPeople iteration:")
    for (person in people) {
        println("${person.name}, ${person.age} years old, from ${person.city}")
    }
    
    // Destructuring in loops
    val coordinates = listOf(Pair(1, 2), Pair(3, 4), Pair(5, 6))
    println("\nCoordinate pairs:")
    for ((x, y) in coordinates) {
        println("Point: ($x, $y)")
    }
    
    println()
}

/**
 * ## While Loops
 * 
 * While and do-while loops provide condition-based iteration when the number of iterations
 * is not known in advance.
 */
fun whileLoops() {
    println("--- While Loops ---")
    
    // Basic while loop
    var count = 1
    println("Counting with while loop:")
    while (count <= 5) {
        println("Count: $count")
        count++
    }
    
    // While loop with complex condition
    var number = 1000
    var iterations = 0
    println("\nHalving $number until it's less than 10:")
    while (number >= 10) {
        println("Iteration ${iterations + 1}: $number")
        number /= 2
        iterations++
    }
    println("Final value: $number after $iterations iterations")
    
    // Do-while loop (executes at least once)
    var input = ""
    var attempts = 0
    val validInputs = setOf("yes", "no", "maybe")
    
    println("\nSimulating user input validation (do-while):")
    do {
        attempts++
        input = when (attempts) {
            1 -> "invalid"
            2 -> "wrong"
            3 -> "yes"
            else -> "no"
        }
        println("Attempt $attempts: User entered '$input'")
        
        if (input !in validInputs) {
            println("Invalid input, please try again...")
        }
    } while (input !in validInputs && attempts < 5)
    
    if (input in validInputs) {
        println("Valid input received: $input")
    } else {
        println("Max attempts reached")
    }
    
    // While with complex data processing
    val queue = mutableListOf("Task1", "Task2", "Task3", "Task4", "Task5")
    println("\nProcessing task queue:")
    while (queue.isNotEmpty()) {
        val task = queue.removeAt(0)
        println("Processing: $task")
        
        // Simulate adding new tasks occasionally
        if (task == "Task2") {
            queue.add("NewTask")
            println("  -> Added new task to queue")
        }
    }
    println("All tasks completed!")
    
    // Infinite loop with break condition
    var randomWalk = 0
    var steps = 0
    val target = 5
    
    println("\nRandom walk simulation (target: $target):")
    while (true) {
        steps++
        // Simulate random step: +1 or -1
        val step = if (steps % 3 == 0) -1 else 1
        randomWalk += step
        
        println("Step $steps: position $randomWalk")
        
        if (randomWalk >= target) {
            println("Target $target reached in $steps steps!")
            break
        }
        
        if (steps > 20) {
            println("Max steps reached without hitting target")
            break
        }
    }
    
    // While with nullable values
    var current: String? = "start"
    val path = mutableListOf<String>()
    
    println("\nTraversing path until null:")
    while (current != null) {
        path.add(current)
        current = when (current) {
            "start" -> "middle"
            "middle" -> "end"
            "end" -> null
            else -> null
        }
    }
    println("Path traversed: ${path.joinToString(" -> ")}")
    
    println()
}

/**
 * ## Loop Control
 * 
 * Break and continue statements provide fine-grained control over loop execution.
 */
fun loopControl() {
    println("--- Loop Control ---")
    
    // Break statement
    println("Finding first number divisible by 7:")
    for (i in 1..100) {
        if (i % 7 == 0) {
            println("Found: $i")
            break
        }
        if (i % 10 == 0) {
            println("  Checked up to $i...")
        }
    }
    
    // Continue statement
    println("\nEven numbers between 1 and 20 (using continue):")
    for (i in 1..20) {
        if (i % 2 != 0) {
            continue  // Skip odd numbers
        }
        print("$i ")
    }
    println()
    
    // Break and continue with conditions
    println("\nProcessing numbers with conditions:")
    for (i in 1..15) {
        when {
            i % 15 == 0 -> {
                println("$i: FizzBuzz - stopping here!")
                break
            }
            i % 3 == 0 -> {
                println("$i: Fizz")
            }
            i % 5 == 0 -> {
                println("$i: Buzz")
            }
            i % 2 == 0 -> {
                println("$i: Even - skipping next...")
                continue
            }
            else -> {
                println("$i: Regular number")
            }
        }
    }
    
    // While loop with break and continue
    println("\nWhile loop with break and continue:")
    var value = 0
    while (value < 20) {
        value++
        
        if (value % 3 == 0) {
            println("$value is divisible by 3, skipping...")
            continue
        }
        
        if (value > 15) {
            println("$value is greater than 15, breaking...")
            break
        }
        
        println("Processing: $value")
    }
    
    // Searching with early exit
    val names = listOf("Alice", "Bob", "Charlie", "David", "Emma", "Frank")
    val searchName = "Charlie"
    
    println("\nSearching for '$searchName':")
    var found = false
    for ((index, name) in names.withIndex()) {
        println("Checking index $index: $name")
        if (name == searchName) {
            println("Found '$searchName' at index $index!")
            found = true
            break
        }
    }
    
    if (!found) {
        println("'$searchName' not found in the list")
    }
    
    // Complex validation with multiple conditions
    val passwords = listOf("abc", "123456", "Password123", "MySecureP@ss!", "short")
    
    println("\nPassword validation with early rejection:")
    for (password in passwords) {
        println("Validating: '$password'")
        
        // Check minimum length
        if (password.length < 8) {
            println("  ❌ Too short, skipping further checks")
            continue
        }
        
        // Check for uppercase
        if (!password.any { it.isUpperCase() }) {
            println("  ❌ No uppercase letters, skipping further checks")
            continue
        }
        
        // Check for digits
        if (!password.any { it.isDigit() }) {
            println("  ❌ No digits, skipping further checks")
            continue
        }
        
        // Check for special characters
        if (!password.any { !it.isLetterOrDigit() }) {
            println("  ❌ No special characters")
            continue
        }
        
        println("  ✅ Password meets all requirements!")
    }
    
    println()
}

/**
 * ## Nested Loops and Labels
 * 
 * Labels provide control over nested loops, allowing break and continue to target specific loops.
 */
fun nestedLoopsAndLabels() {
    println("--- Nested Loops and Labels ---")
    
    // Basic nested loops
    println("Multiplication table (3x3):")
    for (i in 1..3) {
        for (j in 1..3) {
            print("${i * j}\t")
        }
        println()
    }
    
    // Nested loops with break (breaks inner loop only)
    println("\nNested loops with inner break:")
    for (i in 1..3) {
        print("Row $i: ")
        for (j in 1..5) {
            if (j == 3) {
                println("(broke at j=$j)")
                break
            }
            print("$j ")
        }
    }
    
    // Labels for outer loop control
    println("\nUsing labels for outer loop control:")
    outer@ for (i in 1..3) {
        inner@ for (j in 1..5) {
            if (i == 2 && j == 3) {
                println("Breaking outer loop at i=$i, j=$j")
                break@outer
            }
            print("($i,$j) ")
        }
        println()
    }
    
    // Continue with labels
    println("\nContinue with labels:")
    outer@ for (i in 1..4) {
        inner@ for (j in 1..4) {
            if (j == 2) {
                println("  Continuing outer loop at i=$i, j=$j")
                continue@outer
            }
            print("($i,$j) ")
        }
        println(" <- This won't print when j=2")
    }
    
    // Practical example: Matrix search
    val matrix = listOf(
        listOf(1, 2, 3, 4),
        listOf(5, 6, 7, 8),
        listOf(9, 10, 11, 12)
    )
    val target = 7
    
    println("\nSearching for $target in matrix:")
    searchLoop@ for ((rowIndex, row) in matrix.withIndex()) {
        for ((colIndex, value) in row.withIndex()) {
            print("Checking [$rowIndex,$colIndex]=$value ")
            if (value == target) {
                println("\n✅ Found $target at position [$rowIndex,$colIndex]")
                break@searchLoop
            }
        }
        println()
    }
    
    // Complex nested structure with multiple labels
    println("\nComplex nested structure:")
    level1@ for (a in 1..2) {
        println("Level 1: $a")
        level2@ for (b in 1..3) {
            level3@ for (c in 1..3) {
                when {
                    a == 1 && b == 2 && c == 2 -> {
                        println("  Skipping level2 iteration at ($a,$b,$c)")
                        continue@level2
                    }
                    a == 2 && b == 1 && c == 3 -> {
                        println("  Breaking level1 at ($a,$b,$c)")
                        break@level1
                    }
                    else -> {
                        print("    ($a,$b,$c) ")
                    }
                }
            }
            println()
        }
        println("  End of level 1: $a")
    }
    
    // Labeled returns from lambda (advanced)
    println("\nLabeled returns in nested lambdas:")
    listOf(1, 2, 3, 4, 5).forEach outer@{ outer ->
        listOf("a", "b", "c").forEach inner@{ inner ->
            if (outer == 3 && inner == "b") {
                println("  Returning from outer lambda at $outer, $inner")
                return@outer
            }
            print("($outer,$inner) ")
        }
        println()
    }
    
    println()
}

/**
 * ## Functional Iteration
 * 
 * Kotlin provides functional alternatives to traditional loops that are often more expressive.
 */
fun functionalIteration() {
    println("--- Functional Iteration ---")
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    // forEach vs for loop
    println("Using forEach:")
    numbers.forEach { num ->
        print("$num ")
    }
    println()
    
    // forEachIndexed
    println("\nUsing forEachIndexed:")
    numbers.forEachIndexed { index, value ->
        println("Index $index: $value")
    }
    
    // map transformation
    println("\nSquaring numbers with map:")
    val squares = numbers.map { it * it }
    println("Original: $numbers")
    println("Squared:  $squares")
    
    // filter and map combination
    println("\nEven numbers doubled:")
    val evenDoubled = numbers
        .filter { it % 2 == 0 }
        .map { it * 2 }
    println("Result: $evenDoubled")
    
    // any, all, none
    println("\nBoolean operations:")
    println("Any number > 5: ${numbers.any { it > 5 }}")
    println("All numbers < 20: ${numbers.all { it < 20 }}")
    println("No negative numbers: ${numbers.none { it < 0 }}")
    
    // find and first operations
    println("\nFinding elements:")
    val firstEven = numbers.find { it % 2 == 0 }
    val firstGreaterThan7 = numbers.firstOrNull { it > 7 }
    println("First even number: $firstEven")
    println("First number > 7: $firstGreaterThan7")
    
    // groupBy
    println("\nGrouping by even/odd:")
    val grouped = numbers.groupBy { if (it % 2 == 0) "even" else "odd" }
    grouped.forEach { (key, values) ->
        println("$key: $values")
    }
    
    // reduce and fold
    println("\nAggregation operations:")
    val sum = numbers.reduce { acc, num -> acc + num }
    val product = numbers.fold(1) { acc, num -> acc * num }
    println("Sum using reduce: $sum")
    println("Product using fold: $product")
    
    // Custom aggregation
    val concatenated = numbers.fold("Numbers: ") { acc, num -> "$acc$num, " }
    println("Concatenated: ${concatenated.dropLast(2)}")
    
    // Working with indices functionally
    println("\nFunctional index operations:")
    numbers.indices.forEach { index ->
        if (index % 2 == 0) {
            println("Even index $index has value ${numbers[index]}")
        }
    }
    
    // Sequence for lazy evaluation
    println("\nLazy evaluation with sequences:")
    val result = (1..1000000).asSequence()
        .filter { it % 2 == 0 }
        .map { it * it }
        .take(5)
        .toList()
    println("First 5 even squares: $result")
    
    // String manipulation
    val text = "Hello, Kotlin World!"
    println("\nString character processing:")
    text.forEach { char ->
        if (char.isLetter()) {
            print(char.uppercase())
        } else {
            print(char)
        }
    }
    println()
    
    // Nested collection processing
    val nestedList = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    
    println("\nFlattening nested collections:")
    val flattened = nestedList.flatten()
    println("Flattened: $flattened")
    
    val flatMapped = nestedList.flatMap { row ->
        row.map { it * 2 }
    }
    println("Flat mapped (doubled): $flatMapped")
    
    println()
}

/**
 * ## Iterator Patterns
 * 
 * Understanding iterators and custom iteration patterns.
 */
fun iteratorPatterns() {
    println("--- Iterator Patterns ---")
    
    val list = listOf("Apple", "Banana", "Cherry", "Date")
    
    // Manual iterator usage
    println("Manual iterator:")
    val iterator = list.iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        println("Item: $item")
    }
    
    // ListIterator with bidirectional navigation
    println("\nListIterator (bidirectional):")
    val listIterator = list.listIterator()
    
    // Forward iteration
    println("Forward:")
    while (listIterator.hasNext()) {
        println("  ${listIterator.next()}")
    }
    
    // Backward iteration
    println("Backward:")
    while (listIterator.hasPrevious()) {
        println("  ${listIterator.previous()}")
    }
    
    // MutableIterator for safe removal
    val mutableList = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println("\nRemoving even numbers safely:")
    println("Original: $mutableList")
    
    val mutableIterator = mutableList.iterator()
    while (mutableIterator.hasNext()) {
        val item = mutableIterator.next()
        if (item % 2 == 0) {
            mutableIterator.remove()
            println("Removed: $item")
        }
    }
    println("After removal: $mutableList")
    
    // Custom iterator class
    class NumberRange(private val start: Int, private val end: Int, private val step: Int = 1) : Iterable<Int> {
        override fun iterator(): Iterator<Int> {
            return object : Iterator<Int> {
                private var current = start
                
                override fun hasNext(): Boolean = current <= end
                
                override fun next(): Int {
                    if (!hasNext()) throw NoSuchElementException()
                    val result = current
                    current += step
                    return result
                }
            }
        }
    }
    
    println("\nCustom iterator:")
    val customRange = NumberRange(1, 10, 2)
    for (num in customRange) {
        print("$num ")
    }
    println()
    
    // Iterator with state
    class FibonacciIterator(private val maxCount: Int) : Iterator<Long> {
        private var count = 0
        private var current = 0L
        private var next = 1L
        
        override fun hasNext(): Boolean = count < maxCount
        
        override fun next(): Long {
            if (!hasNext()) throw NoSuchElementException()
            
            val result = current
            val temp = current + next
            current = next
            next = temp
            count++
            
            return result
        }
    }
    
    println("\nFibonacci iterator:")
    val fibIterator = FibonacciIterator(10)
    while (fibIterator.hasNext()) {
        print("${fibIterator.next()} ")
    }
    println()
    
    // Iterable extension
    fun <T> Iterable<T>.batches(size: Int): List<List<T>> {
        val result = mutableListOf<List<T>>()
        val iterator = this.iterator()
        
        while (iterator.hasNext()) {
            val batch = mutableListOf<T>()
            repeat(size) {
                if (iterator.hasNext()) {
                    batch.add(iterator.next())
                }
            }
            if (batch.isNotEmpty()) {
                result.add(batch)
            }
        }
        
        return result
    }
    
    println("\nBatch processing:")
    val largeList = (1..13).toList()
    val batches = largeList.batches(4)
    batches.forEachIndexed { index, batch ->
        println("Batch $index: $batch")
    }
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of loops and iteration in common programming scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. Data Processing Pipeline
    data class Sale(val product: String, val amount: Double, val date: String, val region: String)
    
    val sales = listOf(
        Sale("Laptop", 999.99, "2023-01-15", "North"),
        Sale("Mouse", 29.99, "2023-01-16", "South"),
        Sale("Keyboard", 79.99, "2023-01-17", "North"),
        Sale("Monitor", 299.99, "2023-01-18", "East"),
        Sale("Laptop", 899.99, "2023-01-19", "West"),
        Sale("Mouse", 24.99, "2023-01-20", "North")
    )
    
    println("Sales Data Processing:")
    
    // Total sales by region
    val salesByRegion = mutableMapOf<String, Double>()
    for (sale in sales) {
        salesByRegion[sale.region] = salesByRegion.getOrDefault(sale.region, 0.0) + sale.amount
    }
    
    println("Total sales by region:")
    for ((region, total) in salesByRegion.toSortedMap()) {
        println("  $region: $${String.format("%.2f", total)}")
    }
    
    // Product performance analysis
    val productStats = mutableMapOf<String, Pair<Int, Double>>() // count, total
    for (sale in sales) {
        val current = productStats.getOrDefault(sale.product, Pair(0, 0.0))
        productStats[sale.product] = Pair(current.first + 1, current.second + sale.amount)
    }
    
    println("\nProduct performance:")
    for ((product, stats) in productStats) {
        val avgPrice = stats.second / stats.first
        println("  $product: ${stats.first} sales, avg $${String.format("%.2f", avgPrice)}")
    }
    
    // 2. Text Analysis
    val document = """
        Kotlin is a modern programming language that makes developers happier.
        It's concise, safe, and fully interoperable with Java.
        Kotlin can be used for Android development, server-side development, and much more.
    """.trimIndent()
    
    println("\nText Analysis:")
    
    // Word frequency analysis
    val wordCount = mutableMapOf<String, Int>()
    val words = document.lowercase()
        .replace(Regex("[^a-z\\s]"), "")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
    
    for (word in words) {
        wordCount[word] = wordCount.getOrDefault(word, 0) + 1
    }
    
    println("Most frequent words:")
    wordCount.toList()
        .sortedByDescending { it.second }
        .take(5)
        .forEachIndexed { index, (word, count) ->
            println("  ${index + 1}. '$word': $count times")
        }
    
    // Character frequency
    val charCount = mutableMapOf<Char, Int>()
    for (char in document.lowercase()) {
        if (char.isLetter()) {
            charCount[char] = charCount.getOrDefault(char, 0) + 1
        }
    }
    
    println("\nMost frequent letters:")
    charCount.toList()
        .sortedByDescending { it.second }
        .take(5)
        .forEach { (char, count) ->
            println("  '$char': $count times")
        }
    
    // 3. Game Development - Pathfinding
    data class Point(val x: Int, val y: Int)
    
    fun findPath(start: Point, end: Point, obstacles: Set<Point>, gridSize: Int): List<Point>? {
        val visited = mutableSetOf<Point>()
        val queue = mutableListOf(listOf(start))
        
        while (queue.isNotEmpty()) {
            val path = queue.removeAt(0)
            val current = path.last()
            
            if (current == end) {
                return path
            }
            
            if (current in visited) {
                continue
            }
            
            visited.add(current)
            
            // Check all 4 directions
            val directions = listOf(
                Point(0, 1), Point(1, 0), Point(0, -1), Point(-1, 0)
            )
            
            for (direction in directions) {
                val next = Point(current.x + direction.x, current.y + direction.y)
                
                if (next.x in 0 until gridSize && 
                    next.y in 0 until gridSize && 
                    next !in obstacles && 
                    next !in visited) {
                    queue.add(path + next)
                }
            }
        }
        
        return null // No path found
    }
    
    println("\nPathfinding Example:")
    val start = Point(0, 0)
    val end = Point(4, 4)
    val obstacles = setOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2))
    
    val path = findPath(start, end, obstacles, 5)
    if (path != null) {
        println("Path found: ${path.joinToString(" -> ")}")
    } else {
        println("No path found!")
    }
    
    // 4. Data Validation and Cleanup
    val rawData = listOf(
        "  John Doe, 30, john@email.com  ",
        "Jane Smith,25,jane@email.com",
        "  ,35,invalid@  ",
        "Bob Johnson, , bob@email.com",
        "Alice Brown,28,alice@email.com"
    )
    
    println("\nData Validation and Cleanup:")
    val cleanedData = mutableListOf<Triple<String, Int?, String>>()
    
    for ((index, line) in rawData.withIndex()) {
        val trimmed = line.trim()
        if (trimmed.isBlank()) {
            println("Row ${index + 1}: Skipping blank line")
            continue
        }
        
        val parts = trimmed.split(",").map { it.trim() }
        if (parts.size != 3) {
            println("Row ${index + 1}: Invalid format, skipping")
            continue
        }
        
        val name = parts[0]
        val ageStr = parts[1]
        val email = parts[2]
        
        // Validate name
        if (name.isBlank()) {
            println("Row ${index + 1}: Missing name, skipping")
            continue
        }
        
        // Validate age
        val age = if (ageStr.isBlank()) {
            println("Row ${index + 1}: Missing age for $name")
            null
        } else {
            ageStr.toIntOrNull() ?: run {
                println("Row ${index + 1}: Invalid age '$ageStr' for $name")
                null
            }
        }
        
        // Validate email
        if (!email.contains("@") || !email.contains(".")) {
            println("Row ${index + 1}: Invalid email '$email' for $name, skipping")
            continue
        }
        
        cleanedData.add(Triple(name, age, email))
        println("Row ${index + 1}: ✅ Valid data for $name")
    }
    
    println("\nCleaned data summary:")
    cleanedData.forEach { (name, age, email) ->
        val ageText = age?.toString() ?: "unknown"
        println("  $name ($ageText years) - $email")
    }
    
    // 5. Performance Monitoring
    fun measurePerformance<T>(name: String, operation: () -> T): T {
        val startTime = System.nanoTime()
        val result = operation()
        val endTime = System.nanoTime()
        val durationMs = (endTime - startTime) / 1_000_000.0
        println("$name took ${String.format("%.2f", durationMs)} ms")
        return result
    }
    
    println("\nPerformance Comparison:")
    val largeList = (1..100_000).toList()
    
    // Traditional loop
    measurePerformance("Traditional for loop") {
        var sum = 0L
        for (num in largeList) {
            sum += num
        }
        sum
    }
    
    // Functional approach
    measurePerformance("Functional sum") {
        largeList.sum().toLong()
    }
    
    // While loop
    measurePerformance("While loop") {
        var sum = 0L
        var index = 0
        while (index < largeList.size) {
            sum += largeList[index]
            index++
        }
        sum
    }
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice loops and iteration:
 * 
 * 1. Create a function that generates Pascal's triangle up to n rows
 * 2. Implement a simple text-based game with a game loop
 * 3. Build a data parser that handles malformed CSV data gracefully
 * 4. Create a maze solver using iterative pathfinding
 * 5. Implement a basic spell checker with similarity scoring
 */

// TODO: Exercise 1 - Pascal's Triangle
fun generatePascalsTriangle(rows: Int): List<List<Int>> {
    // TODO: Generate Pascal's triangle up to the specified number of rows
    // Each row contains binomial coefficients
    // Row 0: [1], Row 1: [1, 1], Row 2: [1, 2, 1], etc.
    return emptyList()
}

// TODO: Exercise 2 - Simple Game Loop
class SimpleGame {
    // TODO: Implement a simple text-based game with:
    // - Player position on a grid
    // - Items to collect
    // - Simple commands (move, quit, help)
    // - Game loop that continues until win/quit condition
    
    fun start() {
        // TODO: Main game loop implementation
    }
}

// TODO: Exercise 3 - CSV Parser
fun parseCSVWithErrorHandling(csvData: List<String>): Pair<List<Map<String, String>>, List<String>> {
    // TODO: Parse CSV data and return:
    // - List of valid records as maps (column -> value)
    // - List of error messages for invalid rows
    // Handle: missing columns, extra columns, empty fields
    return Pair(emptyList(), emptyList())
}

// TODO: Exercise 4 - Maze Solver
class MazeSolver(private val maze: Array<CharArray>) {
    // TODO: Solve a maze represented as 2D char array
    // '#' = wall, '.' = path, 'S' = start, 'E' = end
    // Return path as list of coordinates or null if no solution
    
    fun solve(): List<Pair<Int, Int>>? {
        // TODO: Implement iterative pathfinding algorithm
        return null
    }
}

// TODO: Exercise 5 - Spell Checker
class SpellChecker(private val dictionary: Set<String>) {
    // TODO: Implement spell checking with similarity scoring
    // Calculate similarity between words using edit distance
    // Return suggestions for misspelled words
    
    fun checkWord(word: String): Pair<Boolean, List<String>> {
        // TODO: Return (isCorrect, suggestions)
        return Pair(false, emptyList())
    }
    
    private fun editDistance(word1: String, word2: String): Int {
        // TODO: Calculate edit distance between two words
        return 0
    }
}