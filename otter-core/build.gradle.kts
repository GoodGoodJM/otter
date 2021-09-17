plugins {
    kotlin("jvm")
}

val exposedVersion: String by project
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

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("ch.qos.logback:logback-classic:1.2.5")
}
