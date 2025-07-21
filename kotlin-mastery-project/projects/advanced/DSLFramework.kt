/**
 * Advanced Project: Domain-Specific Language (DSL) Framework
 * 
 * This project demonstrates building a comprehensive DSL framework in Kotlin:
 * - Type-safe builder patterns
 * - DSL scope control and receiver types
 * - Lambda with receiver functions
 * - Extension functions for DSL syntax
 * - Configuration DSLs for complex systems
 * - HTML/XML generation DSLs
 * - Query builder DSLs
 * - Validation and constraint DSLs
 * - Test specification DSLs
 */

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import java.time.LocalDateTime

// ================================
// Configuration DSL Framework
// ================================

/**
 * Base configuration class with common properties
 */
abstract class Configuration {
    protected val properties = mutableMapOf<String, Any>()
    
    fun property(key: String, value: Any) {
        properties[key] = value
    }
    
    fun getProperty(key: String): Any? = properties[key]
    fun getAllProperties(): Map<String, Any> = properties.toMap()
}

/**
 * Server configuration DSL
 */
@DslMarker
annotation class ServerConfigurationDsl

@ServerConfigurationDsl
class ServerConfiguration : Configuration() {
    var host: String = "localhost"
    var port: Int = 8080
    var ssl: Boolean = false
    var maxConnections: Int = 100
    var timeout: Duration = 30.seconds
    
    private val middlewares = mutableListOf<MiddlewareConfiguration>()
    private var database: DatabaseConfiguration? = null
    private var security: SecurityConfiguration? = null
    private var logging: LoggingConfiguration? = null
    
    fun middleware(init: MiddlewareConfiguration.() -> Unit) {
        val middleware = MiddlewareConfiguration().apply(init)
        middlewares.add(middleware)
    }
    
    fun database(init: DatabaseConfiguration.() -> Unit) {
        database = DatabaseConfiguration().apply(init)
    }
    
    fun security(init: SecurityConfiguration.() -> Unit) {
        security = SecurityConfiguration().apply(init)
    }
    
    fun logging(init: LoggingConfiguration.() -> Unit) {
        logging = LoggingConfiguration().apply(init)
    }
    
    fun getMiddlewares(): List<MiddlewareConfiguration> = middlewares.toList()
    fun getDatabase(): DatabaseConfiguration? = database
    fun getSecurity(): SecurityConfiguration? = security
    fun getLogging(): LoggingConfiguration? = logging
    
    fun summary(): String {
        return buildString {
            appendLine("Server Configuration:")
            appendLine("  Host: $host")
            appendLine("  Port: $port")
            appendLine("  SSL: $ssl")
            appendLine("  Max Connections: $maxConnections")
            appendLine("  Timeout: $timeout")
            appendLine("  Middlewares: ${middlewares.size}")
            database?.let { appendLine("  Database: ${it.type}") }
            security?.let { appendLine("  Security: Enabled") }
            logging?.let { appendLine("  Logging: ${it.level}") }
        }
    }
}

@ServerConfigurationDsl
class MiddlewareConfiguration : Configuration() {
    var name: String = ""
    var enabled: Boolean = true
    var order: Int = 0
    var config: Map<String, Any> = emptyMap()
}

@ServerConfigurationDsl
class DatabaseConfiguration : Configuration() {
    var type: String = "postgresql"
    var host: String = "localhost"
    var port: Int = 5432
    var database: String = ""
    var username: String = ""
    var password: String = ""
    var maxPoolSize: Int = 10
    var connectionTimeout: Duration = 30.seconds
    
    private val migrations = mutableListOf<String>()
    
    fun migration(script: String) {
        migrations.add(script)
    }
    
    fun getMigrations(): List<String> = migrations.toList()
}

