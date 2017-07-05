package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class FixtureDispatcherTest {
    private val pathPrefix = "/prefix/"
    private lateinit var dispatcher: FixtureDispatcher
    private lateinit var responseBuilder: ResponseBuilder

    @Before
    fun setUp() {
        responseBuilder = mock<ResponseBuilder>()
        dispatcher = FixtureDispatcher(pathPrefix, responseBuilder)
        dispatcher.putResponse(Condition.withPathInfix("path"), "pathOnly")
        dispatcher.putResponse(Condition.withPathInfixAndQueryParameter("path", "name"), "pathAndName")
        dispatcher.putResponse(Condition.withPathInfixAndQueryParameter("path", "name", "value"), "pathAndNameAndValue")
    }

    @Test
    fun `throws when no matching response found`() {
        val url = HttpUrl.parse("http://nonexistent.invalid")!!
        assertThatThrownBy { dispatcher.dispatch(url) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unexpected request: $url")
    }

    @Test
    fun `matches response with path only` () {
        val url = HttpUrl.parse("http://test.test/prefix/path")!!
        dispatcher.dispatch(url)
        verify(responseBuilder).buildMockResponse("pathOnly")
    }

    @Test
    fun `matches response with path and parameter name` () {
        val url = HttpUrl.parse("http://test.test/prefix/path?name=whatever")!!
        dispatcher.dispatch(url)
        verify(responseBuilder).buildMockResponse("pathAndName")
    }

    @Test
    fun `matches response with path and parameter name and value` () {
        val url = HttpUrl.parse("http://test.test/prefix/path?name=value")!!
        dispatcher.dispatch(url)
        verify(responseBuilder).buildMockResponse("pathAndNameAndValue")
    }

    @Test
    fun `throws when parameter name matches but path does not` () {
        val url = HttpUrl.parse("http://test.test/prefix/whatever?name=value")!!
        assertThatThrownBy { dispatcher.dispatch(url) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unexpected request: $url")
    }

    @Test
    @Ignore
    fun `matches response with path and multiple parameters with same name` () {
        val url = HttpUrl.parse("http://test.test/prefix/path?name=whatever&name=value")!!
        dispatcher.dispatch(url)
        verify(responseBuilder).buildMockResponse("pathAndNameAndValue")
    }


}