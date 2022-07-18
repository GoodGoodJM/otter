plugins {
    kotlin("jvm") apply true
    id("io.github.gradle-nexus.publish-plugin") apply true
}

allprojects {
    if(this != rootProject) {
        apply(from = rootProject.file("buildScripts/gradle/publishing.gradle.kts"))
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("otter.sonatype.user"))
            password.set(System.getenv("otter.sonatype.password"))
            useStaging.set(true)
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}