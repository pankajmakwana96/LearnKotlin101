#!/bin/bash

# Kotlin Mastery Project Runner
# This script allows you to compile and run individual Kotlin files or examples

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_colored() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

print_usage() {
    echo "🚀 Kotlin Mastery Project Runner"
    echo ""
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  basics          - Run basic Kotlin examples"
    echo "  intermediate    - Run intermediate Kotlin examples"
    echo "  advanced        - Run advanced Kotlin examples"
    echo "  projects        - Run project examples"
    echo "  exercises       - Run exercise examples"
    echo "  koans          - Run Kotlin Koans"
    echo "  calculator     - Run the calculator project"
    echo "  tests          - Run unit tests"
    echo "  compile        - Compile all Kotlin files"
    echo "  clean          - Clean compiled files"
    echo "  help           - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 koans                    # Run Kotlin Koans interactive exercises"
    echo "  $0 calculator               # Run the calculator project"
    echo "  $0 basics                   # Run basic syntax examples"
    echo "  $0 tests                    # Run all unit tests"
    echo ""
}

# Check if Java is installed
check_java() {
    if ! command -v java &> /dev/null; then
        print_colored $RED "❌ Java is not installed or not in PATH"
        print_colored $YELLOW "Please install Java 8 or higher to run Kotlin programs"
        print_colored $BLUE "Visit: https://adoptium.net/ to download Java"
        exit 1
    fi
    
    print_colored $GREEN "✅ Java found: $(java -version 2>&1 | head -n 1)"
}

# Check if Kotlin compiler is available
check_kotlin() {
    if command -v kotlinc &> /dev/null; then
        print_colored $GREEN "✅ Kotlin compiler found: $(kotlinc -version 2>&1)"
        return 0
    else
        print_colored $YELLOW "⚠️  Kotlin compiler not found in PATH"
        print_colored $BLUE "Will try to use Gradle to compile and run"
        return 1
    fi
}

# Compile and run a single Kotlin file
run_kotlin_file() {
    local file_path=$1
    local class_name=$2
    
    if [ ! -f "$file_path" ]; then
        print_colored $RED "❌ File not found: $file_path"
        return 1
    fi
    
    print_colored $BLUE "🔨 Compiling: $file_path"
    
    if command -v kotlinc &> /dev/null; then
        # Use kotlinc if available
        kotlinc "$file_path" -include-runtime -d "${file_path%.kt}.jar"
        if [ $? -eq 0 ]; then
            print_colored $GREEN "✅ Compilation successful"
            print_colored $BLUE "🚀 Running: $class_name"
            echo "----------------------------------------"
            java -jar "${file_path%.kt}.jar"
            echo "----------------------------------------"
            rm -f "${file_path%.kt}.jar"  # Clean up
        else
            print_colored $RED "❌ Compilation failed"
            return 1
        fi
    else
        print_colored $RED "❌ Cannot compile without Kotlin compiler"
        print_colored $YELLOW "Please install Kotlin compiler or use an IDE like IntelliJ IDEA"
        return 1
    fi
}

# Run Gradle tasks
run_gradle_task() {
    local task=$1
    print_colored $BLUE "🔨 Running Gradle task: $task"
    
    if [ -f "./gradlew" ]; then
        ./gradlew $task
    else
        print_colored $RED "❌ Gradle wrapper not found"
        return 1
    fi
}

# Main command dispatcher
case "${1:-help}" in
    "basics")
        print_colored $BLUE "🎯 Running Basic Kotlin Examples"
        echo ""
        
        examples=(
            "basics/syntax/BasicSyntax.kt:BasicSyntaxKt"
            "basics/variables/Variables.kt:VariablesKt" 
            "basics/functions/Functions.kt:FunctionsKt"
            "basics/classes/Classes.kt:ClassesKt"
        )
        
        for example in "${examples[@]}"; do
            file_path="${example%:*}"
            class_name="${example#*:}"
            if [ -f "$file_path" ]; then
                run_kotlin_file "$file_path" "$class_name"
                echo ""
            fi
        done
        ;;
        
    "intermediate")
        print_colored $BLUE "🎯 Running Intermediate Kotlin Examples"
        echo ""
        
        examples=(
            "intermediate/collections/Collections.kt:CollectionsKt"
            "intermediate/functions/HigherOrderFunctions.kt:HigherOrderFunctionsKt"
            "intermediate/generics/Generics.kt:GenericsKt"
        )
        
        for example in "${examples[@]}"; do
            file_path="${example%:*}"
            class_name="${example#*:}"
            if [ -f "$file_path" ]; then
                run_kotlin_file "$file_path" "$class_name"
                echo ""
            fi
        done
        ;;
        
    "advanced")
        print_colored $BLUE "🎯 Running Advanced Kotlin Examples"
        echo ""
        
        examples=(
            "advanced/coroutines/Coroutines.kt:CoroutinesKt"
            "advanced/type-system/AdvancedTypes.kt:AdvancedTypesKt"
        )
        
        for example in "${examples[@]}"; do
            file_path="${example%:*}"
            class_name="${example#*:}"
            if [ -f "$file_path" ]; then
                run_kotlin_file "$file_path" "$class_name"
                echo ""
            fi
        done
        ;;
        
    "koans")
        print_colored $BLUE "🎓 Running Kotlin Koans"
        run_kotlin_file "exercises/kotlin-koans/Introduction.kt" "IntroductionKt"
        ;;
        
    "calculator")
        print_colored $BLUE "🧮 Running Calculator Project"
        run_kotlin_file "projects/beginner/Calculator.kt" "CalculatorKt"
        ;;
        
    "tests")
        print_colored $BLUE "🧪 Running Unit Tests"
        check_java
        run_gradle_task "test"
        ;;
        
    "compile")
        print_colored $BLUE "🔨 Compiling Project"
        check_java
        run_gradle_task "compileKotlin"
        ;;
        
    "clean")
        print_colored $BLUE "🧹 Cleaning Project"
        run_gradle_task "clean"
        # Also clean any .jar files created by direct compilation
        find . -name "*.jar" -not -path "./gradle/*" -delete
        print_colored $GREEN "✅ Project cleaned"
        ;;
        
    "projects")
        print_colored $BLUE "📁 Available Projects:"
        echo ""
        echo "Beginner Projects:"
        echo "  • Calculator - Advanced calculator with memory, history, and expression parsing"
        echo ""
        echo "Advanced Projects:"
        echo "  • Task Management System - Comprehensive project management tool"
        echo ""
        echo "Run: $0 calculator"
        ;;
        
    "exercises")
        print_colored $BLUE "💪 Available Exercises:"
        echo ""
        echo "• Kotlin Koans - Interactive guided exercises"
        echo "• Beginner Exercises - Basic programming challenges"
        echo "• Intermediate Exercises - More complex problems"
        echo "• Advanced Exercises - Expert-level challenges"
        echo ""
        echo "Run: $0 koans"
        ;;
        
    "help"|"-h"|"--help")
        print_usage
        ;;
        
    *)
        print_colored $RED "❌ Unknown command: $1"
        echo ""
        print_usage
        exit 1
        ;;
esac