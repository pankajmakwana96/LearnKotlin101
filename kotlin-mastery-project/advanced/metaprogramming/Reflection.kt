/**
 * Metaprogramming in Kotlin - Reflection
 * 
 * This module covers reflection and metaprogramming in Kotlin, including:
 * - Class and function reflection
 * - Property reflection and manipulation
 * - Annotation processing
 * - Dynamic invocation
 * - KClass, KFunction, KProperty
 * - Reflection-based serialization
 * - DSL creation using reflection
 * - Performance considerations
 */

import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.*

// ================================
// Basic Reflection Examples
// ================================

/**
 * Sample classes for reflection demonstrations
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyAnnotation(val value: String = "", val priority: Int = 0)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonProperty(val name: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Endpoint(val path: String, val method: String = "GET")

@MyAnnotation("User class", priority = 1)
data class User(
    @JsonProperty("user_id")
    val id: String,
    
    @JsonProperty("full_name")
    val name: String,
    
    @JsonProperty("email_address")
    val email: String,
    
    val age: Int = 0
) {
    @MyAnnotation("User greeting function")
    @Endpoint("/greet", "POST")
    fun greet(greeting: String = "Hello"): String = "$greeting, $name!"
    
    fun isAdult(): Boolean = age >= 18
    
    companion object {
        const val DEFAULT_AGE = 0
        
        fun createGuest(): User = User("guest", "Guest User", "guest@example.com")
    }
}

/**
 * Demonstrates basic class reflection
 */
fun demonstrateClassReflection() {
    println("=== Class Reflection ===")
    
    val userClass: KClass<User> = User::class
    
    // Basic class information
    println("Class name: ${userClass.simpleName}")
    println("Qualified name: ${userClass.qualifiedName}")
    println("Is data class: ${userClass.isData}")
    println("Is final: ${userClass.isFinal}")
    println("Is abstract: ${userClass.isAbstract}")
    
    // Annotations on class
    val classAnnotations = userClass.annotations
    println("\nClass annotations:")
    classAnnotations.forEach { annotation ->
        when (annotation) {
            is MyAnnotation -> println("- @MyAnnotation(value='${annotation.value}', priority=${annotation.priority})")
            else -> println("- $annotation")
        }
    }
    
    // Supertypes
    println("\nSupertypes:")
    userClass.supertypes.forEach { type ->
        println("- $type")
    }
    
    // Constructors
    println("\nConstructors:")
    userClass.constructors.forEach { constructor ->
        val params = constructor.parameters.joinToString { param ->
            "${param.name}: ${param.type}"
        }
        println("- constructor($params)")
    }
}

/**
 * Demonstrates property reflection
 */
fun demonstratePropertyReflection() {
    println("\n=== Property Reflection ===")
    
    val userClass = User::class
    val user = User("123", "John Doe", "john@example.com", 30)
    
    // Get all properties
    println("Properties:")
    userClass.memberProperties.forEach { property ->
        println("- ${property.name}: ${property.returnType}")
        
        // Get property value
        val value = property.get(user)
        println("  Value: $value")
        
        // Check annotations
        property.annotations.forEach { annotation ->
            when (annotation) {
                is JsonProperty -> println("  @JsonProperty(name='${annotation.name}')")
            }
        }
        
        // Check if mutable
        if (property is KMutableProperty<*>) {
            println("  Mutable: Yes")
        } else {
            println("  Mutable: No")
        }
        
        println()
    }
    
    // Access specific property by name
    val nameProperty = userClass.memberProperties.find { it.name == "name" }
    nameProperty?.let { prop ->
        val nameValue = prop.get(user)
        println("Name property value: $nameValue")
    }
}

/**
 * Demonstrates function reflection
 */
fun demonstrateFunctionReflection() {
    println("\n=== Function Reflection ===")
    
    val userClass = User::class
    val user = User("123", "John Doe", "john@example.com", 30)
    
    // Get all functions
    println("Functions:")
    userClass.memberFunctions.forEach { function ->
        println("- ${function.name}")
        
        // Parameters
        val params = function.parameters.joinToString { param ->
            "${param.name}: ${param.type}"
        }
        println("  Parameters: $params")
        println("  Return type: ${function.returnType}")
        
        // Annotations
        function.annotations.forEach { annotation ->
            when (annotation) {
                is MyAnnotation -> println("  @MyAnnotation('${annotation.value}')")
                is Endpoint -> println("  @Endpoint(path='${annotation.path}', method='${annotation.method}')")
            }
        }
        
        println()
    }
    
    // Invoke function dynamically
    val greetFunction = userClass.memberFunctions.find { it.name == "greet" }
    greetFunction?.let { func ->
        // Call with default parameter
        val result1 = func.call(user)
        println("Dynamic call greet(): $result1")
        
        // Call with custom parameter
        val result2 = func.call(user, "Hi")
        println("Dynamic call greet('Hi'): $result2")
    }
}

