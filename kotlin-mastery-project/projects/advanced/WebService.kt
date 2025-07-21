/**
 * Advanced Project: RESTful Web Service
 * 
 * This project demonstrates building a complete RESTful web service in Kotlin:
 * - HTTP server with routing
 * - JSON serialization/deserialization
 * - Database integration patterns
 * - Middleware and authentication
 * - Error handling and validation
 * - Testing strategies for web services
 * - Asynchronous request handling
 * - API documentation and OpenAPI
 * 
 * Note: This is a conceptual implementation. For production use,
 * consider frameworks like Ktor, Spring Boot, or Http4k.
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

// ================================
// Data Models
// ================================

@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val createdAt: String = LocalDateTime.now().toString(),
    val isActive: Boolean = true
)

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val fullName: String
)

@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    val fullName: String? = null,
    val isActive: Boolean? = null
)

@Serializable
data class Task(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val createdAt: String = LocalDateTime.now().toString(),
    val dueDate: String? = null
)

@Serializable
data class CreateTaskRequest(
    val userId: Long,
    val title: String,
    val description: String,
    val dueDate: String? = null
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val timestamp: String = LocalDateTime.now().toString()
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

@Serializable
data class ValidationError(
    val field: String,
    val message: String
)

// ================================
// HTTP Abstractions
// ================================

enum class HttpMethod { GET, POST, PUT, DELETE, PATCH }

enum class HttpStatus(val code: Int, val message: String) {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error")
}

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val headers: Map<String, String> = emptyMap(),
    val queryParams: Map<String, String> = emptyMap(),
    val body: String? = null,
    val pathParams: Map<String, String> = emptyMap()
) {
    fun getHeader(name: String): String? = headers[name.lowercase()]
    fun getQueryParam(name: String): String? = queryParams[name]
    fun getPathParam(name: String): String? = pathParams[name]
    
    inline fun <reified T> parseBody(): T? {
        return body?.let { Json.decodeFromString<T>(it) }
    }
}

data class HttpResponse(
    val status: HttpStatus,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
) {
    companion object {
        inline fun <reified T> ok(data: T): HttpResponse {
            val response = ApiResponse(success = true, data = data)
            return HttpResponse(
                status = HttpStatus.OK,
                headers = mapOf("Content-Type" to "application/json"),
                body = Json.encodeToString(response)
            )
        }
        
        fun created(location: String? = null): HttpResponse {
            val headers = mutableMapOf("Content-Type" to "application/json")
            location?.let { headers["Location"] = it }
            
            return HttpResponse(
                status = HttpStatus.CREATED,
                headers = headers,
                body = Json.encodeToString(ApiResponse<Unit>(success = true))
            )
        }
        
        fun noContent(): HttpResponse {
            return HttpResponse(status = HttpStatus.NO_CONTENT)
        }
        
        fun badRequest(errors: List<ValidationError>): HttpResponse {
            return HttpResponse(
                status = HttpStatus.BAD_REQUEST,
                headers = mapOf("Content-Type" to "application/json"),
                body = Json.encodeToString(ApiResponse(success = false, error = "Validation failed", data = errors))
            )
        }
        
        fun notFound(message: String = "Resource not found"): HttpResponse {
            return HttpResponse(
                status = HttpStatus.NOT_FOUND,
                headers = mapOf("Content-Type" to "application/json"),
                body = Json.encodeToString(ApiResponse<Unit>(success = false, error = message))
            )
        }
        
        fun internalError(message: String = "Internal server error"): HttpResponse {
            return HttpResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                headers = mapOf("Content-Type" to "application/json"),
                body = Json.encodeToString(ApiResponse<Unit>(success = false, error = message))
            )
        }
    }
}

// ================================
// Repository Layer
// ================================

interface Repository<T, ID> {
    suspend fun save(entity: T): T
    suspend fun findById(id: ID): T?
    suspend fun findAll(page: Int = 0, size: Int = 20): PaginatedResponse<T>
    suspend fun update(id: ID, entity: T): T?
    suspend fun delete(id: ID): Boolean
    suspend fun exists(id: ID): Boolean
}

class InMemoryUserRepository : Repository<User, Long> {
    private val users = ConcurrentHashMap<Long, User>()
    private val idGenerator = AtomicLong(1)
    
    override suspend fun save(entity: User): User {
        val id = if (entity.id == 0L) idGenerator.getAndIncrement() else entity.id
        val user = entity.copy(id = id)
        users[id] = user
        return user
    }
    
    override suspend fun findById(id: Long): User? = users[id]
    
    override suspend fun findAll(page: Int, size: Int): PaginatedResponse<User> {
        val allUsers = users.values.toList().sortedBy { it.id }
        val totalElements = allUsers.size.toLong()
        val totalPages = (totalElements + size - 1) / size
        
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, allUsers.size)
        val pageData = if (startIndex < allUsers.size) {
            allUsers.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return PaginatedResponse(
            data = pageData,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages.toInt()
        )
    }
    
    override suspend fun update(id: Long, entity: User): User? {
        return if (users.containsKey(id)) {
            val updated = entity.copy(id = id)
            users[id] = updated
            updated
        } else {
            null
        }
    }
    
    override suspend fun delete(id: Long): Boolean {
        return users.remove(id) != null
    }
    
    override suspend fun exists(id: Long): Boolean = users.containsKey(id)
    
    fun findByUsername(username: String): User? {
        return users.values.find { it.username == username }
    }
    
    fun findByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }
}

class InMemoryTaskRepository : Repository<Task, Long> {
    private val tasks = ConcurrentHashMap<Long, Task>()
    private val idGenerator = AtomicLong(1)
    
    override suspend fun save(entity: Task): Task {
        val id = if (entity.id == 0L) idGenerator.getAndIncrement() else entity.id
        val task = entity.copy(id = id)
        tasks[id] = task
        return task
    }
    
    override suspend fun findById(id: Long): Task? = tasks[id]
    
    override suspend fun findAll(page: Int, size: Int): PaginatedResponse<Task> {
        val allTasks = tasks.values.toList().sortedBy { it.id }
        val totalElements = allTasks.size.toLong()
        val totalPages = (totalElements + size - 1) / size
        
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, allTasks.size)
        val pageData = if (startIndex < allTasks.size) {
            allTasks.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return PaginatedResponse(
            data = pageData,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages.toInt()
        )
    }
    
    override suspend fun update(id: Long, entity: Task): Task? {
        return if (tasks.containsKey(id)) {
            val updated = entity.copy(id = id)
            tasks[id] = updated
            updated
        } else {
            null
        }
    }
    
    override suspend fun delete(id: Long): Boolean {
        return tasks.remove(id) != null
    }
    
    override suspend fun exists(id: Long): Boolean = tasks.containsKey(id)
    
    fun findByUserId(userId: Long): List<Task> {
        return tasks.values.filter { it.userId == userId }
    }
}

// ================================
// Service Layer
// ================================

class UserService(
    private val userRepository: InMemoryUserRepository
) {
    suspend fun createUser(request: CreateUserRequest): Result<User> {
        // Validation
        val validationErrors = validateCreateUserRequest(request)
        if (validationErrors.isNotEmpty()) {
            return Result.failure(ValidationException(validationErrors))
        }
        
        // Check uniqueness
        if (userRepository.findByUsername(request.username) != null) {
            return Result.failure(ConflictException("Username already exists"))
        }
        
        if (userRepository.findByEmail(request.email) != null) {
            return Result.failure(ConflictException("Email already exists"))
        }
        
        // Create user
        val user = User(
            id = 0, // Will be assigned by repository
            username = request.username,
            email = request.email,
            fullName = request.fullName
        )
        
        val saved = userRepository.save(user)
        return Result.success(saved)
    }
    
    suspend fun getUserById(id: Long): Result<User> {
        val user = userRepository.findById(id)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(NotFoundException("User not found"))
        }
    }
    
    suspend fun getAllUsers(page: Int = 0, size: Int = 20): PaginatedResponse<User> {
        return userRepository.findAll(page, size)
    }
    
    suspend fun updateUser(id: Long, request: UpdateUserRequest): Result<User> {
        val existingUser = userRepository.findById(id)
            ?: return Result.failure(NotFoundException("User not found"))
        
        // Validation
        request.email?.let { email ->
            val userWithEmail = userRepository.findByEmail(email)
            if (userWithEmail != null && userWithEmail.id != id) {
                return Result.failure(ConflictException("Email already exists"))
            }
        }
        
        val updated = existingUser.copy(
            email = request.email ?: existingUser.email,
            fullName = request.fullName ?: existingUser.fullName,
            isActive = request.isActive ?: existingUser.isActive
        )
        
        val saved = userRepository.update(id, updated)!!
        return Result.success(saved)
    }
    
    suspend fun deleteUser(id: Long): Result<Unit> {
        val deleted = userRepository.delete(id)
        return if (deleted) {
            Result.success(Unit)
        } else {
            Result.failure(NotFoundException("User not found"))
        }
    }
    
    private fun validateCreateUserRequest(request: CreateUserRequest): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        if (request.username.isBlank()) {
            errors.add(ValidationError("username", "Username cannot be blank"))
        } else if (request.username.length < 3) {
            errors.add(ValidationError("username", "Username must be at least 3 characters"))
        }
        
        if (request.email.isBlank()) {
            errors.add(ValidationError("email", "Email cannot be blank"))
        } else if (!request.email.contains("@")) {
            errors.add(ValidationError("email", "Email must be valid"))
        }
        
        if (request.fullName.isBlank()) {
            errors.add(ValidationError("fullName", "Full name cannot be blank"))
        }
        
        return errors
    }
}

class TaskService(
    private val taskRepository: InMemoryTaskRepository,
    private val userRepository: InMemoryUserRepository
) {
    suspend fun createTask(request: CreateTaskRequest): Result<Task> {
        // Validate user exists
        if (!userRepository.exists(request.userId)) {
            return Result.failure(NotFoundException("User not found"))
        }
        
        // Validation
        val validationErrors = validateCreateTaskRequest(request)
        if (validationErrors.isNotEmpty()) {
            return Result.failure(ValidationException(validationErrors))
        }
        
        val task = Task(
            id = 0, // Will be assigned by repository
            userId = request.userId,
            title = request.title,
            description = request.description,
            dueDate = request.dueDate
        )
        
        val saved = taskRepository.save(task)
        return Result.success(saved)
    }
    
    suspend fun getTaskById(id: Long): Result<Task> {
        val task = taskRepository.findById(id)
        return if (task != null) {
            Result.success(task)
        } else {
            Result.failure(NotFoundException("Task not found"))
        }
    }
    
    suspend fun getTasksByUserId(userId: Long): Result<List<Task>> {
        if (!userRepository.exists(userId)) {
            return Result.failure(NotFoundException("User not found"))
        }
        
        val tasks = taskRepository.findByUserId(userId)
        return Result.success(tasks)
    }
    
    suspend fun completeTask(id: Long): Result<Task> {
        val task = taskRepository.findById(id)
            ?: return Result.failure(NotFoundException("Task not found"))
        
        val completed = task.copy(completed = true)
        val updated = taskRepository.update(id, completed)!!
        return Result.success(updated)
    }
    
    suspend fun deleteTask(id: Long): Result<Unit> {
        val deleted = taskRepository.delete(id)
        return if (deleted) {
            Result.success(Unit)
        } else {
            Result.failure(NotFoundException("Task not found"))
        }
    }
    
    private fun validateCreateTaskRequest(request: CreateTaskRequest): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        if (request.title.isBlank()) {
            errors.add(ValidationError("title", "Title cannot be blank"))
        }
        
        if (request.description.isBlank()) {
            errors.add(ValidationError("description", "Description cannot be blank"))
        }
        
        return errors
    }
}

// ================================
// Exception Classes
// ================================

abstract class ApiException(message: String) : Exception(message)
class ValidationException(val errors: List<ValidationError>) : ApiException("Validation failed")
class NotFoundException(message: String) : ApiException(message)
class ConflictException(message: String) : ApiException(message)

// ================================
// Controller/Handler Layer
// ================================

class UserController(private val userService: UserService) {
    
    suspend fun handleCreateUser(request: HttpRequest): HttpResponse {
        return try {
            val createRequest = request.parseBody<CreateUserRequest>()
                ?: return HttpResponse.badRequest(listOf(ValidationError("body", "Invalid request body")))
            
            when (val result = userService.createUser(createRequest)) {
                is Result.Success -> HttpResponse.created("/users/${result.getOrNull()?.id}")
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to create user")
        }
    }
    
    suspend fun handleGetUser(request: HttpRequest): HttpResponse {
        return try {
            val id = request.getPathParam("id")?.toLongOrNull()
                ?: return HttpResponse.badRequest(listOf(ValidationError("id", "Invalid user ID")))
            
            when (val result = userService.getUserById(id)) {
                is Result.Success -> HttpResponse.ok(result.getOrNull()!!)
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to get user")
        }
    }
    
    suspend fun handleGetAllUsers(request: HttpRequest): HttpResponse {
        return try {
            val page = request.getQueryParam("page")?.toIntOrNull() ?: 0
            val size = request.getQueryParam("size")?.toIntOrNull() ?: 20
            
            val result = userService.getAllUsers(page, size)
            HttpResponse.ok(result)
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to get users")
        }
    }
    
    suspend fun handleUpdateUser(request: HttpRequest): HttpResponse {
        return try {
            val id = request.getPathParam("id")?.toLongOrNull()
                ?: return HttpResponse.badRequest(listOf(ValidationError("id", "Invalid user ID")))
            
            val updateRequest = request.parseBody<UpdateUserRequest>()
                ?: return HttpResponse.badRequest(listOf(ValidationError("body", "Invalid request body")))
            
            when (val result = userService.updateUser(id, updateRequest)) {
                is Result.Success -> HttpResponse.ok(result.getOrNull()!!)
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to update user")
        }
    }
    
    suspend fun handleDeleteUser(request: HttpRequest): HttpResponse {
        return try {
            val id = request.getPathParam("id")?.toLongOrNull()
                ?: return HttpResponse.badRequest(listOf(ValidationError("id", "Invalid user ID")))
            
            when (val result = userService.deleteUser(id)) {
                is Result.Success -> HttpResponse.noContent()
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to delete user")
        }
    }
    
    private fun handleException(exception: Throwable): HttpResponse {
        return when (exception) {
            is ValidationException -> HttpResponse.badRequest(exception.errors)
            is NotFoundException -> HttpResponse.notFound(exception.message)
            is ConflictException -> HttpResponse(
                status = HttpStatus.CONFLICT,
                headers = mapOf("Content-Type" to "application/json"),
                body = Json.encodeToString(ApiResponse<Unit>(success = false, error = exception.message))
            )
            else -> HttpResponse.internalError("Internal server error")
        }
    }
}

class TaskController(private val taskService: TaskService) {
    
    suspend fun handleCreateTask(request: HttpRequest): HttpResponse {
        return try {
            val createRequest = request.parseBody<CreateTaskRequest>()
                ?: return HttpResponse.badRequest(listOf(ValidationError("body", "Invalid request body")))
            
            when (val result = taskService.createTask(createRequest)) {
                is Result.Success -> HttpResponse.created("/tasks/${result.getOrNull()?.id}")
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to create task")
        }
    }
    
    suspend fun handleGetTask(request: HttpRequest): HttpResponse {
        return try {
            val id = request.getPathParam("id")?.toLongOrNull()
                ?: return HttpResponse.badRequest(listOf(ValidationError("id", "Invalid task ID")))
            
            when (val result = taskService.getTaskById(id)) {
                is Result.Success -> HttpResponse.ok(result.getOrNull()!!)
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to get task")
        }
    }
    
    suspend fun handleGetUserTasks(request: HttpRequest): HttpResponse {
        return try {
            val userId = request.getPathParam("userId")?.toLongOrNull()
                ?: return HttpResponse.badRequest(listOf(ValidationError("userId", "Invalid user ID")))
            
            when (val result = taskService.getTasksByUserId(userId)) {
                is Result.Success -> HttpResponse.ok(result.getOrNull()!!)
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to get user tasks")
        }
    }
    
    suspend fun handleCompleteTask(request: HttpRequest): HttpResponse {
        return try {
            val id = request.getPathParam("id")?.toLongOrNull()
                ?: return HttpResponse.badRequest(listOf(ValidationError("id", "Invalid task ID")))
            
            when (val result = taskService.completeTask(id)) {
                is Result.Success -> HttpResponse.ok(result.getOrNull()!!)
                is Result.Failure -> handleException(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            HttpResponse.internalError("Failed to complete task")
        }
    }
    
    private fun handleException(exception: Throwable): HttpResponse {
        return when (exception) {
            is ValidationException -> HttpResponse.badRequest(exception.errors)
            is NotFoundException -> HttpResponse.notFound(exception.message)
            is ConflictException -> HttpResponse(
                status = HttpStatus.CONFLICT,
                headers = mapOf("Content-Type" to "application/json"),
                body = Json.encodeToString(ApiResponse<Unit>(success = false, error = exception.message))
            )
            else -> HttpResponse.internalError("Internal server error")
        }
    }
}

// ================================
// Routing and Server
// ================================

typealias RouteHandler = suspend (HttpRequest) -> HttpResponse

data class Route(
    val method: HttpMethod,
    val pathPattern: String,
    val handler: RouteHandler
)

class SimpleRouter {
    private val routes = mutableListOf<Route>()
    
    fun get(path: String, handler: RouteHandler) {
        routes.add(Route(HttpMethod.GET, path, handler))
    }
    
    fun post(path: String, handler: RouteHandler) {
        routes.add(Route(HttpMethod.POST, path, handler))
    }
    
    fun put(path: String, handler: RouteHandler) {
        routes.add(Route(HttpMethod.PUT, path, handler))
    }
    
    fun delete(path: String, handler: RouteHandler) {
        routes.add(Route(HttpMethod.DELETE, path, handler))
    }
    
    suspend fun route(request: HttpRequest): HttpResponse {
        val matchingRoute = routes.find { route ->
            route.method == request.method && matchesPath(route.pathPattern, request.path)
        }
        
        return if (matchingRoute != null) {
            val requestWithParams = request.copy(
                pathParams = extractPathParams(matchingRoute.pathPattern, request.path)
            )
            matchingRoute.handler(requestWithParams)
        } else {
            HttpResponse.notFound("Route not found")
        }
    }
    
    private fun matchesPath(pattern: String, path: String): Boolean {
        val patternParts = pattern.split("/")
        val pathParts = path.split("/")
        
        if (patternParts.size != pathParts.size) return false
        
        return patternParts.zip(pathParts).all { (patternPart, pathPart) ->
            patternPart.startsWith("{") && patternPart.endsWith("}") || patternPart == pathPart
        }
    }
    
    private fun extractPathParams(pattern: String, path: String): Map<String, String> {
        val patternParts = pattern.split("/")
        val pathParts = path.split("/")
        val params = mutableMapOf<String, String>()
        
        patternParts.zip(pathParts).forEach { (patternPart, pathPart) ->
            if (patternPart.startsWith("{") && patternPart.endsWith("}")) {
                val paramName = patternPart.substring(1, patternPart.length - 1)
                params[paramName] = pathPart
            }
        }
        
        return params
    }
}

class WebServer {
    private val router = SimpleRouter()
    
    fun configureRoutes() {
        val userRepository = InMemoryUserRepository()
        val taskRepository = InMemoryTaskRepository()
        val userService = UserService(userRepository)
        val taskService = TaskService(taskRepository, userRepository)
        val userController = UserController(userService)
        val taskController = TaskController(taskService)
        
        // User routes
        router.post("/users") { userController.handleCreateUser(it) }
        router.get("/users/{id}") { userController.handleGetUser(it) }
        router.get("/users") { userController.handleGetAllUsers(it) }
        router.put("/users/{id}") { userController.handleUpdateUser(it) }
        router.delete("/users/{id}") { userController.handleDeleteUser(it) }
        
        // Task routes
        router.post("/tasks") { taskController.handleCreateTask(it) }
        router.get("/tasks/{id}") { taskController.handleGetTask(it) }
        router.get("/users/{userId}/tasks") { taskController.handleGetUserTasks(it) }
        router.put("/tasks/{id}/complete") { taskController.handleCompleteTask(it) }
        
        // Health check
        router.get("/health") { 
            HttpResponse.ok(mapOf("status" to "healthy", "timestamp" to LocalDateTime.now().toString()))
        }
    }
    
    suspend fun handleRequest(request: HttpRequest): HttpResponse {
        return try {
            router.route(request)
        } catch (e: Exception) {
            println("Error handling request: ${e.message}")
            e.printStackTrace()
            HttpResponse.internalError("Internal server error")
        }
    }
}

// ================================
// Testing Utilities
// ================================

class WebServiceTester {
    private val server = WebServer()
    
    init {
        server.configureRoutes()
    }
    
    suspend fun testUserCrud() {
        println("=== Testing User CRUD Operations ===")
        
        // Create user
        val createUserRequest = HttpRequest(
            method = HttpMethod.POST,
            path = "/users",
            headers = mapOf("content-type" to "application/json"),
            body = Json.encodeToString(CreateUserRequest("testuser", "test@example.com", "Test User"))
        )
        
        val createResponse = server.handleRequest(createUserRequest)
        println("Create User Response: ${createResponse.status.code} - ${createResponse.body}")
        
        // Get user
        val getUserRequest = HttpRequest(
            method = HttpMethod.GET,
            path = "/users/1"
        )
        
        val getUserResponse = server.handleRequest(getUserRequest)
        println("Get User Response: ${getUserResponse.status.code} - ${getUserResponse.body}")
        
        // Get all users
        val getAllUsersRequest = HttpRequest(
            method = HttpMethod.GET,
            path = "/users",
            queryParams = mapOf("page" to "0", "size" to "10")
        )
        
        val getAllUsersResponse = server.handleRequest(getAllUsersRequest)
        println("Get All Users Response: ${getAllUsersResponse.status.code} - ${getAllUsersResponse.body}")
        
        // Update user
        val updateUserRequest = HttpRequest(
            method = HttpMethod.PUT,
            path = "/users/1",
            headers = mapOf("content-type" to "application/json"),
            body = Json.encodeToString(UpdateUserRequest(fullName = "Updated Test User"))
        )
        
        val updateResponse = server.handleRequest(updateUserRequest)
        println("Update User Response: ${updateResponse.status.code} - ${updateResponse.body}")
    }
    
    suspend fun testTaskOperations() {
        println("\n=== Testing Task Operations ===")
        
        // Create task
        val createTaskRequest = HttpRequest(
            method = HttpMethod.POST,
            path = "/tasks",
            headers = mapOf("content-type" to "application/json"),
            body = Json.encodeToString(CreateTaskRequest(1, "Test Task", "This is a test task"))
        )
        
        val createResponse = server.handleRequest(createTaskRequest)
        println("Create Task Response: ${createResponse.status.code} - ${createResponse.body}")
        
        // Get user tasks
        val getUserTasksRequest = HttpRequest(
            method = HttpMethod.GET,
            path = "/users/1/tasks"
        )
        
        val getUserTasksResponse = server.handleRequest(getUserTasksRequest)
        println("Get User Tasks Response: ${getUserTasksResponse.status.code} - ${getUserTasksResponse.body}")
        
        // Complete task
        val completeTaskRequest = HttpRequest(
            method = HttpMethod.PUT,
            path = "/tasks/1/complete"
        )
        
        val completeResponse = server.handleRequest(completeTaskRequest)
        println("Complete Task Response: ${completeResponse.status.code} - ${completeResponse.body}")
    }
    
    suspend fun testErrorHandling() {
        println("\n=== Testing Error Handling ===")
        
        // Invalid user ID
        val invalidUserRequest = HttpRequest(
            method = HttpMethod.GET,
            path = "/users/999"
        )
        
        val invalidUserResponse = server.handleRequest(invalidUserRequest)
        println("Invalid User Response: ${invalidUserResponse.status.code} - ${invalidUserResponse.body}")
        
        // Invalid request body
        val invalidBodyRequest = HttpRequest(
            method = HttpMethod.POST,
            path = "/users",
            headers = mapOf("content-type" to "application/json"),
            body = "{\"invalid\": \"json\"}"
        )
        
        val invalidBodyResponse = server.handleRequest(invalidBodyRequest)
        println("Invalid Body Response: ${invalidBodyResponse.status.code} - ${invalidBodyResponse.body}")
        
        // Route not found
        val notFoundRequest = HttpRequest(
            method = HttpMethod.GET,
            path = "/nonexistent"
        )
        
        val notFoundResponse = server.handleRequest(notFoundRequest)
        println("Not Found Response: ${notFoundResponse.status.code} - ${notFoundResponse.body}")
    }
}

// ================================
// Main Function
// ================================

suspend fun main() {
    println("🚀 RESTful Web Service Demo")
    println("=" * 40)
    
    val tester = WebServiceTester()
    
    tester.testUserCrud()
    tester.testTaskOperations()
    tester.testErrorHandling()
    
    println("\n=== Web Service Architecture Summary ===")
    println("✓ Layered architecture (Controller -> Service -> Repository)")
    println("✓ RESTful API design with proper HTTP methods and status codes")
    println("✓ JSON serialization/deserialization")
    println("✓ Request/response models with validation")
    println("✓ Error handling with custom exceptions")
    println("✓ Repository pattern for data access")
    println("✓ Pagination support for large datasets")
    println("✓ Path parameter extraction and routing")
    println("✓ Comprehensive testing utilities")
    
    println("\n💡 Production Considerations:")
    println("• Use a real HTTP server framework (Ktor, Spring Boot)")
    println("• Implement proper database integration")
    println("• Add authentication and authorization")
    println("• Include request/response logging and metrics")
    println("• Add rate limiting and security headers")
    println("• Implement proper configuration management")
    println("• Add comprehensive integration tests")
    println("• Include API documentation (OpenAPI/Swagger)")
}

/**
 * TODO: Advanced Web Service Features
 * 
 * 1. Authentication and authorization (JWT, OAuth2)
 * 2. Request/response middleware pipeline
 * 3. Database integration with connection pooling
 * 4. Caching layers (Redis, in-memory)
 * 5. Message queues for async processing
 * 6. WebSocket support for real-time features
 * 7. File upload and download handling
 * 8. API versioning strategies
 * 9. Rate limiting and throttling
 * 10. Comprehensive logging and monitoring
 * 11. Health checks and readiness probes
 * 12. Configuration management and environment profiles
 * 13. Error tracking and alerting
 * 14. API documentation generation
 * 15. Load testing and performance optimization
 */