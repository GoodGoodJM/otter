plugins {
    base
    kotlin("jvm") version "1.4.32"
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