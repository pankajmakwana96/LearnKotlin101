/**
 * Functional Programming in Kotlin
 * 
 * This module covers functional programming concepts in Kotlin, including:
 * - Pure functions and immutability
 * - Higher-order functions and function composition
 * - Monads and functional data structures
 * - Currying and partial application
 * - Functional error handling
 * - Lazy evaluation and sequences
 * - Function recursion and tail recursion
 * - Functional reactive programming patterns
 */

import kotlin.random.Random

// ================================
// Pure Functions and Immutability
// ================================

/**
 * Pure function - same input always produces same output, no side effects
 */
fun add(a: Int, b: Int): Int = a + b

fun multiply(a: Int, b: Int): Int = a * b

fun calculateCircleArea(radius: Double): Double = Math.PI * radius * radius

/**
 * Impure function (has side effects)
 */
var counter = 0
fun impureIncrement(): Int {
    counter++ // Side effect - modifies external state
    println("Counter: $counter") // Side effect - I/O operation
    return counter
}

/**
 * Pure version using immutable state
 */
fun pureIncrement(currentValue: Int): Int = currentValue + 1

/**
 * Immutable data structures
 */
data class ImmutablePerson(
    val name: String,
    val age: Int,
    val address: String
) {
    // Instead of setters, return new instances
    fun withName(newName: String) = copy(name = newName)
    fun withAge(newAge: Int) = copy(age = newAge)
    fun withAddress(newAddress: String) = copy(address = newAddress)
    
    // Pure functions for calculations
    fun yearsUntilRetirement(retirementAge: Int = 65): Int = 
        maxOf(0, retirementAge - age)
}

/**
 * Immutable list operations
 */
class ImmutableList<T>(private val items: List<T> = emptyList()) {
    
    fun add(item: T): ImmutableList<T> = ImmutableList(items + item)
    
    fun remove(item: T): ImmutableList<T> = ImmutableList(items - item)
    
    fun removeAt(index: Int): ImmutableList<T> = 
        ImmutableList(items.filterIndexed { i, _ -> i != index })
    
    fun update(index: Int, item: T): ImmutableList<T> = 
        ImmutableList(items.mapIndexed { i, existing -> if (i == index) item else existing })
    
    fun get(index: Int): T = items[index]
    
    fun size(): Int = items.size
    
    fun toList(): List<T> = items.toList()
    
    override fun toString(): String = items.toString()
}

// ================================
// Function Composition
// ================================

/**
 * Function composition utilities
 */
infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C = { a -> this(f(a)) }

infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C = { a -> g(this(a)) }

/**
 * Example functions for composition
 */
val double: (Int) -> Int = { it * 2 }
val addOne: (Int) -> Int = { it + 1 }
val toString: (Int) -> String = { it.toString() }
val toUpperCase: (String) -> String = { it.uppercase() }

/**
 * Function composition examples
 */
fun demonstrateFunctionComposition() {
    println("=== Function Composition ===")
    
    // Using compose (right to left)
    val doubleAndAddOne = double compose addOne
    println("doubleAndAddOne(5) = ${doubleAndAddOne(5)}") // (5 + 1) * 2 = 12
    
    // Using andThen (left to right)
    val addOneAndDouble = addOne andThen double
    println("addOneAndDouble(5) = ${addOneAndDouble(5)}") // (5 + 1) * 2 = 12
    
    // Complex composition
    val pipeline = addOne andThen double andThen toString andThen toUpperCase
    println("pipeline(5) = ${pipeline(5)}") // "12"
    
    // Real-world example: data transformation pipeline
    data class User(val name: String, val age: Int)
    
    val extractName: (User) -> String = { it.name }
    val capitalizeFirst: (String) -> String = { it.replaceFirstChar { char -> char.uppercase() } }
    val addGreeting: (String) -> String = { "Hello, $it!" }
    
    val userGreeting = extractName andThen capitalizeFirst andThen addGreeting
    
    val user = User("john doe", 30)
    println("User greeting: ${userGreeting(user)}")
}

// ================================
// Currying and Partial Application
// ================================

