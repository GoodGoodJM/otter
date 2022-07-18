import io.github.goodgoodjm.otter.gradle.*

apply(plugin = "java-library")
apply(plugin = "maven-publish")
apply(plugin = "signing")

_java {
    withJavadocJar()
    withSourcesJar()
}

_publishing {
    publications {
        create<MavenPublication>("OtterJars") {
            artifactId = project.name
            from(project.components["java"])
            pom {
                configureMavenCentralMetadata(project)
            }
            signPublicationIfKeyPresent(project)
        }
    }
}
