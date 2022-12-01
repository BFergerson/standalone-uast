import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://www.jetbrains.com/intellij-repository/releases") { name = "intellij-releases" }
    maven(url = "https://cache-redirector.jetbrains.com/intellij-dependencies/") { name = "intellij-dependencies" }
}

dependencies {
    implementation(files("libs/kotlin-plugin.jar"))
    implementation(files("libs/kotlinc_kotlin-compiler-common.jar"))
    implementation(files("libs/kotlinc_kotlin-compiler-fe10.jar"))
    implementation(files("libs/kotlinc_kotlin-jps-common.jar"))

    val intellijVersion = "222.4459.24"
    implementation("com.jetbrains.intellij.platform:core:$intellijVersion")
    implementation("com.jetbrains.intellij.platform:core-impl:$intellijVersion")
    implementation("com.jetbrains.intellij.platform:lang:$intellijVersion")
    implementation("com.jetbrains.intellij.platform:lang-impl:$intellijVersion") {
        exclude(group = "org.jetbrains.pty4j", module = "pty4j")
    }
    implementation("com.jetbrains.intellij.java:java:$intellijVersion")
    implementation("com.jetbrains.intellij.java:java-impl:$intellijVersion")
    implementation("com.jetbrains.intellij.java:java-psi:$intellijVersion")
    implementation("com.jetbrains.intellij.java:java-uast:$intellijVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}