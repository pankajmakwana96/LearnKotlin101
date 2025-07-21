plugins {
    kotlin("jvm") version "1.9.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
    id("org.jetbrains.dokka") version "1.9.0"
    application
}

group = "com.kotlinmastery"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.assertj:assertj-core:3.24.2")
    
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjsr305=strict",
            "-Xcontext-receivers",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }
}

application {
    mainClass.set("com.kotlinmastery.MainKt")
}

detekt {
    toolVersion = "1.23.1"
    config.setFrom(files("$projectDir/detekt-config.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

ktlint {
    version.set("0.50.0")
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}