@ServerConfigurationDsl
class SecurityConfiguration : Configuration() {
    var jwtSecret: String = ""
    var tokenExpiry: Duration = 24.hours
    var enableCors: Boolean = false
    var corsOrigins: List<String> = emptyList()
    var enableRateLimit: Boolean = false
    var rateLimitRequests: Int = 100
    var rateLimitWindow: Duration = 1.minutes
    
    private val roles = mutableListOf<RoleConfiguration>()
    
    fun role(name: String, init: RoleConfiguration.() -> Unit) {
        val role = RoleConfiguration(name).apply(init)
        roles.add(role)
    }
    
    fun getRoles(): List<RoleConfiguration> = roles.toList()
}

@ServerConfigurationDsl
class RoleConfiguration(val name: String) {
    private val permissions = mutableListOf<String>()
    
    fun permission(permission: String) {
        permissions.add(permission)
    }
    
    fun permissions(vararg perms: String) {
        permissions.addAll(perms)
    }
    
    fun getPermissions(): List<String> = permissions.toList()
}

@ServerConfigurationDsl
class LoggingConfiguration : Configuration() {
    var level: String = "INFO"
    var format: String = "json"
    var output: String = "console"
    var maxFileSize: String = "10MB"
    var maxFiles: Int = 5
    
    private val loggers = mutableMapOf<String, String>()
    
    fun logger(name: String, level: String) {
        loggers[name] = level
    }
    
    fun getLoggers(): Map<String, String> = loggers.toMap()
}

// Top-level function to create server configuration
fun serverConfig(init: ServerConfiguration.() -> Unit): ServerConfiguration {
    return ServerConfiguration().apply(init)
}

// ================================
// HTML DSL Framework
// ================================

@DslMarker
annotation class HtmlDsl

@HtmlDsl
abstract class HtmlElement(val name: String) {
    private val attributes = mutableMapOf<String, String>()
    private val children = mutableListOf<HtmlElement>()
    private var textContent: String? = null
    
    fun attribute(name: String, value: String) {
        attributes[name] = value
    }
    
    fun id(value: String) = attribute("id", value)
    fun className(value: String) = attribute("class", value)
    fun style(value: String) = attribute("style", value)
    
    fun text(content: String) {
        textContent = content
    }
    
    operator fun String.unaryPlus() {
        text(this)
    }
    
    protected fun <T : HtmlElement> addChild(child: T, init: T.() -> Unit): T {
        child.init()
        children.add(child)
        return child
    }
    
    fun render(indent: String = ""): String {
        val attributeString = if (attributes.isNotEmpty()) {
            " " + attributes.map { "${it.key}=\"${it.value}\"" }.joinToString(" ")
        } else ""
        
        return when {
            textContent != null -> "$indent<$name$attributeString>$textContent</$name>"
            children.isEmpty() -> "$indent<$name$attributeString/>"
            else -> buildString {
                appendLine("$indent<$name$attributeString>")
                children.forEach { child ->
                    appendLine(child.render("$indent  "))
                }
                append("$indent</$name>")
            }
        }
    }
}

@HtmlDsl
class Html : HtmlElement("html") {
    fun head(init: Head.() -> Unit) = addChild(Head(), init)
    fun body(init: Body.() -> Unit) = addChild(Body(), init)
}

@HtmlDsl
class Head : HtmlElement("head") {
    fun title(init: Title.() -> Unit) = addChild(Title(), init)
    fun meta(init: Meta.() -> Unit) = addChild(Meta(), init)
    fun link(init: Link.() -> Unit) = addChild(Link(), init)
    fun script(init: Script.() -> Unit) = addChild(Script(), init)
}

@HtmlDsl
class Title : HtmlElement("title")

@HtmlDsl
class Meta : HtmlElement("meta") {
    fun charset(value: String) = attribute("charset", value)
    fun name(value: String) = attribute("name", value)
    fun content(value: String) = attribute("content", value)
}

@HtmlDsl
class Link : HtmlElement("link") {
    fun rel(value: String) = attribute("rel", value)
    fun href(value: String) = attribute("href", value)
    fun type(value: String) = attribute("type", value)
}