/**
 * Currying: transforming a function with multiple arguments into a sequence of functions
 */
fun curry2<A, B, R>(f: (A, B) -> R): (A) -> (B) -> R = { a -> { b -> f(a, b) } }

fun curry3<A, B, C, R>(f: (A, B, C) -> R): (A) -> (B) -> (C) -> R = 
    { a -> { b -> { c -> f(a, b, c) } } }

/**
 * Partial application
 */
fun <A, B, R> ((A, B) -> R).partial1(a: A): (B) -> R = { b -> this(a, b) }

fun <A, B, C, R> ((A, B, C) -> R).partial2(a: A, b: B): (C) -> R = { c -> this(a, b, c) }

/**
 * Examples of currying and partial application
 */
fun demonstrateCurryingAndPartialApplication() {
    println("\n=== Currying and Partial Application ===")
    
    // Original function
    fun addThreeNumbers(a: Int, b: Int, c: Int): Int = a + b + c
    
    // Curried version
    val curriedAdd = curry3(::addThreeNumbers)
    val addFive = curriedAdd(5)
    val addFiveAndThree = addFive(3)
    val result = addFiveAndThree(2)
    
    println("Curried add(5)(3)(2) = $result")
    
    // Partial application
    val partialAdd = ::addThreeNumbers.partial2(10, 20)
    println("Partial application add(10, 20, 5) = ${partialAdd(5)}")
    
    // Real-world example: validation functions
    fun validate(minLength: Int, maxLength: Int, value: String): Boolean =
        value.length >= minLength && value.length <= maxLength
    
    val validateUsername = ::validate.partial2(3, 20)
    val validatePassword = ::validate.partial2(8, 100)
    
    println("Username 'john' valid: ${validateUsername("john")}")
    println("Password 'secret123' valid: ${validatePassword("secret123")}")
    
    // HTTP request builder with currying
    data class HttpRequest(val method: String, val url: String, val headers: Map<String, String>)
    
    fun createRequest(method: String, url: String, headers: Map<String, String>): HttpRequest =
        HttpRequest(method, url, headers)
    
    val curriedRequest = curry3(::createRequest)
    val getRequest = curriedRequest("GET")
    val getApiRequest = getRequest("https://api.example.com")
    
    val authHeaders = mapOf("Authorization" to "Bearer token123")
    val request = getApiRequest(authHeaders)
    
    println("HTTP Request: $request")
}

// ================================
// Monads and Functional Data Structures
// ================================

/**
 * Maybe monad for handling null values functionally
 */
sealed class Maybe<out T> {
    object None : Maybe<Nothing>()
    data class Some<out T>(val value: T) : Maybe<T>()
    
    fun <R> map(f: (T) -> R): Maybe<R> = when (this) {
        is None -> None
        is Some -> Some(f(value))
    }
    
    fun <R> flatMap(f: (T) -> Maybe<R>): Maybe<R> = when (this) {
        is None -> None
        is Some -> f(value)
    }
    
    fun filter(predicate: (T) -> Boolean): Maybe<T> = when (this) {
        is None -> None
        is Some -> if (predicate(value)) this else None
    }
    
    fun getOrElse(default: T): T = when (this) {
        is None -> default
        is Some -> value
    }
    
    fun isDefined(): Boolean = this is Some
    
    companion object {
        fun <T> just(value: T?): Maybe<T> = if (value != null) Some(value) else None
    }
}

/**
 * Either monad for error handling
 */
sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
    
    fun <T> map(f: (R) -> T): Either<L, T> = when (this) {
        is Left -> Left(value)
        is Right -> Right(f(value))
    }
    
    fun <T> flatMap(f: (R) -> Either<L, T>): Either<L, T> = when (this) {
        is Left -> Left(value)
        is Right -> f(value)
    }
    
    fun <T> mapLeft(f: (L) -> T): Either<T, R> = when (this) {
        is Left -> Left(f(value))
        is Right -> Right(value)
    }
    
    fun isLeft(): Boolean = this is Left
    fun isRight(): Boolean = this is Right
    
    fun getOrElse(default: R): R = when (this) {
        is Left -> default
        is Right -> value
    }
}

