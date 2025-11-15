pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    
    plugins {
        kotlin("jvm").version("2.1.0")
        id("org.jetbrains.kotlin.plugin.compose").version("2.1.0")
        id("org.jetbrains.compose").version("1.8.0")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "Lab3"

