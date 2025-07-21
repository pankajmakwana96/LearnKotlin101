package com.kotlinmastery.intermediate.collections

/**
 * # Functional Operations on Collections
 * 
 * Kotlin's collections API provides powerful functional operations for data transformation,
 * filtering, aggregation, and manipulation. This module covers the most important operations
 * and patterns for functional-style collection processing.
 * 
 * ## Learning Objectives
 * - Master transformation operations (map, flatMap, zip)
 * - Use filtering and partitioning effectively
 * - Apply aggregation and reduction operations
 * - Understand lazy evaluation with sequences
 * - Combine operations for complex data processing
 * 
 * ## Prerequisites: Collection types and higher-order functions
 * ## Estimated Time: 6 hours
 */

fun main() {
    println("=== Kotlin Functional Operations Demo ===\n")
    
    transformationOperations()
    filteringOperations()
    aggregationOperations()
    groupingOperations()
    sortingOperations()
    sequenceOperations()
    advancedOperations()
    realWorldDataProcessing()
}

/**
 * ## Transformation Operations
 * 
 * Transform collections by applying functions to elements or changing collection structure.
 */
fun transformationOperations() {
    println("--- Transformation Operations ---")
    
    val numbers = listOf(1, 2, 3, 4, 5)
    val words = listOf("hello", "world", "kotlin", "programming")
    
    // map - transform each element
    println("Map operations:")
    println("Numbers: $numbers")
    println("Squared: ${numbers.map { it * it }}")
    println("Doubled: ${numbers.map { it * 2 }}")
    println("To strings: ${numbers.map { "Number: $it" }}")
    
    println("\nWords: $words")
    println("Uppercase: ${words.map { it.uppercase() }}")
    println("Lengths: ${words.map { it.length }}")
    println("First char: ${words.map { it.firstOrNull() }}")
    
    // mapIndexed - transform with index
    println("\nMapIndexed operations:")
    val indexed = words.mapIndexed { index, word -> 
        "${index + 1}. $word" 
    }
    println("Indexed words: $indexed")
    
    val positionedNumbers = numbers.mapIndexed { index, value ->
        "[$index] = $value"
    }
    println("Positioned numbers: $positionedNumbers")
    
    // mapNotNull - transform and filter nulls
    val mixedStrings = listOf("1", "hello", "2", "world", "3", "kotlin")
    val parsedNumbers = mixedStrings.mapNotNull { it.toIntOrNull() }
    println("\nMapNotNull operations:")
    println("Mixed strings: $mixedStrings")
    println("Parsed numbers: $parsedNumbers")
    
    // flatMap - transform and flatten
    val sentences = listOf("Hello world", "Kotlin is great", "Programming fun")
    val allWords = sentences.flatMap { it.split(" ") }
    println("\nFlatMap operations:")
    println("Sentences: $sentences")
    println("All words: $allWords")
    
    val numberRanges = listOf(1..3, 5..7, 10..12)
    val allNumbers = numberRanges.flatMap { it.toList() }
    println("Number ranges: $numberRanges")
    println("Flattened: $allNumbers")
    
    // Complex transformations
    data class Person(val name: String, val age: Int, val hobbies: List<String>)
    
    val people = listOf(
        Person("Alice", 30, listOf("reading", "swimming", "cooking")),
        Person("Bob", 25, listOf("gaming", "movies", "reading")),
        Person("Charlie", 35, listOf("cooking", "gardening", "photography"))
    )
    
    println("\nComplex transformations:")
    val names = people.map { it.name }
    val ages = people.map { it.age }
    val allHobbies = people.flatMap { it.hobbies }.distinct()
    val nameAgeMapping = people.map { it.name to it.age }
    
    println("Names: $names")
    println("Ages: $ages")
    println("All hobbies: $allHobbies")
    println("Name-age pairs: $nameAgeMapping")
    
    // Nested transformations
    val matrix = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    
    val doubledMatrix = matrix.map { row -> row.map { it * 2 } }
    val matrixSum = matrix.map { it.sum() }
    val flattenedDoubled = matrix.flatMap { row -> row.map { it * 2 } }
    
    println("\nNested transformations:")
    println("Original matrix: $matrix")
    println("Doubled matrix: $doubledMatrix")
    println("Row sums: $matrixSum")
    println("Flattened doubled: $flattenedDoubled")
    
    // Zip operations
    val letters = listOf("a", "b", "c", "d")
    val numbersForZip = listOf(1, 2, 3, 4, 5)
    
    println("\nZip operations:")
    println("Letters: $letters")
    println("Numbers: $numbersForZip")
    println("Zipped: ${letters.zip(numbersForZip)}")
    println("Zipped with transform: ${letters.zip(numbersForZip) { letter, number -> "$letter$number" }}")
    
    // Zip with next (sliding pairs)
    val sequence = listOf(1, 2, 3, 4, 5)
    val pairs = sequence.zipWithNext()
    val differences = sequence.zipWithNext { a, b -> b - a }
    
    println("Sequence: $sequence")
    println("Pairs: $pairs")
    println("Differences: $differences")
    
    println()
}

/**
 * ## Filtering Operations
 * 
 * Select elements that match certain criteria or partition collections.
 */
