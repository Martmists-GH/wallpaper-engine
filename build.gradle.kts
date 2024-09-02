import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.martmists.commons.isStable
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.github.ben-manes.versions")
}

group = "com.martmists"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.martmists.com/snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")

    implementation("com.dorkbox:SystemTray:${Versions.systemTray}:all")  // From my own Maven repository to get v4.5

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

compose {
    desktop {
        application {
            mainClass = "com.martmists.wallpaperengine.MainKt"

            nativeDistributions {
                targetFormats(TargetFormat.AppImage)
                packageName = "Wallpaper Engine"
                packageVersion = project.version as String
            }
        }
    }
}

tasks {
    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isStable(currentVersion) && !isStable(candidate.version)
        }
    }
}

