package pl.droidsonroids.testing.mockwebserver

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType

/**
 * * Created by MS on 14/06/2017.
 */
class ResourcesParserTest {

    private val NON_EXISTENT_FIXTURE_NAME = "noExist"
    internal lateinit var parser: pl.droidsonroids.testing.mockwebserver.ResourcesParser

    @org.junit.Before
    fun setUp() {
        parser = pl.droidsonroids.testing.mockwebserver.ResourcesParser()
    }

    @org.junit.Test
    fun `throws when fixture does not exist`() {
        assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { parser.parseFrom(NON_EXISTENT_FIXTURE_NAME) }
                .withMessageContaining("Invalid path: ")
                .withMessageContaining(NON_EXISTENT_FIXTURE_NAME)
    }

    @org.junit.Test
    fun `parses json object`() {
        val fixture = parser.parseFrom("json_object")
        assertThat(fixture.statusCode).isEqualTo(200)
        assertThat(fixture.headers).containsOnly("Content-Type: application/json")
        assertThat(fixture.body).isEqualToIgnoringWhitespace("{\"test\": null}")
    }

    @org.junit.Test
    fun `parses json array`() {
        val fixture = parser.parseFrom("json_array")
        assertThat(fixture.statusCode).isEqualTo(400)
        assertThat(fixture.headers).isEmpty()
        assertThat(fixture.body).isEqualToIgnoringWhitespace("[]")
    }

    @org.junit.Test
    fun `parses json path`() {
        val fixture = parser.parseFrom("body_path")
        assertThat(fixture.statusCode).isEqualTo(404)
        assertThat(fixture.headers).containsExactlyInAnyOrder("Content-Type: text/plain", "Vary: Accept-Encoding")
        assertThat(fixture.body).isEqualTo("{\"test\"}")
    }

    @org.junit.Test
    fun `throws when yaml is malformed`() {
        assertThatExceptionOfType(Throwable::class.java)
                .isThrownBy { parser.parseFrom("malformed") }
    }
}