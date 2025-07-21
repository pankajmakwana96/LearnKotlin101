/**
 * Database Integration Examples
 * 
 * This module demonstrates database integration patterns in Kotlin:
 * - Connection management and pooling
 * - Repository pattern implementations
 * - Transaction management
 * - ORM-like patterns
 * - Database migration concepts
 * - Query builders and type safety
 * - Connection factory patterns
 * - Error handling and retry logic
 * 
 * Note: These are conceptual implementations. For production use,
 * consider frameworks like Exposed, Room, or Hibernate.
 */

import kotlinx.coroutines.*
import kotlinx.serialization.*
import java.sql.*
import java.time.LocalDateTime
import javax.sql.DataSource
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

// ================================
// Database Configuration and Connection
// ================================

@Serializable
data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String = "org.h2.Driver",
    val maxPoolSize: Int = 10,
    val connectionTimeout: Long = 30000,
    val idleTimeout: Long = 600000,
    val maxLifetime: Long = 1800000
)

/**
 * Simple connection pool implementation
 */
class ConnectionPool(private val config: DatabaseConfig) {
    private val availableConnections = mutableListOf<Connection>()
    private val usedConnections = mutableListOf<Connection>()
    private val maxPoolSize = config.maxPoolSize
    
    init {
        // Load the database driver
        Class.forName(config.driverClassName)
        // Pre-populate pool
        repeat(2) { // Start with 2 connections
            createConnection()?.let { availableConnections.add(it) }
        }
    }
    
    @Synchronized
    fun getConnection(): Connection? {
        return if (availableConnections.isNotEmpty()) {
            val connection = availableConnections.removeAt(0)
            usedConnections.add(connection)
            connection
        } else if (usedConnections.size < maxPoolSize) {
            val connection = createConnection()
            connection?.let { usedConnections.add(it) }
            connection
        } else {
            null // Pool exhausted
        }
    }
    
    @Synchronized
    fun releaseConnection(connection: Connection) {
        if (usedConnections.remove(connection)) {
            if (connection.isValid(5)) {
                availableConnections.add(connection)
            } else {
                connection.close()
            }
        }
    }
    
    private fun createConnection(): Connection? {
        return try {
            DriverManager.getConnection(config.url, config.username, config.password)
        } catch (e: SQLException) {
            println("Failed to create connection: ${e.message}")
            null
        }
    }
    
    fun close() {
        (availableConnections + usedConnections).forEach { it.close() }
        availableConnections.clear()
        usedConnections.clear()
    }
    
    fun getStats(): PoolStats {
        return PoolStats(
            availableConnections = availableConnections.size,
            usedConnections = usedConnections.size,
            totalConnections = availableConnections.size + usedConnections.size,
            maxPoolSize = maxPoolSize
        )
    }
}

data class PoolStats(
    val availableConnections: Int,
    val usedConnections: Int,
    val totalConnections: Int,
    val maxPoolSize: Int
)

/**
 * Database context for managing connections and transactions
 */
class DatabaseContext(private val connectionPool: ConnectionPool) {
    
    suspend fun <T> withConnection(block: suspend (Connection) -> T): T {
        val connection = connectionPool.getConnection() 
            ?: throw SQLException("Unable to obtain database connection")
        
        return try {
            withContext(Dispatchers.IO) {
                block(connection)
            }
        } finally {
            connectionPool.releaseConnection(connection)
        }
    }
    
