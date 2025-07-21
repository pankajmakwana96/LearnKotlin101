/**
 * Task Management System - Progressive Project
 * 
 * This project demonstrates practical application of Kotlin concepts learned:
 * - Data classes and sealed classes for domain modeling
 * - Coroutines for asynchronous operations
 * - Flow for reactive updates
 * - Functional programming patterns
 * - Extension functions and operator overloading
 * - Delegation patterns
 * - Exception handling
 * - Testing strategies
 * 
 * Features:
 * - Create, update, delete tasks
 * - Task priorities and categories
 * - Due date tracking
 * - Progress monitoring
 * - Search and filtering
 * - Export functionality
 * - Real-time updates
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

// ================================
// Domain Models
// ================================

/**
 * Task priority levels
 */
enum class Priority(val level: Int, val displayName: String) {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    URGENT(4, "Urgent");
    
    fun isHigherThan(other: Priority): Boolean = this.level > other.level
    
    companion object {
        fun fromString(str: String): Priority? = values().find { 
            it.name.equals(str, ignoreCase = true) 
        }
    }
}

/**
 * Task status using sealed class
 */
sealed class TaskStatus(val displayName: String) {
    object TODO : TaskStatus("To Do")
    object IN_PROGRESS : TaskStatus("In Progress")
    object COMPLETED : TaskStatus("Completed")
    object CANCELLED : TaskStatus("Cancelled")
    
    fun canTransitionTo(newStatus: TaskStatus): Boolean = when (this) {
        TODO -> newStatus in setOf(IN_PROGRESS, CANCELLED)
        IN_PROGRESS -> newStatus in setOf(COMPLETED, CANCELLED, TODO)
        COMPLETED -> false
        CANCELLED -> newStatus == TODO
    }
}

/**
 * Task category
 */
data class Category(
    val id: String,
    val name: String,
    val color: String = "#007AFF"
) {
    companion object {
        val DEFAULT = Category("default", "General", "#007AFF")
        val WORK = Category("work", "Work", "#FF6B35")
        val PERSONAL = Category("personal", "Personal", "#32D74B")
        val HEALTH = Category("health", "Health", "#FF3B30")
    }
}

/**
 * Main Task data class
 */
data class Task(
    val id: String = generateId(),
    val title: String,
    val description: String = "",
    val category: Category = Category.DEFAULT,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val tags: Set<String> = emptySet(),
    val estimatedHours: Double? = null,
    val actualHours: Double? = null
) {
    val isOverdue: Boolean
        get() = dueDate?.isBefore(LocalDateTime.now()) == true && status !is TaskStatus.COMPLETED
    
    val isCompleted: Boolean
        get() = status is TaskStatus.COMPLETED
    
    val progressPercentage: Int
        get() = when (status) {
            TaskStatus.TODO -> 0
            TaskStatus.IN_PROGRESS -> 50
            TaskStatus.COMPLETED -> 100
            TaskStatus.CANCELLED -> 0
        }
    
    fun updateStatus(newStatus: TaskStatus): Task? = 
        if (status.canTransitionTo(newStatus)) {
            copy(status = newStatus, updatedAt = LocalDateTime.now())
        } else null
    
    fun addTag(tag: String): Task = copy(tags = tags + tag, updatedAt = LocalDateTime.now())
    
    fun removeTag(tag: String): Task = copy(tags = tags - tag, updatedAt = LocalDateTime.now())
    
    fun markCompleted(): Task? = updateStatus(TaskStatus.COMPLETED)
    
    fun estimateVsActual(): String = when {
        estimatedHours != null && actualHours != null -> {
            val variance = ((actualHours - estimatedHours) / estimatedHours * 100).toInt()
            "${if (variance > 0) "+" else ""}$variance%"
        }
        else -> "N/A"
    }
    
    companion object {
        private fun generateId(): String = "task_${Random.nextInt(10000, 99999)}"
    }
}

/**
 * Task search and filter criteria
 */
