package com.kotlinmastery.basics.syntax

import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * # String Templates and Manipulation in Kotlin
 * 
 * String templates in Kotlin provide a powerful and readable way to create formatted strings.
 * This module covers template syntax, expressions, multi-line strings, and advanced manipulation.
 * 
 * ## Learning Objectives
 * - Master string template syntax and interpolation
 * - Use expressions within string templates
 * - Work with multi-line strings and raw strings
 * - Apply advanced string manipulation techniques
 * - Handle special characters and escaping
 * 
 * ## Prerequisites: Variables, types, and basic null safety
 * ## Estimated Time: 2 hours
 */

fun main() {
    println("=== Kotlin String Templates Demo ===\n")
    
    basicStringTemplates()
    expressionsInTemplates()
    multilineStrings()
    rawStrings()
    stringManipulation()
    advancedFormatting()
    performanceConsiderations()
    realWorldExamples()
}

/**
 * ## Basic String Templates
 * 
 * String templates allow you to include variable values and simple expressions
 * directly in string literals using the $ symbol.
 */
fun basicStringTemplates() {
    println("--- Basic String Templates ---")
    
    // Simple variable interpolation
    val name = "Kotlin"
    val version = 1.9
    val isStable = true
    
    // Basic template syntax
    val basicTemplate = "Welcome to $name version $version!"
    println("Basic template: $basicTemplate")
    
    // Templates with different types
    val numberTemplate = "The answer is $version"
    val booleanTemplate = "Is stable: $isStable"
    
    println("Number template: $numberTemplate")
    println("Boolean template: $booleanTemplate")
    
    // Templates with nullable values
    val nullableName: String? = "Alice"
    val nullValue: String? = null
    
    val nullableTemplate1 = "Hello, $nullableName!"
    val nullableTemplate2 = "Hello, $nullValue!"
    
    println("Nullable template 1: $nullableTemplate1")
    println("Nullable template 2: $nullableTemplate2")
    
    // Avoiding variable interpolation with escape
    val price = 29.99
    val priceTemplate = "Price: \$${price}"  // \$ escapes the dollar sign
    println("Price template: $priceTemplate")
    
    // Templates in different contexts
    val fileName = "data"
    val extension = "txt"
    val fullPath = "/home/user/$fileName.$extension"
    println("File path: $fullPath")
    
    // Template with special characters
    val greeting = "Hello"
    val punctuation = "!"
    val excitedGreeting = "$greeting$punctuation$punctuation$punctuation"
    println("Excited greeting: $excitedGreeting")
    
    println()
}

/**
 * ## Expressions in Templates
 * 
 * For more complex expressions, use curly braces ${} to include any valid Kotlin expression.
 */
fun expressionsInTemplates() {
    println("--- Expressions in Templates ---")
    
    val a = 10
    val b = 20
    
    // Arithmetic expressions
    val mathTemplate = "Sum: ${a + b}, Product: ${a * b}, Average: ${(a + b) / 2.0}"
    println("Math template: $mathTemplate")
    
    // Function calls in templates
    fun square(x: Int) = x * x
    val functionTemplate = "Square of $a is ${square(a)}"
    println("Function template: $functionTemplate")
    
    // Method calls on objects
    val text = "kotlin"
    val methodTemplate = "Uppercase: ${text.uppercase()}, Length: ${text.length}"
    println("Method template: $methodTemplate")
    
    // Conditional expressions
    val age = 25
    val statusTemplate = "Status: ${if (age >= 18) "Adult" else "Minor"}"
    println("Status template: $statusTemplate")
    
    // When expressions
    val grade = 85
    val gradeTemplate = "Grade: ${
        when {
            grade >= 90 -> "A"
            grade >= 80 -> "B"
            grade >= 70 -> "C"
            grade >= 60 -> "D"
            else -> "F"
        }
    }"
    println("Grade template: $gradeTemplate")
    
    // Array and collection access
    val colors = arrayOf("Red", "Green", "Blue")
    val arrayTemplate = "First color: ${colors[0]}, Last color: ${colors[colors.size - 1]}"
    println("Array template: $arrayTemplate")
    
    val numbers = listOf(1, 2, 3, 4, 5)
    val collectionTemplate = "Sum of numbers: ${numbers.sum()}, Max: ${numbers.maxOrNull()}"
    println("Collection template: $collectionTemplate")
    
    // Object property access
    data class Person(val firstName: String, val lastName: String, val age: Int)
    val person = Person("John", "Doe", 30)
    val objectTemplate = "Person: ${person.firstName} ${person.lastName}, Age: ${person.age}"
    println("Object template: $objectTemplate")
    
    // Nested expressions
    val x = 5
    val y = 3
    val nestedTemplate = "Result: ${if (x > y) "x ($x) is greater than y ($y)" else "y ($y) is greater than x ($x)"}"
    println("Nested template: $nestedTemplate")
    
    // Safe call in templates
    val nullablePerson: Person? = person
    val safeCallTemplate = "Name: ${nullablePerson?.firstName ?: "Unknown"}"
    println("Safe call template: $safeCallTemplate")
    
    // Elvis operator in templates
    val nullableAge: Int? = null
    val elvisTemplate = "Age: ${nullableAge ?: "Not specified"}"
    println("Elvis template: $elvisTemplate")
    
    println()
}

