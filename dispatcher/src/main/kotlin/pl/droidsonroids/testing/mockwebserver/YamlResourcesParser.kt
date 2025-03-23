package pl.droidsonroids.testing.mockwebserver

import org.apache.commons.text.translate.JavaUnicodeEscaper
import org.yaml.snakeyaml.Yaml

internal class YamlResourcesParser : ResourcesParser {
    private val escaper = JavaUnicodeEscaper.above(0xD800)

    override fun parseFrom(fileName: String): Fixture {
        val path = "fixtures/$fileName.yaml"
        val content = path.getResourceAsString()
        val escapedContent = escaper.translate(content)
        val result = Yaml().loadAs(escapedContent, Fixture::class.java)

        if (!result.hasJsonBody()) {
            if (result.body != null) {
                val bodyPath = "fixtures/${result.body}"
                result.bodyContent =
                    when (result.contentType()) {
                        "application/json" -> BodyContent.Json(bodyPath.getResourceAsString())
                        "text/plain" -> BodyContent.Text(bodyPath.getResourceAsString())
                        else -> BodyContent.Binary(bodyPath.getResourceAsByteArray())
                    }
            }
        } else {
            result.bodyContent = BodyContent.Text(result.body!!)
        }
        return result
    }

    private fun Fixture.contentType() =
        headers
            .firstOrNull { it.startsWith("Content-Type:") }
            ?.split(":")
            ?.lastOrNull()
            ?.trim() ?: "application/json"
}
