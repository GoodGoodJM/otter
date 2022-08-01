plugins {
    kotlin("jvm")
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("otterPlugin") {
            id = "io.github.goodgoodjm.otter.plugin"
            implementationClass = "io.github.goodgoodjm.otter.plugin.OtterPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
