package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test

class FixtureDispatcherTest {
    private lateinit var dispatcher: FixtureDispatcher
    private lateinit var responseBuilder: ResponseBuilder
    val url = HttpUrl.parse("http://test.test")!!

    @Before
    fun setUp() {
        responseBuilder = mock<ResponseBuilder>()
        dispatcher = FixtureDispatcher(responseBuilder)
    }

    @Test
    fun `throws when no matching response found`() {
        assertThatThrownBy { dispatcher.dispatch(url) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unexpected request: $url")
    }

    @Test
    fun `matches single response`() {
        dispatcher.putResponse(mock { on { isUrlMatching(url) } doReturn true }, "response")
        dispatcher.dispatch(url)
        verify(responseBuilder).buildMockResponse("response")
    }

    @Test
    fun `throws when request contains non-matching url`() {
        dispatcher.putResponse(mock { on { isUrlMatching(url) } doReturn false }, "response")
        assertThatThrownBy { dispatcher.dispatch(url) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unexpected request: $url")
    }

    @Test
    fun `matches response with path and parameter name`() {
        dispatcher.putResponse(mock {
            on { isUrlMatching(url) } doReturn true
            on { compareTo(any()) } doReturn -1
        }, "response")
        dispatcher.putResponse(mock {
            on { isUrlMatching(url) } doReturn true
            on { compareTo(any()) } doReturn 1
        }, "response2")

        dispatcher.dispatch(url)
        verify(responseBuilder).buildMockResponse("response")
    }
}