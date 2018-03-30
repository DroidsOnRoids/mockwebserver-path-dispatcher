package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory

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
        dispatcher.putResponse(factory.withPathSuffix("suffix"), "body_path")
        dispatcher.putResponse(factory.withPathSuffix("another_suffix"), "json_object")
        mockWebServer.setDispatcher(dispatcher)

        val client = OkHttpClient()

        val httpUrl = HttpUrl.Builder()
                .host(mockWebServer.hostName)
                .port(mockWebServer.port)
                .scheme("http")
                .encodedPath("/prefix/suffix")
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
                .encodedPath("/prefix/another_suffix")
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