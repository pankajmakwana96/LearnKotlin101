/**
 * Kotlin Coroutines - Basics
 * 
 * This module covers the fundamentals of Kotlin coroutines, including:
 * - Coroutine concepts and benefits
 * - Suspend functions
 * - Coroutine builders (runBlocking, launch, async)
 * - Coroutine scope and context
 * - Structured concurrency
 * - Exception handling in coroutines
 * - Practical examples and patterns
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis
import kotlin.random.Random

// ================================
// Suspend Functions
// ================================

/**
 * Basic suspend function that simulates an async operation
 */
suspend fun fetchUserData(userId: String): String {
    println("Fetching user data for: $userId")
    delay(1000) // Simulate network delay
    return "User data for $userId"
}

/**
 * Suspend function that simulates database query
 */
suspend fun queryDatabase(query: String): List<String> {
    println("Executing query: $query")
    delay(500) // Simulate database latency
    return listOf("Result 1", "Result 2", "Result 3")
}

/**
 * Suspend function that can throw exceptions
 */
suspend fun riskyOperation(shouldFail: Boolean = false): String {
    delay(300)
    if (shouldFail) {
        throw RuntimeException("Operation failed!")
    }
    return "Operation successful"
}

/**
 * Suspend function demonstrating CPU-intensive work
 */
suspend fun performCalculation(n: Int): Long = withContext(Dispatchers.Default) {
    // Switch to computation dispatcher for CPU-intensive work
    var result = 0L
    for (i in 1..n) {
        result += i * i
    }
    result
}

// ================================
// Coroutine Builders
// ================================

/**
 * Demonstrates runBlocking - bridges blocking and non-blocking worlds
 */
fun demonstrateRunBlocking() {
    println("=== RunBlocking Demo ===")
    
    println("Before runBlocking")
    
    runBlocking {
        println("Inside runBlocking")
        
        val userData = fetchUserData("user123")
        println("Retrieved: $userData")
        
        val dbResults = queryDatabase("SELECT * FROM users")
        println("Query results: $dbResults")
    }
    
    println("After runBlocking")
}

/**
 * Demonstrates launch - fire and forget coroutines
 */
fun demonstrateLaunch() = runBlocking {
    println("\n=== Launch Demo ===")
    
    // Launch multiple coroutines concurrently
    val job1 = launch {
        repeat(3) { i ->
            println("Job 1 - Iteration $i")
            delay(100)
        }
    }
    
    val job2 = launch {
        repeat(3) { i ->
            println("Job 2 - Iteration $i")
            delay(150)
        }
    }
    
    val job3 = launch {
        delay(200)
        println("Job 3 - One-time execution")
    }
    
    // Wait for all jobs to complete
    joinAll(job1, job2, job3)
    println("All jobs completed")
}

/**
 * Demonstrates async - concurrent computation with results
 */
fun demonstrateAsync() = runBlocking {
    println("\n=== Async Demo ===")
    
    val time = measureTimeMillis {
        // Sequential execution (slow)
        val sequential1 = fetchUserData("user1")
        val sequential2 = fetchUserData("user2")
        println("Sequential: $sequential1, $sequential2")
    }
    println("Sequential time: ${time}ms")
    
    val concurrentTime = measureTimeMillis {
        // Concurrent execution (fast)
        val deferred1 = async { fetchUserData("user3") }
        val deferred2 = async { fetchUserData("user4") }
        
        // Await results
        val result1 = deferred1.await()
        val result2 = deferred2.await()
        
        println("Concurrent: $result1, $result2")
    }
    println("Concurrent time: ${concurrentTime}ms")
    
    // Multiple async operations
    val deferredList = (1..5).map { id ->
        async { fetchUserData("user$id") }
    }
    
    // Collect all results
    val results = deferredList.awaitAll()
    println("All results: $results")
}

// ================================
// Coroutine Scope and Context
// ================================

/**
 * Demonstrates different coroutine scopes
 */