/**
 * Try monad for exception handling
 */
sealed class Try<out T> {
    data class Success<out T>(val value: T) : Try<T>()
    data class Failure(val exception: Throwable) : Try<Nothing>()
    
    fun <R> map(f: (T) -> R): Try<R> = when (this) {
        is Success -> try { Success(f(value)) } catch (e: Exception) { Failure(e) }
        is Failure -> Failure(exception)
    }
    
    fun <R> flatMap(f: (T) -> Try<R>): Try<R> = when (this) {
        is Success -> try { f(value) } catch (e: Exception) { Failure(e) }
        is Failure -> Failure(exception)
    }
    
    fun recover(f: (Throwable) -> T): Try<T> = when (this) {
        is Success -> this
        is Failure -> try { Success(f(exception)) } catch (e: Exception) { Failure(e) }
    }
    
    fun getOrElse(default: T): T = when (this) {
        is Success -> value
        is Failure -> default
    }
    
    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
    
    companion object {
        fun <T> of(block: () -> T): Try<T> = try {
            Success(block())
        } catch (e: Exception) {
            Failure(e)
        }
    }
}

/**
 * Demonstrates monads
 */
fun demonstrateMonads() {
    println("\n=== Monads ===")
    
    // Maybe monad
    println("Maybe monad:")
    val maybeValue = Maybe.just("hello")
    val maybeNull = Maybe.just(null)
    
    val result1 = maybeValue.map { it.uppercase() }.map { "$it!" }
    val result2 = maybeNull.map { it.toString().uppercase() }
    
    println("Maybe result 1: ${result1.getOrElse("default")}")
    println("Maybe result 2: ${result2.getOrElse("default")}")
    
    // Either monad for validation
    println("\nEither monad:")
    fun validateAge(age: Int): Either<String, Int> =
        if (age >= 0 && age <= 150) Either.Right(age) else Either.Left("Invalid age: $age")
    
    fun validateName(name: String): Either<String, String> =
        if (name.isNotBlank()) Either.Right(name) else Either.Left("Name cannot be blank")
    
    val validAge = validateAge(25)
    val invalidAge = validateAge(-5)
    
    println("Valid age result: ${validAge.map { "Age: $it" }.getOrElse("Error")}")
    println("Invalid age result: ${invalidAge.map { "Age: $it" }.getOrElse("Error")}")
    
    // Try monad for exception handling
    println("\nTry monad:")
    val safeDiv = Try.of { 10 / 2 }
    val unsafeDiv = Try.of { 10 / 0 }
    
    println("Safe division: ${safeDiv.map { "Result: $it" }.getOrElse("Error occurred")}")
    println("Unsafe division: ${unsafeDiv.recover { "Division by zero" }.getOrElse("Unknown error")}")
}

// ================================
// Recursion and Tail Recursion
// ================================

/**
 * Traditional recursion (can cause stack overflow for large inputs)
 */
fun factorial(n: Long): Long = if (n <= 1) 1 else n * factorial(n - 1)

/**
 * Tail recursive version (optimized by Kotlin compiler)
 */
tailrec fun factorialTailRec(n: Long, accumulator: Long = 1): Long =
    if (n <= 1) accumulator else factorialTailRec(n - 1, n * accumulator)

/**
 * Fibonacci - traditional recursion (very inefficient)
 */
fun fibonacci(n: Int): Long = when (n) {
    0 -> 0
    1 -> 1
    else -> fibonacci(n - 1) + fibonacci(n - 2)
}

/**
 * Fibonacci - tail recursive with memoization
 */
tailrec fun fibonacciTailRec(n: Int, a: Long = 0, b: Long = 1): Long =
    if (n == 0) a else fibonacciTailRec(n - 1, b, a + b)

/**
 * List operations using recursion
 */
fun <T> List<T>.sumRecursive(transform: (T) -> Long): Long = when {
    isEmpty() -> 0
    else -> transform(first()) + drop(1).sumRecursive(transform)
}

