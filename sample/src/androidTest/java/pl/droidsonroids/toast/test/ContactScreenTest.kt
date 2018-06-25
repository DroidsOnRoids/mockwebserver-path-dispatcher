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
import pl.droidsonroids.toast.robot.ContactRobot


class ContactScreenTest {
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
        val toolbarTitle = getString(R.string.contact_title)
        with(ContactRobot()) {
            goToContactScreen()
            checkIfToolbarWithTitleIsDisplayed(toolbarTitle, R.id.toolbar)
        }
    }

    @Test
    fun isEveryFieldInContactFormDisplayed() {
        with(ContactRobot()) {
            goToContactScreen()
            checkIfElementWithIdIsDisplayed(R.id.topicSpinner)
            checkIfElementWithIdIsDisplayed(R.id.contactNameInputLayout)
            checkIfHintOnTextInputLayoutIsCorrect(R.id.contactNameInputLayout, getString(R.string.your_name))
            checkIfElementWithIdIsDisplayed(R.id.contactEmailInputLayout)
            checkIfHintOnTextInputLayoutIsCorrect(R.id.contactEmailInputLayout, getString(R.string.email_address))
            checkIfElementWithIdIsDisplayed(R.id.contactMessageInputLayout)
            checkIfHintOnEditTextIsCorrect(R.id.contactMessageEditText, getString(R.string.your_message))
        }
    }

    @Test
    fun isSendButtonDisplayedInDefaultState() {
        with(ContactRobot()) {
            goToContactScreen()
            checkIfElementWithIdIsDisplayed(R.id.disabledSendButton)
            checkIfElementWithIdIsNotDisplayed(R.id.enabledSendButton)
        }
    }

    @Test
    fun isSpinnerWithTopicsExpandableAndHasCorrectData() {
        with(ContactRobot()) {
            goToContactScreen()
            performClickOnElementWithId(R.id.topicSpinner)
            performClickOnDataWithText(getString(R.string.speak_on_the_next_event))
            checkIfSpinnerTextIsCorrect(getString(R.string.speak_on_the_next_event), R.id.topicSpinner)
            performClickOnElementWithId(R.id.topicSpinner)
            performClickOnDataWithText(getString(R.string.claim_a_reward))
            checkIfSpinnerTextIsCorrect(getString(R.string.claim_a_reward), R.id.topicSpinner)
            performClickOnElementWithId(R.id.topicSpinner)
            performClickOnDataWithText(getString(R.string.i_want_to))
            performClickOnDataWithText(getString(R.string.become_a_partner))
            checkIfSpinnerTextIsCorrect(getString(R.string.become_a_partner), R.id.topicSpinner)
        }
    }

    private fun createCounterOutput(countedText : String) : String {
        val countedTextCharNumber : Int = countedText.length
        val limitOfTypedText = " / 250"
        return countedTextCharNumber.toString() + limitOfTypedText
    }

    @Test
    fun isCounterVisibleAndWorkingCorrectly() {
        val countedText = "abcabcabc"
        with(ContactRobot()) {
            goToContactScreen()
            checkIfElementWithIdIsDisplayed(R.id.characterCounter)
            checkIfTextIsCorrect(createCounterOutput(""), R.id.characterCounter)
            performTyping(countedText, R.id.contactMessageEditText)
            checkIfTextIsCorrect(createCounterOutput(countedText), R.id.characterCounter)
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