data class TaskFilter(
    val query: String? = null,
    val category: Category? = null,
    val priority: Priority? = null,
    val status: TaskStatus? = null,
    val overdue: Boolean? = null,
    val tags: Set<String> = emptySet(),
    val dueBefore: LocalDateTime? = null,
    val dueAfter: LocalDateTime? = null
) {
    fun matches(task: Task): Boolean {
        if (query?.isNotEmpty() == true) {
            val searchQuery = query.lowercase()
            if (!task.title.lowercase().contains(searchQuery) && 
                !task.description.lowercase().contains(searchQuery)) {
                return false
            }
        }
        
        if (category != null && task.category != category) return false
        if (priority != null && task.priority != priority) return false
        if (status != null && task.status != status) return false
        if (overdue == true && !task.isOverdue) return false
        if (overdue == false && task.isOverdue) return false
        
        if (tags.isNotEmpty() && !tags.any { it in task.tags }) return false
        
        if (dueBefore != null && (task.dueDate?.isAfter(dueBefore) != false)) return false
        if (dueAfter != null && (task.dueDate?.isBefore(dueAfter) != false)) return false
        
        return true
    }
}

// ================================
// Repository Layer
// ================================

/**
 * Task repository interface
 */
interface TaskRepository {
    suspend fun create(task: Task): Task
    suspend fun update(task: Task): Task?
    suspend fun delete(id: String): Boolean
    suspend fun findById(id: String): Task?
    suspend fun findAll(): List<Task>
    suspend fun findByFilter(filter: TaskFilter): List<Task>
    fun observeAll(): Flow<List<Task>>
    fun observeByFilter(filter: TaskFilter): Flow<List<Task>>
}

/**
 * In-memory task repository implementation
 */
class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableMapOf<String, Task>()
    private val taskUpdates = MutableSharedFlow<List<Task>>(replay = 1)
    
    init {
        // Initialize with empty list
        taskUpdates.tryEmit(emptyList())
    }
    
    override suspend fun create(task: Task): Task {
        tasks[task.id] = task
        emitUpdate()
        return task
    }
    
    override suspend fun update(task: Task): Task? {
        return if (tasks.containsKey(task.id)) {
            tasks[task.id] = task
            emitUpdate()
            task
        } else null
    }
    
    override suspend fun delete(id: String): Boolean {
        val removed = tasks.remove(id) != null
        if (removed) emitUpdate()
        return removed
    }
    
    override suspend fun findById(id: String): Task? = tasks[id]
    
    override suspend fun findAll(): List<Task> = tasks.values.toList()
    
    override suspend fun findByFilter(filter: TaskFilter): List<Task> =
        tasks.values.filter { filter.matches(it) }
    
    override fun observeAll(): Flow<List<Task>> = taskUpdates.asSharedFlow()
    
    override fun observeByFilter(filter: TaskFilter): Flow<List<Task>> =
        taskUpdates.map { tasks -> tasks.filter { filter.matches(it) } }
    
    private fun emitUpdate() {
        taskUpdates.tryEmit(tasks.values.toList())
    }
}

// ================================
// Service Layer
// ================================

/**
 * Task events for system-wide notifications
 */
sealed class TaskEvent {
    data class TaskCreated(val task: Task) : TaskEvent()
    data class TaskUpdated(val task: Task) : TaskEvent()
    data class TaskDeleted(val taskId: String) : TaskEvent()
    data class TaskCompleted(val task: Task) : TaskEvent()
    data class TaskOverdue(val task: Task) : TaskEvent()
}

/**
 * Task service with business logic
 */