    suspend fun <T> withTransaction(block: suspend (Connection) -> T): T {
        return withConnection { connection ->
            connection.autoCommit = false
            try {
                val result = block(connection)
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }
}

// ================================
// Entity Models
// ================================

@Serializable
data class User(
    val id: Long = 0,
    val username: String,
    val email: String,
    val fullName: String,
    val isActive: Boolean = true,
    val createdAt: String = LocalDateTime.now().toString(),
    val updatedAt: String = LocalDateTime.now().toString()
)

@Serializable
data class Product(
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Long,
    val inStock: Boolean = true,
    val createdAt: String = LocalDateTime.now().toString()
)

@Serializable
data class Order(
    val id: Long = 0,
    val userId: Long,
    val totalAmount: Double,
    val status: String = "PENDING",
    val createdAt: String = LocalDateTime.now().toString()
)

@Serializable
data class OrderItem(
    val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val price: Double
)

// ================================
// Repository Pattern Implementation
// ================================

interface Repository<T, ID> {
    suspend fun save(entity: T): T
    suspend fun findById(id: ID): T?
    suspend fun findAll(limit: Int = 100, offset: Int = 0): List<T>
    suspend fun update(entity: T): T?
    suspend fun delete(id: ID): Boolean
    suspend fun count(): Long
}

abstract class BaseRepository<T, ID>(
    protected val dbContext: DatabaseContext,
    protected val tableName: String
) : Repository<T, ID> {
    
    abstract fun mapResultSetToEntity(rs: ResultSet): T
    abstract fun getEntityId(entity: T): ID
    abstract fun buildInsertStatement(entity: T): Pair<String, List<Any>>
    abstract fun buildUpdateStatement(entity: T): Pair<String, List<Any>>
    
    override suspend fun findById(id: ID): T? {
        val sql = "SELECT * FROM $tableName WHERE id = ?"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, id)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    mapResultSetToEntity(resultSet)
                } else {
                    null
                }
            }
        }
    }
    
    override suspend fun findAll(limit: Int, offset: Int): List<T> {
        val sql = "SELECT * FROM $tableName ORDER BY id LIMIT ? OFFSET ?"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, limit)
                statement.setInt(2, offset)
                val resultSet = statement.executeQuery()
                val results = mutableListOf<T>()
                while (resultSet.next()) {
                    results.add(mapResultSetToEntity(resultSet))
                }
                results
            }
        }
    }
    
    override suspend fun save(entity: T): T {
        val (sql, params) = buildInsertStatement(entity)
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
                params.forEachIndexed { index, param ->
                    statement.setObject(index + 1, param)
                }
                statement.executeUpdate()
                
                val generatedKeys = statement.generatedKeys
                if (generatedKeys.next()) {
                    val newId = generatedKeys.getLong(1)
                    findById(newId as ID) ?: entity
                } else {
                    entity
                }
            }
        }
    }
    
    override suspend fun update(entity: T): T? {
        val (sql, params) = buildUpdateStatement(entity)
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                params.forEachIndexed { index, param ->
                    statement.setObject(index + 1, param)
                }
                val rowsAffected = statement.executeUpdate()
                if (rowsAffected > 0) {
                    entity
                } else {
                    null
                }
            }
        }
    }
    
    override suspend fun delete(id: ID): Boolean {
        val sql = "DELETE FROM $tableName WHERE id = ?"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, id)
                statement.executeUpdate() > 0
            }
        }
    }
    
    override suspend fun count(): Long {
        val sql = "SELECT COUNT(*) FROM $tableName"
        return dbContext.withConnection { connection ->
            connection.createStatement().use { statement ->
                val resultSet = statement.executeQuery(sql)
                if (resultSet.next()) {
                    resultSet.getLong(1)
                } else {
                    0L
                }
            }
        }
    }
}

/**
 * User Repository Implementation
 */
class UserRepository(dbContext: DatabaseContext) : BaseRepository<User, Long>(dbContext, "users") {
    
