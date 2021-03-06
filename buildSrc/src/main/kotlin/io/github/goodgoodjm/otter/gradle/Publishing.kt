@file:Suppress("UnstableApiUsage")

package io.github.goodgoodjm.otter.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.gradle.api.plugins.JavaPluginExtension

infix fun <T> Property<T>.by(value: T) {
    set(value)
}

fun MavenPom.configureMavenCentralMetadata(project: Project) {
    name by project.name
    description by "Otter"
    url by "https://github.com/GoodGoodJM/Otter"

    licenses {
        license {
            name by "The Apache Software License, Version 2.0"
            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
            distribution by "repo"
        }
    }

    developers {
        developer {
            id by "goodgoodman"
            name by "goodgoodman"
        }
    }

    scm {
        url by "https://github.com/GoodGoodJM/Otter"
        connection by "scm:git:git://github.com/GoodGoodJM/Otter.git"
        developerConnection by "scm:git:git@github.com:GoodGoodJM/Otter.git"
    }
}

fun MavenPublication.signPublicationIfKeyPresent(project: Project) {
    val keyId = System.getenv("otter.sign.key.id")
    val signingKey = System.getenv("otter.sign.key.private")
    val signingKeyPassword = System.getenv("otter.sign.key.password")
    if (!signingKey.isNullOrBlank()) {
        project.extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(keyId, signingKey.replace(" ", "\r\n"), signingKeyPassword)
            sign(this@signPublicationIfKeyPresent)
        }
    }
}

fun Project._publishing(configure: PublishingExtension.() -> Unit) {
    extensions.configure("publishing", configure)
}

fun Project._java(configure: JavaPluginExtension.() -> Unit) {
    extensions.configure("java", configure)
}