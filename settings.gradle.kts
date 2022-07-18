rootProject.name = "otter"
include("otter-spring-boot-starter")
include("otter-core")

pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.6.20"
    }
}