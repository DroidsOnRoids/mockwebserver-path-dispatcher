package pl.droidsonroids.toast.test

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
import pl.droidsonroids.toast.function.getString
import pl.droidsonroids.toast.robot.EventsRobot


class EventsScreenTest {
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
    fun isToolbarDisplayed() {
        val toolbarTitle = getString(R.string.events_title)
        with(EventsRobot()) {
            checkIfToolbarWithTitleIsDisplayed(toolbarTitle, R.id.toolbar)
        }
    }

    @Test
    fun isEveryElementDisplayedProperlyOnUpcomingEventCard() {
        with(EventsRobot()) {
            checkIfElementWithIdIsDisplayed(R.id.topBar)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventImage)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventTitleText)
            checkIfTextIsCorrect(getString(R.string.android_developers_meet_up), R.id.upcomingEventDescriptionText)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventLocationImage)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventPlaceTitleText)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventPlaceLocationText)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventCalendarImage)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventDateText)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventClockImage)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventTimeText)
        }
    }

    @Test
    fun isEveryDividerDisplayedOnUpcomingEventCard() {
        with(EventsRobot()) {
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventTitleDivider)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventLocationDivider)
            checkIfElementWithIdIsDisplayed(R.id.upcomingEventTimeDivider)
        }
    }

    private fun setPathDispatcher() {
        val dispatcher = FixtureDispatcher()
        val factory = PathQueryConditionFactory("")
        dispatcher.putResponse(factory.withPathSuffix("/events"), "events_200")
        dispatcher.putResponse(factory.withPathSuffix("/events/16"), "event16_200")
        mockWebServer.setDispatcher(dispatcher)
    }
}