fun filteringOperations() {
    println("--- Filtering Operations ---")
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val words = listOf("apple", "banana", "cherry", "date", "elderberry", "fig")
    
    // Basic filtering
    println("Basic filtering:")
    println("Numbers: $numbers")
    println("Even numbers: ${numbers.filter { it % 2 == 0 }}")
    println("Numbers > 5: ${numbers.filter { it > 5 }}")
    println("Numbers in range 3..7: ${numbers.filter { it in 3..7 }}")
    
    println("\nWords: $words")
    println("Long words (>5 chars): ${words.filter { it.length > 5 }}")
    println("Words starting with 'a': ${words.filter { it.startsWith("a") }}")
    println("Words containing 'e': ${words.filter { it.contains("e") }}")
    
    // filterNot - inverse filtering
    println("\nFilterNot operations:")
    println("Not even (odd): ${numbers.filterNot { it % 2 == 0 }}")
    println("Not long words: ${words.filterNot { it.length > 5 }}")
    
    // filterIndexed - filter with index
    println("\nFilterIndexed operations:")
    val evenIndices = numbers.filterIndexed { index, _ -> index % 2 == 0 }
    val oddPositions = words.filterIndexed { index, _ -> index % 2 == 1 }
    
    println("Even indices from numbers: $evenIndices")
    println("Odd positions from words: $oddPositions")
    
    // Type filtering
    val mixedList = listOf(1, "hello", 2.5, "world", 3, true, "kotlin")
    
    println("\nType filtering:")
    println("Mixed list: $mixedList")
    println("Strings only: ${mixedList.filterIsInstance<String>()}")
    println("Numbers only: ${mixedList.filterIsInstance<Number>()}")
    println("Integers only: ${mixedList.filterIsInstance<Int>()}")
    
    // Null filtering
    val nullableList = listOf("hello", null, "world", null, "kotlin")
    println("\nNull filtering:")
    println("With nulls: $nullableList")
    println("Not null: ${nullableList.filterNotNull()}")
    
    // Partitioning
    val allNumbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val (evens, odds) = allNumbers.partition { it % 2 == 0 }
    
    println("\nPartitioning:")
    println("All numbers: $allNumbers")
    println("Evens: $evens")
    println("Odds: $odds")
    
    val longWords = words.partition { it.length > 5 }
    println("Words partitioned by length > 5: $longWords")
    
    // Complex filtering scenarios
    data class Product(val name: String, val price: Double, val category: String, val inStock: Boolean)
    
    val products = listOf(
        Product("Laptop", 999.99, "Electronics", true),
        Product("Mouse", 29.99, "Electronics", true),
        Product("Desk", 299.99, "Furniture", false),
        Product("Chair", 199.99, "Furniture", true),
        Product("Book", 19.99, "Education", true),
        Product("Monitor", 349.99, "Electronics", false)
    )
    
    println("\nComplex filtering - Products:")
    val inStockProducts = products.filter { it.inStock }
    val expensiveProducts = products.filter { it.price > 100 }
    val availableElectronics = products.filter { it.category == "Electronics" && it.inStock }
    val affordableInStock = products.filter { it.price < 200 && it.inStock }
    
    println("In stock: ${inStockProducts.map { it.name }}")
    println("Expensive (>$100): ${expensiveProducts.map { it.name }}")
    println("Available electronics: ${availableElectronics.map { it.name }}")
    println("Affordable in stock: ${affordableInStock.map { it.name }}")
    
    // Multiple filtering conditions
    val premiumAvailableProducts = products
        .filter { it.inStock }
        .filter { it.price > 100 }
        .filter { it.category == "Electronics" }
    
    println("Premium available electronics: ${premiumAvailableProducts.map { "${it.name} - $${it.price}" }}")
    
    // take and drop operations
    println("\nTake and drop operations:")
    println("First 5 numbers: ${numbers.take(5)}")
    println("Last 3 numbers: ${numbers.takeLast(3)}")
    println("Drop first 3: ${numbers.drop(3)}")
    println("Drop last 3: ${numbers.dropLast(3)}")
    
    // Conditional take and drop
    println("Take while < 6: ${numbers.takeWhile { it < 6 }}")
    println("Drop while < 4: ${numbers.dropWhile { it < 4 }}")
    
    // Distinct operations
    val duplicateNumbers = listOf(1, 2, 2, 3, 3, 3, 4, 4, 5)
    println("\nDistinct operations:")
    println("With duplicates: $duplicateNumbers")
    println("Distinct: ${duplicateNumbers.distinct()}")
    
    val people = listOf("Alice", "Bob", "Charlie", "Alice", "David", "Bob")
    val distinctPeople = people.distinct()
    val distinctByLength = people.distinctBy { it.length }
    
    println("People with duplicates: $people")
    println("Distinct people: $distinctPeople")
    println("Distinct by length: $distinctByLength")
    
    println()
}

/**
 * ## Aggregation Operations
 * 
 * Combine collection elements into single values using various aggregation functions.
 */
