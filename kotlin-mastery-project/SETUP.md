# Kotlin Mastery Project - Setup Guide

Welcome to the Complete Kotlin Learning Project! This guide will help you set up your environment and start learning Kotlin.

## 🚀 Quick Start

### Option 1: Using the Runner Script (Recommended)
```bash
# Make the runner script executable (if not already)
chmod +x run.sh

# Run Kotlin Koans (interactive exercises)
./run.sh koans

# Run the calculator project
./run.sh calculator

# Run basic examples
./run.sh basics

# See all available commands
./run.sh help
```

### Option 2: Using an IDE (Best for Learning)
1. **Install IntelliJ IDEA Community Edition** (Free)
   - Download from: https://www.jetbrains.com/idea/download/
   - IntelliJ IDEA has excellent Kotlin support built-in

2. **Open the project**
   - File → Open → Select this directory
   - IntelliJ will automatically configure the Kotlin project

3. **Run examples**
   - Navigate to any `.kt` file
   - Click the green ▶️ button next to the `main()` function

### Option 3: Command Line with Kotlin Compiler
```bash
# Install Kotlin compiler
# macOS with Homebrew:
brew install kotlin

# Or download from: https://kotlinlang.org/docs/command-line.html

# Compile and run a file
kotlinc basics/syntax/BasicSyntax.kt -include-runtime -d BasicSyntax.jar
java -jar BasicSyntax.jar
```

## 📋 Prerequisites

### Required
- **Java 8 or higher** - Kotlin runs on the JVM
  - Check: `java -version`
  - Download: https://adoptium.net/

### Optional (for different approaches)
- **Kotlin Compiler** - For command-line compilation
  - Install via package manager or from kotlinlang.org
- **Gradle** - Build automation (wrapper included)
- **Git** - Version control (recommended)

## 🎯 Learning Path

### 1. Start with Kotlin Koans (Recommended for Beginners)
```bash
./run.sh koans
```
Interactive exercises that teach Kotlin fundamentals step by step.

### 2. Explore Basic Concepts
```bash
./run.sh basics
```
- Variables and data types
- Control flow
- Functions
- Classes and objects

### 3. Work on the Calculator Project
```bash
./run.sh calculator
```
A hands-on project that demonstrates real-world Kotlin usage.

### 4. Advance to Intermediate Topics
```bash
./run.sh intermediate
```
- Collections
- Higher-order functions
- Generics

### 5. Master Advanced Features
```bash
./run.sh advanced
```
- Coroutines
- Advanced type system
- Metaprogramming

## 📁 Project Structure

```
kotlin-mastery-project/
├── basics/                 # Basic Kotlin concepts
│   ├── syntax/            # Basic syntax and variables
│   ├── control-flow/      # If, when, loops
│   ├── functions/         # Function declarations and types
│   └── classes/           # OOP concepts
├── intermediate/          # Intermediate concepts
│   ├── collections/       # Lists, maps, sets
│   ├── functions/         # Higher-order functions
│   └── generics/          # Generic programming
├── advanced/              # Advanced concepts
│   ├── coroutines/        # Asynchronous programming
│   ├── type-system/       # Advanced types
│   └── metaprogramming/   # Reflection and annotations
├── projects/              # Complete projects
│   ├── beginner/          # Calculator project
│   └── advanced/          # Task management system
├── exercises/             # Practice exercises
│   ├── kotlin-koans/      # Interactive guided exercises
│   ├── beginner/          # Basic programming challenges
│   ├── intermediate/      # More complex problems
│   └── advanced/          # Expert-level challenges
├── docs/                  # Documentation
│   ├── README.md          # Project overview
│   ├── learning-path.md   # Structured curriculum
│   └── resources.md       # Additional resources
└── src/test/kotlin/       # Unit tests
```

## 🔧 IDE Setup Instructions

### IntelliJ IDEA (Recommended)
1. **Download and Install**
   - Get IntelliJ IDEA Community Edition (free)
   - https://www.jetbrains.com/idea/download/

2. **Open Project**
   - File → Open → Select `kotlin-mastery-project` folder
   - Wait for Gradle sync to complete

3. **Run Examples**
   - Open any `.kt` file with a `main()` function
   - Click the ▶️ button in the gutter
   - Or right-click and select "Run"

### Visual Studio Code
1. **Install Extensions**
   - Kotlin Language extension
   - Code Runner extension

2. **Open Project**
   - File → Open Folder → Select `kotlin-mastery-project`

3. **Run Files**
   - Open a `.kt` file
   - Use Ctrl+F5 to run

### Android Studio
1. **Open Project**
   - File → Open → Select `kotlin-mastery-project`
   - Choose "Open as Gradle Project"

## 🛠 Troubleshooting

### Common Issues

**"Java not found"**
- Install Java 8 or higher
- Verify with `java -version`
- Set JAVA_HOME environment variable if needed

**"Kotlin compiler not found"**
- Install Kotlin compiler for command-line usage
- Or use IntelliJ IDEA which includes Kotlin support

**"Permission denied" when running ./run.sh**
```bash
chmod +x run.sh
```

**Gradle build fails**
- Ensure Java is installed and in PATH
- Try running `./gradlew clean build`

### Getting Help
- Check the documentation in the `docs/` folder
- Review the comprehensive examples
- Use IntelliJ IDEA's built-in help for Kotlin

## 🎓 Learning Tips

1. **Start Small** - Begin with Kotlin Koans for guided learning
2. **Practice Regularly** - Code a little bit every day
3. **Build Projects** - Apply concepts in real projects
4. **Read Code** - Study the provided examples
5. **Use IDE Features** - IntelliJ offers excellent Kotlin tooling
6. **Join Community** - Kotlin has an active community on forums and Discord

## 📚 Next Steps After Setup

1. **Run Kotlin Koans**: `./run.sh koans`
2. **Complete the Calculator Project**: `./run.sh calculator`
3. **Follow the Learning Path**: See `docs/learning-path.md`
4. **Practice with Exercises**: Solve problems in the `exercises/` folder
5. **Build Your Own Projects**: Apply what you've learned

## 🔗 Useful Links

- **Kotlin Official Site**: https://kotlinlang.org/
- **Kotlin Documentation**: https://kotlinlang.org/docs/
- **Kotlin Playground**: https://play.kotlinlang.org/
- **IntelliJ IDEA**: https://www.jetbrains.com/idea/
- **Kotlin Community**: https://kotlinlang.org/community/

Happy coding with Kotlin! 🚀