fun demonstrateScopes() = runBlocking {
    println("\n=== Coroutine Scopes ===")
    
    // GlobalScope - application-wide scope (use with caution)
    val globalJob = GlobalScope.launch {
        delay(1000)
        println("GlobalScope job completed")
    }
    
    // Custom scope with specific context
    val customScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    customScope.launch {
        println("Custom scope job on ${Thread.currentThread().name}")
        val data = queryDatabase("SELECT * FROM products")
        println("Custom scope data: $data")
    }
    
    // coroutineScope - creates child scope
    coroutineScope {
        launch {
            delay(500)
            println("Child coroutine 1")
        }
        
        launch {
            delay(700)
            println("Child coroutine 2")
        }
        
        println("Parent coroutine")
    }
    
    println("After coroutineScope")
    
    // Wait for global job and cleanup custom scope
    globalJob.join()
    customScope.cancel()
}

/**
 * Demonstrates different dispatchers
 */
fun demonstrateDispatchers() = runBlocking {
    println("\n=== Dispatchers Demo ===")
    
    // Main dispatcher (would be UI thread in Android)
    launch(Dispatchers.Main) {
        println("Main dispatcher: ${Thread.currentThread().name}")
    }
    
    // IO dispatcher for I/O operations
    launch(Dispatchers.IO) {
        println("IO dispatcher: ${Thread.currentThread().name}")
        val data = queryDatabase("SELECT * FROM logs")
        println("IO operation completed: ${data.size} results")
    }
    
    // Default dispatcher for CPU-intensive work
    launch(Dispatchers.Default) {
        println("Default dispatcher: ${Thread.currentThread().name}")
        val result = performCalculation(1000)
        println("Calculation result: $result")
    }
    
    // Unconfined dispatcher (use with caution)
    launch(Dispatchers.Unconfined) {
        println("Unconfined before delay: ${Thread.currentThread().name}")
        delay(100)
        println("Unconfined after delay: ${Thread.currentThread().name}")
    }
    
    delay(2000) // Wait for all to complete
}

// ================================
// Structured Concurrency
// ================================

/**
 * Demonstrates structured concurrency principles
 */
fun demonstrateStructuredConcurrency() = runBlocking {
    println("\n=== Structured Concurrency ===")
    
    try {
        coroutineScope {
            // All child coroutines must complete before this scope completes
            val job1 = launch {
                delay(1000)
                println("Job 1 completed")
            }
            
            val job2 = launch {
                delay(500)
                println("Job 2 completed")
            }
            
            val job3 = launch {
                delay(800)
                println("Job 3 completed")
            }
            
            println("Waiting for all jobs in structured scope...")
            // No need to explicitly join - coroutineScope waits for all children
        }
        
        println("All structured jobs completed")
        
    } catch (e: Exception) {
        println("Exception in structured concurrency: ${e.message}")
    }
}

/**
 * Demonstrates job hierarchy and cancellation
 */
fun demonstrateJobHierarchy() = runBlocking {
    println("\n=== Job Hierarchy ===")
    
    val parentJob = launch {
        println("Parent job started")
        
        val child1 = launch {
            try {
                repeat(5) { i ->
                    println("Child 1 - Iteration $i")
                    delay(200)
                }
            } catch (e: CancellationException) {
                println("Child 1 was cancelled")
            }
        }
        
        val child2 = launch {
            try {
                repeat(5) { i ->
                    println("Child 2 - Iteration $i")
                    delay(300)
                }
            } catch (e: CancellationException) {
                println("Child 2 was cancelled")
            }
        }
        
        delay(1000)
        println("Parent job completed")
    }
    
    delay(600) // Let it run for a bit
    println("Cancelling parent job...")
    parentJob.cancel() // This cancels all children too
    
    parentJob.join() // Wait for cancellation to complete
    println("Parent job and all children cancelled")
}

// ================================
// Exception Handling
// ================================

/**
 * Demonstrates exception handling in coroutines
 */