fun aggregationOperations() {
    println("--- Aggregation Operations ---")
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val prices = listOf(19.99, 29.99, 149.99, 299.99, 9.99, 199.99)
    val words = listOf("apple", "banana", "cherry", "date")
    
    // Basic aggregations
    println("Basic aggregations:")
    println("Numbers: $numbers")
    println("Sum: ${numbers.sum()}")
    println("Average: ${numbers.average()}")
    println("Min: ${numbers.minOrNull()}")
    println("Max: ${numbers.maxOrNull()}")
    println("Count: ${numbers.count()}")
    
    println("\nPrices: $prices")
    println("Total: $${prices.sum()}")
    println("Average price: $${"%.2f".format(prices.average())}")
    println("Cheapest: $${prices.minOrNull()}")
    println("Most expensive: $${prices.maxOrNull()}")
    
    // Conditional aggregations
    println("\nConditional aggregations:")
    println("Count of even numbers: ${numbers.count { it % 2 == 0 }}")
    println("Sum of even numbers: ${numbers.filter { it % 2 == 0 }.sum()}")
    println("Count of expensive items (>$100): ${prices.count { it > 100.0 }}")
    
    // Boolean aggregations
    println("\nBoolean aggregations:")
    println("Any number > 8: ${numbers.any { it > 8 }}")
    println("All numbers < 20: ${numbers.all { it < 20 }}")
    println("No negative numbers: ${numbers.none { it < 0 }}")
    
    println("Any word starts with 'a': ${words.any { it.startsWith("a") }}")
    println("All words have vowels: ${words.all { it.any { char -> char in "aeiou" } }}")
    
    // Min/Max with selectors
    println("\nMin/Max with selectors:")
    println("Longest word: ${words.maxByOrNull { it.length }}")
    println("Shortest word: ${words.minByOrNull { it.length }}")
    println("Word with max first char: ${words.maxByOrNull { it.first() }}")
    
    data class Person(val name: String, val age: Int, val salary: Double)
    
    val people = listOf(
        Person("Alice", 30, 75000.0),
        Person("Bob", 25, 65000.0),
        Person("Charlie", 35, 85000.0),
        Person("Diana", 28, 70000.0)
    )
    
    println("\nPeople aggregations:")
    val oldestPerson = people.maxByOrNull { it.age }
    val youngestPerson = people.minByOrNull { it.age }
    val highestPaid = people.maxByOrNull { it.salary }
    val averageSalary = people.map { it.salary }.average()
    
    println("Oldest: ${oldestPerson?.name} (${oldestPerson?.age})")
    println("Youngest: ${youngestPerson?.name} (${youngestPerson?.age})")
    println("Highest paid: ${highestPaid?.name} ($${highestPaid?.salary})")
    println("Average salary: $${"%.2f".format(averageSalary)}")
    
    // reduce and fold operations
    println("\nReduce and fold operations:")
    val numbersForReduce = listOf(1, 2, 3, 4, 5)
    
    // reduce - combines elements using operation
    val product = numbersForReduce.reduce { acc, element -> acc * element }
    val concatenated = words.reduce { acc, element -> "$acc, $element" }
    
    println("Numbers: $numbersForReduce")
    println("Product (reduce): $product")
    println("Words: $words")
    println("Concatenated (reduce): $concatenated")
    
    // fold - like reduce but with initial value
    val productWithInitial = numbersForReduce.fold(1) { acc, element -> acc * element }
    val sumWithInitial = numbersForReduce.fold(100) { acc, element -> acc + element }
    val wordCount = words.fold(0) { acc, word -> acc + word.length }
    
    println("Product (fold with 1): $productWithInitial")
    println("Sum (fold with 100): $sumWithInitial")
    println("Total character count: $wordCount")
    
    // Practical fold examples
    val shoppingCart = listOf(
        "Item1" to 19.99,
        "Item2" to 29.99,
        "Item3" to 9.99
    )
    
    val totalCost = shoppingCart.fold(0.0) { total, (_, price) -> total + price }
    val itemSummary = shoppingCart.fold("Cart: ") { summary, (item, price) ->
        "$summary\n  $item: $$price"
    }
    
    println("\nShopping cart:")
    println("Total cost: $${"%.2f".format(totalCost)}")
    println(itemSummary)
    
    // runningFold and runningReduce (scan operations)
    println("\nRunning operations:")
    val runningSum = numbersForReduce.runningFold(0) { acc, element -> acc + element }
    val runningProduct = numbersForReduce.runningReduce { acc, element -> acc * element }
    
    println("Numbers: $numbersForReduce")
    println("Running sum: $runningSum")
    println("Running product: $runningProduct")
    
    // Custom aggregation
    fun <T> List<T>.mode(): T? {
        return groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
    
    val repeatedNumbers = listOf(1, 2, 3, 2, 1, 2, 4, 2, 5)
    println("\nCustom aggregation (mode):")
    println("Numbers: $repeatedNumbers")
    println("Mode (most frequent): ${repeatedNumbers.mode()}")
    
    println()
}

/**
 * ## Grouping Operations
 * 
 * Group collection elements by various criteria for analysis and organization.
 */
fun groupingOperations() {
    println("--- Grouping Operations ---")
    
    val words = listOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    
    // Basic grouping
    println("Basic grouping:")
    val wordsByLength = words.groupBy { it.length }
    val numbersByParity = numbers.groupBy { if (it % 2 == 0) "even" else "odd" }
    
    println("Words: $words")
    println("Grouped by length: $wordsByLength")
    println("Numbers: $numbers")
    println("Grouped by parity: $numbersByParity")
    
    // groupBy with value transformation
    val wordLengthsByFirstChar = words.groupBy(
        keySelector = { it.first() },
        valueTransform = { it.length }
    )
    
    println("\nGroupBy with transformation:")
    println("Word lengths by first char: $wordLengthsByFirstChar")
    
    // Multiple grouping criteria
    data class Student(val name: String, val grade: Char, val subject: String, val score: Int)
    
    val students = listOf(
        Student("Alice", 'A', "Math", 95),
        Student("Bob", 'B', "Math", 85),
        Student("Charlie", 'A', "Science", 92),
        Student("Diana", 'B', "Science", 88),
        Student("Alice", 'A', "English", 90),
        Student("Bob", 'C', "English", 75),
        Student("Eve", 'A', "Math", 98)
    )
    
    println("\nStudent grouping:")
    val studentsByGrade = students.groupBy { it.grade }
    val studentsBySubject = students.groupBy { it.subject }
    val studentsByName = students.groupBy { it.name }
    
    println("By grade:")
    studentsByGrade.forEach { (grade, studentList) ->
        println("  Grade $grade: ${studentList.map { it.name }}")
    }
    
    println("By subject:")
    studentsBySubject.forEach { (subject, studentList) ->
        val avgScore = studentList.map { it.score }.average()
        println("  $subject: ${studentList.size} students, avg score: ${"%.1f".format(avgScore)}")
    }
    
    // Nested grouping
    val nestedGrouping = students.groupBy { it.subject }.mapValues { (_, studentList) ->
        studentList.groupBy { it.grade }
    }
    
    println("\nNested grouping (Subject -> Grade):")
    nestedGrouping.forEach { (subject, gradeGroups) ->
        println("  $subject:")
        gradeGroups.forEach { (grade, studentList) ->
            println("    Grade $grade: ${studentList.map { it.name }}")
        }
    }
    
    // associate operations
    println("\nAssociate operations:")
    val wordToLength = words.associate { it to it.length }
    val lengthToWords = words.associateBy { it.length }
    val wordsWithIndex = words.withIndex().associate { it.value to it.index }
    
    println("Word to length: $wordToLength")
    println("Length to words: $lengthToWords")
    println("Words with index: $wordsWithIndex")
    
    // associateWith and associateBy
    val numbersToSquares = numbers.take(5).associateWith { it * it }
    val squaresToNumbers = numbers.take(5).associateBy { it * it }
    
    println("Numbers to squares: $numbersToSquares")
    println("Squares to numbers: $squaresToNumbers")
    
    // Grouping for analysis
    data class Sale(val product: String, val amount: Double, val region: String, val month: String)
    
    val sales = listOf(
        Sale("Laptop", 1200.0, "North", "Jan"),
        Sale("Mouse", 30.0, "North", "Jan"),
        Sale("Laptop", 1200.0, "South", "Jan"),
        Sale("Keyboard", 80.0, "North", "Feb"),
        Sale("Monitor", 400.0, "South", "Feb"),
        Sale("Mouse", 30.0, "East", "Feb"),
        Sale("Laptop", 1200.0, "North", "Mar")
    )
    
    println("\nSales analysis:")
    
    val salesByRegion = sales.groupBy { it.region }
        .mapValues { (_, salesList) -> salesList.sumOf { it.amount } }
    
    val salesByMonth = sales.groupBy { it.month }
        .mapValues { (_, salesList) -> salesList.sumOf { it.amount } }
    
    val salesByProduct = sales.groupBy { it.product }
        .mapValues { (_, salesList) -> 
            salesList.size to salesList.sumOf { it.amount }
        }
    
    println("Sales by region: $salesByRegion")
    println("Sales by month: $salesByMonth")
    println("Sales by product (count, total):")
    salesByProduct.forEach { (product, data) ->
        println("  $product: ${data.first} sales, total: $${data.second}")
    }
    
    // Chunking (grouping by size)
    println("\nChunking operations:")
    val chunks = numbers.chunked(3)
    val chunkSums = numbers.chunked(4) { chunk -> chunk.sum() }
    
    println("Numbers: $numbers")
    println("Chunked by 3: $chunks")
    println("Chunk sums (size 4): $chunkSums")
    
    // Windowing (sliding groups)
    println("\nWindowing operations:")
    val windows = numbers.take(8).windowed(3)
    val windowAverages = numbers.take(8).windowed(3) { window -> 
        window.average() 
    }
    val windowsWithStep = numbers.take(10).windowed(size = 3, step = 2)
    
    println("Numbers (first 8): ${numbers.take(8)}")
    println("Windows (size 3): $windows")
    println("Window averages: ${windowAverages.map { "%.1f".format(it) }}")
    println("Windows with step 2: $windowsWithStep")
    
    // Grouping with counting
    val letterFrequency = words.flatMap { it.toList() }
        .groupingBy { it }
        .eachCount()
    
    println("\nLetter frequency analysis:")
    println("All letters: ${words.flatMap { it.toList() }}")
    println("Letter frequency: $letterFrequency")
    
    // Most common letters
    val topLetters = letterFrequency.toList()
        .sortedByDescending { it.second }
        .take(5)
    
    println("Top 5 letters: $topLetters")
    
    println()
}

/**
 * ## Sorting Operations
 * 
 * Sort collections using various criteria and custom comparators.
 */
fun sortingOperations() {
    println("--- Sorting Operations ---")
    
    val numbers = listOf(3, 1, 4, 1, 5, 9, 2, 6, 5, 3)
    val words = listOf("banana", "apple", "cherry", "date", "elderberry")
    
    // Basic sorting
    println("Basic sorting:")
    println("Numbers: $numbers")
    println("Sorted: ${numbers.sorted()}")
    println("Sorted descending: ${numbers.sortedDescending()}")
    println("Distinct sorted: ${numbers.distinct().sorted()}")
    
    println("\nWords: $words")
    println("Sorted: ${words.sorted()}")
    println("Sorted descending: ${words.sortedDescending()}")
    
    // Sorting with custom criteria
    println("\nCustom sorting criteria:")
    val wordsByLength = words.sortedBy { it.length }
    val wordsByLengthDesc = words.sortedByDescending { it.length }
    val wordsByLastChar = words.sortedBy { it.last() }
    
    println("Words by length: $wordsByLength")
    println("Words by length (desc): $wordsByLengthDesc")
    println("Words by last char: $wordsByLastChar")
    
    // Complex object sorting
    data class Person(val name: String, val age: Int, val salary: Double)
    
    val people = listOf(
        Person("Alice", 30, 75000.0),
        Person("Bob", 25, 65000.0),
        Person("Charlie", 35, 85000.0),
        Person("Diana", 28, 70000.0),
        Person("Alice", 32, 80000.0)  // Same name, different age
    )
    
    println("\nPerson sorting:")
    println("Original: ${people.map { "${it.name}(${it.age})" }}")
    
    val byName = people.sortedBy { it.name }
    val byAge = people.sortedBy { it.age }
    val bySalary = people.sortedByDescending { it.salary }
    
    println("By name: ${byName.map { "${it.name}(${it.age})" }}")
    println("By age: ${byAge.map { "${it.name}(${it.age})" }}")
    println("By salary (desc): ${bySalary.map { "${it.name}($${it.salary.toInt()})" }}")
    
    // Multiple sorting criteria
    val byNameThenAge = people.sortedWith(compareBy({ it.name }, { it.age }))
    val byAgeThenSalary = people.sortedWith(compareBy<Person> { it.age }.thenByDescending { it.salary })
    
    println("\nMultiple criteria sorting:")
    println("By name then age: ${byNameThenAge.map { "${it.name}(${it.age})" }}")
    println("By age then salary desc: ${byAgeThenSalary.map { "${it.name}(${it.age},$${it.salary.toInt()})" }}")
    
    // Custom comparators
    val customComparator = Comparator<Person> { p1, p2 ->
        when {
            p1.salary != p2.salary -> p1.salary.compareTo(p2.salary)
            p1.age != p2.age -> p2.age.compareTo(p1.age)  // Younger first for same salary
            else -> p1.name.compareTo(p2.name)
        }
    }
    
    val customSorted = people.sortedWith(customComparator)
    println("\nCustom comparator (salary asc, age desc, name asc):")
    println(customSorted.map { "${it.name}(${it.age},$${it.salary.toInt()})" })
    
    // Sorting with nulls
    val numbersWithNulls = listOf(3, null, 1, 4, null, 5, 2)
    println("\nSorting with nulls:")
    println("With nulls: $numbersWithNulls")
    println("Nulls first: ${numbersWithNulls.sortedWith(nullsFirst(naturalOrder()))}")
    println("Nulls last: ${numbersWithNulls.sortedWith(nullsLast(naturalOrder()))}")
    
    // Stable sorting demonstration
    data class Item(val name: String, val priority: Int)
    val items = listOf(
        Item("Task A", 1),
        Item("Task B", 2),
        Item("Task C", 1),
        Item("Task D", 2),
        Item("Task E", 1)
    )
    
    val byPriority = items.sortedBy { it.priority }
    println("\nStable sorting (maintains relative order):")
    println("Original: ${items.map { "${it.name}(${it.priority})" }}")
    println("By priority: ${byPriority.map { "${it.name}(${it.priority})" }}")
    
    // Reverse and shuffle
    println("\nReverse and shuffle:")
    val originalNumbers = listOf(1, 2, 3, 4, 5)
    println("Original: $originalNumbers")
    println("Reversed: ${originalNumbers.reversed()}")
    println("Shuffled: ${originalNumbers.shuffled()}")
    
    // Sorting performance considerations
    println("\nSorting performance notes:")
    println("- sorted(): Creates new collection (O(n log n))")
    println("- sortedBy(): Single-key sorting (O(n log n))")
    println("- sortedWith(): Custom comparator (O(n log n))")
    println("- For mutable collections: sort(), sortBy(), sortWith() modify in-place")
    
    // Practical sorting example
    data class ProductSale(val product: String, val quantity: Int, val revenue: Double, val date: String)
    
    val productSales = listOf(
        ProductSale("Laptop", 5, 5000.0, "2024-01"),
        ProductSale("Mouse", 20, 600.0, "2024-01"),
        ProductSale("Laptop", 3, 3000.0, "2024-02"),
        ProductSale("Keyboard", 10, 800.0, "2024-01"),
        ProductSale("Monitor", 2, 800.0, "2024-02")
    )
    
    println("\nProduct sales analysis:")
    
    // Top revenue products
    val topRevenue = productSales.sortedByDescending { it.revenue }.take(3)
    println("Top revenue: ${topRevenue.map { "${it.product}: $${it.revenue}" }}")
    
    // Products by date then revenue
    val byDateAndRevenue = productSales.sortedWith(
        compareBy<ProductSale> { it.date }.thenByDescending { it.revenue }
    )
    println("By date then revenue:")
    byDateAndRevenue.forEach { sale ->
        println("  ${sale.date}: ${sale.product} - $${sale.revenue}")
    }
    
    println()
}

/**
 * ## Sequence Operations
 * 
 * Use sequences for lazy evaluation and efficient processing of large datasets.
 */
fun sequenceOperations() {
    println("--- Sequence Operations ---")
    
    // Creating sequences
    println("Creating sequences:")
    
    val listSequence = listOf(1, 2, 3, 4, 5).asSequence()
    val rangeSequence = (1..1000000).asSequence()
    val generatedSequence = generateSequence(1) { it * 2 }.take(10)
    val fibonacciSequence = generateSequence(Pair(0, 1)) { (a, b) -> Pair(b, a + b) }
        .map { it.first }
        .take(10)
    
    println("List sequence: ${listSequence.toList()}")
    println("Generated sequence (powers of 2): ${generatedSequence.toList()}")
    println("Fibonacci sequence: ${fibonacciSequence.toList()}")
    
    // Lazy evaluation demonstration
    println("\nLazy evaluation demonstration:")
    
    fun expensiveOperation(x: Int): Int {
        println("  Processing $x")
        Thread.sleep(10)  // Simulate expensive operation
        return x * x
    }
    
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    // List operations (eager) - all elements processed immediately
    println("List operations (eager):")
    val listResult = numbers
        .map { expensiveOperation(it) }
        .filter { it > 25 }
        .take(2)
    println("Result: $listResult")
    
    // Sequence operations (lazy) - only necessary elements processed
    println("\nSequence operations (lazy):")
    val sequenceResult = numbers.asSequence()
        .map { expensiveOperation(it) }
        .filter { it > 25 }
        .take(2)
        .toList()
    println("Result: $sequenceResult")
    
    // Infinite sequences
    println("\nInfinite sequences:")
    
    val naturals = generateSequence(1) { it + 1 }
    val evenNumbers = naturals.filter { it % 2 == 0 }
    val firstTenEvens = evenNumbers.take(10).toList()
    
    println("First 10 even numbers: $firstTenEvens")
    
    // Prime number sequence
    fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        return (2..kotlin.math.sqrt(n.toDouble()).toInt()).none { n % it == 0 }
    }
    
    val primes = generateSequence(2) { it + 1 }
        .filter { isPrime(it) }
    
    val first20Primes = primes.take(20).toList()
    println("First 20 primes: $first20Primes")
    
    // File reading simulation with sequences
    println("\nFile processing with sequences:")
    
    fun simulateFileLines(): Sequence<String> = sequence {
        repeat(1000000) { lineNumber ->
            yield("Line $lineNumber: Some data here")
        }
    }
    
    val fileLines = simulateFileLines()
    val processedLines = fileLines
        .filter { it.contains("data") }
        .map { it.uppercase() }
        .take(5)
        .toList()
    
    println("Processed file lines: $processedLines")
    
    // Sequence transformations
    println("\nSequence transformations:")
    
    val wordSequence = sequenceOf("apple", "banana", "cherry", "date", "elderberry")
    
    val transformedSequence = wordSequence
        .filter { it.length > 5 }
        .map { it.uppercase() }
        .map { "FRUIT: $it" }
    
    println("Transformed sequence: ${transformedSequence.toList()}")
    
    // Performance comparison
    println("\nPerformance comparison:")
    
    fun measureTime(description: String, operation: () -> Unit) {
        val start = System.nanoTime()
        operation()
        val duration = (System.nanoTime() - start) / 1_000_000
        println("$description: ${duration}ms")
    }
    
    val largeList = (1..1000000).toList()
    
    measureTime("List processing") {
        largeList
            .map { it * 2 }
            .filter { it > 1000000 }
            .take(10)
    }
    
    measureTime("Sequence processing") {
        largeList.asSequence()
            .map { it * 2 }
            .filter { it > 1000000 }
            .take(10)
            .toList()
    }
    
    // When to use sequences
    println("\nWhen to use sequences:")
    println("✅ Large datasets where you don't need all results")
    println("✅ Multiple chained operations")
    println("✅ Expensive operations that can be avoided")
    println("✅ Infinite or very large data streams")
    println("❌ Small collections (overhead not worth it)")
    println("❌ When you need random access to elements")
    println("❌ When collection will be reused multiple times")
    
    // Sequence termination operations
    println("\nSequence termination operations:")
    val testSequence = (1..10).asSequence()
    
    // These operations trigger evaluation
    println("First element: ${testSequence.first()}")
    println("Any > 5: ${testSequence.any { it > 5 }}")
    println("Count of evens: ${testSequence.count { it % 2 == 0 }}")
    println("Sum: ${testSequence.sum()}")
    
    // Multiple terminal operations require re-creating sequence
    val reusableSequence = (1..5).asSequence().map { it * it }
    println("Squares: ${reusableSequence.toList()}")
    // println("Sum of squares: ${reusableSequence.sum()}")  // Would fail - sequence already consumed
    
    val freshSequence = (1..5).asSequence().map { it * it }
    println("Sum of squares: ${freshSequence.sum()}")
    
    println()
}

