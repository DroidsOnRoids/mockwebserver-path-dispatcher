package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.condition.HttpUrlCondition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory
import com.sun.corba.se.spi.presentation.rmi.StubAdapter.request


class FixtureDispatcherIntegrationTest {
    @get:Rule
    val mockWebServer = MockWebServer()

    @Test
    fun `response dispatched with mockwebserver`() {
        val expectedText = Thread.currentThread()
                .contextClassLoader
                .getResource("fixtures/body.txt")
                .readText()

		val dispatcher = FixtureDispatcher()
        val factory = PathQueryConditionFactory("/prefix/")
        dispatcher.putResponse(factory.withPathInfix("infix"), "body_path")
        dispatcher.putResponse(factory.withPathInfix("another_infix"), "json_object")
        mockWebServer.setDispatcher(dispatcher)

        val client = OkHttpClient()

        val httpUrl = HttpUrl.Builder()
                .host(mockWebServer.hostName)
                .port(mockWebServer.port)
                .scheme("http")
                .encodedPath("/prefix/infix")
                .build()

        client.newCall(Request.Builder()
                .url(httpUrl)
                .build())
                .execute()
                .use {
                    assertThat(it.code()).isEqualTo(404)
                    assertThat(it.body()?.string()).isEqualToIgnoringWhitespace(expectedText)
                }

        val anotherHttpUrl = HttpUrl.Builder()
                .host(mockWebServer.hostName)
                .port(mockWebServer.port)
                .scheme("http")
                .encodedPath("/prefix/another_infix")
                .build()

        client.newCall(Request.Builder()
                .url(anotherHttpUrl)
                .build())
                .execute()
                .use {
                    assertThat(it.code()).isEqualTo(200)
                    assertThat(it.header("Content-Type") == "application/json")
                }
    }

}