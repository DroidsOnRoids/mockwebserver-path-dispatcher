package pl.droidsonroids.testing.mockwebserver.condition

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.CALLS_REAL_METHODS
import java.net.ServerSocket
import java.net.Socket

class HttpUrlConditionTest {

	@Test
	fun `request without URL does not match`() {
		val matchAllUrlCondition = object : HttpUrlCondition() {
			override fun isUrlMatching(url: HttpUrl) = true
			override fun compareTo(other: Condition) = 0
		}
		val request = RecordedRequest("", Headers.headersOf(), emptyList(), 0, Buffer(), 0, Socket())

		assertThat(matchAllUrlCondition.isRequestMatching(request))
	}

	@Test
	fun `request with URL checked by isUrlMatching` () {
		val captor = argumentCaptor<HttpUrl>()
		val condition:HttpUrlCondition = mock(defaultAnswer = CALLS_REAL_METHODS)
		whenever(condition.isUrlMatching(captor.capture())).thenReturn(true)

		val serverSocket = ServerSocket(0)
		serverSocket.use {
			val socket = Socket(serverSocket.inetAddress, serverSocket.localPort)
			socket.use {
				val request = RecordedRequest("GET / HTTP/1.1", Headers.headersOf(), emptyList(), 0, Buffer(), 0, Socket())
				assertThat(condition.isRequestMatching(request)).isTrue

				val url = captor.firstValue
				assertThat(url.scheme).isEqualTo("http")
				assertThat(url.host).isEqualToIgnoringCase(socket.inetAddress.hostName)
				assertThat(url.pathSize).isEqualTo(1)
			}
		}
	}

}