// ================================
// Dynamic Object Creation
// ================================

/**
 * Object factory using reflection
 */
class ReflectionObjectFactory {
    
    inline fun <reified T : Any> create(vararg args: Any?): T? {
        return create(T::class, *args)
    }
    
    fun <T : Any> create(kClass: KClass<T>, vararg args: Any?): T? {
        val constructor = kClass.constructors.firstOrNull { ctor ->
            ctor.parameters.size == args.size
        }
        
        return try {
            constructor?.call(*args)
        } catch (e: Exception) {
            println("Failed to create instance: ${e.message}")
            null
        }
    }
    
    fun <T : Any> createWithMap(kClass: KClass<T>, values: Map<String, Any?>): T? {
        val constructor = kClass.primaryConstructor ?: return null
        
        try {
            val args = constructor.parameters.map { param ->
                values[param.name] ?: when {
                    param.isOptional -> return@map null // Will use default value
                    param.type.isMarkedNullable -> null
                    else -> throw IllegalArgumentException("Missing required parameter: ${param.name}")
                }
            }.toTypedArray()
            
            return constructor.call(*args)
        } catch (e: Exception) {
            println("Failed to create instance from map: ${e.message}")
            return null
        }
    }
}

/**
 * Demonstrates dynamic object creation
 */
fun demonstrateDynamicObjectCreation() {
    println("\n=== Dynamic Object Creation ===")
    
    val factory = ReflectionObjectFactory()
    
    // Create User with constructor args
    val user1 = factory.create<User>("1", "Alice", "alice@example.com", 25)
    println("Created user 1: $user1")
    
    // Create User from map
    val userData = mapOf(
        "id" to "2",
        "name" to "Bob",
        "email" to "bob@example.com",
        "age" to 30
    )
    
    val user2 = factory.createWithMap(User::class, userData)
    println("Created user 2: $user2")
    
    // Create with partial data (using default values)
    val partialUserData = mapOf(
        "id" to "3",
        "name" to "Charlie",
        "email" to "charlie@example.com"
        // age will use default value
    )
    
    val user3 = factory.createWithMap(User::class, partialUserData)
    println("Created user 3: $user3")
}

// ================================
// Serialization Using Reflection
// ================================

/**
 * Simple JSON serializer using reflection
 */
class ReflectionJsonSerializer {
    
    fun serialize(obj: Any): String {
        val kClass = obj::class
        
        if (kClass.isData) {
            return serializeDataClass(obj)
        }
        
        return serializeObject(obj)
    }
    
    private fun serializeDataClass(obj: Any): String {
        val kClass = obj::class
        val properties = kClass.memberProperties
        
        val jsonFields = properties.map { property ->
            val propertyName = getJsonPropertyName(property)
            val value = property.get(obj)
            val serializedValue = serializeValue(value)
            "\"$propertyName\": $serializedValue"
        }
        
        return "{${jsonFields.joinToString(", ")}}"
    }
    
    private fun serializeObject(obj: Any): String {
        // Simplified object serialization
        return "\"${obj.toString()}\""
    }
    
    private fun getJsonPropertyName(property: KProperty1<out Any, *>): String {
        val jsonPropertyAnnotation = property.annotations
            .filterIsInstance<JsonProperty>()
            .firstOrNull()
        
        return jsonPropertyAnnotation?.name ?: property.name
    }
    
    private fun serializeValue(value: Any?): String = when (value) {
        null -> "null"
        is String -> "\"${value.replace("\"", "\\\"")}\""
        is Number -> value.toString()
        is Boolean -> value.toString()
        is Collection<*> -> "[${value.joinToString(", ") { serializeValue(it) }}]"
        else -> serialize(value)
    }
}

/**
 * Simple JSON deserializer using reflection
 */
class ReflectionJsonDeserializer {
    
    inline fun <reified T : Any> deserialize(json: String): T? {
        return deserialize(T::class, json)
    }
    
    fun <T : Any> deserialize(kClass: KClass<T>, json: String): T? {
        if (!kClass.isData) {
            println("Only data classes are supported")
            return null
        }
        
        val jsonMap = parseJsonToMap(json)
        val constructor = kClass.primaryConstructor ?: return null
        
        try {
            val args = constructor.parameters.map { param ->
                val jsonKey = getJsonPropertyName(kClass, param.name!!)
                val value = jsonMap[jsonKey]
                convertValue(value, param.type)
            }.toTypedArray()
            
            return constructor.call(*args)
        } catch (e: Exception) {
            println("Deserialization failed: ${e.message}")
            return null
        }
    }
    
