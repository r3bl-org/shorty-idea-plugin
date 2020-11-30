import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    kotlin("jvm") version "1.4.20"
}

group = "com.r3bl.plugins"
version = "1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    // Information on IJ versions https://www.jetbrains.org/intellij/sdk/docs/reference_guide/intellij_artifacts.html
    // You can use release build numbers or snapshot name for the version.
    // 1) IJ Release Repository w/ build numbers https://www.jetbrains.com/intellij-repository/releases/
    // 2) IJ Snapshots Repository w/ snapshot names https://www.jetbrains.com/intellij-repository/snapshots/
    version = "203.5981-EAP-CANDIDATE-SNAPSHOT" // You can also use LATEST-EAP-SNAPSHOT here.

    // Declare a dependency on the markdown plugin to be able to access the
    // MarkdownRecursiveElementVisitor.kt file. More info:
    // https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
    // https://plugins.jetbrains.com/plugin/7793-markdown/versions
    setPlugins("java", "markdown")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.languageVersion = "1.4"
        kotlinOptions.apiVersion = "1.4"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.languageVersion = "1.4"
        kotlinOptions.apiVersion = "1.4"
    }
}

// Testing with JUnit4 and AssertJ.
// - To run tests from the command line, simply run: `./gradlew build test --info`
// - JUnit4 config info: https://docs.gradle.org/current/samples/sample_java_components_with_junit4_tests.html
// - No need to specify `sourceSets` for `test` since this project uses the default folder layout for gradle.
dependencies {
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("junit:junit:4.13")
}

// JetBrains Marketplace related.
tasks.publishPlugin {
    // The property named "intellijPublishToken" is loaded from gradle.properties file.
    val intellijPublishToken: String? by project
    token(intellijPublishToken)
    // The following line does the same thing as above:
    // token(project.findProperty(intellijPublishToken) as String?)
}

tasks {
    runPluginVerifier {
        ideVersions(listOf<String>("2020.1.4", "2020.2.3", "2020.3"))
    }
}

// Add color-console library.
repositories {
    jcenter()
}

dependencies {
    implementation("com.developerlife:color-console:1.0")
}