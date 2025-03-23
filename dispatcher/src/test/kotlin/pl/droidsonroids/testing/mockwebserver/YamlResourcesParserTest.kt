package pl.droidsonroids.testing.mockwebserver

import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Before
import org.junit.Test

private const val NON_EXISTENT_FIXTURE_NAME = "nonExistent"

class YamlResourcesParserTest {
    private lateinit var parser: YamlResourcesParser

    @Before
    fun setUp() {
        parser = YamlResourcesParser()
    }

    @Test
    fun `throws when fixture does not exist`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { parser.parseFrom(NON_EXISTENT_FIXTURE_NAME) }
            .withMessageContaining("Invalid path: ")
            .withMessageContaining(NON_EXISTENT_FIXTURE_NAME)
    }

    @Test
    fun `parses json object`() {
        val fixture = parser.parseFrom("json_object")
        assertThat(fixture.statusCode).isEqualTo(200)
        assertThat(fixture.headers).containsOnly("Content-Type: application/json")
        assertThat(fixture.body).isEqualToIgnoringWhitespace("""{"test": null}""")
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `sanitizes extended unicode`() {
        val fixture = parser.parseFrom("unescaped_unicode")
        assertThat(fixture.statusCode).isEqualTo(200)
        assertThat(fixture.headers).containsOnly("Content-Type: application/json")
        assertThat(fixture.body).isEqualToIgnoringWhitespace("""{"test": "\uD83D\uDC31"}""")
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses json array`() {
        val fixture = parser.parseFrom("json_array")
        assertThat(fixture.statusCode).isEqualTo(400)
        assertThat(fixture.headers).isEmpty()
        assertThat(fixture.body).isEqualToIgnoringWhitespace("[]")
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses json path when no content type is specified`() {
        val fixture = parser.parseFrom("body_unknown_content_type_path")
        assertThat(fixture.statusCode).isEqualTo(404)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
            "Vary: Accept-Encoding"
        )
        assertThat(fixture.body).isEqualTo("body_json.txt")
        assertThat(fixture.bodyContent).isEqualTo(BodyContent.Json("""{"test"}"""))
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses json path`() {
        val fixture = parser.parseFrom("body_json_path")
        assertThat(fixture.statusCode).isEqualTo(404)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
            "Content-Type: application/json",
            "Vary: Accept-Encoding"
        )
        assertThat(fixture.body).isEqualTo("body_json.txt")
        assertThat(fixture.bodyContent).isEqualTo(BodyContent.Json("""{"test"}"""))
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses text path`() {
        val fixture = parser.parseFrom("body_text_path")
        assertThat(fixture.statusCode).isEqualTo(404)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
            "Content-Type: text/plain",
            "Vary: Accept-Encoding"
        )
        assertThat(fixture.body).isEqualTo("body_plain.txt")
        assertThat(fixture.bodyContent).isEqualTo(BodyContent.Text("test"))
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses binary path`() {
        val fixture = parser.parseFrom("body_binary_path")
        assertThat(fixture.statusCode).isEqualTo(404)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
            "Content-Type: image/jpeg",
            "Vary: Accept-Encoding"
        )
        assertThat(fixture.body).isEqualTo("image.jpeg")
        assertThat(fixture.bodyContent).isEqualTo(
            BodyContent.Binary(
                Buffer().write(
                    byteArrayOf(
                        0xef.toByte()
                    )
                )
            )
        )
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses response without body`() {
        val fixture = parser.parseFrom("no_body")
        assertThat(fixture.statusCode).isEqualTo(204)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
            "Content-Type: text/plain",
            "Vary: Accept-Encoding"
        )
        assertThat(fixture.body).isNull()
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses response with connection failure`() {
        val fixture = parser.parseFrom("connection_failure")
        assertThat(fixture.statusCode).isEqualTo(200)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
            "Content-Type: text/plain",
        )
        assertThat(fixture.body).isNull()
        assertThat(fixture.connectionFailure).isTrue
        assertThat(fixture.timeoutFailure).isFalse
    }

    @Test
    fun `parses response with timeout failure`() {
        val fixture = parser.parseFrom("timeout_failure")
        assertThat(fixture.statusCode).isEqualTo(200)
        assertThat(fixture.headers).containsExactlyInAnyOrder(
                "Content-Type: text/plain",
        )
        assertThat(fixture.body).isNull()
        assertThat(fixture.connectionFailure).isFalse
        assertThat(fixture.timeoutFailure).isTrue
    }

    @Test
    fun `throws when yaml is malformed`() {
        assertThatExceptionOfType(Throwable::class.java)
            .isThrownBy { parser.parseFrom("malformed") }
    }
}