    override fun mapResultSetToEntity(rs: ResultSet): User {
        return User(
            id = rs.getLong("id"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            fullName = rs.getString("full_name"),
            isActive = rs.getBoolean("is_active"),
            createdAt = rs.getString("created_at"),
            updatedAt = rs.getString("updated_at")
        )
    }
    
    override fun getEntityId(entity: User): Long = entity.id
    
    override fun buildInsertStatement(entity: User): Pair<String, List<Any>> {
        val sql = """
            INSERT INTO users (username, email, full_name, is_active, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val params = listOf(
            entity.username,
            entity.email,
            entity.fullName,
            entity.isActive,
            entity.createdAt,
            entity.updatedAt
        )
        return sql to params
    }
    
    override fun buildUpdateStatement(entity: User): Pair<String, List<Any>> {
        val sql = """
            UPDATE users SET username = ?, email = ?, full_name = ?, 
                           is_active = ?, updated_at = ? 
            WHERE id = ?
        """.trimIndent()
        val params = listOf(
            entity.username,
            entity.email,
            entity.fullName,
            entity.isActive,
            LocalDateTime.now().toString(),
            entity.id
        )
        return sql to params
    }
    
    suspend fun findByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, username)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    mapResultSetToEntity(resultSet)
                } else {
                    null
                }
            }
        }
    }
    
    suspend fun findByEmail(email: String): User? {
        val sql = "SELECT * FROM users WHERE email = ?"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, email)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    mapResultSetToEntity(resultSet)
                } else {
                    null
                }
            }
        }
    }
    
    suspend fun findActiveUsers(): List<User> {
        val sql = "SELECT * FROM users WHERE is_active = true ORDER BY username"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                val resultSet = statement.executeQuery()
                val results = mutableListOf<User>()
                while (resultSet.next()) {
                    results.add(mapResultSetToEntity(resultSet))
                }
                results
            }
        }
    }
}

/**
 * Product Repository Implementation
 */
class ProductRepository(dbContext: DatabaseContext) : BaseRepository<Product, Long>(dbContext, "products") {
    
    override fun mapResultSetToEntity(rs: ResultSet): Product {
        return Product(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            price = rs.getDouble("price"),
            categoryId = rs.getLong("category_id"),
            inStock = rs.getBoolean("in_stock"),
            createdAt = rs.getString("created_at")
        )
    }
    
    override fun getEntityId(entity: Product): Long = entity.id
    
    override fun buildInsertStatement(entity: Product): Pair<String, List<Any>> {
        val sql = """
            INSERT INTO products (name, description, price, category_id, in_stock, created_at) 
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val params = listOf(
            entity.name,
            entity.description,
            entity.price,
            entity.categoryId,
            entity.inStock,
            entity.createdAt
        )
        return sql to params
    }
    
    override fun buildUpdateStatement(entity: Product): Pair<String, List<Any>> {
        val sql = """
            UPDATE products SET name = ?, description = ?, price = ?, 
                              category_id = ?, in_stock = ? 
            WHERE id = ?
        """.trimIndent()
        val params = listOf(
            entity.name,
            entity.description,
            entity.price,
            entity.categoryId,
            entity.inStock,
            entity.id
        )
        return sql to params
    }
    
    suspend fun findByCategory(categoryId: Long): List<Product> {
        val sql = "SELECT * FROM products WHERE category_id = ? AND in_stock = true"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, categoryId)
                val resultSet = statement.executeQuery()
                val results = mutableListOf<Product>()
                while (resultSet.next()) {
                    results.add(mapResultSetToEntity(resultSet))
                }
                results
            }
        }
    }
    
    suspend fun findByPriceRange(minPrice: Double, maxPrice: Double): List<Product> {
        val sql = "SELECT * FROM products WHERE price BETWEEN ? AND ? ORDER BY price"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setDouble(1, minPrice)
                statement.setDouble(2, maxPrice)
                val resultSet = statement.executeQuery()
                val results = mutableListOf<Product>()
                while (resultSet.next()) {
                    results.add(mapResultSetToEntity(resultSet))
                }
                results
            }
        }
    }
}

// ================================
// Migration System
// ================================

data class Migration(
    val version: String,
    val description: String,
    val script: String,
    val rollbackScript: String? = null
)

class MigrationManager(private val dbContext: DatabaseContext) {
    