tailrec fun <T> List<T>.sumTailRec(transform: (T) -> Long, accumulator: Long = 0): Long = when {
    isEmpty() -> accumulator
    else -> drop(1).sumTailRec(transform, accumulator + transform(first()))
}

/**
 * Tree traversal using recursion
 */
data class TreeNode<T>(
    val value: T,
    val left: TreeNode<T>? = null,
    val right: TreeNode<T>? = null
)

fun <T> TreeNode<T>.inOrderTraversal(): List<T> {
    fun traverse(node: TreeNode<T>?, result: MutableList<T>) {
        if (node != null) {
            traverse(node.left, result)
            result.add(node.value)
            traverse(node.right, result)
        }
    }
    
    val result = mutableListOf<T>()
    traverse(this, result)
    return result
}

fun <T> TreeNode<T>.inOrderTraversalFunctional(): List<T> = when {
    left == null && right == null -> listOf(value)
    left == null -> listOf(value) + (right?.inOrderTraversalFunctional() ?: emptyList())
    right == null -> (left.inOrderTraversalFunctional()) + listOf(value)
    else -> left.inOrderTraversalFunctional() + listOf(value) + right.inOrderTraversalFunctional()
}

/**
 * Demonstrates recursion
 */
fun demonstrateRecursion() {
    println("\n=== Recursion ===")
    
    println("Factorial:")
    println("factorial(5) = ${factorial(5)}")
    println("factorialTailRec(5) = ${factorialTailRec(5)}")
    
    println("\nFibonacci:")
    println("fibonacci(10) = ${fibonacci(10)}")
    println("fibonacciTailRec(10) = ${fibonacciTailRec(10)}")
    
    println("\nList sum:")
    val numbers = listOf(1, 2, 3, 4, 5)
    println("sumRecursive: ${numbers.sumRecursive { it.toLong() }}")
    println("sumTailRec: ${numbers.sumTailRec { it.toLong() }}")
    
    println("\nTree traversal:")
    val tree = TreeNode(
        4,
        TreeNode(2, TreeNode(1), TreeNode(3)),
        TreeNode(6, TreeNode(5), TreeNode(7))
    )
    
    println("In-order traversal: ${tree.inOrderTraversalFunctional()}")
}

// ================================
// Lazy Evaluation and Sequences
// ================================

/**
 * Lazy evaluation examples
 */
fun demonstrateLazyEvaluation() {
    println("\n=== Lazy Evaluation ===")
    
    // Lazy property
    val expensiveComputation by lazy {
        println("Computing expensive value...")
        Thread.sleep(1000)
        "Expensive result"
    }
    
    println("Lazy value created")
    println("First access: $expensiveComputation")
    println("Second access: $expensiveComputation") // No recomputation
    
    // Sequences for lazy evaluation
    println("\nSequences vs Lists:")
    
    fun isEven(n: Int): Boolean {
        println("Checking if $n is even")
        return n % 2 == 0
    }
    
    fun square(n: Int): Int {
        println("Squaring $n")
        return n * n
    }
    
    // Eager evaluation with List
    println("List (eager):")
    val listResult = (1..6).toList()
        .filter { isEven(it) }
        .map { square(it) }
        .take(2)
    println("List result: $listResult")
    
    println("\nSequence (lazy):")
    val sequenceResult = (1..6).asSequence()
        .filter { isEven(it) }
        .map { square(it) }
        .take(2)
        .toList()
    println("Sequence result: $sequenceResult")
    
    // Infinite sequences
    println("\nInfinite sequence:")
    val infiniteNumbers = generateSequence(1) { it + 1 }
    val firstFiveEvenSquares = infiniteNumbers
        .filter { isEven(it) }
        .map { square(it) }
        .take(3)
        .toList()
    
    println("First three even squares: $firstFiveEvenSquares")
}

// ================================
// Functional Reactive Programming Patterns
// ================================

/**
 * Observable pattern using functional approach
 */
class Observable<T> {
    private val observers = mutableListOf<(T) -> Unit>()
    
    fun subscribe(observer: (T) -> Unit): () -> Unit {
        observers.add(observer)
        return { observers.remove(observer) } // Return unsubscribe function
    }
    
