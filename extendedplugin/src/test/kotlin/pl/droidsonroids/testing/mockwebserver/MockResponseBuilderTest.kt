package pl.droidsonroids.testing.mockwebserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.Fixture
import pl.droidsonroids.testing.mockwebserver.MockResponseBuilder
import pl.droidsonroids.testing.mockwebserver.ResourcesParser

/**
 * * Created by MS on 14/06/2017.
 */
internal class MockResponseBuilderTest {
    lateinit var builder: MockResponseBuilder
    private val BODY = "body"
    val fixture = Fixture()

    @Before
    fun setUp() {
        builder = MockResponseBuilder()
        builder.parser = object : ResourcesParser() {
            override fun parseFrom(fileName: String) = fixture
        }
    }

    @Test
    fun `body set when present`() {
        fixture.statusCode = 200
        fixture.body = BODY
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.body.readUtf8()).isEqualTo(BODY)
    }

    @Test
    fun `for each headers added`() {
        fixture.statusCode = 400
        fixture.headers = listOf("name:value", "name2:value2")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("400")
        assertThat(mockResponse.body).isNull()
        assertThat(mockResponse.headers["name"]).isEqualTo("value")
        assertThat(mockResponse.headers["name2"]).isEqualTo("value2")
    }
}