@HtmlDsl
class Script : HtmlElement("script") {
    fun src(value: String) = attribute("src", value)
    fun type(value: String) = attribute("type", value)
}

@HtmlDsl
class Body : HtmlElement("body") {
    fun div(init: Div.() -> Unit) = addChild(Div(), init)
    fun p(init: P.() -> Unit) = addChild(P(), init)
    fun h1(init: H1.() -> Unit) = addChild(H1(), init)
    fun h2(init: H2.() -> Unit) = addChild(H2(), init)
    fun ul(init: Ul.() -> Unit) = addChild(Ul(), init)
    fun a(init: A.() -> Unit) = addChild(A(), init)
    fun form(init: Form.() -> Unit) = addChild(Form(), init)
    fun table(init: Table.() -> Unit) = addChild(Table(), init)
}

@HtmlDsl
class Div : HtmlElement("div") {
    fun div(init: Div.() -> Unit) = addChild(Div(), init)
    fun p(init: P.() -> Unit) = addChild(P(), init)
    fun span(init: Span.() -> Unit) = addChild(Span(), init)
    fun a(init: A.() -> Unit) = addChild(A(), init)
}

@HtmlDsl
class P : HtmlElement("p")

@HtmlDsl
class Span : HtmlElement("span")

@HtmlDsl
class H1 : HtmlElement("h1")

@HtmlDsl
class H2 : HtmlElement("h2")

@HtmlDsl
class Ul : HtmlElement("ul") {
    fun li(init: Li.() -> Unit) = addChild(Li(), init)
}

@HtmlDsl
class Li : HtmlElement("li")

@HtmlDsl
class A : HtmlElement("a") {
    fun href(value: String) = attribute("href", value)
    fun target(value: String) = attribute("target", value)
}

@HtmlDsl
class Form : HtmlElement("form") {
    fun action(value: String) = attribute("action", value)
    fun method(value: String) = attribute("method", value)
    fun input(init: Input.() -> Unit) = addChild(Input(), init)
    fun button(init: Button.() -> Unit) = addChild(Button(), init)
}

@HtmlDsl
class Input : HtmlElement("input") {
    fun type(value: String) = attribute("type", value)
    fun name(value: String) = attribute("name", value)
    fun value(value: String) = attribute("value", value)
    fun placeholder(value: String) = attribute("placeholder", value)
}

@HtmlDsl
class Button : HtmlElement("button") {
    fun type(value: String) = attribute("type", value)
}

@HtmlDsl
class Table : HtmlElement("table") {
    fun thead(init: Thead.() -> Unit) = addChild(Thead(), init)
    fun tbody(init: Tbody.() -> Unit) = addChild(Tbody(), init)
}

@HtmlDsl
class Thead : HtmlElement("thead") {
    fun tr(init: Tr.() -> Unit) = addChild(Tr(), init)
}

@HtmlDsl
class Tbody : HtmlElement("tbody") {
    fun tr(init: Tr.() -> Unit) = addChild(Tr(), init)
}

@HtmlDsl
class Tr : HtmlElement("tr") {
    fun th(init: Th.() -> Unit) = addChild(Th(), init)
    fun td(init: Td.() -> Unit) = addChild(Td(), init)
}

@HtmlDsl
class Th : HtmlElement("th")

@HtmlDsl
class Td : HtmlElement("td")

// Top-level function to create HTML
fun html(init: Html.() -> Unit): Html {
    return Html().apply(init)
}

// ================================
// Query Builder DSL
// ================================

@DslMarker
annotation class QueryDsl

@QueryDsl
class SelectQuery {
    private val selectColumns = mutableListOf<String>()
    private var fromTable: String? = null
    private val joins = mutableListOf<String>()
    private val whereConditions = mutableListOf<String>()
    private val groupByColumns = mutableListOf<String>()
    private val havingConditions = mutableListOf<String>()
    private val orderByColumns = mutableListOf<String>()
    private var limitValue: Int? = null
    private var offsetValue: Int? = null
    
