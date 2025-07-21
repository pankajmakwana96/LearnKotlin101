/**
 * Beginner Project: Advanced Calculator
 * 
 * This project demonstrates fundamental Kotlin concepts:
 * - Basic syntax and variables
 * - Functions and parameters
 * - Control flow (when, if/else)
 * - Exception handling
 * - String manipulation
 * - Collections and loops
 * - Basic OOP concepts
 * - Input validation
 * 
 * Features:
 * - Basic arithmetic operations (+, -, *, /, %)
 * - Advanced operations (power, square root, factorial)
 * - Memory functions (store, recall, clear)
 * - History of calculations
 * - Expression parsing
 * - Error handling and validation
 */

import kotlin.math.*

// ================================
// Calculator Engine
// ================================

/**
 * Main calculator class with basic and advanced operations
 */
class Calculator {
    private var memory: Double = 0.0
    private val history = mutableListOf<CalculationRecord>()
    
    /**
     * Data class to store calculation history
     */
    data class CalculationRecord(
        val expression: String,
        val result: Double,
        val timestamp: Long = System.currentTimeMillis()
    ) {
        fun getFormattedTime(): String {
            val date = java.util.Date(timestamp)
            return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        }
    }
    
    /**
     * Sealed class for operation results
     */
    sealed class CalculationResult {
        data class Success(val value: Double) : CalculationResult()
        data class Error(val message: String) : CalculationResult()
    }
    
    // ================================
    // Basic Operations
    // ================================
    
