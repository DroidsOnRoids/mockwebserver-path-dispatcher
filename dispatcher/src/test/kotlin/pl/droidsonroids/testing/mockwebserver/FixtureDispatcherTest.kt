package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import okhttp3.Headers
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import java.net.Socket

class FixtureDispatcherTest {
    private lateinit var dispatcher: FixtureDispatcher
    private lateinit var responseBuilder: ResponseBuilder
    private val request =
        RecordedRequest("", Headers.headersOf(), emptyList(), 0, Buffer(), 0, Socket())

    @Before
    fun setUp() {
        responseBuilder = mock()
        dispatcher = FixtureDispatcher(responseBuilder)
    }

    @Test
    fun `throws when no matching response found`() {
        assertThatThrownBy { dispatcher.dispatch(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Unexpected request: $request")
    }

    @Test
    fun `matches single response`() {
        dispatcher.putResponse(mock { on { isRequestMatching(any()) } doReturn true }, "response")
        dispatcher.dispatch(request)
        verify(responseBuilder).buildMockResponse("response")
    }

    @Test
    fun `throws when request contains non-matching url`() {
        dispatcher.putResponse(mock { on { isRequestMatching(any()) } doReturn false }, "response")
        assertThatThrownBy { dispatcher.dispatch(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Unexpected request: $request")
    }

    @Test
    fun `matches response with path and parameter name`() {
        dispatcher.putResponse(mock {
            on { isRequestMatching(any()) } doReturn true
            on { compareTo(any()) } doReturn -1
        }, "response")
        dispatcher.putResponse(mock {
            on { isRequestMatching(any()) } doReturn true
            on { compareTo(any()) } doReturn 1
        }, "response2")

        dispatcher.dispatch(request)
        verify(responseBuilder).buildMockResponse("response")
    }
}