fun demonstrateExceptionHandling() = runBlocking {
    println("\n=== Exception Handling ===")
    
    // Exception handling with try-catch
    try {
        val result = riskyOperation(shouldFail = true)
        println("Result: $result")
    } catch (e: Exception) {
        println("Caught exception: ${e.message}")
    }
    
    // Exception handling with launch
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler caught: ${exception.message}")
    }
    
    val job = launch(exceptionHandler) {
        riskyOperation(shouldFail = true)
    }
    
    job.join()
    
    // Exception handling with async
    val deferredResult = async {
        riskyOperation(shouldFail = true)
    }
    
    try {
        val result = deferredResult.await()
        println("Async result: $result")
    } catch (e: Exception) {
        println("Exception from async: ${e.message}")
    }
    
    // SupervisorJob - doesn't cancel siblings on failure
    supervisorScope {
        val job1 = launch {
            delay(100)
            throw RuntimeException("Job 1 failed")
        }
        
        val job2 = launch {
            delay(200)
            println("Job 2 completed successfully")
        }
        
        val job3 = launch {
            delay(300)
            println("Job 3 completed successfully")
        }
        
        // Even though job1 fails, job2 and job3 continue
        try {
            job1.join()
        } catch (e: Exception) {
            println("Job 1 failed but others continue")
        }
        
        joinAll(job2, job3)
    }
}

// ================================
// Practical Examples
// ================================

/**
 * Simulates a real-world API service
 */
class ApiService {
    suspend fun fetchProfile(userId: String): UserProfile {
        delay(Random.nextLong(100, 500)) // Simulate network latency
        return UserProfile(userId, "User $userId", "user$userId@example.com")
    }
    
    suspend fun fetchPosts(userId: String): List<Post> {
        delay(Random.nextLong(200, 800))
        return (1..3).map { Post("post$it", "Post $it by $userId", userId) }
    }
    
    suspend fun fetchComments(postId: String): List<Comment> {
        delay(Random.nextLong(150, 400))
        return (1..2).map { Comment("comment$it", "Comment $it on $postId", "user$it") }
    }
}

data class UserProfile(val id: String, val name: String, val email: String)
data class Post(val id: String, val title: String, val authorId: String)
data class Comment(val id: String, val text: String, val authorId: String)

/**
 * Service that uses coroutines for efficient data fetching
 */
class UserDataService(private val apiService: ApiService) {
    
    /**
     * Fetch user data concurrently
     */
    suspend fun getUserDashboard(userId: String): UserDashboard {
        return coroutineScope {
            // Fetch profile and posts concurrently
            val profileDeferred = async { apiService.fetchProfile(userId) }
            val postsDeferred = async { apiService.fetchPosts(userId) }
            
            val profile = profileDeferred.await()
            val posts = postsDeferred.await()
            
            // Fetch comments for all posts concurrently
            val commentsDeferred = posts.map { post ->
                async { post.id to apiService.fetchComments(post.id) }
            }
            
            val commentsByPost = commentsDeferred.awaitAll().toMap()
            
            UserDashboard(profile, posts, commentsByPost)
        }
    }
    
    /**
     * Fetch multiple user profiles with timeout
     */
    suspend fun fetchMultipleUsers(userIds: List<String>): List<UserProfile> {
        return withTimeoutOrNull(5000) { // 5 second timeout
            coroutineScope {
                userIds.map { userId ->
                    async { apiService.fetchProfile(userId) }
                }.awaitAll()
            }
        } ?: emptyList() // Return empty list if timeout
    }
}

data class UserDashboard(
    val profile: UserProfile,
    val posts: List<Post>,
    val commentsByPost: Map<String, List<Comment>>
)

/**
 * Background task manager using coroutines
 */
class TaskManager {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    fun scheduleTask(name: String, delayMs: Long, task: suspend () -> Unit): Job {
        return scope.launch {
            delay(delayMs)
            try {
                println("Executing task: $name")
                task()
                println("Task completed: $name")
            } catch (e: Exception) {
                println("Task failed: $name - ${e.message}")
            }
        }
    }
    
