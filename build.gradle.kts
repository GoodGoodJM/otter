plugins {
    base
    kotlin("jvm") version "1.5.0"
    maven
}

allprojects {
    group = "com.goodgoodman"
    version = "1.0.0"

    repositories {
        mavenCentral()
        jcenter()
    }
}