/**
 * ## Multi-line Strings
 * 
 * Triple-quoted strings allow you to create multi-line strings while preserving formatting.
 */
fun multilineStrings() {
    println("--- Multi-line Strings ---")
    
    // Basic multi-line string
    val poem = """
        Roses are red,
        Violets are blue,
        Kotlin is awesome,
        And so are you!
    """
    println("Basic multi-line string:")
    println(poem)
    
    // Multi-line with trimIndent()
    val formattedPoem = """
        Roses are red,
        Violets are blue,
        Kotlin is awesome,
        And so are you!
    """.trimIndent()
    
    println("With trimIndent():")
    println(formattedPoem)
    
    // Multi-line with templates
    val userName = "Alice"
    val userAge = 28
    val personalizedMessage = """
        Hello, $userName!
        
        Your profile information:
        - Name: $userName
        - Age: $userAge
        - Status: ${if (userAge >= 18) "Adult" else "Minor"}
        
        Thank you for using our service!
    """.trimIndent()
    
    println("Multi-line with templates:")
    println(personalizedMessage)
    
    // Multi-line for SQL queries
    val tableName = "users"
    val minAge = 21
    val sqlQuery = """
        SELECT u.id, u.name, u.email
        FROM $tableName u
        WHERE u.age >= $minAge
          AND u.active = true
        ORDER BY u.name ASC
        LIMIT 100
    """.trimIndent()
    
    println("SQL query:")
    println(sqlQuery)
    
    // Multi-line for JSON-like structures
    val jsonLike = """
        {
            "name": "$userName",
            "age": $userAge,
            "preferences": {
                "theme": "dark",
                "notifications": true
            }
        }
    """.trimIndent()
    
    println("JSON-like structure:")
    println(jsonLike)
    
    // Multi-line with custom margin
    val customMargin = """
        |This is a multi-line string
        |with custom margin marker.
        |Each line starts with |
        |which will be removed.
    """.trimMargin()
    
    println("Custom margin:")
    println(customMargin)
    
    // Custom margin with different marker
    val customMarker = """
        >This uses > as margin marker
        >instead of the default |
        >Very flexible for different formats
    """.trimMargin(">")
    
    println("Custom marker:")
    println(customMarker)
    
    println()
}

/**
 * ## Raw Strings
 * 
 * Raw strings (triple-quoted) don't process escape sequences, making them perfect
 * for regex patterns, file paths, and other content with special characters.
 */
