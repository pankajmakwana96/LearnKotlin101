package com.kotlinmastery.intermediate.collections

/**
 * # Collection Types in Kotlin
 * 
 * Kotlin provides a rich collections framework with immutable and mutable variants.
 * This module covers List, Set, Map, and their characteristics, creation patterns, and usage.
 * 
 * ## Learning Objectives
 * - Understand immutable vs mutable collections
 * - Master List, Set, and Map creation and operations
 * - Work with collection interfaces and implementations
 * - Apply appropriate collection types for different scenarios
 * - Handle collection conversions and transformations
 * 
 * ## Prerequisites: Basic OOP, functions, and control flow
 * ## Estimated Time: 5 hours
 */

fun main() {
    println("=== Kotlin Collection Types Demo ===\n")
    
    collectionHierarchy()
    listCollections()
    setCollections()
    mapCollections()
    mutableVsImmutable()
    collectionCreation()
    collectionConversions()
    realWorldExamples()
}

/**
 * ## Collection Hierarchy
 * 
 * Understanding Kotlin's collection hierarchy and the relationship between interfaces.
 */
fun collectionHierarchy() {
    println("--- Collection Hierarchy ---")
    
    // Collection interfaces hierarchy:
    // Iterable<T>
    //   └── Collection<T>
    //       ├── List<T>
    //       └── Set<T>
    // Map<K, V> (separate hierarchy)
    
    // Demonstrating collection types
    val numbers: Collection<Int> = listOf(1, 2, 3, 4, 5)
    val uniqueNumbers: Collection<Int> = setOf(1, 2, 3, 4, 5)
    val numberMap: Map<String, Int> = mapOf("one" to 1, "two" to 2)
    
    println("Collection types demonstration:")
    println("List as Collection: $numbers")
    println("Set as Collection: $uniqueNumbers")
    println("Map (separate hierarchy): $numberMap")
    
    // Iterable operations (available to all collections)
    fun demonstrateIterable(iterable: Iterable<Int>, name: String) {
        println("\n$name iterable operations:")
        println("  Elements: ${iterable.joinToString(", ")}")
        println("  First element: ${iterable.first()}")
        println("  Contains 3: ${3 in iterable}")
        println("  Count: ${iterable.count()}")
        println("  Any > 3: ${iterable.any { it > 3 }}")
    }
    
    demonstrateIterable(numbers, "List")
    demonstrateIterable(uniqueNumbers, "Set")
    
    // Collection operations (size, isEmpty, contains)
    fun demonstrateCollection(collection: Collection<Int>, name: String) {
        println("\n$name collection operations:")
        println("  Size: ${collection.size}")
        println("  Is empty: ${collection.isEmpty()}")
        println("  Contains all [1,2,3]: ${collection.containsAll(listOf(1, 2, 3))}")
    }
    
    demonstrateCollection(numbers, "List")
    demonstrateCollection(uniqueNumbers, "Set")
    
    // Map operations
    println("\nMap operations:")
    println("  Keys: ${numberMap.keys}")
    println("  Values: ${numberMap.values}")
    println("  Entries: ${numberMap.entries}")
    println("  Contains key 'one': ${"one" in numberMap}")
    println("  Contains value 3: ${3 in numberMap.values}")
    
    // Type relationships
    println("\nType relationships:")
    val list = listOf(1, 2, 3)
    val set = setOf(1, 2, 3)
    
    println("List is Collection: ${list is Collection<*>}")
    println("List is Iterable: ${list is Iterable<*>}")
    println("Set is Collection: ${set is Collection<*>}")
    println("Set is Iterable: ${set is Iterable<*>}")
    println("Map is Collection: ${numberMap is Collection<*>}")  // false
    println("Map is Iterable: ${numberMap is Iterable<*>}")      // false
    
    println()
}

/**
 * ## List Collections
 * 
 * Lists are ordered collections that allow duplicate elements and indexed access.
 */
