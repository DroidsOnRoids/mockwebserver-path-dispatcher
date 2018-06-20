package pl.droidsonroids.testing.mockwebserver

import org.apache.commons.text.translate.JavaUnicodeEscaper
import org.yaml.snakeyaml.Yaml

internal class YamlResourcesParser : ResourcesParser {
    private val escaper = JavaUnicodeEscaper.above(0xD800)
    private val parser = Yaml()

    override fun parseFrom(fileName: String): Fixture {
        val path = "fixtures/$fileName.yaml"
        val content = path.getResourceAsString()
        val escapedContent = escaper.translate(content)
        val result = parser.loadAs(escapedContent, Fixture::class.java)

        if (!result.hasJsonBody()) {
            val bodyPath = "fixtures/${result.body}"
            result.body = bodyPath.getResourceAsString()
        }
        return result
    }
}