fun rawStrings() {
    println("--- Raw Strings ---")
    
    // Raw string vs regular string
    val regularString = "Path: C:\\Users\\Alice\\Documents\\file.txt"
    val rawString = """Path: C:\Users\Alice\Documents\file.txt"""
    
    println("Regular string (escaped): $regularString")
    println("Raw string (no escaping): $rawString")
    
    // Raw strings for regex patterns
    val emailRegex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    val phoneRegex = """\+?1?[-.\s]?\(?([0-9]{3})\)?[-.\s]?([0-9]{3})[-.\s]?([0-9]{4})"""
    
    println("Email regex: $emailRegex")
    println("Phone regex: $phoneRegex")
    
    // Raw strings for Windows paths
    val windowsPath = """C:\Program Files\JetBrains\IntelliJ IDEA\bin\idea64.exe"""
    println("Windows path: $windowsPath")
    
    // Raw strings for URLs with parameters
    val complexUrl = """https://api.example.com/search?q="kotlin tutorial"&sort=date&format=json"""
    println("Complex URL: $complexUrl")
    
    // Raw strings for HTML/XML
    val htmlTemplate = """
        <div class="user-card">
            <h2>$userName</h2>
            <p>Age: $userAge</p>
            <p>Email: ${userName.lowercase()}@example.com</p>
        </div>
    """.trimIndent()
    
    println("HTML template:")
    println(htmlTemplate)
    
    // Raw strings with literal $ (when you don't want interpolation)
    val bashScript = """
        #!/bin/bash
        echo "Starting backup process..."
        BACKUP_DIR="/backup/$(date +%Y%m%d)"
        mkdir -p ${'$'}BACKUP_DIR
        echo "Backup directory: ${'$'}BACKUP_DIR"
    """.trimIndent()
    
    println("Bash script:")
    println(bashScript)
    
    // Raw strings for documentation
    val documentation = """
        ## String Templates Usage
        
        Basic syntax: ${'$'}variable
        Expression syntax: ${'$'}{expression}
        
        Examples:
        - Simple: "Hello, ${'$'}name!"
        - Complex: "Result: ${'$'}{x + y}"
    """.trimIndent()
    
    println("Documentation:")
    println(documentation)
    
    println()
}

/**
 * ## String Manipulation
 * 
 * Kotlin provides extensive string manipulation functions for common operations.
 */
fun stringManipulation() {
    println("--- String Manipulation ---")
    
    val sampleText = "  Hello, Kotlin World!  "
    
    // Basic transformations
    println("Original: '$sampleText'")
    println("Trimmed: '${sampleText.trim()}'")
    println("Uppercase: '${sampleText.uppercase()}'")
    println("Lowercase: '${sampleText.lowercase()}'")
    println("Capitalized: '${sampleText.trim().replaceFirstChar { it.uppercase() }}'")
    
    // Replacement operations
    val text = "Kotlin is great. Kotlin is powerful. Kotlin is fun."
    println("\nOriginal text: $text")
    println("Replace first: ${text.replaceFirst("Kotlin", "Java")}")
    println("Replace all: ${text.replace("Kotlin", "Java")}")
    println("Replace with regex: ${text.replace(Regex("\\bis\\b"), "was")}")
    
    // Substring operations
    val longText = "The quick brown fox jumps over the lazy dog"
    println("\nSubstring operations:")
    println("Original: $longText")
    println("Substring(4, 9): '${longText.substring(4, 9)}'")
    println("Substring(10): '${longText.substring(10)}'")
    println("Take(9): '${longText.take(9)}'")
    println("Drop(4): '${longText.drop(4)}'")
    println("TakeLast(3): '${longText.takeLast(3)}'")
    println("DropLast(4): '${longText.dropLast(4)}'")
    
    // Splitting and joining
    val csvData = "Alice,30,Engineer,New York"
    val fields = csvData.split(",")
    println("\nSplit CSV: $fields")
    
    val rejoined = fields.joinToString(" | ")
    println("Rejoined with |: $rejoined")
    
    val customJoin = fields.joinToString(
        separator = " • ",
        prefix = "[ ",
        postfix = " ]"
    ) { it.trim().uppercase() }
    println("Custom join: $customJoin")
    
    // Padding and alignment
    val number = "42"
    println("\nPadding operations:")
    println("Original: '$number'")
    println("Pad start (5, '0'): '${number.padStart(5, '0')}'")
    println("Pad end (5, '.'): '${number.padEnd(5, '.')}'")
    
    val title = "Kotlin"
    val width = 20
    val centered = title.padStart((width + title.length) / 2).padEnd(width)
    println("Centered in $width chars: '${centered}'")
    
    // Character operations
    val mixedCase = "Hello World 123!"
    println("\nCharacter analysis:")
    println("Original: $mixedCase")
    println("Letters only: ${mixedCase.filter { it.isLetter() }}")
    println("Digits only: ${mixedCase.filter { it.isDigit() }}")
    println("Uppercase letters: ${mixedCase.filter { it.isUpperCase() }}")
    println("Whitespace count: ${mixedCase.count { it.isWhitespace() }}")
    
    // String validation
    val email = "user@example.com"
    val invalidEmail = "invalid-email"
    
    println("\nString validation:")
    println("'$email' contains @: ${email.contains("@")}")
    println("'$email' ends with .com: ${email.endsWith(".com")}")
    println("'$invalidEmail' is blank: ${invalidEmail.isBlank()}")
    println("'$email' matches email pattern: ${email.matches(Regex("[^@]+@[^.]+\\..+"))}")
    
    println()
}

