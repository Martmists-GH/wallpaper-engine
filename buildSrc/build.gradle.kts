plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.martmists.com/releases")
}

dependencies {
    implementation(kotlin("gradle-plugin", "2.0.20"))
    implementation(kotlin("serialization", "2.0.20"))
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.0.20")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.51.0")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.6.11")
    implementation("com.martmists.commons:commons-gradle:1.0.4")
}
