import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.martmists.commons.isStable

plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.github.ben-manes.versions")
}

group = "com.martmists"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")

    implementation("com.dorkbox:SystemTray:${Versions.systemTray}:all")
//    implementation("com.dorkbox:OS:1.11")  // Until SystemTray updates

    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.animation)
    implementation(compose.animationGraphics)
    implementation(compose.ui)
    implementation("org.jetbrains.compose.components:components-animatedimage:${Versions.compose}")
    implementation(compose.uiTooling) {
        exclude(group="org.jetbrains.compose.material", module="material")
    }
    runtimeOnly(compose.desktop.currentOs) {
        exclude(group="org.jetbrains.compose.material", module="material")
    }
}

tasks {
    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isStable(currentVersion) && !isStable(candidate.version)
        }
    }
}

