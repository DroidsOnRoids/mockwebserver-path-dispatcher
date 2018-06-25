package pl.droidsonroids.toast.test

import android.support.test.espresso.Espresso
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.rule.ActivityTestRule
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.FixtureDispatcher
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory
import pl.droidsonroids.toast.R
import pl.droidsonroids.toast.app.home.MainActivity
import pl.droidsonroids.toast.robot.SpeakersRobot

class SpeakerDetailsTest {
    @JvmField
    @Rule
    val activityRule = IntentsTestRule(MainActivity::class.java, true, false)

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        setPathDispatcher()
        mockWebServer.start(12345)
        activityRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun isHeaderDisplayedWithProperElements() {
        with(SpeakersRobot()) {
            goToSpeakerDetailsScreen()
            checkIfElementWithIdIsDisplayed(R.id.collapsingToolbar)
            checkIfElementWithIdIsDisplayed(R.id.avatarImage)
            checkIfElementWithIdIsDisplayed(R.id.avatarBorderSmall)
            checkIfElementWithIdIsDisplayed(R.id.avatarBorderBig)
            checkIfElementWithIdIsDisplayed(R.id.speakerName)
            checkIfElementWithIdIsDisplayed(R.id.speakerJob)
        }
    }

    @Test
    fun isSpeakersListDisplayedAfterClickingBackFromSpeakerDetails() {
        with(SpeakersRobot()) {
            goToSpeakerDetailsScreen()
            Espresso.pressBack()
            checkIfElementWithIdIsDisplayed(R.id.searchImageButton)
        }
    }

    private fun setPathDispatcher() {
        val dispatcher = FixtureDispatcher()
        val factory = PathQueryConditionFactory("")
        dispatcher.putResponse(factory.withPathSuffix("/events"), "events17_200")
        dispatcher.putResponse(factory.withPathSuffix("/events/17"), "event17_200")
        dispatcher.putResponse(factory.withPathSuffix("/speakers"), "speakers_200")
        dispatcher.putResponse(factory.withPathSuffix("/speakers/16"), "speakers16_200")
        mockWebServer.setDispatcher(dispatcher)
    }
}
