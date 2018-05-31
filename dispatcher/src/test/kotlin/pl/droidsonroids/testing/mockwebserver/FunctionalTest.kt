package pl.droidsonroids.testing.mockwebserver

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory

private const val TEST_JSON_OBJECT = """{
  "test": null
}"""

private const val TEST_JSON_ARRAY = "[ ]"

class FunctionalTest {

    @JvmField @Rule val mockWebServer = MockWebServer()

    private var port: Int = 0
    private lateinit var client: OkHttpClient
    private lateinit var requestBuilder: Request.Builder

    @Before
    fun setUp() {
        port = mockWebServer.port
        client = OkHttpClient()
        requestBuilder = Request.Builder()
    }

    @Test
    fun `responds with correct fixtures`() {
        val dispatcher = FixtureDispatcher()

        //all URLs beginning with http://example.test/user/
        val factory = PathQueryConditionFactory("/user/")

        /*
            all URLs with path ending with events, if there're no more specific mappings e.g.:
            http://example.test/user/events
            http://example.test/user/events?id=1
            http://example.test/user/events?own=true
         */
        dispatcher.putResponse(factory.withPathSuffix("events"), "body_path")

        /*
            all URLs with path ending with events and id query parameter equal to 2 e.g.:
            http://example.test/user/events?id=2
            http://example.test/user/events?id=2&own=true
         */
        dispatcher.putResponse(factory.withPathSuffixAndQueryParameter("events", "id", "2"), "json_object")

        /*
            all URLs with path ending with profile, if there're no more specific mappings e.g.:
            http://example.test/user/profile
            http://example.test/user/profile?name=true
        */
        dispatcher.putResponse(factory.withPathSuffix("profile"), "json_object")
        dispatcher.enqueue(factory.withPathSuffix("profile"), "json_array")
        dispatcher.enqueue(factory.withPathSuffix("profile"), "body_path")

        /*
            all URLs with path ending with profile and picture query parameter e.g.:
            http://example.test/user/profile?picture=false
            http://example.test/user/profile?picture=true
        */
        dispatcher.putResponse(factory.withPathSuffixAndQueryParameter("profile", "picture"), "json_array")

        mockWebServer.setDispatcher(dispatcher)

        val events = "http://localhost:$port/user/events".download()
        assertThat(events).isEqualTo("fixtures/body.txt".getResourceAsString())

        val event = "http://localhost:$port/user/events?id=1".download()
        assertThat(event).isEqualTo("fixtures/body.txt".getResourceAsString())

        val ownEvents = "http://localhost:$port/user/events?own=true".download()
        assertThat(ownEvents).isEqualTo("fixtures/body.txt".getResourceAsString())

        val secondEvent = "http://localhost:$port/user/events?id=2".download()
        assertThat(secondEvent).isEqualTo(TEST_JSON_OBJECT)

        val secondOwnEvent = "http://localhost:$port/user/events?id=2&own=true".download()
        assertThat(secondOwnEvent).isEqualTo(TEST_JSON_OBJECT)

        val firstProfile = "http://localhost:$port/user/profile".download()
        val secondProfile = "http://localhost:$port/user/profile".download()
        val thirdProfile = "http://localhost:$port/user/profile".download()
        val fourthProfile = "http://localhost:$port/user/profile".download()

        assertThat(firstProfile).isEqualTo(TEST_JSON_ARRAY)
        assertThat(secondProfile).isEqualTo("fixtures/body.txt".getResourceAsString())
        assertThat(thirdProfile).isEqualTo(TEST_JSON_OBJECT)
        assertThat(fourthProfile).isEqualTo(thirdProfile)

        val name = "http://localhost:$port/user/profile?name=true".download()
        assertThat(name).isEqualTo(TEST_JSON_OBJECT)

        val withoutPicture = "http://localhost:$port/user/profile?picture=false".download()
        assertThat(withoutPicture).isEqualTo(TEST_JSON_ARRAY)

        val picture = "http://localhost:$port/user/profile?picture=true".download()
        assertThat(picture).isEqualTo(TEST_JSON_ARRAY)
    }


    @Test
    fun `responds with correct fixtures without prefix`() {
        val dispatcher = FixtureDispatcher()

        //all URLs beginning with http://example.test
        val factory = PathQueryConditionFactory()

        /*
            all URLs with path equal to "/events" e.g.:
            http://example.test/events
            http://example.test/events?id=1
            http://example.test/events?own=true
        */
        dispatcher.putResponse(factory.withPath("/events"), "body_path")

    }

    private fun String.download() = client.newCall(
        requestBuilder.url(this)
            .get()
            .build()
    ).execute()
        .body()
        ?.string()
}