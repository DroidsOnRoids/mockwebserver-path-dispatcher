package pl.droidsonroids.testing.mockwebserver

import mockwebserver3.RecordedRequest
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.ByteString
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FixtureDispatcherTest {
    private lateinit var dispatcher: FixtureDispatcher
    private lateinit var responseBuilder: ResponseBuilder
    private val request =
        RecordedRequest(
            connectionIndex = 0,
            headers = Headers.headersOf(),
            chunkSizes = emptyList(),
            bodySize = 0,
            body = ByteString.EMPTY,
            exchangeIndex = 0,
            handshake = null,
            handshakeServerNames = emptyList(),
            method = "",
            target = "",
            version = "",
            url = "http://localhost:8080".toHttpUrl(),
        )

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
    fun `matches single response with body content transformer`() {
        val bodyContentTransformer: BodyContentTransformer = mock()
        dispatcher.setBodyContentTransformerResponse(bodyContentTransformer)
        dispatcher.putResponse(mock { on { isRequestMatching(any()) } doReturn true }, "response")
        dispatcher.dispatch(request)
        verify(responseBuilder).buildMockResponse("response", bodyContentTransformer)
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

    @Test
    fun `matches response with path and parameter name and body content transformer`() {
        val bodyContentTransformer: BodyContentTransformer = mock()
        dispatcher.setBodyContentTransformerResponse(bodyContentTransformer)
        dispatcher.putResponse(mock {
            on { isRequestMatching(any()) } doReturn true
            on { compareTo(any()) } doReturn -1
        }, "response")
        dispatcher.putResponse(mock {
            on { isRequestMatching(any()) } doReturn true
            on { compareTo(any()) } doReturn 1
        }, "response2")

        dispatcher.dispatch(request)
        verify(responseBuilder).buildMockResponse("response", bodyContentTransformer)
    }

    @Test
    fun `dispatches fallback response when no matching response found`() {
        dispatcher.setFallbackResponse("response")
        dispatcher.dispatch(request)
        verify(responseBuilder).buildMockResponse("response")
    }

    @Test
    fun `dispatches fallback with body content transformer response when no matching response found`() {
        val bodyContentTransformer: BodyContentTransformer = mock()
        dispatcher.setBodyContentTransformerResponse(bodyContentTransformer)
        dispatcher.setFallbackResponse("response")
        dispatcher.dispatch(request)
        verify(responseBuilder).buildMockResponse("response", bodyContentTransformer)
    }
}
