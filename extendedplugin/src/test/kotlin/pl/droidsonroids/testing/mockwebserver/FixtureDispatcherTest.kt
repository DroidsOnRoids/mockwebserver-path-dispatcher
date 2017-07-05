package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class FixtureDispatcherTest {
    @get:Rule val expectedException = ExpectedException.none()

    lateinit var dispatcher: FixtureDispatcher
    val pathPrefix = "/prefix/"

    @Before
    fun setUp() {
        dispatcher = FixtureDispatcher(pathPrefix)
        dispatcher.responseBuilder = mock<MockResponseBuilder>()
        dispatcher.addResponse(Condition.withPathInfix("path"), "pathOnly")
        dispatcher.addResponse(Condition.withPathInfixAndQueryParameter("path", "name"), "pathAndName")
        dispatcher.addResponse(Condition.withPathInfixAndQueryParameter("path", "name", "value"), "pathAndNameAndValue")
    }

    @Test
    fun `throws when no matching response found`() {
        expectedException.expect(IllegalArgumentException::class.java)
        val url = HttpUrl.parse("http://nonexistent.invalid")!!
        expectedException.expectMessage("Unexpected request: $url")
        dispatcher.dispatch(url)
    }

    @Test
    fun `matches response with path only` () {
        val url = HttpUrl.parse("http://test.test/prefix/path")!!
        dispatcher.dispatch(url)
        verify(dispatcher.responseBuilder).buildMockResponse("pathOnly")
    }

    @Test
    fun `matches response with path and parameter name` () {
        val url = HttpUrl.parse("http://test.test/prefix/path?name=whatever")!!
        dispatcher.dispatch(url)
        verify(dispatcher.responseBuilder).buildMockResponse("pathAndName")
    }

    @Test
    fun `matches response with path and parameter name and value` () {
        val url = HttpUrl.parse("http://test.test/prefix/path?name=value")!!
        dispatcher.dispatch(url)
        verify(dispatcher.responseBuilder).buildMockResponse("pathAndNameAndValue")
    }

    @Test
    fun `throws when parameter name matches but path does not` () {
        val url = HttpUrl.parse("http://test.test/prefix/whatever?name=value")!!
        expectedException.expectMessage("Unexpected request: $url")
        dispatcher.dispatch(url)
        verify(dispatcher.responseBuilder).buildMockResponse("pathAndNameAndValue")
    }

    @Test
    @Ignore
    fun `matches response with path and multiple parameters with same name` () {
        val url = HttpUrl.parse("http://test.test/prefix/path?name=whatever&name=value")!!
        dispatcher.dispatch(url)
        verify(dispatcher.responseBuilder).buildMockResponse("pathAndNameAndValue")
    }


}