/**
 * ## Advanced Formatting
 * 
 * Advanced string formatting techniques for numbers, dates, and custom formats.
 */
fun advancedFormatting() {
    println("--- Advanced Formatting ---")
    
    // Number formatting
    val price = 1234.56
    val quantity = 1000000
    
    // Using String.format (Java-style)
    val formattedPrice = String.format("$%.2f", price)
    val formattedQuantity = String.format("%,d", quantity)
    
    println("Java-style formatting:")
    println("Price: $formattedPrice")
    println("Quantity: $formattedQuantity")
    
    // Using NumberFormat
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    
    println("\nNumberFormat:")
    println("Currency: ${currencyFormat.format(price)}")
    println("Number: ${numberFormat.format(quantity)}")
    
    // Custom number formatting
    fun formatNumber(value: Double, decimals: Int = 2): String {
        return "%.${decimals}f".format(value)
    }
    
    fun formatWithCommas(value: Long): String {
        return value.toString().reversed().chunked(3).joinToString(",").reversed()
    }
    
    println("\nCustom formatting:")
    println("Custom decimal: ${formatNumber(3.14159, 4)}")
    println("Custom commas: ${formatWithCommas(1234567890L)}")
    
    // Date and time formatting
    val now = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val customFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")
    
    println("\nDate formatting:")
    println("Standard: ${now.format(dateFormatter)}")
    println("Custom: ${now.format(customFormatter)}")
    
    // Template with date formatting
    val userName = "Alice"
    val timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val logEntry = "[$timestamp] User $userName logged in successfully"
    println("Log entry: $logEntry")
    
    // Percentage formatting
    val completionRate = 0.8543
    val percentageTemplate = "Progress: ${String.format("%.1f%%", completionRate * 100)}"
    println("Percentage: $percentageTemplate")
    
    // File size formatting
    fun formatFileSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return "${String.format("%.1f", size)} ${units[unitIndex]}"
    }
    
    val fileSize = 1048576L  // 1 MB in bytes
    println("File size: ${formatFileSize(fileSize)}")
    
    // Scientific notation
    val largeNumber = 123456789.0
    val scientificNotation = String.format("%.2e", largeNumber)
    println("Scientific notation: $scientificNotation")
    
    println()
}

/**
 * ## Performance Considerations
 * 
 * Understanding the performance implications of string operations and templates.
 */
fun performanceConsiderations() {
    println("--- Performance Considerations ---")
    
    // String concatenation vs templates
    val name = "Kotlin"
    val version = "1.9"
    
    // Template (efficient for simple cases)
    val template = "Using $name version $version"
    
    // Concatenation (can be less efficient)
    val concatenation = "Using " + name + " version " + version
    
    // StringBuilder (efficient for many operations)
    val builder = StringBuilder()
        .append("Using ")
        .append(name)
        .append(" version ")
        .append(version)
        .toString()
    
    println("Template result: $template")
    println("Concatenation result: $concatenation")
    println("StringBuilder result: $builder")
    
    // When to use StringBuilder
    println("\nStringBuilder for multiple operations:")
    val items = listOf("Apple", "Banana", "Cherry", "Date", "Elderberry")
    
    // Inefficient: multiple string concatenations
    var result1 = "Items: "
    for (item in items) {
        result1 += "$item, "
    }
    result1 = result1.dropLast(2)  // Remove last comma
    
    // Efficient: StringBuilder
    val result2 = buildString {
        append("Items: ")
        items.forEachIndexed { index, item ->
            append(item)
            if (index < items.size - 1) append(", ")
        }
    }
    
    // Most efficient: joinToString
    val result3 = "Items: ${items.joinToString(", ")}"
    
    println("Multiple concatenations: $result1")
    println("StringBuilder: $result2")
    println("JoinToString: $result3")
    
    // Template compilation insights
    println("\nTemplate compilation insights:")
    println("Simple templates like '\$variable' are optimized by the compiler")
    println("Complex expressions like '\${function()}' may create temporary objects")
    println("For performance-critical code, consider StringBuilder for many operations")
    
    // Memory considerations
    println("\nMemory considerations:")
    println("- Strings are immutable, each operation creates a new string")
    println("- Templates with simple variables are optimized")
    println("- Raw strings and multi-line strings have minimal overhead")
    println("- Use StringBuilder for building large strings incrementally")
    
    println()
}