    fun scheduleRepeatingTask(
        name: String, 
        initialDelayMs: Long, 
        periodMs: Long, 
        task: suspend () -> Unit
    ): Job {
        return scope.launch {
            delay(initialDelayMs)
            
            while (isActive) {
                try {
                    println("Executing repeating task: $name")
                    task()
                    delay(periodMs)
                } catch (e: CancellationException) {
                    println("Repeating task cancelled: $name")
                    throw e
                } catch (e: Exception) {
                    println("Task failed: $name - ${e.message}")
                    delay(periodMs) // Continue despite failure
                }
            }
        }
    }
    
    fun shutdown() {
        scope.cancel("TaskManager shutdown")
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateRealWorldExample() = runBlocking {
    println("\n=== Real-World Example ===")
    
    val apiService = ApiService()
    val userDataService = UserDataService(apiService)
    
    val time = measureTimeMillis {
        val dashboard = userDataService.getUserDashboard("user123")
        
        println("Profile: ${dashboard.profile}")
        println("Posts: ${dashboard.posts.size}")
        dashboard.commentsByPost.forEach { (postId, comments) ->
            println("Post $postId has ${comments.size} comments")
        }
    }
    
    println("Dashboard loaded in ${time}ms")
    
    // Fetch multiple users with timeout
    val userIds = listOf("user1", "user2", "user3", "user4", "user5")
    val profiles = userDataService.fetchMultipleUsers(userIds)
    println("Fetched ${profiles.size} profiles: ${profiles.map { it.name }}")
}

fun demonstrateTaskManager() = runBlocking {
    println("\n=== Task Manager Example ===")
    
    val taskManager = TaskManager()
    
    // Schedule one-time tasks
    val task1 = taskManager.scheduleTask("Backup Database", 500) {
        delay(200)
        println("Database backup completed")
    }
    
    val task2 = taskManager.scheduleTask("Send Emails", 300) {
        delay(400)
        println("Emails sent")
    }
    
    // Schedule repeating task
    val repeatingTask = taskManager.scheduleRepeatingTask("Health Check", 200, 800) {
        delay(100)
        println("Health check: All systems operational")
    }
    
    delay(3000) // Let tasks run for 3 seconds
    
    println("Cancelling repeating task...")
    repeatingTask.cancel()
    
    // Wait for one-time tasks to complete
    joinAll(task1, task2)
    
    // Shutdown task manager
    taskManager.shutdown()
    println("Task manager shutdown complete")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a download manager that downloads multiple files concurrently
 * 2. Implement a cache with automatic expiration using coroutines
 * 3. Build a rate limiter using coroutines and channels
 * 4. Create a retry mechanism with exponential backoff
 * 5. Implement a concurrent web scraper with proper error handling
 * 6. Build a real-time chat system using coroutines and flows
 * 7. Create a batch processor that processes items in parallel with batching
 * 8. Implement a load balancer that distributes requests across multiple services
 * 9. Build a monitoring system that collects metrics from multiple sources
 * 10. Create a distributed task queue using coroutines
 */

fun main() {
    demonstrateRunBlocking()
    demonstrateLaunch()
    demonstrateAsync()
    demonstrateScopes()
    demonstrateDispatchers()
    demonstrateStructuredConcurrency()
    demonstrateJobHierarchy()
    demonstrateExceptionHandling()
    demonstrateRealWorldExample()
    demonstrateTaskManager()
    
    println("\n=== Coroutines Basics Summary ===")
    println("✓ Suspend functions for non-blocking operations")
    println("✓ Coroutine builders: runBlocking, launch, async")
    println("✓ Structured concurrency and proper scope management")
    println("✓ Different dispatchers for different types of work")
    println("✓ Exception handling strategies in concurrent code")
    println("✓ Real-world patterns: parallel data fetching, task scheduling")
    println("✓ Proper resource management and cleanup")
}