fun listCollections() {
    println("--- List Collections ---")
    
    // Creating lists
    val emptyList = emptyList<String>()
    val singletonList = listOf("single")
    val numberList = listOf(1, 2, 3, 4, 5, 3, 2, 1)  // Duplicates allowed
    val mixedList = listOf("apple", 42, true, 3.14)   // Mixed types
    
    println("List creation:")
    println("Empty list: $emptyList")
    println("Singleton list: $singletonList")
    println("Number list (with duplicates): $numberList")
    println("Mixed list: $mixedList")
    
    // List-specific operations
    println("\nList indexing and access:")
    val fruits = listOf("apple", "banana", "cherry", "date", "elderberry")
    
    println("Fruits: $fruits")
    println("First fruit: ${fruits[0]} or ${fruits.first()}")
    println("Last fruit: ${fruits[fruits.size - 1]} or ${fruits.last()}")
    println("Third fruit: ${fruits[2]} or ${fruits.get(2)}")
    println("Index of 'cherry': ${fruits.indexOf("cherry")}")
    println("Last index of 'apple': ${fruits.lastIndexOf("apple")}")
    
    // Safe access
    println("\nSafe access:")
    println("Element at index 10: ${fruits.getOrNull(10)}")
    println("Element at index 10 with default: ${fruits.getOrElse(10) { "Not found" }}")
    
    // Sublist operations
    println("\nSublist operations:")
    println("Sublist (1..3): ${fruits.subList(1, 4)}")  // End index exclusive
    println("Take 3: ${fruits.take(3)}")
    println("Drop 2: ${fruits.drop(2)}")
    println("Take last 2: ${fruits.takeLast(2)}")
    println("Drop last 2: ${fruits.dropLast(2)}")
    
    // List with different implementations
    val arrayList = arrayListOf("a", "b", "c")  // ArrayList implementation
    val linkedList = mutableListOf("x", "y", "z")  // Default mutable list
    
    println("\nDifferent list implementations:")
    println("ArrayList: $arrayList (${arrayList.javaClass.simpleName})")
    println("MutableList: $linkedList (${linkedList.javaClass.simpleName})")
    
    // List operations performance characteristics
    println("\nList characteristics:")
    println("- Ordered: Elements maintain insertion order")
    println("- Indexed: O(1) random access by index")
    println("- Duplicates: Allows duplicate elements")
    println("- ArrayList: Good for random access, poor for insertions/deletions")
    println("- LinkedList: Good for insertions/deletions, poor for random access")
    
    // Working with indices
    println("\nWorking with indices:")
    fruits.forEachIndexed { index, fruit ->
        println("[$index] $fruit")
    }
    
    // List comprehensions (using builders)
    val squares = List(5) { index -> index * index }
    val evenNumbers = List(10) { index -> index * 2 }
    
    println("\nGenerated lists:")
    println("Squares: $squares")
    println("Even numbers: $evenNumbers")
    
    // Nested lists
    val matrix = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    
    println("\nNested lists (matrix):")
    matrix.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, value ->
            print("[$rowIndex,$colIndex]=$value ")
        }
        println()
    }
    
    println()
}

/**
 * ## Set Collections
 * 
 * Sets are collections that contain unique elements with no defined order.
 */
fun setCollections() {
    println("--- Set Collections ---")
    
    // Creating sets
    val emptySet = emptySet<Int>()
    val numberSet = setOf(1, 2, 3, 4, 5, 3, 2, 1)  // Duplicates removed
    val stringSet = setOf("apple", "banana", "apple")
    
    println("Set creation:")
    println("Empty set: $emptySet")
    println("Number set (duplicates removed): $numberSet")
    println("String set: $stringSet")
    
    // Set-specific operations
    val fruits = setOf("apple", "banana", "cherry")
    val citrus = setOf("orange", "lemon", "lime")
    val tropicalFruits = setOf("banana", "mango", "pineapple")
    
    println("\nSet operations:")
    println("Fruits: $fruits")
    println("Citrus: $citrus")
    println("Tropical: $tropicalFruits")
    
    // Union (all elements from both sets)
    val allFruits = fruits union citrus
    println("Union (fruits ∪ citrus): $allFruits")
    
    // Intersection (common elements)
    val commonFruits = fruits intersect tropicalFruits
    println("Intersection (fruits ∩ tropical): $commonFruits")
    
    // Difference (elements in first set but not in second)
    val nonTropicalFruits = fruits subtract tropicalFruits
    println("Difference (fruits - tropical): $nonTropicalFruits")
    
    // Set membership
    println("\nSet membership:")
    println("Contains 'apple': ${"apple" in fruits}")
    println("Contains 'mango': ${"mango" in fruits}")
    
    // Different set implementations
    val hashSet = hashSetOf("a", "b", "c")      // HashSet - no order
    val linkedHashSet = linkedSetOf("x", "y", "z")  // LinkedHashSet - insertion order
    val sortedSet = sortedSetOf("zebra", "apple", "banana")  // TreeSet - sorted order
    
    println("\nDifferent set implementations:")
    println("HashSet: $hashSet (${hashSet.javaClass.simpleName})")
    println("LinkedHashSet: $linkedHashSet (${linkedHashSet.javaClass.simpleName})")
    println("SortedSet: $sortedSet (${sortedSet.javaClass.simpleName})")
    
    // Set characteristics and performance
    println("\nSet characteristics:")
    println("- Unique elements: No duplicates allowed")
    println("- HashSet: O(1) average lookup, no order guarantee")
    println("- LinkedHashSet: O(1) average lookup, maintains insertion order")
    println("- TreeSet: O(log n) lookup, maintains sorted order")
    
    // Practical set usage
    val userPermissions = setOf("read", "write", "execute")
    val adminPermissions = setOf("read", "write", "execute", "delete", "admin")
    val guestPermissions = setOf("read")
    
    println("\nPermission system example:")
    println("User permissions: $userPermissions")
    println("Admin permissions: $adminPermissions")
    println("Guest permissions: $guestPermissions")
    
    // Check if user has admin privileges
    val hasAllAdminPermissions = adminPermissions.containsAll(userPermissions)
    println("User has all admin permissions: $hasAllAdminPermissions")
    
    // Additional permissions admin has
    val additionalAdminPermissions = adminPermissions subtract userPermissions
    println("Additional admin permissions: $additionalAdminPermissions")
    
    // Working with custom objects in sets
    data class Person(val name: String, val age: Int)
    
    val people = setOf(
        Person("Alice", 30),
        Person("Bob", 25),
        Person("Alice", 30),  // Duplicate (same data) - will be removed
        Person("Charlie", 35)
    )
    
    println("\nCustom objects in sets:")
    println("People set: $people")
    println("Set size: ${people.size}")  // Should be 3, not 4
    
    // Converting between collections
    val listFromSet = fruits.toList()
    val setFromList = listOf("a", "b", "a", "c", "b").toSet()
    
    println("\nConversions:")
    println("Set to list: $listFromSet")
    println("List to set (removes duplicates): $setFromList")
    
    println()
}

