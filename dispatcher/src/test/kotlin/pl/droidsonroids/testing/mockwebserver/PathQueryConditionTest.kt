package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.mock
import okhttp3.HttpUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.condition.Condition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryCondition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory

class PathQueryConditionTest {
    private val INFIX = "/infix"
    private val PARAMETER_NAME = "param"
    private val PARAMETER_VALUE = "value"

    private lateinit var infixPathQueryCondition: PathQueryCondition
    private lateinit var parameterNamePathQueryCondition: PathQueryCondition
    private lateinit var parameterValuePathQueryCondition: PathQueryCondition

    @Before
    fun setUp() {
        val factory = PathQueryConditionFactory("")
        infixPathQueryCondition = factory.withPathInfix(INFIX)
        parameterNamePathQueryCondition = factory.withPathInfixAndQueryParameter(INFIX, PARAMETER_NAME)
        parameterValuePathQueryCondition = factory.withPathInfixAndQueryParameter(INFIX, PARAMETER_NAME, PARAMETER_VALUE)
    }

    @Test
    fun `has correct infix`() {
        assertThat(infixPathQueryCondition.path).isEqualTo(INFIX)
    }

    @Test
    fun `has correct infix and query parameter name`() {
        assertThat(parameterNamePathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterNamePathQueryCondition.queryParameterName).isEqualTo(PARAMETER_NAME)
    }

    @Test
    fun `has correct infix and query parameter name and value`() {
        assertThat(parameterValuePathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterValuePathQueryCondition.queryParameterName).isEqualTo(PARAMETER_NAME)
        assertThat(parameterValuePathQueryCondition.queryParameterValue).isEqualTo(PARAMETER_VALUE)
    }

    @Test
    fun `is name less than infix`() {
        assertThat(parameterNamePathQueryCondition).isLessThan(infixPathQueryCondition)
    }

    @Test
    fun `is name greater than value`() {
        assertThat(parameterNamePathQueryCondition).isGreaterThan(parameterValuePathQueryCondition)
    }

    @Test
    fun `is value less than infix`() {
        assertThat(parameterValuePathQueryCondition).isLessThan(infixPathQueryCondition)
    }

    @Test
    fun `is unrelated condition equal to path query condition`() {
        assertThat(parameterValuePathQueryCondition as Condition).isEqualByComparingTo(mock<Condition>())
    }

    @Test
    fun `url with different path does not match`() {
        val url = HttpUrl.parse("http://test.test")!!
        assertThat(infixPathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `url with different query does not match`() {
        val url = HttpUrl.parse("http://test.test?another")!!
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `url with equal infix and no query parameter matches`() {
        val url = HttpUrl.parse("http://test.test/infix")!!
        assertThat(infixPathQueryCondition.isUrlMatching(url)).isTrue()
    }

    @Test
    fun `url with infix and query parameter name matches`() {
        val url = HttpUrl.parse("http://test.test/infix?param")!!
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isTrue()
    }

    @Test
    fun `url with equal infix and different query parameter name does not match`() {
        val url = HttpUrl.parse("http://test.test/infix?param2")!!
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `url with infix, query parameter name and value matches`() {
        val url = HttpUrl.parse("http://test.test/infix?param=value")!!
        assertThat(parameterValuePathQueryCondition.isUrlMatching(url)).isTrue()
    }

    @Test
    fun `url with equal infix, query parameter name with different value does not match`() {
        val url = HttpUrl.parse("http://test.test/infix?param=value2")!!
        assertThat(parameterValuePathQueryCondition.isUrlMatching(url)).isFalse()
    }
}