class TaskService(
    private val repository: TaskRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val _events = MutableSharedFlow<TaskEvent>()
    val events: SharedFlow<TaskEvent> = _events.asSharedFlow()
    
    // Statistics flow
    val statistics: Flow<TaskStatistics> = repository.observeAll().map { tasks ->
        calculateStatistics(tasks)
    }
    
    init {
        startOverdueMonitoring()
    }
    
    suspend fun createTask(
        title: String,
        description: String = "",
        category: Category = Category.DEFAULT,
        priority: Priority = Priority.MEDIUM,
        dueDate: LocalDateTime? = null,
        estimatedHours: Double? = null
    ): Task {
        val task = Task(
            title = title,
            description = description,
            category = category,
            priority = priority,
            dueDate = dueDate,
            estimatedHours = estimatedHours
        )
        
        val createdTask = repository.create(task)
        _events.emit(TaskEvent.TaskCreated(createdTask))
        
        return createdTask
    }
    
    suspend fun updateTask(task: Task): Task? {
        val updatedTask = repository.update(task)
        updatedTask?.let {
            _events.emit(TaskEvent.TaskUpdated(it))
        }
        return updatedTask
    }
    
    suspend fun updateTaskStatus(id: String, newStatus: TaskStatus): Task? {
        val existingTask = repository.findById(id) ?: return null
        val updatedTask = existingTask.updateStatus(newStatus) ?: return null
        
        repository.update(updatedTask)
        _events.emit(TaskEvent.TaskUpdated(updatedTask))
        
        if (newStatus is TaskStatus.COMPLETED) {
            _events.emit(TaskEvent.TaskCompleted(updatedTask))
        }
        
        return updatedTask
    }
    
    suspend fun completeTask(id: String): Task? = updateTaskStatus(id, TaskStatus.COMPLETED)
    
    suspend fun deleteTask(id: String): Boolean {
        val deleted = repository.delete(id)
        if (deleted) {
            _events.emit(TaskEvent.TaskDeleted(id))
        }
        return deleted
    }
    
    suspend fun addTagToTask(id: String, tag: String): Task? {
        val task = repository.findById(id) ?: return null
        val updatedTask = task.addTag(tag)
        return repository.update(updatedTask)
    }
    
    suspend fun searchTasks(query: String): List<Task> {
        val filter = TaskFilter(query = query)
        return repository.findByFilter(filter)
    }
    
    suspend fun getTasksByPriority(priority: Priority): List<Task> {
        val filter = TaskFilter(priority = priority)
        return repository.findByFilter(filter)
    }
    
    suspend fun getOverdueTasks(): List<Task> {
        val filter = TaskFilter(overdue = true)
        return repository.findByFilter(filter)
    }
    
    suspend fun getTasksByCategory(category: Category): List<Task> {
        val filter = TaskFilter(category = category)
        return repository.findByFilter(filter)
    }
    
    fun observeTasksByFilter(filter: TaskFilter): Flow<List<Task>> =
        repository.observeByFilter(filter)
    
    private fun startOverdueMonitoring() {
        scope.launch {
            repository.observeAll()
                .map { tasks -> tasks.filter { it.isOverdue && it.status !is TaskStatus.COMPLETED } }
                .distinctUntilChanged()
                .collect { overdueTasks ->
                    overdueTasks.forEach { task ->
                        _events.emit(TaskEvent.TaskOverdue(task))
                    }
                }
        }
    }
    
    private fun calculateStatistics(tasks: List<Task>): TaskStatistics {
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val overdue = tasks.count { it.isOverdue }
        val inProgress = tasks.count { it.status is TaskStatus.IN_PROGRESS }
        val byPriority = tasks.groupingBy { it.priority }.eachCount()
        val byCategory = tasks.groupingBy { it.category }.eachCount()
        
        return TaskStatistics(
            totalTasks = total,
            completedTasks = completed,
            overdueTasks = overdue,
            inProgressTasks = inProgress,
            completionRate = if (total > 0) completed.toDouble() / total else 0.0,
            tasksByPriority = byPriority,
            tasksByCategory = byCategory
        )
    }
}

/**
 * Task statistics data class
 */
data class TaskStatistics(
    val totalTasks: Int,
    val completedTasks: Int,
    val overdueTasks: Int,
    val inProgressTasks: Int,
    val completionRate: Double,
    val tasksByPriority: Map<Priority, Int>,
    val tasksByCategory: Map<Category, Int>
) {
    val pendingTasks: Int = totalTasks - completedTasks
    
    val completionPercentage: Int = (completionRate * 100).toInt()
    
    fun getTopPriority(): Priority? = tasksByPriority.maxByOrNull { it.value }?.key
    
    fun getTopCategory(): Category? = tasksByCategory.maxByOrNull { it.value }?.key
}

// ================================
// Export Functionality
// ================================

/**
 * Task export formats
 */
enum class ExportFormat {
    JSON, CSV, MARKDOWN
}

/**
 * Task exporter using functional programming
 */
class TaskExporter {
    
    fun export(tasks: List<Task>, format: ExportFormat): String = when (format) {
        ExportFormat.JSON -> exportJson(tasks)
        ExportFormat.CSV -> exportCsv(tasks)
        ExportFormat.MARKDOWN -> exportMarkdown(tasks)
    }
    
