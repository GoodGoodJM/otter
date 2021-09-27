plugins {
    maven apply true
    signing apply true
    java apply true
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
                            "name"("otter-core")
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