    fun select(vararg columns: String) {
        selectColumns.addAll(columns)
    }
    
    fun from(table: String) {
        fromTable = table
    }
    
    fun join(table: String, condition: String) {
        joins.add("JOIN $table ON $condition")
    }
    
    fun leftJoin(table: String, condition: String) {
        joins.add("LEFT JOIN $table ON $condition")
    }
    
    fun where(condition: String) {
        whereConditions.add(condition)
    }
    
    fun where(init: WhereBuilder.() -> String) {
        whereConditions.add(WhereBuilder().init())
    }
    
    fun groupBy(vararg columns: String) {
        groupByColumns.addAll(columns)
    }
    
    fun having(condition: String) {
        havingConditions.add(condition)
    }
    
    fun orderBy(column: String, direction: String = "ASC") {
        orderByColumns.add("$column $direction")
    }
    
    fun limit(count: Int) {
        limitValue = count
    }
    
    fun offset(count: Int) {
        offsetValue = count
    }
    
    fun build(): String {
        val query = StringBuilder()
        
        // SELECT
        query.append("SELECT ")
        query.append(if (selectColumns.isNotEmpty()) selectColumns.joinToString(", ") else "*")
        
        // FROM
        fromTable?.let { query.append("\nFROM $it") }
        
        // JOIN
        if (joins.isNotEmpty()) {
            query.append("\n").append(joins.joinToString("\n"))
        }
        
        // WHERE
        if (whereConditions.isNotEmpty()) {
            query.append("\nWHERE ").append(whereConditions.joinToString(" AND "))
        }
        
        // GROUP BY
        if (groupByColumns.isNotEmpty()) {
            query.append("\nGROUP BY ").append(groupByColumns.joinToString(", "))
        }
        
        // HAVING
        if (havingConditions.isNotEmpty()) {
            query.append("\nHAVING ").append(havingConditions.joinToString(" AND "))
        }
        
        // ORDER BY
        if (orderByColumns.isNotEmpty()) {
            query.append("\nORDER BY ").append(orderByColumns.joinToString(", "))
        }
        
        // LIMIT
        limitValue?.let { query.append("\nLIMIT $it") }
        
        // OFFSET
        offsetValue?.let { query.append("\nOFFSET $it") }
        
        return query.toString()
    }
}

@QueryDsl
class WhereBuilder {
    fun column(name: String): ColumnExpression = ColumnExpression(name)
}

@QueryDsl
class ColumnExpression(private val columnName: String) {
    infix fun eq(value: Any): String = "$columnName = ${formatValue(value)}"
    infix fun neq(value: Any): String = "$columnName != ${formatValue(value)}"
    infix fun gt(value: Any): String = "$columnName > ${formatValue(value)}"
    infix fun lt(value: Any): String = "$columnName < ${formatValue(value)}"
    infix fun gte(value: Any): String = "$columnName >= ${formatValue(value)}"
    infix fun lte(value: Any): String = "$columnName <= ${formatValue(value)}"
    infix fun like(pattern: String): String = "$columnName LIKE '$pattern'"
    infix fun `in`(values: List<Any>): String = "$columnName IN (${values.joinToString(", ") { formatValue(it) }})"
    
    fun isNull(): String = "$columnName IS NULL"
    fun isNotNull(): String = "$columnName IS NOT NULL"
    
    private fun formatValue(value: Any): String = when (value) {
        is String -> "'$value'"
        is Number -> value.toString()
        is Boolean -> value.toString().uppercase()
        else -> "'$value'"
    }
}

fun select(init: SelectQuery.() -> Unit): SelectQuery {
    return SelectQuery().apply(init)
}

// ================================
// Validation DSL
// ================================

@DslMarker
annotation class ValidationDsl

