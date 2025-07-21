/**
 * Kotlin Multiplatform Development Concepts
 * 
 * This module covers Kotlin Multiplatform (KMP) development concepts:
 * - Expect/actual declarations for platform-specific implementations
 * - Common code sharing strategies
 * - Platform-specific modules organization
 * - Cross-platform library development
 * - Multiplatform project structure
 * - Platform type mappings
 * - Interoperability patterns
 * 
 * Note: This is a conceptual demonstration. For actual multiplatform development,
 * you would need separate platform-specific source sets.
 */

// ================================
// Common Declarations (expect)
// ================================

/**
 * Platform abstraction for different platforms
 */
expect object Platform {
    val name: String
    val isDebug: Boolean
    fun currentTimeMillis(): Long
}

/**
 * Platform-specific logging implementation
 */
expect class Logger {
    fun info(message: String)
    fun error(message: String, throwable: Throwable?)
    fun debug(message: String)
}

/**
 * Platform-specific file operations
 */
expect class FileHandler {
    fun writeText(filename: String, content: String): Boolean
    fun readText(filename: String): String?
    fun exists(filename: String): Boolean
    fun delete(filename: String): Boolean
}

/**
 * Platform-specific HTTP client
 */
expect class HttpClient {
    suspend fun get(url: String): String
    suspend fun post(url: String, body: String): String
    fun close()
}

/**
 * Platform-specific preferences/settings storage
 */
expect class Settings {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String): String
    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int): Int
    fun clear()
}

/**
 * Platform-specific cryptography operations
 */
expect object Crypto {
    fun md5(input: String): String
    fun sha256(input: String): String
    fun generateUUID(): String
}

// ================================
// Common Business Logic
// ================================

/**
 * User data model shared across platforms
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: Long = Platform.currentTimeMillis()
) {
    fun toJsonString(): String {
        return """
        {
            "id": "$id",
            "name": "$name", 
            "email": "$email",
            "createdAt": $createdAt
        }
        """.trimIndent()
    }
    
    companion object {
        fun fromJsonString(json: String): User? {
            // Simple JSON parsing for demonstration
            return try {
                val id = json.substringAfter("\"id\": \"").substringBefore("\"")
                val name = json.substringAfter("\"name\": \"").substringBefore("\"")
                val email = json.substringAfter("\"email\": \"").substringBefore("\"")
                val createdAt = json.substringAfter("\"createdAt\": ").substringBefore("}").toLong()
                User(id, name, email, createdAt)
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Repository interface using platform-specific implementations
 */
class UserRepository {
    private val logger = Logger()
    private val fileHandler = FileHandler()
    private val settings = Settings()
    private val httpClient = HttpClient()
    
    suspend fun saveUser(user: User): Boolean {
        return try {
            logger.info("Saving user: ${user.name}")
            
            // Save to local storage
            val json = user.toJsonString()
            val saved = fileHandler.writeText("user_${user.id}.json", json)
            
            // Cache user count
            val currentCount = settings.getInt("user_count", 0)
            settings.putInt("user_count", currentCount + 1)
            
            // Sync to server (if available)
            try {
                httpClient.post("https://api.example.com/users", json)
                logger.info("User synced to server successfully")
            } catch (e: Exception) {
                logger.error("Failed to sync to server", e)
                // Continue anyway - local save succeeded
            }
            
            saved
        } catch (e: Exception) {
            logger.error("Failed to save user", e)
            false
        }
    }
    
    fun loadUser(userId: String): User? {
        return try {
            logger.debug("Loading user: $userId")
            
            val filename = "user_$userId.json"
            if (!fileHandler.exists(filename)) {
                logger.info("User file not found: $filename")
                return null
            }
            
            val json = fileHandler.readText(filename)
            json?.let { User.fromJsonString(it) }
        } catch (e: Exception) {
            logger.error("Failed to load user", e)
            null
        }
    }
    
    fun deleteUser(userId: String): Boolean {
        return try {
            logger.info("Deleting user: $userId")
            
            val filename = "user_$userId.json"
            val deleted = fileHandler.delete(filename)
            
            if (deleted) {
                // Update user count
                val currentCount = settings.getInt("user_count", 0)
                if (currentCount > 0) {
                    settings.putInt("user_count", currentCount - 1)
                }
            }
            
            deleted
        } catch (e: Exception) {
            logger.error("Failed to delete user", e)
            false
        }
    }
    
    fun getUserCount(): Int {
        return settings.getInt("user_count", 0)
    }
    
    fun clearAllUsers(): Boolean {
        return try {
            logger.info("Clearing all users")
            settings.clear()
            true
        } catch (e: Exception) {
            logger.error("Failed to clear users", e)
            false
        }
    }
    
