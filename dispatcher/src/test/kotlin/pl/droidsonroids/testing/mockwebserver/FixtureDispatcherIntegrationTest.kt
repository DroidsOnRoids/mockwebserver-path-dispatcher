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
            ?.getResource("fixtures/body.txt")
            ?.readText()

        val dispatcher = FixtureDispatcher()
        val factory = PathQueryConditionFactory("/prefix/")
        dispatcher.enqueue(factory.withPathSuffix("suffix"), "json_array")
        dispatcher.putResponse(factory.withPathSuffix("suffix"), "body_path")
        dispatcher.putResponse(factory.withPathSuffix("another_suffix"), "json_object")
        mockWebServer.dispatcher = dispatcher

        val client = OkHttpClient()

        val httpUrlWithSuffix = HttpUrl.Builder()
            .host(mockWebServer.hostName)
            .port(mockWebServer.port)
            .scheme("http")
            .encodedPath("/prefix/suffix")
            .build()

        client.newCall(
            Request.Builder()
                .url(httpUrlWithSuffix)
                .build()
        )
            .execute()
            .use {
                assertThat(it.code).isEqualTo(400)
                assertThat(it.body?.string()).isEqualToIgnoringWhitespace("[ ]")
            }

        client.newCall(
            Request.Builder()
                .url(httpUrlWithSuffix)
                .build()
        )
            .execute()
            .use {
                assertThat(it.code).isEqualTo(404)
                assertThat(it.body?.string()).isEqualToIgnoringWhitespace(expectedText)
            }

        val httpUrlWithAnotherSuffix = HttpUrl.Builder()
            .host(mockWebServer.hostName)
            .port(mockWebServer.port)
            .scheme("http")
            .encodedPath("/prefix/another_suffix")
            .build()

        client.newCall(
            Request.Builder()
                .url(httpUrlWithAnotherSuffix)
                .build()
        )
            .execute()
            .use {
                assertThat(it.code).isEqualTo(200)
                assertThat(it.header("Content-Type") == "application/json")
            }
    }

}