/**
 * ## Advanced Operations
 * 
 * Advanced collection operations for complex data processing scenarios.
 */
fun advancedOperations() {
    println("--- Advanced Operations ---")
    
    // Fold and reduce variations
    println("Advanced fold and reduce:")
    
    val numbers = listOf(1, 2, 3, 4, 5)
    
    // foldIndexed - fold with index
    val indexedSum = numbers.foldIndexed(0) { index, acc, element ->
        acc + (index * element)
    }
    println("Indexed sum (index * element): $indexedSum")
    
    // reduceIndexed - reduce with index
    val indexedProduct = numbers.reduceIndexed { index, acc, element ->
        if (index % 2 == 0) acc * element else acc
    }
    println("Indexed product (even indices only): $indexedProduct")
    
    // scan operations (running operations)
    val runningSums = numbers.scan(0) { acc, element -> acc + element }
    val runningProducts = numbers.scan(1) { acc, element -> acc * element }
    
    println("Numbers: $numbers")
    println("Running sums: $runningSums")
    println("Running products: $runningProducts")
    
    // Advanced filtering
    println("\nAdvanced filtering:")
    
    data class Order(val id: String, val amount: Double, val status: String, val customerId: String)
    
    val orders = listOf(
        Order("O001", 100.0, "completed", "C001"),
        Order("O002", 250.0, "pending", "C002"),
        Order("O003", 50.0, "completed", "C001"),
        Order("O004", 300.0, "cancelled", "C003"),
        Order("O005", 150.0, "completed", "C002")
    )
    
    // Complex filtering with multiple conditions
    val completedHighValueOrders = orders
        .filter { it.status == "completed" && it.amount > 75.0 }
        .sortedByDescending { it.amount }
    
    println("Completed high-value orders:")
    completedHighValueOrders.forEach { order ->
        println("  ${order.id}: $${order.amount} for ${order.customerId}")
    }
    
    // Advanced grouping with transformations
    val customerOrderSummary = orders
        .filter { it.status == "completed" }
        .groupBy { it.customerId }
        .mapValues { (_, customerOrders) ->
            mapOf(
                "count" to customerOrders.size,
                "total" to customerOrders.sumOf { it.amount },
                "average" to customerOrders.map { it.amount }.average()
            )
        }
    
    println("\nCustomer order summary:")
    customerOrderSummary.forEach { (customerId, summary) ->
        println("  $customerId: ${summary["count"]} orders, total: $${summary["total"]}, avg: $${"%.2f".format(summary["average"])}")
    }
    
    // flatMap with complex transformations
    data class Department(val name: String, val employees: List<String>)
    
    val departments = listOf(
        Department("Engineering", listOf("Alice", "Bob", "Charlie")),
        Department("Marketing", listOf("Diana", "Eve")),
        Department("Sales", listOf("Frank", "Grace", "Henry"))
    )
    
    val allEmployeesWithDept = departments.flatMap { dept ->
        dept.employees.map { employee -> "$employee (${dept.name})" }
    }
    
    println("\nAll employees with departments:")
    allEmployeesWithDept.forEach { println("  $it") }
    
    // Advanced map operations
    data class Product(val name: String, val price: Double, val category: String)
    
    val products = listOf(
        Product("Laptop", 1000.0, "Electronics"),
        Product("Mouse", 25.0, "Electronics"),
        Product("Chair", 200.0, "Furniture"),
        Product("Desk", 300.0, "Furniture")
    )
    
    // Create complex mappings
    val categoryStats = products
        .groupBy { it.category }
        .mapValues { (_, categoryProducts) ->
            mapOf(
                "count" to categoryProducts.size,
                "totalValue" to categoryProducts.sumOf { it.price },
                "avgPrice" to categoryProducts.map { it.price }.average(),
                "priceRange" to (categoryProducts.minOfOrNull { it.price } to categoryProducts.maxOfOrNull { it.price })
            )
        }
    
    println("\nCategory statistics:")
    categoryStats.forEach { (category, stats) ->
        println("  $category:")
        println("    Count: ${stats["count"]}")
        println("    Total value: $${stats["totalValue"]}")
        println("    Average price: $${"%.2f".format(stats["avgPrice"])}")
        val range = stats["priceRange"] as Pair<*, *>
        println("    Price range: $${range.first} - $${range.second}")
    }
    
    // Combining multiple collections
    println("\nCombining collections:")
    
    val customerNames = listOf("Alice", "Bob", "Charlie")
    val customerAges = listOf(30, 25, 35)
    val customerCities = listOf("NYC", "LA", "Chicago")
    
    data class CustomerProfile(val name: String, val age: Int, val city: String)
    
    val customerProfiles = customerNames.zip(customerAges).zip(customerCities) { (name, age), city ->
        CustomerProfile(name, age, city)
    }
    
    println("Customer profiles:")
    customerProfiles.forEach { profile ->
        println("  ${profile.name}, ${profile.age} years old from ${profile.city}")
    }
    
    // Advanced sequence operations
    println("\nAdvanced sequence operations:")
    
    // Sequence with state
    fun movingAverage(values: Sequence<Double>, windowSize: Int): Sequence<Double> = sequence {
        val buffer = ArrayDeque<Double>()
        
        for (value in values) {
            buffer.addLast(value)
            if (buffer.size > windowSize) {
                buffer.removeFirst()
            }
            
            if (buffer.size == windowSize) {
                yield(buffer.average())
            }
        }
    }
    
    val dataPoints = sequenceOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
    val movingAverages = movingAverage(dataPoints, 3).toList()
    
    println("Moving averages (window size 3): $movingAverages")
    
    // Custom collectors
    inline fun <T, R> Iterable<T>.collectTo(
        initial: R,
        operation: R.(T) -> Unit
    ): R {
        val result = initial
        for (element in this) {
            result.operation(element)
        }
        return result
    }
    
    val wordStats = listOf("hello", "world", "kotlin", "programming")
        .collectTo(mutableMapOf<String, Any>()) { word ->
            this["totalLength"] = (this["totalLength"] as? Int ?: 0) + word.length
            this["wordCount"] = (this["wordCount"] as? Int ?: 0) + 1
            this["longestWord"] = if (word.length > (this["longestWord"] as? String ?: "").length) word else (this["longestWord"] ?: "")
        }
    
    println("\nWord statistics: $wordStats")
    
    println()
}

