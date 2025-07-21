/**
 * Unit Tests for Basic Kotlin Syntax
 * 
 * This test class demonstrates how to write unit tests in Kotlin using JUnit 5.
 * It covers testing of basic Kotlin concepts and serves as an example for
 * learners to understand testing patterns.
 */

package com.kotlinmastery.basics

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.CsvSource

class BasicSyntaxTest {
    
    // ================================
    // Variables and Types Tests
    // ================================
    
    @Test
    @DisplayName("Test variable declarations and type inference")
    fun testVariableDeclarations() {
        // Test val (immutable)
        val immutableVar = 42
        assertEquals(42, immutableVar)
        
        // Test var (mutable)
        var mutableVar = "Hello"
        assertEquals("Hello", mutableVar)
        
        mutableVar = "World"
        assertEquals("World", mutableVar)
        
        // Test type inference
        val inferredInt = 100
        val inferredString = "Kotlin"
        val inferredDouble = 3.14
        
        assertTrue(inferredInt is Int)
        assertTrue(inferredString is String)
        assertTrue(inferredDouble is Double)
    }
    
    @Test
    @DisplayName("Test nullable types and null safety")
    fun testNullSafety() {
        // Non-nullable type
        val nonNullString: String = "Hello"
        assertEquals("Hello", nonNullString)
        
        // Nullable type
        val nullableString: String? = null
        assertNull(nullableString)
        
        val anotherNullableString: String? = "World"
        assertNotNull(anotherNullableString)
        
        // Safe call operator
        assertEquals(null, nullableString?.length)
        assertEquals(5, anotherNullableString?.length)
        
        // Elvis operator
        val length = nullableString?.length ?: 0
        assertEquals(0, length)
        
        val anotherLength = anotherNullableString?.length ?: 0
        assertEquals(5, anotherLength)
    }
    
    // ================================
    // String Template Tests
    // ================================
    
    @Test
    @DisplayName("Test string templates and interpolation")
    fun testStringTemplates() {
        val name = "Kotlin"
        val version = 1.9
        
        // Simple string template
        val simpleTemplate = "Hello, $name!"
        assertEquals("Hello, Kotlin!", simpleTemplate)
        
        // Expression in string template
        val expressionTemplate = "Version: ${version.toString()}"
        assertEquals("Version: 1.9", expressionTemplate)
        
        // Complex expression
        val numbers = listOf(1, 2, 3)
        val complexTemplate = "Numbers: ${numbers.joinToString(", ")}"
        assertEquals("Numbers: 1, 2, 3", complexTemplate)
    }
    
    // ================================
    // Function Tests
    // ================================
    
    // Helper functions for testing
    private fun add(a: Int, b: Int): Int = a + b
    
    private fun greet(name: String = "World"): String = "Hello, $name!"
    
    private fun multiply(vararg numbers: Int): Int = numbers.fold(1) { acc, n -> acc * n }
    
    @Test
    @DisplayName("Test function declarations and calls")
    fun testFunctions() {
        // Simple function
        assertEquals(7, add(3, 4))
        assertEquals(0, add(-5, 5))
        
        // Function with default parameter
        assertEquals("Hello, World!", greet())
        assertEquals("Hello, Kotlin!", greet("Kotlin"))
        
        // Varargs function
        assertEquals(1, multiply())
        assertEquals(24, multiply(2, 3, 4))
        assertEquals(120, multiply(1, 2, 3, 4, 5))
    }
    
    @Test
    @DisplayName("Test higher-order functions and lambdas")
    fun testHigherOrderFunctions() {
        val numbers = listOf(1, 2, 3, 4, 5)
        
        // Filter with lambda
        val evenNumbers = numbers.filter { it % 2 == 0 }
        assertEquals(listOf(2, 4), evenNumbers)
        
        // Map with lambda
        val squared = numbers.map { it * it }
        assertEquals(listOf(1, 4, 9, 16, 25), squared)
        
        // Reduce
        val sum = numbers.reduce { acc, n -> acc + n }
        assertEquals(15, sum)
        
        // Custom higher-order function
        fun applyOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
            return operation(a, b)
        }
        