/**
 * ## Real-World Examples
 * 
 * Practical applications of string templates in common programming scenarios.
 */
fun realWorldExamples() {
    println("--- Real-World Examples ---")
    
    // 1. Configuration file generation
    data class ServerConfig(
        val host: String,
        val port: Int,
        val database: String,
        val maxConnections: Int,
        val enableSsl: Boolean
    )
    
    fun generateConfigFile(config: ServerConfig): String {
        return """
            # Server Configuration
            server.host=${config.host}
            server.port=${config.port}
            
            # Database Configuration
            database.name=${config.database}
            database.maxConnections=${config.maxConnections}
            
            # Security Configuration
            security.ssl.enabled=${config.enableSsl}
            security.ssl.port=${if (config.enableSsl) config.port + 443 else "N/A"}
            
            # Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
        """.trimIndent()
    }
    
    val serverConfig = ServerConfig("localhost", 8080, "app_db", 50, true)
    println("Generated config file:")
    println(generateConfigFile(serverConfig))
    
    // 2. Email template generation
    data class EmailContext(
        val recipientName: String,
        val senderName: String,
        val subject: String,
        val orderNumber: String,
        val amount: Double,
        val items: List<String>
    )
    
    fun generateOrderConfirmationEmail(context: EmailContext): String {
        val itemsList = context.items.joinToString("\n") { "• $it" }
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale.US).format(context.amount)
        
        return """
            Subject: ${context.subject}
            
            Dear ${context.recipientName},
            
            Thank you for your order! Here are the details:
            
            Order Number: ${context.orderNumber}
            Total Amount: $formattedAmount
            
            Items ordered:
            $itemsList
            
            Your order will be processed within 24 hours.
            
            Best regards,
            ${context.senderName}
            Customer Service Team
        """.trimIndent()
    }
    
    val emailContext = EmailContext(
        recipientName = "Alice Johnson",
        senderName = "Bob Smith",
        subject = "Order Confirmation #12345",
        orderNumber = "ORD-12345",
        amount = 129.99,
        items = listOf("Wireless Mouse", "Mechanical Keyboard", "USB-C Cable")
    )
    
    println("\nGenerated email:")
    println(generateOrderConfirmationEmail(emailContext))
    
    // 3. SQL query building
    data class QueryFilter(
        val table: String,
        val conditions: Map<String, Any>,
        val orderBy: String? = null,
        val limit: Int? = null
    )
    
    fun buildSelectQuery(filter: QueryFilter): String {
        val whereClause = if (filter.conditions.isNotEmpty()) {
            "WHERE " + filter.conditions.entries.joinToString(" AND ") { (key, value) ->
                when (value) {
                    is String -> "$key = '$value'"
                    is Number -> "$key = $value"
                    else -> "$key = '$value'"
                }
            }
        } else ""
        
        val orderClause = filter.orderBy?.let { "ORDER BY $it" } ?: ""
        val limitClause = filter.limit?.let { "LIMIT $it" } ?: ""
        
        return """
            SELECT * FROM ${filter.table}
            $whereClause
            $orderClause
            $limitClause
        """.trimIndent().replace(Regex("\\n\\s*\\n"), "\n").trim()
    }
    
    val queryFilter = QueryFilter(
        table = "users",
        conditions = mapOf("active" to true, "age" to 25, "city" to "New York"),
        orderBy = "name ASC",
        limit = 10
    )
    
    println("\nGenerated SQL query:")
    println(buildSelectQuery(queryFilter))
    
    // 4. Log message formatting
    enum class LogLevel { DEBUG, INFO, WARN, ERROR }
    
    fun formatLogMessage(
        level: LogLevel,
        component: String,
        message: String,
        exception: Throwable? = null
    ): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val threadName = Thread.currentThread().name
        
        val baseMessage = "[$timestamp] [$level] [$threadName] [$component] $message"
        
        return if (exception != null) {
            """
                $baseMessage
                Exception: ${exception.javaClass.simpleName}: ${exception.message}
            """.trimIndent()
        } else {
            baseMessage
        }
    }
    
    println("\nLog message examples:")
    println(formatLogMessage(LogLevel.INFO, "DatabaseService", "Connection established successfully"))
    println(formatLogMessage(LogLevel.ERROR, "PaymentService", "Payment processing failed", 
        RuntimeException("Invalid credit card number")))
    
    // 5. Test data generation
    fun generateTestUser(id: Int): String {
        val firstNames = listOf("Alice", "Bob", "Charlie", "Diana", "Eve")
        val lastNames = listOf("Smith", "Johnson", "Williams", "Brown", "Jones")
        val domains = listOf("gmail.com", "yahoo.com", "outlook.com", "company.com")
        
        val firstName = firstNames[id % firstNames.size]
        val lastName = lastNames[(id / firstNames.size) % lastNames.size]
        val domain = domains[id % domains.size]
        val email = "${firstName.lowercase()}.${lastName.lowercase()}@$domain"
        
        return """
            User #$id:
            Name: $firstName $lastName
            Email: $email
            Username: ${firstName.lowercase()}${id.toString().padStart(3, '0')}
            Age: ${20 + (id % 50)}
        """.trimIndent()
    }
    
    println("\nGenerated test users:")
    repeat(3) { index ->
        println(generateTestUser(index + 1))
        println()
    }
}

