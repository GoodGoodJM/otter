plugins {
    kotlin("jvm") version "1.4.32"
}

group = "org.goodgoodman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("test"))
    implementation(kotlin("test-junit"))
    implementation("org.slf4j", "slf4j-api", "1.7.30")
}
