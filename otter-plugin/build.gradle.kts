plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.0.0"
}

version = "0.0.0"
group = "io.github.goodgoodjm.otter.plugin"

gradlePlugin {
    plugins {
        register("otterPlugin") {
            id = "io.github.goodgoodjm.otter.plugin"
            implementationClass = "io.github.goodgoodjm.otter.plugin.OtterPlugin"
            displayName = "Otter Plugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/GoodGoodJM/Otter"
    vcsUrl = "https://github.com/GoodGoodJM/Otter.git"
    tags = listOf("kotlin")
    description = "Otter Plugin is a Gradle Plugin for Otter DBMigration tool."
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