@ValidationDsl
class ValidationRule<T>(
    private val fieldName: String,
    private val getValue: () -> T
) {
    private val constraints = mutableListOf<Constraint<T>>()
    
    fun required() {
        constraints.add(RequiredConstraint())
    }
    
    fun minLength(length: Int) {
        constraints.add(MinLengthConstraint(length))
    }
    
    fun maxLength(length: Int) {
        constraints.add(MaxLengthConstraint(length))
    }
    
    fun pattern(regex: String) {
        constraints.add(PatternConstraint(Regex(regex)))
    }
    
    fun email() {
        constraints.add(EmailConstraint())
    }
    
    fun min(value: Number) {
        constraints.add(MinValueConstraint(value))
    }
    
    fun max(value: Number) {
        constraints.add(MaxValueConstraint(value))
    }
    
    fun custom(validator: (T) -> Boolean, errorMessage: String) {
        constraints.add(CustomConstraint(validator, errorMessage))
    }
    
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        val value = getValue()
        
        constraints.forEach { constraint ->
            if (!constraint.isValid(value)) {
                errors.add("$fieldName: ${constraint.errorMessage}")
            }
        }
        
        return ValidationResult(fieldName, errors)
    }
}

@ValidationDsl
class ValidationSchema {
    private val rules = mutableListOf<ValidationRule<*>>()
    
    fun <T> field(name: String, getValue: () -> T, init: ValidationRule<T>.() -> Unit) {
        val rule = ValidationRule(name, getValue).apply(init)
        rules.add(rule)
    }
    
    fun validate(): List<ValidationResult> {
        return rules.map { it.validate() }
    }
    
    fun isValid(): Boolean {
        return rules.all { it.validate().isValid }
    }
}

data class ValidationResult(
    val fieldName: String,
    val errors: List<String>
) {
    val isValid: Boolean = errors.isEmpty()
}

interface Constraint<T> {
    val errorMessage: String
    fun isValid(value: T): Boolean
}

class RequiredConstraint<T> : Constraint<T> {
    override val errorMessage = "is required"
    override fun isValid(value: T): Boolean = when (value) {
        null -> false
        is String -> value.isNotBlank()
        is Collection<*> -> value.isNotEmpty()
        else -> true
    }
}

class MinLengthConstraint(private val minLength: Int) : Constraint<String?> {
    override val errorMessage = "must be at least $minLength characters"
    override fun isValid(value: String?): Boolean = value?.length ?: 0 >= minLength
}

class MaxLengthConstraint(private val maxLength: Int) : Constraint<String?> {
    override val errorMessage = "must be at most $maxLength characters"
    override fun isValid(value: String?): Boolean = value?.length ?: 0 <= maxLength
}

class PatternConstraint(private val pattern: Regex) : Constraint<String?> {
    override val errorMessage = "format is invalid"
    override fun isValid(value: String?): Boolean = value?.let { pattern.matches(it) } ?: false
}

class EmailConstraint : Constraint<String?> {
    override val errorMessage = "must be a valid email address"
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    override fun isValid(value: String?): Boolean = value?.let { emailRegex.matches(it) } ?: false
}

class MinValueConstraint(private val minValue: Number) : Constraint<Number?> {
    override val errorMessage = "must be at least $minValue"
    override fun isValid(value: Number?): Boolean = value?.toDouble() ?: 0.0 >= minValue.toDouble()
}

class MaxValueConstraint(private val maxValue: Number) : Constraint<Number?> {
    override val errorMessage = "must be at most $maxValue"
    override fun isValid(value: Number?): Boolean = value?.toDouble() ?: 0.0 <= maxValue.toDouble()
}

class CustomConstraint<T>(
    private val validator: (T) -> Boolean,
    override val errorMessage: String
) : Constraint<T> {
    override fun isValid(value: T): Boolean = validator(value)
}

fun validate(init: ValidationSchema.() -> Unit): ValidationSchema {
    return ValidationSchema().apply(init)
}

// ================================
// Test Specification DSL
// ================================