    private fun getJsonPropertyName(kClass: KClass<*>, propertyName: String): String {
        val property = kClass.memberProperties.find { it.name == propertyName }
        val jsonPropertyAnnotation = property?.annotations
            ?.filterIsInstance<JsonProperty>()
            ?.firstOrNull()
        
        return jsonPropertyAnnotation?.name ?: propertyName
    }
    
    private fun parseJsonToMap(json: String): Map<String, Any?> {
        // Simplified JSON parsing - in real implementation use proper JSON library
        val content = json.trim().removeSurrounding("{", "}")
        val map = mutableMapOf<String, Any?>()
        
        // Very basic parsing - handles simple cases only
        val pairs = content.split(",").map { it.trim() }
        for (pair in pairs) {
            val parts = pair.split(":", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim().removeSurrounding("\"")
                val value = parts[1].trim()
                
                map[key] = when {
                    value == "null" -> null
                    value == "true" -> true
                    value == "false" -> false
                    value.startsWith("\"") && value.endsWith("\"") -> value.removeSurrounding("\"")
                    value.toIntOrNull() != null -> value.toInt()
                    value.toDoubleOrNull() != null -> value.toDouble()
                    else -> value
                }
            }
        }
        
        return map
    }
    
    private fun convertValue(value: Any?, targetType: KType): Any? {
        if (value == null) return null
        
        val classifier = targetType.classifier as? KClass<*> ?: return value
        
        return when (classifier) {
            String::class -> value.toString()
            Int::class -> when (value) {
                is Number -> value.toInt()
                is String -> value.toIntOrNull()
                else -> null
            }
            Double::class -> when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull()
                else -> null
            }
            Boolean::class -> when (value) {
                is Boolean -> value
                is String -> value.toBoolean()
                else -> null
            }
            else -> value
        }
    }
}

/**
 * Demonstrates reflection-based serialization
 */
fun demonstrateReflectionSerialization() {
    println("\n=== Reflection-based Serialization ===")
    
    val serializer = ReflectionJsonSerializer()
    val deserializer = ReflectionJsonDeserializer()
    
    val user = User("123", "John Doe", "john@example.com", 30)
    
    // Serialize
    val json = serializer.serialize(user)
    println("Serialized JSON: $json")
    
    // Deserialize
    val deserializedUser = deserializer.deserialize<User>(json)
    println("Deserialized user: $deserializedUser")
    
    println("Objects equal: ${user == deserializedUser}")
}

// ================================
// DSL Creation Using Reflection
// ================================

/**
 * HTTP client DSL using reflection
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val path: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class POST(val path: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(val name: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Query(val name: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Body

/**
 * Example API interface
 */
interface UserApi {
    @GET("/users/{id}")
    fun getUser(@Path("id") id: String): User?
    
    @GET("/users")
    fun getUsers(@Query("page") page: Int = 1, @Query("size") size: Int = 10): List<User>
    
    @POST("/users")
    fun createUser(@Body user: User): User?
}

/**
 * HTTP client that generates implementations using reflection
 */
class ReflectionHttpClient {
    
    fun <T : Any> create(apiInterface: KClass<T>): T {
        // In a real implementation, this would use dynamic proxies (Java) or code generation
        // For demonstration, we'll return a simple implementation
        
        @Suppress("UNCHECKED_CAST")
        return object : Any() {
            // This is a simplified demonstration - real implementation would be much more complex
        } as T
    }
    
    fun analyzeApi(apiInterface: KClass<*>) {
        println("Analyzing API interface: ${apiInterface.simpleName}")
        
        apiInterface.memberFunctions.forEach { function ->
            println("\nFunction: ${function.name}")
            
            // Check HTTP method annotations
            val httpMethod = function.annotations.find { 
                it is GET || it is POST 
            }
            
            when (httpMethod) {
                is GET -> println("  HTTP Method: GET ${httpMethod.path}")
                is POST -> println("  HTTP Method: POST ${httpMethod.path}")
            }
            
            // Analyze parameters
            function.parameters.drop(1).forEach { param -> // Skip 'this' parameter
                println("  Parameter: ${param.name} (${param.type})")
                
                param.annotations.forEach { annotation ->
                    when (annotation) {
                        is Path -> println("    @Path('${annotation.name}')")
                        is Query -> println("    @Query('${annotation.name}')")
                        is Body -> println("    @Body")
                    }
                }
            }
            
            println("  Return type: ${function.returnType}")
        }
    }
}

