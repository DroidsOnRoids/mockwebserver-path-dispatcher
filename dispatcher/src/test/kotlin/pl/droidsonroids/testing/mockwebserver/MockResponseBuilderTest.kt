package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import okhttp3.mockwebserver.SocketPolicy
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
    fun `body set when present`() {
        fixture.statusCode = 200
        fixture.body = body
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()?.readUtf8()).isEqualTo(body)
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
    fun `DISCONNECT_AT_START set when connection failure is true`() {
        fixture.statusCode = 200
        fixture.connectionFailure = true
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()).isNull()
        assertThat(mockResponse.socketPolicy).isEqualTo(SocketPolicy.DISCONNECT_AT_START)
    }
}