    private fun exportJson(tasks: List<Task>): String {
        val tasksJson = tasks.joinToString(",\n") { task ->
            """
            {
                "id": "${task.id}",
                "title": "${task.title.replace("\"", "\\\"")}",
                "description": "${task.description.replace("\"", "\\\"")}",
                "category": "${task.category.name}",
                "priority": "${task.priority.name}",
                "status": "${task.status.displayName}",
                "dueDate": ${task.dueDate?.let { "\"$it\"" } ?: "null"},
                "isOverdue": ${task.isOverdue},
                "tags": [${task.tags.joinToString { "\"$it\"" }}]
            }
            """.trimIndent()
        }
        
        return "[\n$tasksJson\n]"
    }
    
    private fun exportCsv(tasks: List<Task>): String {
        val header = "ID,Title,Description,Category,Priority,Status,Due Date,Overdue,Tags"
        val rows = tasks.joinToString("\n") { task ->
            listOf(
                task.id,
                "\"${task.title}\"",
                "\"${task.description}\"",
                task.category.name,
                task.priority.name,
                task.status.displayName,
                task.dueDate?.toString() ?: "",
                task.isOverdue.toString(),
                "\"${task.tags.joinToString(";")}\""
            ).joinToString(",")
        }
        
        return "$header\n$rows"
    }
    
    private fun exportMarkdown(tasks: List<Task>): String {
        val completedTasks = tasks.filter { it.isCompleted }
        val pendingTasks = tasks.filter { !it.isCompleted }
        
        return buildString {
            appendLine("# Task Export")
            appendLine()
            appendLine("Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}")
            appendLine("Total tasks: ${tasks.size}")
            appendLine("Completed: ${completedTasks.size}")
            appendLine("Pending: ${pendingTasks.size}")
            appendLine()
            
            if (pendingTasks.isNotEmpty()) {
                appendLine("## Pending Tasks")
                appendLine()
                pendingTasks.sortedBy { it.priority.level }.reversed().forEach { task ->
                    appendLine("### ${task.title}")
                    appendLine("- **Priority**: ${task.priority.displayName}")
                    appendLine("- **Category**: ${task.category.name}")
                    appendLine("- **Status**: ${task.status.displayName}")
                    if (task.dueDate != null) {
                        appendLine("- **Due Date**: ${task.dueDate}")
                    }
                    if (task.description.isNotEmpty()) {
                        appendLine("- **Description**: ${task.description}")
                    }
                    if (task.tags.isNotEmpty()) {
                        appendLine("- **Tags**: ${task.tags.joinToString(", ")}")
                    }
                    appendLine()
                }
            }
            
            if (completedTasks.isNotEmpty()) {
                appendLine("## Completed Tasks")
                appendLine()
                completedTasks.forEach { task ->
                    appendLine("- [x] ${task.title}")
                }
            }
        }
    }
}

// ================================
// Console Interface
// ================================

/**
 * Simple console interface for the task management system
 */
