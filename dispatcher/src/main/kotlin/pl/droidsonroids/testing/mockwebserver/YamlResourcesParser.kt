package pl.droidsonroids.testing.mockwebserver

import org.yaml.snakeyaml.Yaml

internal class YamlResourcesParser : ResourcesParser {
    private val parser = Yaml()

    override fun parseFrom(fileName: String): Fixture {
        val path = "fixtures/$fileName.yaml"
        val content = path.getResourceAsString()
        val result = parser.loadAs(content, Fixture::class.java)

        if (!result.hasJsonBody()) {
            val bodyPath = "fixtures/${result.body}"
            result.body = bodyPath.getResourceAsString()
        }
        return result
    }
}