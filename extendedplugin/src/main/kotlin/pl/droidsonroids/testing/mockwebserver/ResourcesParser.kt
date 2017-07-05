package pl.droidsonroids.testing.mockwebserver

import java.lang.IllegalStateException

internal open class ResourcesParser {
    private val parser = org.yaml.snakeyaml.Yaml()

    open fun parseFrom(fileName: String): Fixture {
        val path = "fixtures/$fileName.yaml"
        val content = path.getResourceAsString()
        val result = parser.loadAs(content, Fixture::class.java)

        if (!result.hasJsonBody()) {
            val bodyPath = "fixtures/${result.body}"
            result.body = bodyPath.getResourceAsString()
        }
        return result
    }

    private fun String.getResourceAsString(): String {
        val loader = Thread.currentThread().contextClassLoader
        return loader.getResource(this)?.readText() ?: throw IllegalStateException("Invalid path: $this")
    }
}