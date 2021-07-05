plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("test"))
    implementation(kotlin("test-junit"))
    implementation(kotlin("reflect"))

    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-jsr223"))
    implementation("org.slf4j", "slf4j-api", "1.7.30")
}