    fun emit(value: T) {
        observers.forEach { it(value) }
    }
    
    fun <R> map(transform: (T) -> R): Observable<R> {
        val mapped = Observable<R>()
        subscribe { value -> mapped.emit(transform(value)) }
        return mapped
    }
    
    fun filter(predicate: (T) -> Boolean): Observable<T> {
        val filtered = Observable<T>()
        subscribe { value -> 
            if (predicate(value)) filtered.emit(value) 
        }
        return filtered
    }
    
    fun <R> flatMap(transform: (T) -> Observable<R>): Observable<R> {
        val flattened = Observable<R>()
        subscribe { value ->
            transform(value).subscribe { mapped -> flattened.emit(mapped) }
        }
        return flattened
    }
}

/**
 * Event system using functional patterns
 */
sealed class Event {
    data class Click(val x: Int, val y: Int) : Event()
    data class KeyPress(val key: String) : Event()
    data class MouseMove(val x: Int, val y: Int) : Event()
}

class EventSystem {
    private val eventStream = Observable<Event>()
    
    fun publishEvent(event: Event) = eventStream.emit(event)
    
    fun onClick(): Observable<Event.Click> = eventStream
        .filter { it is Event.Click }
        .map { it as Event.Click }
    
    fun onKeyPress(): Observable<Event.KeyPress> = eventStream
        .filter { it is Event.KeyPress }
        .map { it as Event.KeyPress }
    
    fun onMouseMove(): Observable<Event.MouseMove> = eventStream
        .filter { it is Event.MouseMove }
        .map { it as Event.MouseMove }
}

/**
 * Demonstrates functional reactive patterns
 */
fun demonstrateFunctionalReactiveProgramming() {
    println("\n=== Functional Reactive Programming ===")
    
    val eventSystem = EventSystem()
    
    // Subscribe to click events
    val unsubscribeClick = eventSystem.onClick().subscribe { click ->
        println("Click at (${click.x}, ${click.y})")
    }
    
    // Subscribe to filtered key presses
    val unsubscribeKeys = eventSystem.onKeyPress()
        .filter { it.key.length == 1 } // Single character keys
        .map { it.key.uppercase() }
        .subscribe { key -> 
            println("Single key pressed: $key") 
        }
    
    // Emit some events
    eventSystem.publishEvent(Event.Click(100, 200))
    eventSystem.publishEvent(Event.KeyPress("a"))
    eventSystem.publishEvent(Event.KeyPress("Shift"))
    eventSystem.publishEvent(Event.MouseMove(150, 250))
    eventSystem.publishEvent(Event.Click(300, 400))
    eventSystem.publishEvent(Event.KeyPress("b"))
    
    // Cleanup
    unsubscribeClick()
    unsubscribeKeys()
}

// ================================
// Real-World Functional Examples
// ================================

/**
 * Functional validation system
 */
data class ValidationError(val field: String, val message: String)

typealias Validator<T> = (T) -> List<ValidationError>

fun <T> combine(vararg validators: Validator<T>): Validator<T> = { value ->
    validators.flatMap { validator -> validator(value) }
}

fun <T, R> Validator<T>.contraMap(transform: (R) -> T): Validator<R> = { value ->
    this(transform(value))
}

/**
 * User validation example
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val age: Int
)

val usernameValidator: Validator<String> = { username ->
    buildList {
        if (username.isBlank()) add(ValidationError("username", "Username cannot be blank"))
        if (username.length < 3) add(ValidationError("username", "Username must be at least 3 characters"))
        if (!username.matches("[a-zA-Z0-9_]+".toRegex())) {
            add(ValidationError("username", "Username can only contain letters, numbers, and underscores"))
        }
    }
}

val emailValidator: Validator<String> = { email ->
    buildList {
        if (email.isBlank()) add(ValidationError("email", "Email cannot be blank"))
        if (!email.contains("@")) add(ValidationError("email", "Email must contain @"))
        if (!email.contains(".")) add(ValidationError("email", "Email must contain a domain"))
    }
}

val passwordValidator: Validator<String> = { password ->
    buildList {
        if (password.length < 8) add(ValidationError("password", "Password must be at least 8 characters"))
        if (!password.any { it.isDigit() }) add(ValidationError("password", "Password must contain a digit"))
        if (!password.any { it.isUpperCase() }) add(ValidationError("password", "Password must contain uppercase letter"))
    }
}

val ageValidator: Validator<Int> = { age ->
    buildList {
        if (age < 13) add(ValidationError("age", "Must be at least 13 years old"))
        if (age > 120) add(ValidationError("age", "Age seems unrealistic"))
    }
}

val registerRequestValidator: Validator<RegisterRequest> = combine(
    usernameValidator.contraMap { it.username },
    emailValidator.contraMap { it.email },
    passwordValidator.contraMap { it.password },
    ageValidator.contraMap { it.age }
)

/**
 * Functional data processing pipeline
 */