/**
 * ## Real-World Data Processing
 * 
 * Complex real-world scenarios demonstrating functional operations in practice.
 */
fun realWorldDataProcessing() {
    println("--- Real-World Data Processing ---")
    
    // 1. E-commerce Analytics
    data class Transaction(
        val id: String,
        val customerId: String,
        val productId: String,
        val productName: String,
        val category: String,
        val amount: Double,
        val quantity: Int,
        val timestamp: String,
        val region: String
    )
    
    val transactions = listOf(
        Transaction("T001", "C001", "P001", "Laptop", "Electronics", 1200.0, 1, "2024-01-15", "North"),
        Transaction("T002", "C002", "P002", "Mouse", "Electronics", 25.0, 2, "2024-01-15", "South"),
        Transaction("T003", "C001", "P003", "Keyboard", "Electronics", 75.0, 1, "2024-01-16", "North"),
        Transaction("T004", "C003", "P004", "Chair", "Furniture", 250.0, 1, "2024-01-16", "East"),
        Transaction("T005", "C002", "P001", "Laptop", "Electronics", 1200.0, 1, "2024-01-17", "South"),
        Transaction("T006", "C004", "P005", "Desk", "Furniture", 400.0, 1, "2024-01-17", "West"),
        Transaction("T007", "C001", "P002", "Mouse", "Electronics", 25.0, 3, "2024-01-18", "North"),
        Transaction("T008", "C003", "P003", "Keyboard", "Electronics", 75.0, 2, "2024-01-18", "East")
    )
    
    println("E-commerce Analytics:")
    
    // Total revenue analysis
    val totalRevenue = transactions.sumOf { it.amount * it.quantity }
    println("Total revenue: $${"%.2f".format(totalRevenue)}")
    
    // Top products by revenue
    val productRevenue = transactions
        .groupBy { it.productName }
        .mapValues { (_, txns) -> 
            txns.sumOf { it.amount * it.quantity } 
        }
        .toList()
        .sortedByDescending { it.second }
        .take(3)
    
    println("Top products by revenue:")
    productRevenue.forEach { (product, revenue) ->
        println("  $product: $${"%.2f".format(revenue)}")
    }
    
    // Customer analysis
    val customerAnalysis = transactions
        .groupBy { it.customerId }
        .mapValues { (_, txns) ->
            mapOf(
                "transactionCount" to txns.size,
                "totalSpent" to txns.sumOf { it.amount * it.quantity },
                "avgTransactionSize" to txns.map { it.amount * it.quantity }.average(),
                "favoriteCategory" to txns.groupBy { it.category }
                    .maxByOrNull { it.value.size }?.key ?: "None"
            )
        }
    
    println("\nCustomer analysis:")
    customerAnalysis.forEach { (customerId, stats) ->
        println("  $customerId:")
        println("    Transactions: ${stats["transactionCount"]}")
        println("    Total spent: $${"%.2f".format(stats["totalSpent"])}")
        println("    Avg transaction: $${"%.2f".format(stats["avgTransactionSize"])}")
        println("    Favorite category: ${stats["favoriteCategory"]}")
    }
    
    // Regional performance
    val regionalStats = transactions
        .groupBy { it.region }
        .mapValues { (_, regionTxns) ->
            mapOf(
                "revenue" to regionTxns.sumOf { it.amount * it.quantity },
                "transactionCount" to regionTxns.size,
                "avgOrderValue" to regionTxns.map { it.amount * it.quantity }.average(),
                "topProduct" to regionTxns.groupBy { it.productName }
                    .maxByOrNull { it.value.size }?.key ?: "None"
            )
        }
        .toList()
        .sortedByDescending { (_, stats) -> stats["revenue"] as Double }
    
    println("\nRegional performance (by revenue):")
    regionalStats.forEach { (region, stats) ->
        println("  $region: $${stats["revenue"]}, ${stats["transactionCount"]} transactions, top product: ${stats["topProduct"]}")
    }
    
    // 2. Log Analysis
    data class LogEntry(
        val timestamp: String,
        val level: String,
        val service: String,
        val message: String,
        val responseTime: Int? = null
    )
    
    val logs = listOf(
        LogEntry("2024-01-15 10:00:01", "INFO", "api", "Request processed", 120),
        LogEntry("2024-01-15 10:00:02", "ERROR", "database", "Connection timeout", 5000),
        LogEntry("2024-01-15 10:00:03", "INFO", "api", "Request processed", 95),
        LogEntry("2024-01-15 10:00:04", "WARN", "cache", "Cache miss", 50),
        LogEntry("2024-01-15 10:00:05", "ERROR", "api", "Invalid request", null),
        LogEntry("2024-01-15 10:00:06", "INFO", "database", "Query executed", 200),
        LogEntry("2024-01-15 10:00:07", "INFO", "api", "Request processed", 110),
        LogEntry("2024-01-15 10:00:08", "ERROR", "database", "Deadlock detected", 3000)
    )
    
    println("\nLog Analysis:")
    
    // Error rate by service
    val errorRates = logs
        .groupBy { it.service }
        .mapValues { (_, serviceLogs) ->
            val totalCount = serviceLogs.size
            val errorCount = serviceLogs.count { it.level == "ERROR" }
            (errorCount.toDouble() / totalCount * 100)
        }
        .toList()
        .sortedByDescending { it.second }
    
    println("Error rates by service:")
    errorRates.forEach { (service, errorRate) ->
        println("  $service: ${"%.1f".format(errorRate)}%")
    }
    
    // Performance analysis
    val performanceStats = logs
        .filter { it.responseTime != null }
        .groupBy { it.service }
        .mapValues { (_, serviceLogs) ->
            val responseTimes = serviceLogs.mapNotNull { it.responseTime }
            mapOf(
                "avgResponseTime" to responseTimes.average(),
                "maxResponseTime" to responseTimes.maxOrNull(),
                "minResponseTime" to responseTimes.minOrNull(),
                "slowRequests" to responseTimes.count { it > 1000 }
            )
        }
    
    println("\nPerformance statistics:")
    performanceStats.forEach { (service, stats) ->
        println("  $service:")
        println("    Avg response time: ${"%.1f".format(stats["avgResponseTime"])}ms")
        println("    Max response time: ${stats["maxResponseTime"]}ms")
        println("    Slow requests (>1000ms): ${stats["slowRequests"]}")
    }
    
    // 3. Sensor Data Processing
    data class SensorReading(
        val sensorId: String,
        val timestamp: String,
        val temperature: Double,
        val humidity: Double,
        val location: String
    )
    
    val sensorData = listOf(
        SensorReading("S001", "2024-01-15 09:00", 22.5, 45.0, "Room A"),
        SensorReading("S002", "2024-01-15 09:00", 23.1, 48.5, "Room B"),
        SensorReading("S001", "2024-01-15 09:15", 22.8, 46.2, "Room A"),
        SensorReading("S002", "2024-01-15 09:15", 23.4, 49.1, "Room B"),
        SensorReading("S003", "2024-01-15 09:00", 21.9, 44.8, "Room C"),
        SensorReading("S001", "2024-01-15 09:30", 23.2, 47.5, "Room A"),
        SensorReading("S003", "2024-01-15 09:15", 22.1, 45.5, "Room C"),
        SensorReading("S002", "2024-01-15 09:30", 23.7, 50.2, "Room B")
    )
    
    println("\nSensor Data Analysis:")
    
    // Environmental averages by location
    val locationAverages = sensorData
        .groupBy { it.location }
        .mapValues { (_, readings) ->
            mapOf(
                "avgTemperature" to readings.map { it.temperature }.average(),
                "avgHumidity" to readings.map { it.humidity }.average(),
                "readingCount" to readings.size
            )
        }
    
    println("Environmental averages by location:")
    locationAverages.forEach { (location, stats) ->
        println("  $location: ${"%.1f".format(stats["avgTemperature"])}°C, ${"%.1f".format(stats["avgHumidity"])}% humidity (${stats["readingCount"]} readings)")
    }
    
    // Sensor performance tracking
    val sensorPerformance = sensorData
        .groupBy { it.sensorId }
        .mapValues { (_, readings) ->
            val temperatures = readings.map { it.temperature }
            val humidities = readings.map { it.humidity }
            
            mapOf(
                "location" to readings.first().location,
                "readingCount" to readings.size,
                "tempRange" to (temperatures.minOrNull() to temperatures.maxOrNull()),
                "tempStability" to (temperatures.maxOrNull()!! - temperatures.minOrNull()!!),
                "avgHumidity" to humidities.average()
            )
        }
        .toList()
        .sortedBy { (_, stats) -> stats["tempStability"] as Double }
    
    println("\nSensor performance (by temperature stability):")
    sensorPerformance.forEach { (sensorId, stats) ->
        val tempRange = stats["tempRange"] as Pair<*, *>
        println("  $sensorId (${stats["location"]}): temp range ${tempRange.first}-${tempRange.second}°C, stability: ${"%.2f".format(stats["tempStability"])}")
    }
    
    // Alert detection
    val alerts = sensorData
        .filter { it.temperature > 23.5 || it.humidity > 50.0 }
        .map { reading ->
            val alertType = when {
                reading.temperature > 23.5 -> "High Temperature"
                reading.humidity > 50.0 -> "High Humidity"
                else -> "Unknown"
            }
            "$alertType at ${reading.location} (Sensor ${reading.sensorId}): ${reading.temperature}°C, ${reading.humidity}%"
        }
    
    println("\nEnvironmental alerts:")
    alerts.forEach { alert ->
        println("  🚨 $alert")
    }
    
    println("\nProcessing complete! ${transactions.size} transactions, ${logs.size} log entries, and ${sensorData.size} sensor readings analyzed.")
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice functional operations:
 * 
 * 1. Build a grade analyzer with statistical functions
 * 2. Create a text analyzer with word frequency and readability metrics  
 * 3. Implement a sales report generator with multiple aggregations
 * 4. Build a playlist analyzer for music streaming data
 * 5. Create a performance monitoring system for application metrics
 */

// TODO: Exercise 1 - Grade Analyzer
data class StudentGrade(
    val studentName: String,
    val subject: String,
    val score: Double,
    val maxScore: Double,
    val semester: String
)

class GradeAnalyzer(private val grades: List<StudentGrade>) {
    // TODO: Implement statistical analysis functions
    
    fun getClassAverage(subject: String): Double {
        // TODO: Calculate class average for a subject
        return 0.0
    }
    
    fun getStudentPerformance(studentName: String): Map<String, Any> {
        // TODO: Return student's overall performance metrics
        // Include: GPA, best subject, worst subject, trend analysis
        return emptyMap()
    }
    
    fun getSubjectStatistics(): Map<String, Map<String, Double>> {
        // TODO: Return statistics for each subject
        // Include: mean, median, std deviation, min, max
        return emptyMap()
    }
    
    fun getTopPerformers(count: Int): List<Pair<String, Double>> {
        // TODO: Return top N students by GPA
        return emptyList()
    }
}

// TODO: Exercise 2 - Text Analyzer  
class TextAnalyzer(private val text: String) {
    // TODO: Implement comprehensive text analysis
    
    fun getWordFrequency(): Map<String, Int> {
        // TODO: Return word frequency map (case-insensitive)
        return emptyMap()
    }
    
    fun getReadabilityScore(): Double {
        // TODO: Calculate readability score (e.g., Flesch Reading Ease)
        return 0.0
    }
    
    fun getSentenceAnalysis(): Map<String, Any> {
        // TODO: Analyze sentences - count, avg length, complexity
        return emptyMap()
    }
    
    fun getCommonPhrases(minLength: Int, topN: Int): List<Pair<String, Int>> {
        // TODO: Find most common phrases of at least minLength words
        return emptyList()
    }
}

// TODO: Exercise 3 - Sales Report Generator
data class SaleRecord(
    val date: String,
    val productId: String,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val salesPersonId: String,
    val region: String
)

class SalesReportGenerator(private val sales: List<SaleRecord>) {
    // TODO: Generate comprehensive sales reports
    
    fun generateMonthlySummary(month: String): Map<String, Any> {
        // TODO: Total revenue, units sold, avg order size, top products
        return emptyMap()
    }
    
    fun getSalesPersonPerformance(): Map<String, Map<String, Any>> {
        // TODO: Performance metrics for each salesperson
        return emptyMap()
    }
    
    fun getProductAnalysis(): List<Map<String, Any>> {
        // TODO: Product performance analysis with trends
        return emptyList()
    }
    
    fun getRegionalInsights(): Map<String, Map<String, Any>> {
        // TODO: Regional sales analysis and comparisons
        return emptyMap()
    }
}

// TODO: Exercise 4 - Playlist Analyzer
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val duration: Int, // seconds
    val releaseYear: Int
)