    suspend fun initializeMigrationTable() {
        val createTableSql = """
            CREATE TABLE IF NOT EXISTS schema_migrations (
                version VARCHAR(255) PRIMARY KEY,
                description VARCHAR(500),
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        
        dbContext.withConnection { connection ->
            connection.createStatement().use { statement ->
                statement.execute(createTableSql)
            }
        }
    }
    
    suspend fun runMigrations(migrations: List<Migration>) {
        initializeMigrationTable()
        
        for (migration in migrations) {
            if (!isMigrationApplied(migration.version)) {
                println("Applying migration ${migration.version}: ${migration.description}")
                applyMigration(migration)
                recordMigration(migration)
            } else {
                println("Migration ${migration.version} already applied")
            }
        }
    }
    
    private suspend fun isMigrationApplied(version: String): Boolean {
        val sql = "SELECT COUNT(*) FROM schema_migrations WHERE version = ?"
        return dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, version)
                val resultSet = statement.executeQuery()
                resultSet.next() && resultSet.getInt(1) > 0
            }
        }
    }
    
    private suspend fun applyMigration(migration: Migration) {
        dbContext.withTransaction { connection ->
            connection.createStatement().use { statement ->
                // Split script by semicolons and execute each statement
                migration.script.split(";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .forEach { sql ->
                        statement.execute(sql)
                    }
            }
        }
    }
    
    private suspend fun recordMigration(migration: Migration) {
        val sql = "INSERT INTO schema_migrations (version, description) VALUES (?, ?)"
        dbContext.withConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, migration.version)
                statement.setString(2, migration.description)
                statement.executeUpdate()
            }
        }
    }
}

// ================================
// Service Layer with Database Integration
// ================================

class UserService(private val userRepository: UserRepository) {
    
    suspend fun createUser(username: String, email: String, fullName: String): Result<User> {
        // Check for existing user
        val existingUserByUsername = userRepository.findByUsername(username)
        if (existingUserByUsername != null) {
            return Result.failure(Exception("Username already exists"))
        }
        
        val existingUserByEmail = userRepository.findByEmail(email)
        if (existingUserByEmail != null) {
            return Result.failure(Exception("Email already exists"))
        }
        
        val user = User(
            username = username,
            email = email,
            fullName = fullName
        )
        
        return try {
            val savedUser = userRepository.save(user)
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(id: Long): User? {
        return userRepository.findById(id)
    }
    
    suspend fun getAllActiveUsers(): List<User> {
        return userRepository.findActiveUsers()
    }
    
    suspend fun updateUser(user: User): Result<User> {
        return try {
            val updatedUser = userRepository.update(user)
            if (updatedUser != null) {
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deactivateUser(id: Long): Boolean {
        val user = userRepository.findById(id) ?: return false
        val deactivatedUser = user.copy(isActive = false)
        return userRepository.update(deactivatedUser) != null
    }
}

// ================================
// Database Setup and Demo
// ================================

object DatabaseSetup {
    
    fun getTestMigrations(): List<Migration> {
        return listOf(
            Migration(
                version = "1.0.0",
                description = "Create users table",
                script = """
                    CREATE TABLE users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) UNIQUE NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        full_name VARCHAR(255) NOT NULL,
                        is_active BOOLEAN DEFAULT TRUE,
                        created_at VARCHAR(50) NOT NULL,
                        updated_at VARCHAR(50) NOT NULL
                    )
                """.trimIndent()
            ),
            Migration(
                version = "1.1.0",
                description = "Create products table",
                script = """
                    CREATE TABLE products (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        price DECIMAL(10,2) NOT NULL,
                        category_id BIGINT NOT NULL,
                        in_stock BOOLEAN DEFAULT TRUE,
                        created_at VARCHAR(50) NOT NULL
                    )
                """.trimIndent()
            ),
            Migration(
                version = "1.2.0",
                description = "Create orders and order_items tables",
                script = """
                    CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL,
                        status VARCHAR(50) DEFAULT 'PENDING',
                        created_at VARCHAR(50) NOT NULL
                    );
                    
                    CREATE TABLE order_items (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        price DECIMAL(10,2) NOT NULL
                    )
                """.trimIndent()
            ),
            Migration(
                version = "1.3.0",
                description = "Add indexes for better performance",
                script = """
                    CREATE INDEX idx_users_username ON users(username);
                    CREATE INDEX idx_users_email ON users(email);
                    CREATE INDEX idx_products_category ON products(category_id);
                    CREATE INDEX idx_orders_user ON orders(user_id);
                    CREATE INDEX idx_order_items_order ON order_items(order_id)
                """.trimIndent()
            )
        )
    }
    
    fun createTestDatabaseConfig(): DatabaseConfig {
        return DatabaseConfig(
            url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            username = "sa",
            password = "",
            driverClassName = "org.h2.Driver",
            maxPoolSize = 5
        )
    }
}

// ================================
// Demo Functions
// ================================

suspend fun demonstrateDatabaseIntegration() {
    println("=== Database Integration Demo ===")
    
    // Setup
    val config = DatabaseSetup.createTestDatabaseConfig()
    val connectionPool = ConnectionPool(config)
    val dbContext = DatabaseContext(connectionPool)
    val migrationManager = MigrationManager(dbContext)
    
    try {
        // Run migrations
        println("🔄 Running database migrations...")
        migrationManager.runMigrations(DatabaseSetup.getTestMigrations())
        
        // Setup repositories and services
        val userRepository = UserRepository(dbContext)
        val productRepository = ProductRepository(dbContext)
        val userService = UserService(userRepository)
        
        // Create some users
        println("\n👤 Creating users...")
        val user1Result = userService.createUser("john_doe", "john@example.com", "John Doe")
        val user2Result = userService.createUser("jane_smith", "jane@example.com", "Jane Smith")
        
        user1Result.onSuccess { user -> println("Created user: ${user.username}") }
        user2Result.onSuccess { user -> println("Created user: ${user.username}") }
        
        // Create some products
        println("\n📦 Creating products...")
        val product1 = Product(
            name = "Laptop",
            description = "High-performance laptop",
            price = 999.99,
            categoryId = 1
        )
        
        val product2 = Product(
            name = "Mouse",
            description = "Wireless mouse",
            price = 29.99,
            categoryId = 1
        )
        
        val savedProduct1 = productRepository.save(product1)
        val savedProduct2 = productRepository.save(product2)
        println("Created product: ${savedProduct1.name}")
        println("Created product: ${savedProduct2.name}")
        
        // Query data
        println("\n📊 Querying data...")
        val allUsers = userRepository.findAll()
        println("Total users: ${allUsers.size}")
        allUsers.forEach { user ->
            println("  - ${user.fullName} (${user.username})")
        }
        
        val allProducts = productRepository.findAll()
        println("Total products: ${allProducts.size}")
        allProducts.forEach { product ->
            println("  - ${product.name}: $${product.price}")
        }
        
        // Test specific queries
        println("\n🔍 Testing specific queries...")
        val userByUsername = userRepository.findByUsername("john_doe")
        println("Found user by username: ${userByUsername?.fullName}")
        
        val productsInRange = productRepository.findByPriceRange(20.0, 50.0)
        println("Products in price range $20-$50: ${productsInRange.size}")
        productsInRange.forEach { product ->
            println("  - ${product.name}: $${product.price}")
        }
        
        // Test updates
        println("\n✏️  Testing updates...")
        val updatedUser = user1Result.getOrNull()?.copy(fullName = "John Updated Doe")
        updatedUser?.let {
            userRepository.update(it)
            val refreshedUser = userRepository.findById(it.id)
            println("Updated user name: ${refreshedUser?.fullName}")
        }
        
        // Connection pool stats
        println("\n📈 Connection pool stats:")
        val stats = connectionPool.getStats()
        println("Available: ${stats.availableConnections}, Used: ${stats.usedConnections}, Total: ${stats.totalConnections}")
        
    } finally {
        connectionPool.close()
    }
}

suspend fun main() {
    demonstrateDatabaseIntegration()
    
    println("\n=== Database Integration Summary ===")
    println("✓ Connection pooling for efficient resource management")
    println("✓ Repository pattern for data access abstraction")
    println("✓ Transaction management for data consistency")
    println("✓ Migration system for database evolution")
    println("✓ Type-safe entity mapping")
    println("✓ Coroutine-based async database operations")
    println("✓ Error handling and resource cleanup")
    println("✓ Custom query methods for business logic")
    
    println("\n💡 Production Considerations:")
    println("• Use production database drivers (PostgreSQL, MySQL)")
    println("• Implement proper connection pooling (HikariCP)")
    println("• Add comprehensive error handling and retry logic")
    println("• Include database monitoring and health checks")
    println("• Implement proper logging and audit trails")
    println("• Add database backup and recovery procedures")
    println("• Consider using ORM frameworks (Exposed, Hibernate)")
    println("• Implement database security best practices")
}

/**
 * TODO: Advanced Database Integration Features
 * 
 * 1. Advanced connection pooling with HikariCP
 * 2. Database clustering and read/write splitting
 * 3. Prepared statement caching
 * 4. Batch operations for better performance
 * 5. Database health checks and monitoring
 * 6. Advanced migration strategies (blue/green)
 * 7. Database sharding and partitioning
 * 8. Full-text search integration
 * 9. Database backup and restore automation
 * 10. Performance profiling and query optimization
 * 11. Database connection encryption (SSL/TLS)
 * 12. Advanced transaction isolation levels
 * 13. Database-specific features (PostGIS, JSONB)
 * 14. Integration with database proxies (PgBouncer)
 * 15. Automated database testing strategies
 */