        assertEquals(8, applyOperation(3, 5, { x, y -> x + y }))
        assertEquals(15, applyOperation(3, 5, { x, y -> x * y }))
    }
    
    // ================================
    // Control Flow Tests
    // ================================
    
    private fun classifyNumber(n: Int): String = when {
        n < 0 -> "negative"
        n == 0 -> "zero"
        n > 0 -> "positive"
        else -> "unknown"
    }
    
    private fun isEven(n: Int): Boolean = n % 2 == 0
    
    @Test
    @DisplayName("Test conditional expressions")
    fun testConditionalExpressions() {
        // If expression
        val max = if (5 > 3) 5 else 3
        assertEquals(5, max)
        
        // When expression
        assertEquals("negative", classifyNumber(-5))
        assertEquals("zero", classifyNumber(0))
        assertEquals("positive", classifyNumber(10))
        
        // When with ranges
        fun categorizeAge(age: Int): String = when (age) {
            in 0..12 -> "child"
            in 13..19 -> "teenager"  
            in 20..64 -> "adult"
            else -> "senior"
        }
        
        assertEquals("child", categorizeAge(8))
        assertEquals("teenager", categorizeAge(16))
        assertEquals("adult", categorizeAge(30))
        assertEquals("senior", categorizeAge(70))
    }
    
    @Test
    @DisplayName("Test loops and iterations")
    fun testLoops() {
        // For loop with range
        val numbers = mutableListOf<Int>()
        for (i in 1..5) {
            numbers.add(i)
        }
        assertEquals(listOf(1, 2, 3, 4, 5), numbers)
        
        // For loop with collection
        val doubled = mutableListOf<Int>()
        for (num in listOf(1, 2, 3)) {
            doubled.add(num * 2)
        }
        assertEquals(listOf(2, 4, 6), doubled)
        
        // While loop
        var count = 0
        var sum = 0
        while (count < 5) {
            sum += count
            count++
        }
        assertEquals(10, sum) // 0 + 1 + 2 + 3 + 4
    }
    
    // ================================
    // Collection Tests
    // ================================
    
    @Test
    @DisplayName("Test list operations")
    fun testListOperations() {
        val numbers = listOf(1, 2, 3, 4, 5)
        
        // Basic properties
        assertEquals(5, numbers.size)
        assertEquals(1, numbers.first())
        assertEquals(5, numbers.last())
        assertTrue(numbers.contains(3))
        assertFalse(numbers.contains(10))
        
        // Functional operations
        assertEquals(listOf(2, 4), numbers.filter { it % 2 == 0 })
        assertEquals(listOf(1, 4, 9, 16, 25), numbers.map { it * it })
        assertEquals(15, numbers.sum())
        
        // Mutable list
        val mutableNumbers = mutableListOf(1, 2, 3)
        mutableNumbers.add(4)
        mutableNumbers.removeAt(0)
        assertEquals(listOf(2, 3, 4), mutableNumbers)
    }
    
    @Test
    @DisplayName("Test map operations")
    fun testMapOperations() {
        val scores = mapOf("Alice" to 95, "Bob" to 87, "Carol" to 92)
        
        assertEquals(95, scores["Alice"])
        assertEquals(3, scores.size)
        assertTrue(scores.containsKey("Bob"))
        assertTrue(scores.containsValue(92))
        
        // Functional operations on maps
        val highScores = scores.filter { it.value > 90 }
        assertEquals(mapOf("Alice" to 95, "Carol" to 92), highScores)
        
        val bonusScores = scores.mapValues { it.value + 5 }
        assertEquals(mapOf("Alice" to 100, "Bob" to 92, "Carol" to 97), bonusScores)
    }
    
    // ================================
    // Exception Handling Tests
    // ================================
    
    private fun divide(a: Int, b: Int): Double {
        if (b == 0) {
            throw ArithmeticException("Division by zero")
        }
        return a.toDouble() / b
    }
    
    @Test
    @DisplayName("Test exception handling")
    fun testExceptionHandling() {
        // Normal case
        assertEquals(2.0, divide(10, 5))
        
        // Exception case
        val exception = assertThrows<ArithmeticException> {
            divide(10, 0)
        }
        assertEquals("Division by zero", exception.message)
        
        // Try-catch
        val result = try {
            divide(10, 0)
        } catch (e: ArithmeticException) {
            -1.0
        }
        assertEquals(-1.0, result)
    }
    
    // ================================
    // Class and Object Tests
    // ================================
    
    // Test classes
    data class Person(val name: String, val age: Int) {
        fun isAdult(): Boolean = age >= 18
    }
    
    class Calculator {
        fun add(a: Int, b: Int): Int = a + b
        fun multiply(a: Int, b: Int): Int = a * b
    }
    
    @Test
    @DisplayName("Test data classes")
    fun testDataClasses() {
        val person1 = Person("Alice", 25)
        val person2 = Person("Bob", 30)
        val person3 = Person("Alice", 25)
        
        // toString
        assertEquals("Person(name=Alice, age=25)", person1.toString())
        
        // equals and hashCode
        assertNotEquals(person1, person2)
        assertEquals(person1, person3)
        assertEquals(person1.hashCode(), person3.hashCode())
        
        // copy
        val olderAlice = person1.copy(age = 26)
        assertEquals("Alice", olderAlice.name)
        assertEquals(26, olderAlice.age)
        
        // destructuring
        val (name, age) = person1
        assertEquals("Alice", name)
        assertEquals(25, age)
        
        // Methods
        assertTrue(person1.isAdult())
        assertFalse(Person("Child", 12).isAdult())
    }
    
    @Test
    @DisplayName("Test regular classes")
    fun testRegularClasses() {
        val calculator = Calculator()
        
        assertEquals(7, calculator.add(3, 4))
        assertEquals(12, calculator.multiply(3, 4))
    }
    
    // ================================
    // Parameterized Tests
    // ================================
    
    @ParameterizedTest
    @ValueSource(ints = [2, 4, 6, 8, 10])
    @DisplayName("Test even numbers")
    fun testEvenNumbers(number: Int) {
        assertTrue(isEven(number))
    }
    
    @ParameterizedTest
    @CsvSource("1,1,2", "2,3,5", "5,7,12", "-1,1,0")
    @DisplayName("Test addition with multiple inputs")
    fun testAddition(a: Int, b: Int, expected: Int) {
        assertEquals(expected, add(a, b))
    }
    
    // ================================
    // Setup and Teardown
    // ================================
    
    private lateinit var testList: MutableList<String>
    
    @BeforeEach
    fun setUp() {
        testList = mutableListOf("item1", "item2", "item3")
    }
    
    @Test
    @DisplayName("Test with setup")
    fun testWithSetup() {
        assertEquals(3, testList.size)
        assertTrue(testList.contains("item1"))
        
        testList.add("item4")
        assertEquals(4, testList.size)
    }
}