data class PlayEvent(
    val songId: String,
    val userId: String,
    val timestamp: String,
    val playDuration: Int, // seconds actually played
    val skipped: Boolean
)

class PlaylistAnalyzer(private val songs: List<Song>, private val playEvents: List<PlayEvent>) {
    // TODO: Analyze music streaming data
    
    fun getMostPlayedSongs(limit: Int): List<Pair<Song, Int>> {
        // TODO: Return most played songs with play counts
        return emptyList()
    }
    
    fun getUserListeningHabits(userId: String): Map<String, Any> {
        // TODO: Analyze user's listening patterns
        // Include: favorite genres, avg session length, skip rate
        return emptyMap()
    }
    
    fun getGenrePopularity(): Map<String, Map<String, Any>> {
        // TODO: Genre analysis - popularity, avg play time, user engagement
        return emptyMap()
    }
    
    fun getSkipAnalysis(): Map<String, Any> {
        // TODO: Analyze why songs are skipped - patterns by genre, duration, etc.
        return emptyMap()
    }
}

// TODO: Exercise 5 - Performance Monitor
data class PerformanceMetric(
    val timestamp: String,
    val service: String,
    val operation: String,
    val responseTime: Long, // milliseconds
    val success: Boolean,
    val errorType: String? = null,
    val memoryUsage: Long, // bytes
    val cpuUsage: Double // percentage
)

class PerformanceMonitor(private val metrics: List<PerformanceMetric>) {
    // TODO: Monitor and analyze application performance
    
    fun getServiceHealth(): Map<String, Map<String, Any>> {
        // TODO: Overall health metrics per service
        // Include: success rate, avg response time, error distribution
        return emptyMap()
    }
    
    fun getPerformanceTrends(timeWindow: String): Map<String, List<Double>> {
        // TODO: Performance trends over time (response time, error rate, resource usage)
        return emptyMap()
    }
    
    fun detectAnomalies(): List<String> {
        // TODO: Detect performance anomalies (unusually high response times, error spikes)
        return emptyList()
    }
    
    fun getResourceUtilization(): Map<String, Map<String, Double>> {
        // TODO: Resource utilization analysis (memory, CPU) by service
        return emptyMap()
    }
    
    fun generateAlerts(): List<String> {
        // TODO: Generate alerts based on performance thresholds
        return emptyList()
    }
}