package pl.droidsonroids.testing.mockwebserver.condition

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.net.Socket

@RunWith(MockitoJUnitRunner::class)
class HttpUrlConditionTest {
    @Spy private val socket = Socket()

    @Before
    fun setUp() {
        whenever(socket.localPort).doReturn(8080)
    }

    @Test
    fun `request without URL does not match`() {
        val matchAllUrlCondition = object : HttpUrlCondition() {
            override fun isUrlMatching(url: HttpUrl) = true
            override fun compareTo(other: Condition) = 0
        }
        val request =
            RecordedRequest("", Headers.headersOf(), emptyList(), 0, Buffer(), 0, socket)

        assertThat(matchAllUrlCondition.isRequestMatching(request)).isFalse
    }

    @Test
    fun `request with different HTTP method does not match`() {
        val matchAllUrlCondition = object : HttpUrlCondition() {
            override val httpMethod: HTTPMethod
                get() = HTTPMethod.PUT

            override fun isUrlMatching(url: HttpUrl) = true
            override fun compareTo(other: Condition) = 0
        }
        val request =
            RecordedRequest("GET /some/path HTTP/1.1", Headers.headersOf(), emptyList(), 0, Buffer(), 0, socket)

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
            RecordedRequest("GET /some/path HTTP/1.1", Headers.headersOf(), emptyList(), 0, Buffer(), 0, socket)

        assertThat(matchAllUrlCondition.isRequestMatching(request)).isTrue
    }

    @Test
    fun `request with URL with any request method should match any request method`() {
        HTTPMethod.values()
            .filter { it != HTTPMethod.ANY }
            .forEach { httpMethod->
                val matchAllUrlCondition = object : HttpUrlCondition() {
                    override val httpMethod: HTTPMethod
                        get() = HTTPMethod.ANY

                    override fun isUrlMatching(url: HttpUrl) = true
                    override fun compareTo(other: Condition) = 0
                }
                val request =
                    RecordedRequest("${httpMethod.name} /some/path HTTP/1.1", Headers.headersOf(), emptyList(), 0, Buffer(), 0, socket)

                assertThat(matchAllUrlCondition.isRequestMatching(request))
                    .withFailMessage { "${httpMethod.name} does not match condition" }
                    .isTrue
            }
    }
}
