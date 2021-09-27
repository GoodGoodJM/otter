plugins {
    maven apply true
    signing apply true
    java apply true
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    api(project(":otter-core"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(kotlin("script-runtime"))
    testImplementation(kotlin("scripting-jsr223"))

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("ch.qos.logback:logback-classic:1.2.5")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
tasks.withType<org.gradle.jvm.tasks.Jar> { enabled = true }


val ossrhUsername: String by project
val ossrhPassword: String by project

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }

    getByName<Upload>("uploadArchives") {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenDeployer {
                    beforeDeployment { signing.signPom(this) }
                    withGroovyBuilder {
                        "repository"("url" to uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")) {
                            "authentication"("userName" to ossrhUsername, "password" to ossrhPassword)
                        }
                        "snapshotRepository"("url" to uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")) {
                            "authentication"("userName" to ossrhUsername, "password" to ossrhPassword)
                        }
                    }

                    pom.project {
                        withGroovyBuilder {
                            "name"("otter-spring-boot-starter")
                            "description"("Otter, DB Migration tools for kotlin")
                            "url"("https://github.com/goodgoodjm/otter")
                            "scm" {
                                "connection"("scm:git:git://github.com/goodgoodjm/otter.git")
                                "developerConnection"("scm:git:ssh://git@github.com:goodgoodjm/otter.git")
                                "url"("https://github.com/goodgoodjm/otter")
                            }
                            "licenses" {
                                "license" {
                                    "name"("The Apache Software License, Version 2.0")
                                    "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                    "distribution"("repo")
                                }
                            }
                            "developers" {
                                "developer" {
                                    "id"("goodgoodman")
                                    "name"("goodgoodman")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

signing {
    sign(configurations.archives.get())
}