@DslMarker
annotation class TestDsl

@TestDsl
class TestSuite(val name: String) {
    private val testCases = mutableListOf<TestCase>()
    private var setupBlock: (() -> Unit)? = null
    private var teardownBlock: (() -> Unit)? = null
    
    fun setup(block: () -> Unit) {
        setupBlock = block
    }
    
    fun teardown(block: () -> Unit) {
        teardownBlock = block
    }
    
    fun test(description: String, block: TestContext.() -> Unit) {
        testCases.add(TestCase(description, block))
    }
    
    fun run(): TestResults {
        val results = mutableListOf<TestResult>()
        
        println("🧪 Running test suite: $name")
        println("=" * (name.length + 24))
        
        testCases.forEach { testCase ->
            setupBlock?.invoke()
            
            val result = try {
                val context = TestContext()
                testCase.block(context)
                TestResult(testCase.description, true, null)
            } catch (e: AssertionError) {
                TestResult(testCase.description, false, e.message)
            } catch (e: Exception) {
                TestResult(testCase.description, false, "Unexpected error: ${e.message}")
            }
            
            results.add(result)
            
            val status = if (result.passed) "✅ PASS" else "❌ FAIL"
            println("  $status ${result.description}")
            result.error?.let { println("    Error: $it") }
            
            teardownBlock?.invoke()
        }
        
        val passed = results.count { it.passed }
        val failed = results.count { !it.passed }
        
        println("\nResults: $passed passed, $failed failed")
        
        return TestResults(name, results)
    }
}

@TestDsl
class TestContext {
    fun <T> expect(actual: T): Expectation<T> = Expectation(actual)
    
    fun assertTrue(condition: Boolean, message: String = "Expected true but was false") {
        if (!condition) throw AssertionError(message)
    }
    
    fun assertFalse(condition: Boolean, message: String = "Expected false but was true") {
        if (condition) throw AssertionError(message)
    }
    
    fun fail(message: String): Nothing = throw AssertionError(message)
}

@TestDsl
class Expectation<T>(private val actual: T) {
    infix fun toBe(expected: T) {
        if (actual != expected) {
            throw AssertionError("Expected $expected but was $actual")
        }
    }
    
    infix fun toEqual(expected: T) = toBe(expected)
    
    fun toBeNull() {
        if (actual != null) {
            throw AssertionError("Expected null but was $actual")
        }
    }
    
    fun toBeNotNull() {
        if (actual == null) {
            throw AssertionError("Expected not null but was null")
        }
    }
    
    fun toBeTrue() {
        if (actual != true) {
            throw AssertionError("Expected true but was $actual")
        }
    }
    
    fun toBeFalse() {
        if (actual != false) {
            throw AssertionError("Expected false but was $actual")
        }
    }
    
    infix fun toContain(element: Any) {
        when (actual) {
            is Collection<*> -> {
                if (!actual.contains(element)) {
                    throw AssertionError("Expected $actual to contain $element")
                }
            }
            is String -> {
                if (!actual.contains(element.toString())) {
                    throw AssertionError("Expected '$actual' to contain '$element'")
                }
            }
            else -> throw AssertionError("Cannot check contains on ${actual::class.simpleName}")
        }
    }
}

data class TestCase(
    val description: String,
    val block: TestContext.() -> Unit
)

data class TestResult(
    val description: String,
    val passed: Boolean,
    val error: String?
)

data class TestResults(
    val suiteName: String,
    val results: List<TestResult>
) {
    val totalTests = results.size
    val passedTests = results.count { it.passed }
    val failedTests = results.count { !it.passed }
    val successRate = if (totalTests > 0) (passedTests.toDouble() / totalTests * 100) else 0.0
}