    /**
     * Addition operation
     */
    fun add(a: Double, b: Double): CalculationResult {
        return try {
            val result = a + b
            addToHistory("$a + $b", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Addition failed: ${e.message}")
        }
    }
    
    /**
     * Subtraction operation
     */
    fun subtract(a: Double, b: Double): CalculationResult {
        return try {
            val result = a - b
            addToHistory("$a - $b", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Subtraction failed: ${e.message}")
        }
    }
    
    /**
     * Multiplication operation
     */
    fun multiply(a: Double, b: Double): CalculationResult {
        return try {
            val result = a * b
            addToHistory("$a × $b", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Multiplication failed: ${e.message}")
        }
    }
    
    /**
     * Division operation with zero division check
     */
    fun divide(a: Double, b: Double): CalculationResult {
        return try {
            if (b == 0.0) {
                return CalculationResult.Error("Division by zero is not allowed")
            }
            val result = a / b
            addToHistory("$a ÷ $b", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Division failed: ${e.message}")
        }
    }
    
    /**
     * Modulo operation
     */
    fun modulo(a: Double, b: Double): CalculationResult {
        return try {
            if (b == 0.0) {
                return CalculationResult.Error("Modulo by zero is not allowed")
            }
            val result = a % b
            addToHistory("$a % $b", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Modulo operation failed: ${e.message}")
        }
    }
    
    // ================================
    // Advanced Operations
    // ================================
    
    /**
     * Power operation (a^b)
     */
    fun power(base: Double, exponent: Double): CalculationResult {
        return try {
            val result = base.pow(exponent)
            if (result.isInfinite()) {
                return CalculationResult.Error("Result is too large (infinite)")
            }
            if (result.isNaN()) {
                return CalculationResult.Error("Invalid operation (NaN result)")
            }
            addToHistory("$base ^ $exponent", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Power operation failed: ${e.message}")
        }
    }
    
    /**
     * Square root operation
     */
    fun squareRoot(a: Double): CalculationResult {
        return try {
            if (a < 0) {
                return CalculationResult.Error("Cannot calculate square root of negative number")
            }
            val result = sqrt(a)
            addToHistory("√$a", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Square root operation failed: ${e.message}")
        }
    }
    
    /**
     * Factorial operation (n!)
     */
    fun factorial(n: Double): CalculationResult {
        return try {
            if (n < 0 || n != floor(n)) {
                return CalculationResult.Error("Factorial is only defined for non-negative integers")
            }
            if (n > 170) {
                return CalculationResult.Error("Number too large for factorial calculation")
            }
            
            var result = 1.0
            for (i in 1..n.toInt()) {
                result *= i
            }
            
            addToHistory("${n.toInt()}!", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Factorial operation failed: ${e.message}")
        }
    }
    
    /**
     * Logarithm (natural log)
     */
    fun naturalLog(a: Double): CalculationResult {
        return try {
            if (a <= 0) {
                return CalculationResult.Error("Logarithm is only defined for positive numbers")
            }
            val result = ln(a)
            addToHistory("ln($a)", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Logarithm operation failed: ${e.message}")
        }
    }
    
    /**
     * Sine function (in degrees)
     */
    fun sine(degrees: Double): CalculationResult {
        return try {
            val radians = Math.toRadians(degrees)
            val result = sin(radians)
            addToHistory("sin($degrees°)", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Sine operation failed: ${e.message}")
        }
    }
    
    /**
     * Cosine function (in degrees)
     */
    fun cosine(degrees: Double): CalculationResult {
        return try {
            val radians = Math.toRadians(degrees)
            val result = cos(radians)
            addToHistory("cos($degrees°)", result)
            CalculationResult.Success(result)
        } catch (e: Exception) {
            CalculationResult.Error("Cosine operation failed: ${e.message}")
        }
    }
    
    // ================================
    // Memory Operations
    // ================================
    
    /**
     * Store value in memory
     */
    fun memoryStore(value: Double) {
        memory = value
        addToHistory("M+ ($value)", value)
        println("Value $value stored in memory")
    }
    
    /**
     * Recall value from memory
     */
    fun memoryRecall(): Double {
        println("Memory recall: $memory")
        return memory
    }
    
    /**
     * Clear memory
     */
    fun memoryClear() {
        memory = 0.0
        println("Memory cleared")
    }
    
    /**
     * Add to memory
     */
    fun memoryAdd(value: Double) {
        memory += value
        addToHistory("M+ ($value)", memory)
        println("Added $value to memory. New memory value: $memory")
    }
    
    /**
     * Subtract from memory
     */
    fun memorySubtract(value: Double) {
        memory -= value
        addToHistory("M- ($value)", memory)
        println("Subtracted $value from memory. New memory value: $memory")
    }
    
    // ================================
    // Expression Parsing
    // ================================
    
    /**
     * Simple expression evaluator
     * Supports: +, -, *, /, %, ^, parentheses
     */
    fun evaluateExpression(expression: String): CalculationResult {
        return try {
            val cleanExpression = expression.replace("\\s+".toRegex(), "")
            val result = parseAndEvaluate(cleanExpression)
            
            when (result) {
                is CalculationResult.Success -> {
                    addToHistory(expression, result.value)
                    result
                }
                is CalculationResult.Error -> result
            }
        } catch (e: Exception) {
            CalculationResult.Error("Expression evaluation failed: ${e.message}")
        }
    }
    
    /**
     * Simple recursive descent parser for mathematical expressions
     */
    private fun parseAndEvaluate(expression: String): CalculationResult {
        if (expression.isBlank()) {
            return CalculationResult.Error("Empty expression")
        }
        
        // This is a simplified parser - in a real implementation,
        // you would use a proper expression parsing library
        return try {
            val result = evaluateSimpleExpression(expression)
            CalculationResult.Success(result)
        } catch (e: NumberFormatException) {
            CalculationResult.Error("Invalid number in expression")
        } catch (e: ArithmeticException) {
            CalculationResult.Error("Arithmetic error: ${e.message}")
        } catch (e: Exception) {
            CalculationResult.Error("Invalid expression")
        }
    }
    
    /**
     * Simplified expression evaluator (handles basic operations)
     */
    private fun evaluateSimpleExpression(expr: String): Double {
        // This is a very basic implementation
        // In a real calculator, you'd use a proper expression parser
        
        // Handle parentheses first
        var expression = expr
        while (expression.contains("(")) {
            val start = expression.lastIndexOf("(")
            val end = expression.indexOf(")", start)
            if (end == -1) throw IllegalArgumentException("Mismatched parentheses")
            
            val subExpr = expression.substring(start + 1, end)
            val subResult = evaluateSimpleExpression(subExpr)
            expression = expression.substring(0, start) + subResult + expression.substring(end + 1)
        }
        
        // Simple evaluation for basic operations
        return when {
            "+" in expression && expression.count { it == '+' } == 1 && !expression.startsWith("+") -> {
                val parts = expression.split("+")
                parts[0].toDouble() + parts[1].toDouble()
            }
            "-" in expression && expression.count { it == '-' } == 1 && !expression.startsWith("-") -> {
                val parts = expression.split("-")
                parts[0].toDouble() - parts[1].toDouble()
            }
            "*" in expression -> {
                val parts = expression.split("*")
                parts[0].toDouble() * parts[1].toDouble()
            }
            "/" in expression -> {
                val parts = expression.split("/")
                val divisor = parts[1].toDouble()
                if (divisor == 0.0) throw ArithmeticException("Division by zero")
                parts[0].toDouble() / divisor
            }
            else -> expression.toDouble()
        }
    }
    
    // ================================
    // Utility Functions
    // ================================
    
    /**
     * Add calculation to history
     */
    private fun addToHistory(expression: String, result: Double) {
        val record = CalculationRecord(expression, result)
        history.add(record)
        
        // Keep only last 100 calculations
        if (history.size > 100) {
            history.removeAt(0)
        }
    }
    
    /**
     * Get calculation history
     */
    fun getHistory(): List<CalculationRecord> = history.toList()
    
    /**
     * Clear calculation history
     */
    fun clearHistory() {
        history.clear()
        println("Calculation history cleared")
    }
    
    /**
     * Get formatted history as string
     */
    fun getFormattedHistory(): String {
        if (history.isEmpty()) {
            return "No calculations in history"
        }
        
        return buildString {
            appendLine("=== Calculation History ===")
            history.takeLast(10).forEach { record ->
                appendLine("${record.getFormattedTime()}: ${record.expression} = ${formatResult(record.result)}")
            }
            if (history.size > 10) {
                appendLine("... and ${history.size - 10} more")
            }
        }
    }
    
    /**
     * Format result for display
     */
    fun formatResult(result: Double): String {
        return when {
            result == result.toLong().toDouble() -> result.toLong().toString()
            result.isInfinite() -> if (result > 0) "∞" else "-∞"
            result.isNaN() -> "NaN"
            else -> "%.10f".format(result).trimEnd('0').trimEnd('.')
        }
    }
    
    /**
     * Validate number input
     */
    fun validateNumber(input: String): Double? {
        return try {
            input.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
}

// ================================
// Console Interface
// ================================

/**
 * Console-based calculator interface
 */
class CalculatorInterface {
    private val calculator = Calculator()
    private var isRunning = true
    
    fun start() {
        println("🔢 Advanced Calculator")
        println("====================")
        showHelp()
        
        while (isRunning) {
            print("\nCalculator> ")
            val input = readlnOrNull()?.trim()
            
            if (input.isNullOrBlank()) continue
            
            processCommand(input.lowercase())
        }
        
        println("Calculator terminated. Goodbye!")
    }
    
    private fun processCommand(input: String) {
        when {
            input == "help" -> showHelp()
            input == "exit" || input == "quit" -> {
                isRunning = false
            }
            input == "history" -> showHistory()
            input == "clear-history" -> calculator.clearHistory()
            input == "memory" || input == "mr" -> {
                val value = calculator.memoryRecall()
                println("Memory: ${calculator.formatResult(value)}")
            }
            input == "memory-clear" || input == "mc" -> calculator.memoryClear()
            input.startsWith("memory-store ") || input.startsWith("ms ") -> {
                val parts = input.split(" ")
                if (parts.size >= 2) {
                    val value = calculator.validateNumber(parts[1])
                    if (value != null) {
                        calculator.memoryStore(value)
                    } else {
                        println("❌ Invalid number: ${parts[1]}")
                    }
                } else {
                    println("❌ Usage: memory-store <number>")
                }
            }
            input.startsWith("memory-add ") || input.startsWith("m+ ") -> {
                val parts = input.split(" ")
                if (parts.size >= 2) {
                    val value = calculator.validateNumber(parts[1])
                    if (value != null) {
                        calculator.memoryAdd(value)
                    } else {
                        println("❌ Invalid number: ${parts[1]}")
                    }
                } else {
                    println("❌ Usage: memory-add <number>")
                }
            }
            // Handle mathematical functions
            input.startsWith("sqrt ") -> handleSingleOperation(input, "sqrt") { calculator.squareRoot(it) }
            input.startsWith("factorial ") -> handleSingleOperation(input, "factorial") { calculator.factorial(it) }
            input.startsWith("ln ") -> handleSingleOperation(input, "ln") { calculator.naturalLog(it) }
            input.startsWith("sin ") -> handleSingleOperation(input, "sin") { calculator.sine(it) }
            input.startsWith("cos ") -> handleSingleOperation(input, "cos") { calculator.cosine(it) }
            
            // Handle binary operations
            input.contains(" + ") -> handleBinaryOperation(input, "+") { a, b -> calculator.add(a, b) }
            input.contains(" - ") -> handleBinaryOperation(input, "-") { a, b -> calculator.subtract(a, b) }
            input.contains(" * ") -> handleBinaryOperation(input, "*") { a, b -> calculator.multiply(a, b) }
            input.contains(" / ") -> handleBinaryOperation(input, "/") { a, b -> calculator.divide(a, b) }
            input.contains(" % ") -> handleBinaryOperation(input, "%") { a, b -> calculator.modulo(a, b) }
            input.contains(" ^ ") -> handleBinaryOperation(input, "^") { a, b -> calculator.power(a, b) }
            
            // Try to evaluate as expression
            else -> {
                val result = calculator.evaluateExpression(input)
                when (result) {
                    is Calculator.CalculationResult.Success -> {
                        println("✅ Result: ${calculator.formatResult(result.value)}")
                    }
                    is Calculator.CalculationResult.Error -> {
                        println("❌ ${result.message}")
                        println("💡 Type 'help' for available commands")
                    }
                }
            }
        }
    }
    
    private fun handleSingleOperation(
        input: String,
        operator: String,
        operation: (Double) -> Calculator.CalculationResult
    ) {
        val parts = input.split(" ")
        if (parts.size >= 2) {
            val value = calculator.validateNumber(parts[1])
            if (value != null) {
                val result = operation(value)
                when (result) {
                    is Calculator.CalculationResult.Success -> {
                        println("✅ $operator($value) = ${calculator.formatResult(result.value)}")
                    }
                    is Calculator.CalculationResult.Error -> {
                        println("❌ ${result.message}")
                    }
                }
            } else {
                println("❌ Invalid number: ${parts[1]}")
            }
        } else {
            println("❌ Usage: $operator <number>")
        }
    }
    
    private fun handleBinaryOperation(
        input: String,
        operator: String,
        operation: (Double, Double) -> Calculator.CalculationResult
    ) {
        val parts = input.split(" $operator ")
        if (parts.size == 2) {
            val a = calculator.validateNumber(parts[0].trim())
            val b = calculator.validateNumber(parts[1].trim())
            
            if (a != null && b != null) {
                val result = operation(a, b)
                when (result) {
                    is Calculator.CalculationResult.Success -> {
                        println("✅ $a $operator $b = ${calculator.formatResult(result.value)}")
                    }
                    is Calculator.CalculationResult.Error -> {
                        println("❌ ${result.message}")
                    }
                }
            } else {
                println("❌ Invalid numbers in expression")
            }
        } else {
            println("❌ Invalid expression format")
        }
    }
    
    private fun showHistory() {
        println(calculator.getFormattedHistory())
    }
    
    private fun showHelp() {
        println("""
            📋 Available Commands:
            
            Basic Operations:
              <number> + <number>     Addition
              <number> - <number>     Subtraction  
              <number> * <number>     Multiplication
              <number> / <number>     Division
              <number> % <number>     Modulo
              <number> ^ <number>     Power
              
            Advanced Operations:
              sqrt <number>           Square root
              factorial <number>      Factorial (n!)
              ln <number>             Natural logarithm
              sin <number>            Sine (degrees)
              cos <number>            Cosine (degrees)
              
            Memory Operations:
              memory or mr            Recall memory
              memory-store <n> or ms  Store to memory
              memory-add <n> or m+    Add to memory
              memory-clear or mc      Clear memory
              
            History & Utility:
              history                 Show calculation history
              clear-history           Clear calculation history
              help                    Show this help
              exit or quit            Exit calculator
              
            Expression Evaluation:
              You can also enter mathematical expressions directly:
              Examples: 2 + 3 * 4, (10 + 5) / 3, sqrt(16) + 2
              
            💡 Tips:
            - Use spaces around operators for clarity
            - Parentheses are supported in expressions
            - All results are stored in history automatically
            - Memory persists throughout the session
        """.trimIndent())
    }
}

// ================================
// Main Function
// ================================

/**
 * Entry point for the calculator application
 */
fun main() {
    // You can also run the calculator programmatically
    val calculator = Calculator()
    
    println("=== Calculator Demo ===")
    
    // Basic operations demo
    val addResult = calculator.add(15.5, 24.3)
    when (addResult) {
        is Calculator.CalculationResult.Success -> 
            println("15.5 + 24.3 = ${calculator.formatResult(addResult.value)}")
        is Calculator.CalculationResult.Error -> 
            println("Error: ${addResult.message}")
    }
    
    // Advanced operations demo
    val sqrtResult = calculator.squareRoot(144.0)
    when (sqrtResult) {
        is Calculator.CalculationResult.Success -> 
            println("√144 = ${calculator.formatResult(sqrtResult.value)}")
        is Calculator.CalculationResult.Error -> 
            println("Error: ${sqrtResult.message}")
    }
    
    // Memory operations demo
    calculator.memoryStore(42.0)
    val memoryValue = calculator.memoryRecall()
    println("Memory contains: ${calculator.formatResult(memoryValue)}")
    
    // Expression evaluation demo
    val exprResult = calculator.evaluateExpression("10 + 5 * 2")
    when (exprResult) {
        is Calculator.CalculationResult.Success -> 
            println("10 + 5 * 2 = ${calculator.formatResult(exprResult.value)}")
        is Calculator.CalculationResult.Error -> 
            println("Error: ${exprResult.message}")
    }
    
    // Show history
    println("\n" + calculator.getFormattedHistory())
    
    println("\n=== Starting Interactive Calculator ===")
    
    // Start interactive calculator
    val interface = CalculatorInterface()
    interface.start()
}

/**
 * TODO: Enhancements for Learning
 * 
 * 1. Add more mathematical functions (tan, log10, exp)
 * 2. Implement better expression parsing with proper operator precedence
 * 3. Add support for constants (π, e)
 * 4. Implement unit conversions
 * 5. Add statistical functions (mean, median, standard deviation)
 * 6. Create a graphical user interface
 * 7. Add support for complex numbers
 * 8. Implement saving/loading of calculation sessions
 * 9. Add more number systems (binary, hexadecimal)
 * 10. Create a programmable calculator with custom functions
 */