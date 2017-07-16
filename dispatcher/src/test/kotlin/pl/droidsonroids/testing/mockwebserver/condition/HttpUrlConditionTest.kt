package pl.droidsonroids.testing.mockwebserver.condition

import com.nhaarman.mockito_kotlin.*
import com.sun.corba.se.impl.legacy.connection.DefaultSocketFactory
import okhttp3.HttpUrl
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.CALLS_REAL_METHODS
import java.lang.RuntimeException
import java.net.ServerSocket
import java.net.Socket

class HttpUrlConditionTest {

	@Test
	fun `request without URL does not match`() {
		val matchAllUrlCondition = object : HttpUrlCondition() {
			override fun isUrlMatching(url: HttpUrl) = true
			override fun compareTo(other: Condition) = 0
		}
		val request = RecordedRequest(null, null, null, 0, null, 0, null)

		assertThat(matchAllUrlCondition.isRequestMatching(request))
	}

	@Test
	fun `request with URL checked with isUrlMatching` () {
		val captor = argumentCaptor<HttpUrl>()
		val condition:HttpUrlCondition = mock(defaultAnswer = CALLS_REAL_METHODS)
		whenever(condition.isUrlMatching(captor.capture())).thenReturn(true)

		val serverSocket = ServerSocket(0)
		serverSocket.use {
			val socket = Socket(serverSocket.inetAddress, serverSocket.localPort)
			socket.use {
				val request = RecordedRequest("GET / HTTP/1.1", null, null, 0, null, 0, socket)
				assertThat(condition.isRequestMatching(request)).isTrue()
			}
		}

		val url = captor.firstValue
		assertThat(url.scheme()).isEqualTo("http")
		assertThat(url.host()).isEqualTo("localhost")
		assertThat(url.pathSize()).isEqualTo(1)
	}

}