class TaskConsoleInterface(
    private val taskService: TaskService,
    private val exporter: TaskExporter = TaskExporter()
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    fun start() {
        scope.launch {
            println("=== Task Management System ===")
            println("Type 'help' for available commands")
            
            // Start event monitoring
            taskService.events.collect { event ->
                when (event) {
                    is TaskEvent.TaskCreated -> println("✅ Task created: ${event.task.title}")
                    is TaskEvent.TaskCompleted -> println("🎉 Task completed: ${event.task.title}")
                    is TaskEvent.TaskOverdue -> println("⚠️ Task overdue: ${event.task.title}")
                    else -> {}
                }
            }
        }
        
        runBlocking {
            demoWorkflow()
        }
    }
    
    private suspend fun demoWorkflow() {
        println("\n=== Demo Workflow ===")
        
        // Create some sample tasks
        println("Creating sample tasks...")
        
        val task1 = taskService.createTask(
            title = "Complete Kotlin project",
            description = "Finish the task management system implementation",
            category = Category.WORK,
            priority = Priority.HIGH,
            dueDate = LocalDateTime.now().plusDays(3),
            estimatedHours = 8.0
        )
        
        val task2 = taskService.createTask(
            title = "Review code",
            description = "Review pull requests from team",
            category = Category.WORK,
            priority = Priority.MEDIUM,
            dueDate = LocalDateTime.now().plusDays(1)
        )
        
        val task3 = taskService.createTask(
            title = "Exercise",
            description = "30 minutes cardio workout",
            category = Category.HEALTH,
            priority = Priority.MEDIUM,
            dueDate = LocalDateTime.now().plusHours(2)
        )
        
        // Add some tags
        taskService.addTagToTask(task1.id, "kotlin")
        taskService.addTagToTask(task1.id, "programming")
        taskService.addTagToTask(task2.id, "review")
        taskService.addTagToTask(task3.id, "health")
        
        delay(100) // Allow events to propagate
        
        // Show statistics
        taskService.statistics.take(1).collect { stats ->
            println("\n=== Current Statistics ===")
            println("Total tasks: ${stats.totalTasks}")
            println("Completed: ${stats.completedTasks}")
            println("In progress: ${stats.inProgressTasks}")
            println("Overdue: ${stats.overdueTasks}")
            println("Completion rate: ${stats.completionPercentage}%")
        }
        
        // Update task status
        println("\nUpdating task status...")
        taskService.updateTaskStatus(task2.id, TaskStatus.IN_PROGRESS)
        taskService.completeTask(task3.id)
        
        delay(100)
        
        // Show tasks by priority
        println("\n=== High Priority Tasks ===")
        val highPriorityTasks = taskService.getTasksByPriority(Priority.HIGH)
        highPriorityTasks.forEach { task ->
            println("- ${task.title} (${task.status.displayName})")
        }
        
        // Search tasks
        println("\n=== Search Results for 'code' ===")
        val searchResults = taskService.searchTasks("code")
        searchResults.forEach { task ->
            println("- ${task.title}")
        }
        
        // Export tasks
        println("\n=== Export Demo ===")
        val allTasks = taskService.searchTasks("")
        val markdown = exporter.export(allTasks, ExportFormat.MARKDOWN)
        println("Markdown export preview:")
        println(markdown.take(500) + if (markdown.length > 500) "..." else "")
        
        println("\n=== Demo completed ===")
    }
}

// ================================
// Main Function and Demo
// ================================

suspend fun main() {
    val repository = InMemoryTaskRepository()
    val taskService = TaskService(repository)
    val consoleInterface = TaskConsoleInterface(taskService)
    
    consoleInterface.start()
}

/**
 * Additional utility extensions
 */

// Extension function for LocalDateTime
fun LocalDateTime.toDisplayString(): String = 
    this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"))

// Extension function for Task list
fun List<Task>.byPriority(): Map<Priority, List<Task>> = groupBy { it.priority }

fun List<Task>.overdue(): List<Task> = filter { it.isOverdue }

fun List<Task>.completed(): List<Task> = filter { it.isCompleted }

// Operator overloading for Priority
operator fun Priority.plus(other: Priority): Priority = 
    if (this.level + other.level >= URGENT.level) URGENT else values()[this.level + other.level - 1]

// DSL for creating tasks
class TaskBuilder {
    private var title: String = ""
    private var description: String = ""
    private var category: Category = Category.DEFAULT
    private var priority: Priority = Priority.MEDIUM
    private var dueDate: LocalDateTime? = null
    private var estimatedHours: Double? = null
    private val tags = mutableSetOf<String>()
    
    fun title(title: String) { this.title = title }
    fun description(description: String) { this.description = description }
    fun category(category: Category) { this.category = category }
    fun priority(priority: Priority) { this.priority = priority }
    fun dueDate(dueDate: LocalDateTime) { this.dueDate = dueDate }
    fun estimatedHours(hours: Double) { this.estimatedHours = hours }
    fun tag(tag: String) { tags.add(tag) }
    
    fun build(): Task = Task(
        title = title,
        description = description,
        category = category,
        priority = priority,
        dueDate = dueDate,
        estimatedHours = estimatedHours,
        tags = tags
    )
}

fun task(init: TaskBuilder.() -> Unit): Task {
    val builder = TaskBuilder()
    builder.init()
    return builder.build()
}

// Usage example of DSL:
// val myTask = task {
//     title("Learn Kotlin")
//     description("Complete the Kotlin mastery project")
//     category(Category.WORK)
//     priority(Priority.HIGH)
//     tag("kotlin")
//     tag("learning")
// }