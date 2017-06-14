package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

public class FixtureDispatcherTest {
    @get:Rule
    public val expectedException = ExpectedException.none()

    lateinit var dispatcher: FixtureDispatcher

    @Before
    fun setUp() {
        dispatcher = FixtureDispatcher("test")
        dispatcher.responseBuilder = mock<MockResponseBuilder>()
        dispatcher.addResponse(Condition.withPathInfix("path"), "pathOnly")
        dispatcher.addResponse(Condition.withPathInfixAndQueryParameter("path", "name"), "pathAndName")
        dispatcher.addResponse(Condition.withPathInfixAndQueryParameter("path", "name", "value"), "pathAndNameAndValue")
    }

    @Test
    fun throwsWhenNoMatchingResponse() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unexpected request path: nonExistent")
        dispatcher.dispatch("nonExistent", "")
    }

    @Test
    @Ignore
    fun pathOnlyResponseMatched() {
        dispatcher.dispatch("path", "pathOnly")
        verify(dispatcher.responseBuilder).buildMockResponse("pathOnly")
    }


}