package pl.droidsonroids.testing.mockwebserver.condition

import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.net.Socket

class HttpUrlConditionTest {

    @Test
    fun `request without URL does not match`() {
        val matchAllUrlCondition = object : HttpUrlCondition() {
            override fun isUrlMatching(url: HttpUrl) = true
            override fun compareTo(other: Condition) = 0
        }
        val request =
            RecordedRequest("", Headers.headersOf(), emptyList(), 0, Buffer(), 0, Socket())

        assertThat(matchAllUrlCondition.isRequestMatching(request)).isFalse
    }
}