fun describe(suiteName: String, init: TestSuite.() -> Unit): TestSuite {
    return TestSuite(suiteName).apply(init)
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateConfigurationDSL() {
    println("=== Configuration DSL Demo ===")
    
    val config = serverConfig {
        host = "example.com"
        port = 443
        ssl = true
        maxConnections = 1000
        timeout = 60.seconds
        
        database {
            type = "postgresql"
            host = "db.example.com"
            port = 5432
            database = "myapp"
            username = "admin"
            password = "secret"
            maxPoolSize = 20
            
            migration("V1__create_users_table.sql")
            migration("V2__add_email_index.sql")
        }
        
        security {
            jwtSecret = "super-secret-key"
            tokenExpiry = 24.hours
            enableCors = true
            corsOrigins = listOf("https://myapp.com", "https://admin.myapp.com")
            
            role("admin") {
                permissions("users:read", "users:write", "orders:read", "orders:write")
            }
            
            role("user") {
                permissions("profile:read", "profile:write", "orders:read")
            }
        }
        
        logging {
            level = "INFO"
            format = "json"
            output = "file"
            
            logger("com.myapp.security", "DEBUG")
            logger("com.myapp.database", "WARN")
        }
        
        middleware {
            name = "authentication"
            enabled = true
            order = 1
        }
        
        middleware {
            name = "cors"
            enabled = true
            order = 2
        }
    }
    
    println(config.summary())
}

fun demonstrateHtmlDSL() {
    println("\n=== HTML DSL Demo ===")
    
    val page = html {
        head {
            title { +"My Awesome Website" }
            meta {
                charset("UTF-8")
            }
            meta {
                name("viewport")
                content("width=device-width, initial-scale=1.0")
            }
            link {
                rel("stylesheet")
                href("styles.css")
            }
        }
        
        body {
            div {
                className("header")
                h1 { +"Welcome to My Website" }
            }
            
            div {
                className("content")
                p { +"This is a paragraph created with our HTML DSL." }
                
                ul {
                    li { +"First item" }
                    li { +"Second item" }
                    li { +"Third item" }
                }
                
                form {
                    action("/submit")
                    method("POST")
                    
                    input {
                        type("text")
                        name("username")
                        placeholder("Enter your username")
                    }
                    
                    input {
                        type("password")
                        name("password")
                        placeholder("Enter your password")
                    }
                    
                    button {
                        type("submit")
                        +"Login"
                    }
                }
            }
        }
    }
    
    println(page.render())
}

fun demonstrateQueryBuilderDSL() {
    println("\n=== Query Builder DSL Demo ===")
    
    val query1 = select {
        select("u.name", "u.email", "COUNT(o.id) as order_count")
        from("users u")
        leftJoin("orders o", "u.id = o.user_id")
        where("u.active = true")
        where("u.created_at > '2023-01-01'")
        groupBy("u.id", "u.name", "u.email")
        having("COUNT(o.id) > 0")
        orderBy("order_count", "DESC")
        limit(10)
    }
    
    println("Query 1:")
    println(query1.build())
    
    val query2 = select {
        select("*")
        from("products")
        where { column("price") gt 100 }
        where { column("category") `in` listOf("electronics", "books") }
        orderBy("name")
    }
    
    println("\nQuery 2:")
    println(query2.build())
}

fun demonstrateValidationDSL() {
    println("\n=== Validation DSL Demo ===")
    
    // Sample data to validate
    val username = "john_doe"
    val email = "john@example.com"
    val age = 25
    val password = "secret123"
    
    val validation = validate {
        field("username", { username }) {
            required()
            minLength(3)
            maxLength(20)
            pattern("^[a-zA-Z0-9_]+$")
        }
        
        field("email", { email }) {
            required()
            email()
        }
        
        field("age", { age }) {
            required()
            min(18)
            max(120)
        }
        
        field("password", { password }) {
            required()
            minLength(8)
            custom({ it.any { char -> char.isDigit() } }, "must contain at least one digit")
            custom({ it.any { char -> char.isLetter() } }, "must contain at least one letter")
        }
    }
    
    val results = validation.validate()
    
    println("Validation Results:")
    results.forEach { result ->
        val status = if (result.isValid) "✅ Valid" else "❌ Invalid"
        println("  ${result.fieldName}: $status")
        result.errors.forEach { error ->
            println("    - $error")
        }
    }
    
    println("Overall valid: ${validation.isValid()}")
}

fun demonstrateTestDSL() {
    println("\n=== Test DSL Demo ===")
    
    val suite = describe("Calculator Tests") {
        var calculator: SimpleCalculator? = null
        
        setup {
            calculator = SimpleCalculator()
        }
        
        teardown {
            calculator = null
        }
        
        test("should add two numbers correctly") {
            val result = calculator!!.add(2, 3)
            expect(result) toBe 5
        }
        
        test("should subtract two numbers correctly") {
            val result = calculator!!.subtract(10, 4)
            expect(result) toBe 6
        }
        
        test("should multiply two numbers correctly") {
            val result = calculator!!.multiply(3, 4)
            expect(result) toBe 12
        }
        
        test("should handle division by zero") {
            val result = try {
                calculator!!.divide(10, 0)
                "no exception"
            } catch (e: ArithmeticException) {
                "exception thrown"
            }
            expect(result) toBe "exception thrown"
        }
        
        test("should work with negative numbers") {
            val result = calculator!!.add(-5, 3)
            expect(result) toBe -2
        }
    }
    
    suite.run()
}

// Simple calculator for testing
class SimpleCalculator {
    fun add(a: Int, b: Int): Int = a + b
    fun subtract(a: Int, b: Int): Int = a - b
    fun multiply(a: Int, b: Int): Int = a * b
    fun divide(a: Int, b: Int): Int {
        if (b == 0) throw ArithmeticException("Division by zero")
        return a / b
    }
}

// Extension for Duration
private val Int.hours: Duration get() = Duration.parse("${this}h")

// ================================
// Main Function
// ================================

fun main() {
    println("🎨 Domain-Specific Language (DSL) Framework Demo")
    println("=" * 60)
    
    demonstrateConfigurationDSL()
    demonstrateHtmlDSL()
    demonstrateQueryBuilderDSL()
    demonstrateValidationDSL()
    demonstrateTestDSL()
    
    println("\n=== DSL Framework Architecture Summary ===")
    println("✓ Type-safe builder patterns with @DslMarker")
    println("✓ Lambda with receiver for fluent syntax")
    println("✓ Extension functions for natural language constructs")
    println("✓ Scope control to prevent misuse")
    println("✓ Configuration DSLs for complex systems")
    println("✓ HTML generation with nested structure")
    println("✓ SQL query builders with type safety")
    println("✓ Validation rules with custom constraints")
    println("✓ Test specification with BDD-style syntax")
    
    println("\n💡 DSL Design Best Practices:")
    println("• Use @DslMarker to prevent scope pollution")
    println("• Leverage receiver types for context")
    println("• Provide both fluent and declarative APIs")
    println("• Include comprehensive validation")
    println("• Design for readability and maintainability")
    println("• Document DSL usage patterns clearly")
    println("• Consider IDE support and auto-completion")
    println("• Test DSL thoroughly with various scenarios")
}

/**
 * TODO: Advanced DSL Features
 * 
 * 1. Gradle build script DSL extensions
 * 2. Configuration management with environment profiles
 * 3. REST API client DSL with type safety
 * 4. Database migration DSL with rollback support
 * 5. Email template DSL with localization
 * 6. Chart/visualization DSL for data presentation
 * 7. State machine DSL for workflow management
 * 8. Rule engine DSL for business logic
 * 9. Mock data generation DSL for testing
 * 10. Deployment pipeline DSL for CI/CD
 * 11. JSON/XML schema validation DSL
 * 12. API documentation DSL (OpenAPI)
 * 13. Performance test specification DSL
 * 14. Configuration validation and migration DSL
 * 15. Custom annotation processors for DSL generation
 */