/**
 * Integration test examples
 */
class IntegrationTest {
    
    @Test
    @DisplayName("Test complex scenario")
    fun testComplexScenario() {
        // Setup
        val students = mutableListOf<Triple<String, Int, String>>() // name, score, grade
        
        // Add students
        students.add(Triple("Alice", 95, ""))
        students.add(Triple("Bob", 87, ""))
        students.add(Triple("Carol", 92, ""))
        students.add(Triple("Dave", 78, ""))
        
        // Calculate grades
        val gradedStudents = students.map { (name, score, _) ->
            val grade = when {
                score >= 90 -> "A"
                score >= 80 -> "B" 
                score >= 70 -> "C"
                score >= 60 -> "D"
                else -> "F"
            }
            Triple(name, score, grade)
        }
        
        // Verify results
        assertEquals("A", gradedStudents.find { it.first == "Alice" }?.third)
        assertEquals("B", gradedStudents.find { it.first == "Bob" }?.third)
        assertEquals("A", gradedStudents.find { it.first == "Carol" }?.third)
        assertEquals("C", gradedStudents.find { it.first == "Dave" }?.third)
        
        // Statistics
        val averageScore = gradedStudents.map { it.second }.average()
        assertEquals(88.0, averageScore)
        
        val aGrades = gradedStudents.count { it.third == "A" }
        assertEquals(2, aGrades)
    }
}