/**
 * Configuration DSL using reflection
 */
class ConfigurationBuilder {
    private val properties = mutableMapOf<String, Any>()
    
    fun set(key: String, value: Any) {
        properties[key] = value
    }
    
    inline fun <reified T : Any> build(): T {
        return build(T::class)
    }
    
    fun <T : Any> build(kClass: KClass<T>): T {
        val constructor = kClass.primaryConstructor 
            ?: throw IllegalArgumentException("No primary constructor found")
        
        val args = constructor.parameters.map { param ->
            val value = properties[param.name]
            when {
                value != null -> value
                param.isOptional -> null // Use default value
                param.type.isMarkedNullable -> null
                else -> throw IllegalArgumentException("Missing required property: ${param.name}")
            }
        }.toTypedArray()
        
        return constructor.call(*args)
    }
}

data class DatabaseConfig(
    val host: String = "localhost",
    val port: Int = 5432,
    val database: String,
    val username: String,
    val password: String,
    val maxConnections: Int = 10
)

/**
 * Demonstrates DSL creation
 */
fun demonstrateDslCreation() {
    println("\n=== DSL Creation ===")
    
    // HTTP client DSL analysis
    val httpClient = ReflectionHttpClient()
    httpClient.analyzeApi(UserApi::class)
    
    // Configuration DSL
    println("\nConfiguration DSL:")
    val configBuilder = ConfigurationBuilder()
    
    configBuilder.set("host", "prod-db.example.com")
    configBuilder.set("database", "myapp")
    configBuilder.set("username", "appuser")
    configBuilder.set("password", "secret123")
    configBuilder.set("maxConnections", 20)
    
    val config = configBuilder.build<DatabaseConfig>()
    println("Built configuration: $config")
}

// ================================
// Performance Monitoring
// ================================

/**
 * Performance monitoring using reflection
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Timed

class PerformanceMonitor {
    
    fun <T : Any> monitor(obj: T): T {
        val kClass = obj::class
        
        println("Performance monitoring for ${kClass.simpleName}:")
        
        kClass.memberFunctions.forEach { function ->
            if (function.annotations.any { it is Timed }) {
                println("- ${function.name} is marked for timing")
            }
        }
        
        // In a real implementation, this would create a proxy that intercepts method calls
        return obj
    }
}

class ExampleService {
    @Timed
    fun expensiveOperation(n: Int): Long {
        Thread.sleep(100) // Simulate work
        return (1..n).map { it.toLong() }.sum()
    }
    
    fun regularOperation(): String = "Quick result"
}

/**
 * Demonstrates performance monitoring
 */
fun demonstratePerformanceMonitoring() {
    println("\n=== Performance Monitoring ===")
    
    val monitor = PerformanceMonitor()
    val service = ExampleService()
    
    monitor.monitor(service)
    
    // The service would be wrapped to time @Timed methods
    val result = service.expensiveOperation(100)
    println("Result: $result")
}

/**
 * TODO: Practice Exercises
 * 
 * 1. Create a dependency injection container using reflection
 * 2. Build a validation framework that validates objects based on annotations
 * 3. Implement a simple ORM (Object-Relational Mapping) using reflection
 * 4. Create an automatic REST API generator from interface definitions
 * 5. Build a configuration system that loads from multiple sources using reflection
 * 6. Implement a simple mocking framework using reflection
 * 7. Create a plugin system that loads and instantiates plugins dynamically
 * 8. Build a serialization framework that handles nested objects and collections
 * 9. Implement an AOP (Aspect-Oriented Programming) framework with annotations
 * 10. Create a code generator that creates builders for data classes
 */

fun main() {
    demonstrateClassReflection()
    demonstratePropertyReflection()
    demonstrateFunctionReflection()
    demonstrateDynamicObjectCreation()
    demonstrateReflectionSerialization()
    demonstrateDslCreation()
    demonstratePerformanceMonitoring()
    
    println("\n=== Reflection Summary ===")
    println("✓ Class, property, and function reflection for runtime introspection")
    println("✓ Dynamic object creation and method invocation")
    println("✓ Annotation processing for metadata-driven programming")
    println("✓ Reflection-based serialization and deserialization")
    println("✓ DSL creation using reflection and annotations")
    println("✓ Performance monitoring and AOP-like capabilities")
    println("✓ Real-world applications: frameworks, serializers, configuration systems")
    
    println("\nNote: Reflection has performance overhead. Use judiciously in production code.")
}