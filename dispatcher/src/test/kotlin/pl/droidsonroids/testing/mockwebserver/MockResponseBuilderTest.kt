package pl.droidsonroids.testing.mockwebserver

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import okhttp3.mockwebserver.SocketPolicy
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

internal class MockResponseBuilderTest {
    private val body = "body"
    private lateinit var builder: MockResponseBuilder
    private lateinit var fixture: Fixture

    @Before
    fun setUp() {
        fixture = Fixture()
        builder = MockResponseBuilder(mock {
            on { parseFrom(any()) } doReturn fixture
        })
    }

    @Test
    fun `body set as string when bodyContent present as Json`() {
        fixture.statusCode = 200
        fixture.bodyContent = BodyContent.Text(body)
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()?.readUtf8()).isEqualTo(body)
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `body set as binary when bodyContent present as binary`() {
        fixture.statusCode = 200
        fixture.bodyContent = BodyContent.Binary(Buffer().writeUtf8(body))
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()?.readByteArray()).isEqualTo(body.toByteArray())
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `body not set when bodyContent is not present and body is present`() {
        fixture.statusCode = 200
        fixture.body = body
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()?.readUtf8()).isNull()
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `body is transformed when a body transformer is passed`() {
        fixture.statusCode = 200
        fixture.bodyContent = BodyContent.Json(body)
        val mockResponse = builder.buildMockResponse("") { _ ->
            BodyContent.Json("transformed body")
        }
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()?.readUtf8()).isEqualTo("transformed body")
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `for each headers added`() {
        fixture.statusCode = 400
        fixture.headers = listOf("name:value", "name2:value2")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("400")
        assertThat(mockResponse.getBody()).isNull()
        assertThat(mockResponse.headers["name"]).isEqualTo("value")
        assertThat(mockResponse.headers["name2"]).isEqualTo("value2")
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `replace content type header when body is text`() {
        fixture.headers = listOf("Content-Type:application/json")
        fixture.bodyContent = BodyContent.Text("text")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.headers["Content-Type"]).isEqualTo("text/plain")
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `replace content type header when body is json`() {
        fixture.headers = listOf("Content-Type:text/plain")
        fixture.bodyContent = BodyContent.Json("""{"text"}""")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.headers["Content-Type"]).isEqualTo("application/json")
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `set content type header when body is text`() {
        fixture.headers = emptyList()
        fixture.bodyContent = BodyContent.Text("text")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.headers["Content-Type"]).isEqualTo("text/plain")
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `set content type header when body is json`() {
        fixture.headers = emptyList()
        fixture.bodyContent = BodyContent.Json("""{"text"}""")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.headers["Content-Type"]).isEqualTo("application/json")
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.KEEP_OPEN)
    }

    @Test
    fun `DISCONNECT_AT_START set when connection failure is true`() {
        fixture.statusCode = 200
        fixture.connectionFailure = true
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()).isNull()
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.DISCONNECT_AT_START)
    }

    @Test
    fun `NO_RESPONSE set when timeout failure is true`() {
        fixture.statusCode = 200
        fixture.timeoutFailure = true
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()).isNull()
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.NO_RESPONSE)
    }
}