data class SalesRecord(
    val date: String,
    val product: String,
    val category: String,
    val amount: Double,
    val region: String
)

fun demonstrateFunctionalDataProcessing() {
    println("\n=== Functional Data Processing ===")
    
    val salesData = listOf(
        SalesRecord("2023-01-01", "Laptop", "Electronics", 999.99, "North"),
        SalesRecord("2023-01-02", "Mouse", "Electronics", 29.99, "South"),
        SalesRecord("2023-01-03", "Desk", "Furniture", 299.99, "North"),
        SalesRecord("2023-01-04", "Phone", "Electronics", 699.99, "East"),
        SalesRecord("2023-01-05", "Chair", "Furniture", 149.99, "West"),
        SalesRecord("2023-01-06", "Tablet", "Electronics", 399.99, "North")
    )
    
    // Functional pipeline for sales analysis
    val pipeline = { data: List<SalesRecord> ->
        data.asSequence()
            .filter { it.amount > 100 }                    // High-value sales only
            .groupBy { it.category }                       // Group by category
            .mapValues { (_, records) ->                   // Calculate category totals
                records.sumOf { it.amount }
            }
            .toList()                                      // Convert to list
            .sortedByDescending { it.second }              // Sort by total amount
    }
    
    val categoryTotals = pipeline(salesData)
    
    println("Sales by category (high-value items):")
    categoryTotals.forEach { (category, total) ->
        println("$category: $%.2f".format(total))
    }
    
    // Validation example
    println("\nValidation example:")
    val validRequest = RegisterRequest("johndoe", "john@example.com", "SecurePass123", 25)
    val invalidRequest = RegisterRequest("ab", "invalid-email", "weak", 10)
    
    val validResult = registerRequestValidator(validRequest)
    val invalidResult = registerRequestValidator(invalidRequest)
    
    println("Valid request errors: ${validResult.size}")
    println("Invalid request errors: ${invalidResult.size}")
    invalidResult.forEach { error ->
        println("- ${error.field}: ${error.message}")
    }
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Implement a functional JSON parser using monads for error handling
 * 2. Create a functional state machine for game logic
 * 3. Build a functional reactive calculator that updates in real-time
 * 4. Implement a functional caching system with automatic expiration
 * 5. Create a functional command pattern for undo/redo functionality
 * 6. Build a functional pub/sub system with filtering and transformation
 * 7. Implement a functional parser combinator library
 * 8. Create a functional dependency injection container
 * 9. Build a functional finite state automaton
 * 10. Implement a functional database query builder
 */

fun main() {
    demonstrateFunctionComposition()
    demonstrateCurryingAndPartialApplication()
    demonstrateMonads()
    demonstrateRecursion()
    demonstrateLazyEvaluation()
    demonstrateFunctionalReactiveProgramming()
    demonstrateFunctionalDataProcessing()
    
    println("\n=== Functional Programming Summary ===")
    println("✓ Pure functions and immutable data structures")
    println("✓ Function composition, currying, and partial application")
    println("✓ Monads for functional error handling (Maybe, Either, Try)")
    println("✓ Tail recursion optimization for performance")
    println("✓ Lazy evaluation with sequences for efficiency")
    println("✓ Functional reactive programming patterns")
    println("✓ Real-world applications: validation, data processing pipelines")
}