    fun close() {
        httpClient.close()
    }
}

/**
 * Utility functions using platform abstractions
 */
object PlatformUtils {
    fun getPlatformInfo(): String {
        return buildString {
            appendLine("Platform: ${Platform.name}")
            appendLine("Debug Mode: ${Platform.isDebug}")
            appendLine("Current Time: ${Platform.currentTimeMillis()}")
            appendLine("UUID: ${Crypto.generateUUID()}")
        }
    }
    
    fun generateSecureId(input: String): String {
        val hash = Crypto.sha256(input + Platform.currentTimeMillis())
        return hash.take(16) // Take first 16 characters
    }
    
    fun createUserHash(user: User): String {
        val combined = "${user.id}:${user.email}:${user.createdAt}"
        return Crypto.md5(combined)
    }
}

/**
 * Configuration management across platforms
 */
class ConfigurationManager {
    private val settings = Settings()
    private val logger = Logger()
    
    fun setApiBaseUrl(url: String) {
        settings.putString("api_base_url", url)
        logger.info("API base URL updated: $url")
    }
    
    fun getApiBaseUrl(): String {
        return settings.getString("api_base_url", "https://api.default.com")
    }
    
    fun setDebugEnabled(enabled: Boolean) {
        settings.putString("debug_enabled", enabled.toString())
        logger.info("Debug mode: $enabled")
    }
    
    fun isDebugEnabled(): Boolean {
        val value = settings.getString("debug_enabled", Platform.isDebug.toString())
        return value.toBooleanStrictOrNull() ?: Platform.isDebug
    }
    
    fun setMaxRetries(retries: Int) {
        settings.putInt("max_retries", retries)
        logger.info("Max retries set to: $retries")
    }
    
    fun getMaxRetries(): Int {
        return settings.getInt("max_retries", 3)
    }
    
    fun resetToDefaults() {
        settings.clear()
        logger.info("Configuration reset to defaults")
    }
}

// ================================
// Common Application Logic
// ================================

/**
 * Main application class using platform abstractions
 */
class MultiplatformApp {
    private val userRepository = UserRepository()
    private val configManager = ConfigurationManager()
    private val logger = Logger()
    
    suspend fun initialize(): Boolean {
        return try {
            logger.info("Initializing ${Platform.name} application")
            
            // Platform-specific initialization
            logger.info("Platform info: ${PlatformUtils.getPlatformInfo()}")
            
            // Set default configuration
            if (configManager.getApiBaseUrl() == "https://api.default.com") {
                configManager.setApiBaseUrl("https://api.myapp.com")
            }
            
            logger.info("Application initialized successfully")
            true
        } catch (e: Exception) {
            logger.error("Failed to initialize application", e)
            false
        }
    }
    
