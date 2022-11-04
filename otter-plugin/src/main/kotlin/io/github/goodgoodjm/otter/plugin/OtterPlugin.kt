package io.github.goodgoodjm.otter.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


open class OtterPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("asd")
        target.task("hello") {
            println("wow")
        }
    }
}