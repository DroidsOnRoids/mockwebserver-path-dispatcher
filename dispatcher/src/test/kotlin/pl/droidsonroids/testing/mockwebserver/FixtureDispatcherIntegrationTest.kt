package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class FixtureDispatcherIntegrationTest {
	@get:Rule
	val server = MockWebServer()

	@Test
	fun `response dispatched with mockwebserver`() {
		val expectedText = Thread.currentThread().contextClassLoader.getResource("fixtures/body.txt").readText()

		val dispatcher = FixtureDispatcher("/prefix/")
		dispatcher.putResponse(Condition.withPathInfix("infix"), "body_path")
		server.setDispatcher(dispatcher)
		val httpUrl = HttpUrl.Builder()
				.host(server.hostName)
				.port(server.port)
				.scheme("http")
				.encodedPath("/prefix/infix")
				.build()
		OkHttpClient().newCall(Request.Builder()
				.url(httpUrl)
				.build())
				.execute()
				.use {
					assertThat(it.code()).isEqualTo(404)
					assertThat(it.body()?.string()).isEqualToIgnoringWhitespace(expectedText)
				}
	}

}