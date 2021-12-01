plugins {
  id("org.jetbrains.intellij") version "1.3.0"
  kotlin("jvm") version "1.6.0"
}

group = "com.r3bl.plugins"
version = "1.8"

repositories {
  mavenCentral()
  // Add color-console library.
  maven{
    url = uri("https://jitpack.io")
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  // Add color-console library.
  implementation("com.github.nazmulidris:color-console:1.0.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
  // Information on IJ versions https://www.jetbrains.org/intellij/sdk/docs/reference_guide/intellij_artifacts.html
  // You can use release build numbers or snapshot name for the version.
  // 1) IJ Release Repository w/ build numbers https://www.jetbrains.com/intellij-repository/releases/
  // 2) IJ Snapshots Repository w/ snapshot names https://www.jetbrains.com/intellij-repository/snapshots/
  version.set("2021.3") // You can also use LATEST-EAP-SNAPSHOT here.

  // Declare a dependency on the markdown plugin to be able to access the
  // MarkdownRecursiveElementVisitor.kt file. More info:
  // https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
  // https://plugins.jetbrains.com/plugin/7793-markdown/versions
  plugins.set(listOf("java", "markdown"))
}

tasks {
  buildSearchableOptions {
    enabled = false
  }
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "16"
    kotlinOptions.languageVersion = "1.6"
    kotlinOptions.apiVersion = "1.6"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "16"
    kotlinOptions.languageVersion = "1.6"
    kotlinOptions.apiVersion = "1.6"
  }
}

// Testing with JUnit4 and AssertJ.
// - To run tests from the command line, simply run: `./gradlew build test --info`
// - JUnit4 config info: https://docs.gradle.org/current/samples/sample_java_components_with_junit4_tests.html
// - No need to specify `sourceSets` for `test` since this project uses the default folder layout for gradle.
dependencies {
  testImplementation("org.assertj:assertj-core:3.21.0")
  testImplementation("junit:junit:4.13.2")
}

// JetBrains Marketplace related.
tasks.publishPlugin {
  // The property named "intellijPublishToken" is loaded from gradle.properties file.
  val intellijPublishToken: String? by project
  token.set(intellijPublishToken)
  // The following line does the same thing as above:
  // token(project.findProperty(intellijPublishToken) as String?)
}

tasks {
  runPluginVerifier {
    ideVersions.set(listOf("2020.2.4", "2020.3.4", "2021.1.1", "2021.3"))
  }
}