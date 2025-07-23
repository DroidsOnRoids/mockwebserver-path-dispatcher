package pl.droidsonroids.testing.mockwebserver.condition

import mockwebserver3.RecordedRequest
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.ByteString
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HttpUrlConditionTest {

    @Test
    fun `request with different HTTP method does not match`() {
        val matchAllUrlCondition = object : HttpUrlCondition() {
            override val httpMethod: HTTPMethod
                get() = HTTPMethod.PUT

            override fun isUrlMatching(url: HttpUrl) = true
            override fun compareTo(other: Condition) = 0
        }
        val request =
            RecordedRequest(
                connectionIndex = 0,
                headers = Headers.headersOf(),
                chunkSizes = emptyList(),
                bodySize = 0,
                body = ByteString.EMPTY,
                exchangeIndex = 0,
                handshake = null,
                handshakeServerNames = emptyList(),
                method = "GET",
                target = "/some/path",
                version = "HTTP/1.1",
                url = "http://localhost:8080".toHttpUrl(),
            )

        assertThat(matchAllUrlCondition.isRequestMatching(request)).isFalse
    }

    @Test
    fun `request with URL and same request method should match`() {
        val matchAllUrlCondition = object : HttpUrlCondition() {
            override val httpMethod: HTTPMethod
                get() = HTTPMethod.GET

            override fun isUrlMatching(url: HttpUrl) = true
            override fun compareTo(other: Condition) = 0
        }
        val request =
            RecordedRequest(
                connectionIndex = 0,
                headers = Headers.headersOf(),
                chunkSizes = emptyList(),
                bodySize = 0,
                body = ByteString.EMPTY,
                exchangeIndex = 0,
                handshake = null,
                handshakeServerNames = emptyList(),
                method = "GET",
                target = "/some/path",
                version = "HTTP/1.1",
                url = "http://localhost:8080".toHttpUrl(),
            )

        assertThat(matchAllUrlCondition.isRequestMatching(request)).isTrue
    }

    @Test
    fun `request with URL with any request method should match any request method`() {
        HTTPMethod.values()
            .filter { it != HTTPMethod.ANY }
            .forEach { httpMethod ->
                val matchAllUrlCondition = object : HttpUrlCondition() {
                    override val httpMethod: HTTPMethod
                        get() = HTTPMethod.ANY

                    override fun isUrlMatching(url: HttpUrl) = true
                    override fun compareTo(other: Condition) = 0
                }
                val request =
                    RecordedRequest(
                        connectionIndex = 0,
                        headers = Headers.headersOf(),
                        chunkSizes = emptyList(),
                        bodySize = 0,
                        body = ByteString.EMPTY,
                        exchangeIndex = 0,
                        handshake = null,
                        handshakeServerNames = emptyList(),
                        method = httpMethod.name,
                        target = "/some/path",
                        version = "HTTP/1.1",
                        url = "http://localhost:8080".toHttpUrl(),
                    )

                assertThat(matchAllUrlCondition.isRequestMatching(request))
                    .withFailMessage { "${httpMethod.name} does not match condition" }
                    .isTrue
            }
    }
}
