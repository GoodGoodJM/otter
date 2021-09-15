plugins {
    base
    kotlin("jvm") version "1.5.21"
    maven
}

repositories {
    mavenCentral()
}

allprojects {
    group = "com.goodgoodman"
    version = "1.0.0"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}