/**
 * ## TODO Exercises
 * 
 * Complete these exercises to practice string templates and manipulation:
 * 
 * 1. Create a function that generates a formatted invoice
 * 2. Build a URL builder with query parameters
 * 3. Implement a simple template engine for HTML generation
 * 4. Create a CSV row formatter with proper escaping
 * 5. Build a function that formats file paths for different operating systems
 */

// TODO: Exercise 1 - Invoice generator
data class InvoiceItem(val name: String, val quantity: Int, val price: Double)
data class Invoice(val invoiceNumber: String, val customerName: String, val items: List<InvoiceItem>)

fun generateInvoice(invoice: Invoice): String {
    // TODO: Generate a formatted invoice with:
    // - Header with invoice number and customer name
    // - Itemized list with quantities, prices, and subtotals
    // - Total amount calculation
    // - Current date
    return ""
}

// TODO: Exercise 2 - URL builder
fun buildUrl(baseUrl: String, path: String, parameters: Map<String, String>): String {
    // TODO: Build a complete URL with:
    // - Base URL and path combination
    // - URL-encoded query parameters
    // - Proper handling of special characters
    return ""
}

// TODO: Exercise 3 - HTML template engine
fun generateHtmlPage(title: String, content: String, cssClass: String? = null): String {
    // TODO: Generate a complete HTML page with:
    // - DOCTYPE and HTML structure
    // - Title in head section
    // - Optional CSS class on body
    // - Content in main div
    return ""
}

// TODO: Exercise 4 - CSV formatter
fun formatCsvRow(values: List<String>): String {
    // TODO: Format values as CSV row with:
    // - Proper comma separation
    // - Quote values containing commas or quotes
    // - Escape quotes by doubling them
    return ""
}

// TODO: Exercise 5 - Cross-platform file paths
enum class OperatingSystem { WINDOWS, UNIX }

fun formatFilePath(parts: List<String>, os: OperatingSystem): String {
    // TODO: Format file path for target OS:
    // - Use appropriate path separator (\ or /)
    // - Handle drive letters on Windows
    // - Validate path components
    return ""
}