/**
 * ## Map Collections
 * 
 * Maps store key-value pairs with unique keys.
 */
fun mapCollections() {
    println("--- Map Collections ---")
    
    // Creating maps
    val emptyMap = emptyMap<String, Int>()
    val numberMap = mapOf("one" to 1, "two" to 2, "three" to 3)
    val mixedMap = mapOf(1 to "one", 2.5 to "two-and-half", true to "boolean")
    
    println("Map creation:")
    println("Empty map: $emptyMap")
    println("Number map: $numberMap")
    println("Mixed map: $mixedMap")
    
    // Map access
    val capitals = mapOf(
        "USA" to "Washington D.C.",
        "UK" to "London",
        "France" to "Paris",
        "Germany" to "Berlin",
        "Japan" to "Tokyo"
    )
    
    println("\nMap access:")
    println("Capitals: $capitals")
    println("Capital of USA: ${capitals["USA"]}")
    println("Capital of Italy: ${capitals["Italy"]}")  // null
    println("Capital of Italy (with default): ${capitals.getOrDefault("Italy", "Unknown")}")
    println("Capital of France (with elvis): ${capitals["France"] ?: "Unknown"}")
    
    // Map operations
    println("\nMap operations:")
    println("Keys: ${capitals.keys}")
    println("Values: ${capitals.values}")
    println("Entries: ${capitals.entries}")
    println("Size: ${capitals.size}")
    println("Is empty: ${capitals.isEmpty()}")
    println("Contains key 'USA': ${capitals.containsKey("USA")}")
    println("Contains value 'Tokyo': ${capitals.containsValue("Tokyo")}")
    
    // Iterating over maps
    println("\nIterating over maps:")
    
    // Using entries
    for ((country, capital) in capitals) {
        println("$country -> $capital")
    }
    
    // Using forEach
    println("\nUsing forEach:")
    capitals.forEach { (country, capital) ->
        println("The capital of $country is $capital")
    }
    
    // Different map implementations
    val hashMap = hashMapOf("a" to 1, "b" to 2)        // HashMap - no order
    val linkedHashMap = linkedMapOf("x" to 1, "y" to 2)  // LinkedHashMap - insertion order
    val sortedMap = sortedMapOf("zebra" to 1, "apple" to 2, "banana" to 3)  // TreeMap - key order
    
    println("\nDifferent map implementations:")
    println("HashMap: $hashMap (${hashMap.javaClass.simpleName})")
    println("LinkedHashMap: $linkedHashMap (${linkedHashMap.javaClass.simpleName})")
    println("SortedMap: $sortedMap (${sortedMap.javaClass.simpleName})")
    
    // Map characteristics
    println("\nMap characteristics:")
    println("- Key-value pairs: Each key maps to exactly one value")
    println("- Unique keys: Keys must be unique, values can be duplicated")
    println("- HashMap: O(1) average lookup, no order guarantee")
    println("- LinkedHashMap: O(1) average lookup, maintains insertion order")
    println("- TreeMap: O(log n) lookup, maintains key sort order")
    
    // Complex maps
    val studentGrades = mapOf(
        "Alice" to mapOf("Math" to 95, "Science" to 88, "English" to 92),
        "Bob" to mapOf("Math" to 78, "Science" to 85, "English" to 90),
        "Charlie" to mapOf("Math" to 92, "Science" to 94, "English" to 87)
    )
    
    println("\nNested maps (student grades):")
    studentGrades.forEach { (student, grades) ->
        println("$student's grades:")
        grades.forEach { (subject, grade) ->
            println("  $subject: $grade")
        }
        val average = grades.values.average()
        println("  Average: ${"%.1f".format(average)}")
        println()
    }
    
    // Map with lists as values
    val countryLanguages = mapOf(
        "Switzerland" to listOf("German", "French", "Italian", "Romansh"),
        "Canada" to listOf("English", "French"),
        "India" to listOf("Hindi", "English", "Bengali", "Telugu", "Tamil"),
        "USA" to listOf("English")
    )
    
    println("Countries and their languages:")
    countryLanguages.forEach { (country, languages) ->
        println("$country: ${languages.joinToString(", ")}")
    }
    
    // Practical map usage: caching
    val cache = mutableMapOf<String, String>()
    
    fun expensiveOperation(input: String): String {
        return cache.getOrPut(input) {
            println("  Computing result for '$input'...")
            Thread.sleep(100)  // Simulate expensive operation
            "Processed: $input"
        }
    }
    
    println("\nCaching example:")
    println("First call: ${expensiveOperation("test")}")
    println("Second call: ${expensiveOperation("test")}")  // From cache
    println("Third call: ${expensiveOperation("other")}")
    println("Cache contents: $cache")
    
    // Map filtering and transformation
    val numbers = mapOf("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5)
    
    val evenNumbers = numbers.filterValues { it % 2 == 0 }
    val shortKeys = numbers.filterKeys { it.length <= 3 }
    val doubledValues = numbers.mapValues { (_, value) -> value * 2 }
    val uppercaseKeys = numbers.mapKeys { (key, _) -> key.uppercase() }
    
    println("\nMap filtering and transformation:")
    println("Original: $numbers")
    println("Even values: $evenNumbers")
    println("Short keys: $shortKeys")
    println("Doubled values: $doubledValues")
    println("Uppercase keys: $uppercaseKeys")
    
    println()
}

/**
 * ## Mutable vs Immutable Collections
 * 
 * Understanding the difference between read-only and mutable collections.
 */
fun mutableVsImmutable() {
    println("--- Mutable vs Immutable Collections ---")
    
    // Immutable collections (read-only)
    val immutableList = listOf("a", "b", "c")
    val immutableSet = setOf(1, 2, 3)
    val immutableMap = mapOf("key1" to "value1", "key2" to "value2")
    
    println("Immutable collections:")
    println("List: $immutableList")
    println("Set: $immutableSet")
    println("Map: $immutableMap")
    
    // These would cause compilation errors:
    // immutableList.add("d")        // ❌ No such method
    // immutableSet.remove(1)        // ❌ No such method
    // immutableMap.put("key3", "value3")  // ❌ No such method
    
    // Mutable collections
    val mutableList = mutableListOf("a", "b", "c")
    val mutableSet = mutableSetOf(1, 2, 3)
    val mutableMap = mutableMapOf("key1" to "value1", "key2" to "value2")
    
    println("\nMutable collections (initial):")
    println("List: $mutableList")
    println("Set: $mutableSet")
    println("Map: $mutableMap")
    
    // Modifying mutable collections
    mutableList.add("d")
    mutableList.removeAt(0)
    
    mutableSet.add(4)
    mutableSet.remove(1)
    
    mutableMap["key3"] = "value3"
    mutableMap.remove("key1")
    
    println("\nMutable collections (after modifications):")
    println("List: $mutableList")
    println("Set: $mutableSet")
    println("Map: $mutableMap")
    
    // Collection builders (create mutable, return immutable)
    val builtList = buildList {
        add("first")
        add("second")
        addAll(listOf("third", "fourth"))
        if (size > 3) {
            add("bonus")
        }
    }
    
    val builtSet = buildSet {
        add(1)
        add(2)
        add(1)  // Duplicate, will be ignored
        addAll(listOf(3, 4, 3))
    }
    
    val builtMap = buildMap {
        put("a", 1)
        put("b", 2)
        if (containsKey("a")) {
            put("c", 3)
        }
    }
    
    println("\nBuilt collections (immutable result):")
    println("Built list: $builtList (${builtList.javaClass.simpleName})")
    println("Built set: $builtSet (${builtSet.javaClass.simpleName})")
    println("Built map: $builtMap (${builtMap.javaClass.simpleName})")
    
    // Converting between mutable and immutable
    val originalList = listOf(1, 2, 3)
    val mutableCopy = originalList.toMutableList()
    val immutableCopy = mutableList.toList()
    
    println("\nConversions:")
    println("Original immutable: $originalList")
    println("Mutable copy: $mutableCopy")
    mutableCopy.add(4)
    println("Mutable copy after modification: $mutableCopy")
    println("Original unchanged: $originalList")
    
    // Reference vs copy semantics
    val listReference: MutableList<String> = mutableList
    val listCopy = mutableList.toMutableList()
    
    listReference.add("reference")
    listCopy.add("copy")
    
    println("\nReference vs copy:")
    println("Original list: $mutableList")
    println("Reference (same object): $listReference")
    println("Copy (different object): $listCopy")
    
    // Read-only view of mutable collection
    val mutableData = mutableListOf("data1", "data2")
    val readOnlyView: List<String> = mutableData  // Read-only reference
    
    println("\nRead-only view:")
    println("Mutable data: $mutableData")
    println("Read-only view: $readOnlyView")
    
    mutableData.add("data3")  // Modifies the underlying collection
    println("After modifying through mutable reference:")
    println("Mutable data: $mutableData")
    println("Read-only view: $readOnlyView")  // Also shows the change
    
    // Thread safety considerations
    println("\nThread safety:")
    println("- Immutable collections: Thread-safe by design")
    println("- Mutable collections: Generally NOT thread-safe")
    println("- Use Collections.synchronizedList() or concurrent collections for thread safety")
    
    // Performance implications
    println("\nPerformance implications:")
    println("- Immutable: Safe to share, may use structural sharing")
    println("- Mutable: Can modify in-place, but copying required for safety")
    println("- Builder pattern: Best of both worlds for construction")
    
    println()
}

/**
 * ## Collection Creation Patterns
 * 
 * Various ways to create and initialize collections efficiently.
 */
fun collectionCreation() {
    println("--- Collection Creation Patterns ---")
    
    // Empty collections
    val emptyList1 = emptyList<String>()
    val emptyList2 = listOf<String>()
    val emptySet1 = emptySet<Int>()
    val emptyMap1 = emptyMap<String, Int>()
    
    println("Empty collections:")
    println("Empty list: $emptyList1")
    println("Empty set: $emptySet1")
    println("Empty map: $emptyMap1")
    
    // Single element collections
    val singletonList = listOf("single")
    val singletonSet = setOf(42)
    val singletonMap = mapOf("key" to "value")
    
    println("\nSingleton collections:")
    println("Singleton list: $singletonList")
    println("Singleton set: $singletonSet")
    println("Singleton map: $singletonMap")
    
    // Multiple element collections
    val numberList = listOf(1, 2, 3, 4, 5)
    val stringSet = setOf("apple", "banana", "cherry")
    val gradeMap = mapOf("A" to 90, "B" to 80, "C" to 70)
    
    println("\nMultiple element collections:")
    println("Number list: $numberList")
    println("String set: $stringSet")
    println("Grade map: $gradeMap")
    
    // Generated collections
    val squares = List(5) { it * it }
    val letters = List(5) { ('A' + it).toString() }
    val defaultValues = List(3) { "default" }
    
    println("\nGenerated collections:")
    println("Squares: $squares")
    println("Letters: $letters")
    println("Default values: $defaultValues")
    
    // Range-based creation
    val rangeList = (1..10).toList()
    val evenNumbers = (2..20 step 2).toList()
    val charRange = ('a'..'z').toList()
    
    println("\nRange-based creation:")
    println("Range list: $rangeList")
    println("Even numbers: $evenNumbers")
    println("Character range: $charRange")
    
    // Array conversion
    val arrayData = arrayOf("one", "two", "three")
    val listFromArray = arrayData.toList()
    val setFromArray = arrayData.toSet()
    
    println("\nArray conversion:")
    println("Array: ${arrayData.contentToString()}")
    println("List from array: $listFromArray")
    println("Set from array: $setFromArray")
    
    // String to collection
    val stringChars = "Hello".toList()
    val stringLines = "Line1\nLine2\nLine3".lines()
    val csvValues = "apple,banana,cherry".split(",")
    
    println("\nString to collection:")
    println("String chars: $stringChars")
    println("String lines: $stringLines")
    println("CSV values: $csvValues")
    
    // Pair and Triple collections
    val pairs = listOf("a" to 1, "b" to 2, "c" to 3)
    val triples = listOf(Triple("a", 1, true), Triple("b", 2, false))
    val mapFromPairs = pairs.toMap()
    
    println("\nPair and Triple collections:")
    println("Pairs: $pairs")
    println("Triples: $triples")
    println("Map from pairs: $mapFromPairs")
    
    // Conditional creation
    fun createCollection(includeOptional: Boolean): List<String> {
        return buildList {
            add("always")
            add("included")
            if (includeOptional) {
                add("optional")
                add("conditional")
            }
        }
    }
    
    println("\nConditional creation:")
    println("With optional: ${createCollection(true)}")
    println("Without optional: ${createCollection(false)}")
    
    // Lazy collection creation
    val lazyList by lazy {
        println("  Creating expensive list...")
        (1..1000000).toList()
    }
    
    println("\nLazy creation:")
    println("List size: ${lazyList.size}")  // Creates the list here
    println("List size again: ${lazyList.size}")  // Uses cached list
    
    // Factory functions
    fun createStringList(vararg elements: String): List<String> = elements.toList()
    
    fun createNumberMap(vararg pairs: Pair<String, Int>): Map<String, Int> = mapOf(*pairs)
    
    println("\nFactory functions:")
    val customList = createStringList("custom", "factory", "function")
    val customMap = createNumberMap("one" to 1, "two" to 2, "three" to 3)
    
    println("Custom list: $customList")
    println("Custom map: $customMap")
    
    // Nested collection creation
    val matrix = List(3) { row ->
        List(3) { col ->
            row * 3 + col + 1
        }
    }
    
    val jaggedArray = listOf(
        listOf(1),
        listOf(2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9, 10)
    )
    
    println("\nNested collections:")
    println("Matrix:")
    matrix.forEach { row ->
        println("  $row")
    }
    
    println("Jagged array:")
    jaggedArray.forEachIndexed { index, row ->
        println("  Row $index: $row")
    }
    
    println()
}

/**
 * ## Collection Conversions
 * 
 * Converting between different collection types and formats.
 */
fun collectionConversions() {
    println("--- Collection Conversions ---")
    
    val originalList = listOf(1, 2, 3, 2, 4, 3, 5)
    
    // Basic conversions
    println("Basic conversions:")
    println("Original list: $originalList")
    println("To set (removes duplicates): ${originalList.toSet()}")
    println("To mutable list: ${originalList.toMutableList()}")
    println("To array: ${originalList.toTypedArray().contentToString()}")
    println("To int array: ${originalList.toIntArray().contentToString()}")
    
    // Map conversions
    val keyValuePairs = listOf("name" to "Alice", "age" to "30", "city" to "NYC")
    val stringMap = keyValuePairs.toMap()
    
    println("\nMap conversions:")
    println("Pairs: $keyValuePairs")
    println("To map: $stringMap")
    println("Map keys to list: ${stringMap.keys.toList()}")
    println("Map values to list: ${stringMap.values.toList()}")
    println("Map entries to list: ${stringMap.entries.toList()}")
    
    // Collection with transformation
    val strings = listOf("1", "2", "3", "4", "5")
    
    println("\nConversions with transformation:")
    println("String list: $strings")
    println("To int list: ${strings.map { it.toInt() }}")
    println("To int set: ${strings.map { it.toInt() }.toSet()}")
    println("To string-int map: ${strings.associateWith { it.toInt() }}")
    
    // Advanced map creation from collections
    val people = listOf("Alice", "Bob", "Charlie", "David")
    
    val nameToLength = people.associateWith { it.length }
    val lengthToNames = people.groupBy { it.length }
    val indexToName = people.withIndex().associate { (index, name) -> index to name }
    val nameToIndex = people.withIndex().associate { (index, name) -> name to index }
    
    println("\nAdvanced map creation:")
    println("People: $people")
    println("Name to length: $nameToLength")
    println("Length to names: $lengthToNames")
    println("Index to name: $indexToName")
    println("Name to index: $nameToIndex")
    
    // Nested collection conversions
    val nestedList = listOf(
        listOf(1, 2, 3),
        listOf(4, 5),
        listOf(6, 7, 8, 9)
    )
    
    println("\nNested collection conversions:")
    println("Nested list: $nestedList")
    println("Flattened: ${nestedList.flatten()}")
    println("Flat mapped (doubled): ${nestedList.flatMap { it.map { n -> n * 2 } }}")
    
    // String conversions
    val wordList = listOf("hello", "world", "kotlin", "programming")
    
    println("\nString conversions:")
    println("Word list: $wordList")
    println("Joined with spaces: ${wordList.joinToString(" ")}")
    println("Joined with custom separator: ${wordList.joinToString(" | ")}")
    println("Joined with prefix/suffix: ${wordList.joinToString(", ", "[", "]")}")
    println("Joined with transformation: ${wordList.joinToString { it.uppercase() }}")
    
    // Chunking and windowing
    val sequence = (1..10).toList()
    
    println("\nChunking and windowing:")
    println("Sequence: $sequence")
    println("Chunked by 3: ${sequence.chunked(3)}")
    println("Windowed size 3: ${sequence.windowed(3)}")
    println("Windowed with step 2: ${sequence.windowed(3, 2)}")
    println("Windowed with transform: ${sequence.windowed(3) { it.sum() }}")
    
    // Zip operations
    val letters = listOf("a", "b", "c", "d")
    val numbers = listOf(1, 2, 3, 4, 5)
    
    println("\nZip operations:")
    println("Letters: $letters")
    println("Numbers: $numbers")
    println("Zipped: ${letters.zip(numbers)}")
    println("Zipped with transform: ${letters.zip(numbers) { letter, number -> "$letter$number" }}")
    println("Unzipped: ${letters.zip(numbers).unzip()}")
    
    // Partition operations
    val mixedNumbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val (evens, odds) = mixedNumbers.partition { it % 2 == 0 }
    
    println("\nPartition operations:")
    println("Mixed numbers: $mixedNumbers")
    println("Evens: $evens")
    println("Odds: $odds")
    
    // Type conversions with safety
    val mixedList = listOf("1", 2, "3.14", 4, "hello")
    
    val stringValues = mixedList.filterIsInstance<String>()
    val intValues = mixedList.filterIsInstance<Int>()
    val safeIntValues = mixedList.mapNotNull { 
        when (it) {
            is String -> it.toIntOrNull()
            is Int -> it
            else -> null
        }
    }
    
    println("\nType conversions with safety:")
    println("Mixed list: $mixedList")
    println("String values: $stringValues")
    println("Int values: $intValues")
    println("Safe int values: $safeIntValues")
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of collections in real scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. Shopping Cart System
    data class Product(val id: String, val name: String, val price: Double, val category: String)
    data class CartItem(val product: Product, val quantity: Int) {
        val total: Double get() = product.price * quantity
    }
    
    class ShoppingCart {
        private val items = mutableMapOf<String, CartItem>()
        
        fun addItem(product: Product, quantity: Int = 1) {
            val existingItem = items[product.id]
            if (existingItem != null) {
                items[product.id] = existingItem.copy(quantity = existingItem.quantity + quantity)
            } else {
                items[product.id] = CartItem(product, quantity)
            }
        }
        
        fun removeItem(productId: String) {
            items.remove(productId)
        }
        
        fun updateQuantity(productId: String, newQuantity: Int) {
            if (newQuantity <= 0) {
                removeItem(productId)
            } else {
                items[productId]?.let { item ->
                    items[productId] = item.copy(quantity = newQuantity)
                }
            }
        }
        
        fun getItems(): List<CartItem> = items.values.toList()
        
        fun getTotalPrice(): Double = items.values.sumOf { it.total }
        
        fun getItemCount(): Int = items.values.sumOf { it.quantity }
        
        fun getItemsByCategory(): Map<String, List<CartItem>> = 
            items.values.groupBy { it.product.category }
        
        fun clear() = items.clear()
    }
    
    // Shopping cart demo
    val cart = ShoppingCart()
    val products = listOf(
        Product("P001", "Laptop", 999.99, "Electronics"),
        Product("P002", "Mouse", 29.99, "Electronics"),
        Product("P003", "Coffee", 12.99, "Food"),
        Product("P004", "Book", 24.99, "Education")
    )
    
    println("Shopping Cart Demo:")
    cart.addItem(products[0], 1)  // Laptop
    cart.addItem(products[1], 2)  // Mouse x2
    cart.addItem(products[2], 3)  // Coffee x3
    cart.addItem(products[3], 1)  // Book
    
    println("Cart items:")
    cart.getItems().forEach { item ->
        println("  ${item.product.name} x${item.quantity} = $${item.total}")
    }
    
    println("Total: $${cart.getTotalPrice()}")
    println("Total items: ${cart.getItemCount()}")
    
    println("\nItems by category:")
    cart.getItemsByCategory().forEach { (category, items) ->
        println("  $category:")
        items.forEach { item ->
            println("    ${item.product.name} x${item.quantity}")
        }
    }
    
    // 2. Student Grade Management
    data class Student(val id: String, val name: String, val email: String)
    data class Grade(val subject: String, val score: Double, val maxScore: Double = 100.0) {
        val percentage: Double get() = (score / maxScore) * 100
    }
    
    class GradeBook {
        private val studentGrades = mutableMapOf<String, MutableList<Grade>>()
        private val students = mutableMapOf<String, Student>()
        
        fun addStudent(student: Student) {
            students[student.id] = student
            studentGrades[student.id] = mutableListOf()
        }
        
        fun addGrade(studentId: String, grade: Grade) {
            studentGrades[studentId]?.add(grade)
        }
        
        fun getStudentGrades(studentId: String): List<Grade> = 
            studentGrades[studentId]?.toList() ?: emptyList()
        
        fun getStudentAverage(studentId: String): Double {
            val grades = studentGrades[studentId] ?: return 0.0
            return if (grades.isNotEmpty()) grades.map { it.percentage }.average() else 0.0
        }
        
        fun getSubjectAverages(): Map<String, Double> {
            val allGrades = studentGrades.values.flatten()
            return allGrades.groupBy { it.subject }
                .mapValues { (_, grades) -> grades.map { it.percentage }.average() }
        }
        
        fun getTopStudents(count: Int = 5): List<Pair<Student, Double>> {
            return students.values.map { student ->
                student to getStudentAverage(student.id)
            }.sortedByDescending { it.second }.take(count)
        }
        
        fun getStudentsInTrouble(threshold: Double = 60.0): List<Student> {
            return students.values.filter { student ->
                getStudentAverage(student.id) < threshold
            }
        }
    }
    
    // Grade book demo
    val gradeBook = GradeBook()
    val studentList = listOf(
        Student("S001", "Alice Johnson", "alice@school.edu"),
        Student("S002", "Bob Smith", "bob@school.edu"),
        Student("S003", "Charlie Brown", "charlie@school.edu"),
        Student("S004", "Diana Wilson", "diana@school.edu")
    )
    
    println("\nGrade Book Demo:")
    
    // Add students
    studentList.forEach { gradeBook.addStudent(it) }
    
    // Add grades
    gradeBook.addGrade("S001", Grade("Math", 95.0))
    gradeBook.addGrade("S001", Grade("Science", 88.0))
    gradeBook.addGrade("S001", Grade("English", 92.0))
    
    gradeBook.addGrade("S002", Grade("Math", 78.0))
    gradeBook.addGrade("S002", Grade("Science", 85.0))
    gradeBook.addGrade("S002", Grade("English", 90.0))
    
    gradeBook.addGrade("S003", Grade("Math", 65.0))
    gradeBook.addGrade("S003", Grade("Science", 70.0))
    gradeBook.addGrade("S003", Grade("English", 68.0))
    
    gradeBook.addGrade("S004", Grade("Math", 92.0))
    gradeBook.addGrade("S004", Grade("Science", 94.0))
    gradeBook.addGrade("S004", Grade("English", 89.0))
    
    // Analysis
    println("Top students:")
    gradeBook.getTopStudents(3).forEach { (student, average) ->
        println("  ${student.name}: ${"%.1f".format(average)}%")
    }
    
    println("\nSubject averages:")
    gradeBook.getSubjectAverages().forEach { (subject, average) ->
        println("  $subject: ${"%.1f".format(average)}%")
    }
    
    println("\nStudents needing help:")
    gradeBook.getStudentsInTrouble(75.0).forEach { student ->
        val average = gradeBook.getStudentAverage(student.id)
        println("  ${student.name}: ${"%.1f".format(average)}%")
    }
    
    // 3. Inventory Management
    data class InventoryItem(
        val sku: String,
        val name: String,
        val category: String,
        var quantity: Int,
        val minStock: Int,
        val price: Double
    )
    
    class Inventory {
        private val items = mutableMapOf<String, InventoryItem>()
        
        fun addItem(item: InventoryItem) {
            items[item.sku] = item
        }
        
        fun updateStock(sku: String, newQuantity: Int) {
            items[sku]?.quantity = newQuantity
        }
        
        fun getLowStockItems(): List<InventoryItem> = 
            items.values.filter { it.quantity <= it.minStock }
        
        fun getItemsByCategory(): Map<String, List<InventoryItem>> = 
            items.values.groupBy { it.category }
        
        fun getTotalValue(): Double = 
            items.values.sumOf { it.quantity * it.price }
        
        fun searchItems(query: String): List<InventoryItem> = 
            items.values.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.sku.contains(query, ignoreCase = true)
            }
        
        fun getTopValueItems(count: Int = 10): List<InventoryItem> = 
            items.values.sortedByDescending { it.quantity * it.price }.take(count)
    }
    
    // Inventory demo
    val inventory = Inventory()
    val inventoryItems = listOf(
        InventoryItem("SKU001", "Gaming Laptop", "Electronics", 5, 2, 1299.99),
        InventoryItem("SKU002", "Wireless Mouse", "Electronics", 1, 5, 49.99),
        InventoryItem("SKU003", "Office Chair", "Furniture", 8, 3, 299.99),
        InventoryItem("SKU004", "Standing Desk", "Furniture", 2, 2, 599.99),
        InventoryItem("SKU005", "Coffee Beans", "Food", 20, 10, 24.99),
        InventoryItem("SKU006", "Programming Book", "Education", 0, 1, 49.99)
    )
    
    println("\nInventory Management Demo:")
    
    inventoryItems.forEach { inventory.addItem(it) }
    
    println("Low stock alerts:")
    inventory.getLowStockItems().forEach { item ->
        println("  ${item.name} (${item.sku}): ${item.quantity} units (min: ${item.minStock})")
    }
    
    println("\nInventory by category:")
    inventory.getItemsByCategory().forEach { (category, items) ->
        val totalValue = items.sumOf { it.quantity * it.price }
        println("  $category: ${items.size} items, total value: $${"%.2f".format(totalValue)}")
    }
    
    println("\nTotal inventory value: $${"%.2f".format(inventory.getTotalValue())}")
    
    println("\nTop value items:")
    inventory.getTopValueItems(3).forEach { item ->
        val value = item.quantity * item.price
        println("  ${item.name}: $${"%.2f".format(value)}")
    }
    
    println()
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice collection types:
 * 
 * 1. Create a playlist manager using different collection types
 * 2. Build a contact book with grouping and search functionality
 * 3. Implement a simple cache with size limits and eviction policies
 * 4. Create a word frequency analyzer for text processing
 * 5. Build a tournament bracket system using nested collections
 */

// TODO: Exercise 1 - Playlist Manager
class Song(
    // TODO: Add properties for song details
    val title: String,
    val artist: String,
    val duration: Int, // in seconds
    val genre: String
)

class Playlist {
    // TODO: Use appropriate collection types for:
    // - Ordered list of songs
    // - Quick lookup by song ID
    // - Grouping by genre/artist
    // - Recently played songs
    
    fun addSong(song: Song) {
        // TODO: Implement
    }
    
    fun removeSong(songId: String) {
        // TODO: Implement
    }
    
    fun shuffle(): List<Song> {
        // TODO: Return shuffled playlist
        return emptyList()
    }
    
    fun getSongsByGenre(genre: String): List<Song> {
        // TODO: Filter songs by genre
        return emptyList()
    }
}

// TODO: Exercise 2 - Contact Book
data class Contact(
    val name: String,
    val phoneNumbers: List<String>,
    val email: String?,
    val groups: Set<String>
)

class ContactBook {
    // TODO: Implement contact storage and search
    // - Fast lookup by name/phone/email
    // - Grouping by categories
    // - Favorite contacts
    
    fun addContact(contact: Contact) {
        // TODO: Implement
    }
    
    fun searchContacts(query: String): List<Contact> {
        // TODO: Search across all fields
        return emptyList()
    }
    
    fun getContactsByGroup(group: String): List<Contact> {
        // TODO: Filter by group
        return emptyList()
    }
}

// TODO: Exercise 3 - Simple Cache
class SimpleCache<K, V>(private val maxSize: Int) {
    // TODO: Implement cache with:
    // - Size limit
    // - LRU eviction policy
    // - Hit/miss statistics
    
    fun get(key: K): V? {
        // TODO: Implement
        return null
    }
    
    fun put(key: K, value: V) {
        // TODO: Implement with eviction
    }
    
    fun getStats(): Map<String, Int> {
        // TODO: Return hit/miss statistics
        return emptyMap()
    }
}

// TODO: Exercise 4 - Word Frequency Analyzer
class WordFrequencyAnalyzer {
    // TODO: Analyze text and provide statistics
    
    fun analyzeText(text: String): Map<String, Int> {
        // TODO: Count word frequencies
        return emptyMap()
    }
    
    fun getMostFrequentWords(count: Int): List<Pair<String, Int>> {
        // TODO: Return top N words
        return emptyList()
    }
    
    fun getWordsByLength(): Map<Int, List<String>> {
        // TODO: Group words by length
        return emptyMap()
    }
}

// TODO: Exercise 5 - Tournament Bracket
sealed class BracketNode {
    // TODO: Define bracket structure
    // - Match nodes with competitors
    // - Result tracking
    // - Winner advancement
}

class Tournament {
    // TODO: Manage tournament bracket
    // - Initialize bracket from competitors
    // - Record match results
    // - Advance winners
    // - Generate bracket visualization
    
    fun addCompetitor(name: String) {
        // TODO: Implement
    }
    
    fun recordMatchResult(matchId: String, winner: String) {
        // TODO: Implement
    }
    
    fun getBracketStatus(): List<String> {
        // TODO: Return current bracket state
        return emptyList()
    }
}