    suspend fun createUser(name: String, email: String): User? {
        return try {
            val userId = PlatformUtils.generateSecureId(email)
            val user = User(userId, name, email)
            
            val saved = userRepository.saveUser(user)
            if (saved) {
                logger.info("User created successfully: ${user.name}")
                user
            } else {
                logger.error("Failed to save user", null)
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to create user", e)
            null
        }
    }
    
    fun getUser(userId: String): User? {
        return userRepository.loadUser(userId)
    }
    
    fun deleteUser(userId: String): Boolean {
        return userRepository.deleteUser(userId)
    }
    
    fun getStats(): Map<String, Any> {
        return mapOf(
            "platform" to Platform.name,
            "userCount" to userRepository.getUserCount(),
            "apiBaseUrl" to configManager.getApiBaseUrl(),
            "debugEnabled" to configManager.isDebugEnabled(),
            "currentTime" to Platform.currentTimeMillis()
        )
    }
    
    fun shutdown() {
        logger.info("Shutting down application")
        userRepository.close()
    }
}

// ================================
// Mock Actual Implementations (for JVM)
// ================================
// Note: In a real multiplatform project, these would be in platform-specific source sets

/**
 * Mock JVM Platform implementation
 */
actual object Platform {
    actual val name: String = "JVM"
    actual val isDebug: Boolean = System.getProperty("debug", "false").toBoolean()
    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}

/**
 * Mock JVM Logger implementation
 */
actual class Logger {
    actual fun info(message: String) {
        println("[INFO] $message")
    }
    
    actual fun error(message: String, throwable: Throwable?) {
        println("[ERROR] $message")
        throwable?.printStackTrace()
    }
    
    actual fun debug(message: String) {
        if (Platform.isDebug) {
            println("[DEBUG] $message")
        }
    }
}

/**
 * Mock JVM FileHandler implementation
 */
actual class FileHandler {
    private val basePath = System.getProperty("user.home", "/tmp")
    
    actual fun writeText(filename: String, content: String): Boolean {
        return try {
            java.io.File(basePath, filename).writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual fun readText(filename: String): String? {
        return try {
            java.io.File(basePath, filename).readText()
        } catch (e: Exception) {
            null
        }
    }
    
    actual fun exists(filename: String): Boolean {
        return java.io.File(basePath, filename).exists()
    }
    
    actual fun delete(filename: String): Boolean {
        return try {
            java.io.File(basePath, filename).delete()
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Mock JVM HttpClient implementation
 */
actual class HttpClient {
    actual suspend fun get(url: String): String {
        // Mock implementation
        return """{"status": "success", "method": "GET", "url": "$url"}"""
    }
    
    actual suspend fun post(url: String, body: String): String {
        // Mock implementation
        return """{"status": "success", "method": "POST", "url": "$url", "bodyLength": ${body.length}}"""
    }
    
    actual fun close() {
        // Mock implementation
        println("HTTP client closed")
    }
}

/**
 * Mock JVM Settings implementation using Properties
 */
actual class Settings {
    private val props = java.util.Properties()
    
    actual fun putString(key: String, value: String) {
        props.setProperty(key, value)
    }
    
    actual fun getString(key: String, defaultValue: String): String {
        return props.getProperty(key, defaultValue)
    }
    
    actual fun putInt(key: String, value: Int) {
        props.setProperty(key, value.toString())
    }
    
    actual fun getInt(key: String, defaultValue: Int): Int {
        return props.getProperty(key, defaultValue.toString()).toIntOrNull() ?: defaultValue
    }
    
    actual fun clear() {
        props.clear()
    }
}

/**
 * Mock JVM Crypto implementation
 */
actual object Crypto {
    private val md = java.security.MessageDigest.getInstance("MD5")
    private val sha = java.security.MessageDigest.getInstance("SHA-256")
    
    actual fun md5(input: String): String {
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    actual fun sha256(input: String): String {
        val digest = sha.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    actual fun generateUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateMultiplatformConcepts() {
    println("=== Multiplatform Concepts Demo ===")
    
    // Platform information
    println("Platform Info:")
    println(PlatformUtils.getPlatformInfo())
    
    // Configuration management
    val config = ConfigurationManager()
    config.setApiBaseUrl("https://myapi.com/v1")
    config.setDebugEnabled(true)
    config.setMaxRetries(5)
    
    println("\nConfiguration:")
    println("API Base URL: ${config.getApiBaseUrl()}")
    println("Debug Enabled: ${config.isDebugEnabled()}")
    println("Max Retries: ${config.getMaxRetries()}")
}

suspend fun demonstrateMultiplatformApp() {
    println("\n=== Multiplatform App Demo ===")
    
    val app = MultiplatformApp()
    
    // Initialize the app
    val initialized = app.initialize()
    println("App initialized: $initialized")
    
    if (initialized) {
        // Create a user
        val user = app.createUser("John Doe", "john@example.com")
        println("Created user: $user")
        
        user?.let {
            // Load the user back
            val loadedUser = app.getUser(it.id)
            println("Loaded user: $loadedUser")
            
            // Generate secure hash
            val hash = PlatformUtils.createUserHash(it)
            println("User hash: $hash")
        }
        
        // Show stats
        val stats = app.getStats()
        println("App stats: $stats")
        
        // Cleanup
        app.shutdown()
    }
}

// ================================
// Main Function
// ================================

fun main() {
    // Run synchronous demo
    demonstrateMultiplatformConcepts()
    
    // Note: In a real app, you'd use proper coroutine management
    // For demonstration, we'll use runBlocking (not recommended for production)
    kotlinx.coroutines.runBlocking {
        demonstrateMultiplatformApp()
    }
    
    println("\n=== Multiplatform Development Tips ===")
    println("✓ Use expect/actual for platform-specific implementations")
    println("✓ Keep business logic in common code")
    println("✓ Abstract platform differences behind interfaces")
    println("✓ Use dependency injection for platform-specific instances")
    println("✓ Test common code with platform-agnostic tests")
    println("✓ Consider using multiplatform libraries (Ktor, SQLDelight, etc.)")
}

/**
 * TODO: Advanced Multiplatform Topics
 * 
 * 1. Gradle multiplatform configuration
 * 2. iOS and Android specific implementations
 * 3. JavaScript target implementations
 * 4. Native target implementations
 * 5. Hierarchical project structure
 * 6. Multiplatform library publication
 * 7. Platform-specific tests
 * 8. Interoperability with platform APIs
 * 9. Multiplatform dependency management
 * 10. Build variants and flavors for multiplatform
 */