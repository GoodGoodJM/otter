package io.github.goodgoodjm.otter.core.resourceresolver

import io.github.goodgoodjm.otter.core.resourceresolver.exception.ResourceLoadException
import java.io.File
import java.net.JarURLConnection

class ResourceResolver {
    private val classLoader: ClassLoader = this::class.java.classLoader
    fun resolveEntries(path: String): List<String> {
        val url = classLoader.getResource(path) ?: throw ResourceLoadException("Resource($path) does not exist.")
        return when (url.protocol) {
            "file" -> File(url.file).list()!!.map { "$path/$it" }
            "jar" -> (url.openConnection() as JarURLConnection).jarFile.use { jar ->
                jar.entries().asSequence()
                    .filter { it.name.startsWith(path) && it.name.endsWith(".kts") }
                    .map { it.name }
                    .toList()
            }
            else -> throw ResourceLoadException("Protocol(${url.protocol}) does not support.")
        }
    }
}