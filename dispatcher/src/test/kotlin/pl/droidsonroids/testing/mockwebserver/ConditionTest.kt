package pl.droidsonroids.testing.mockwebserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ConditionTest {
    private val INFIX = "test"
    private val PARAMETER_NAME = "param"
    private val PARAMETER_VALUE = "value"

    private lateinit var infixCondition: Condition
    private lateinit var parameterNameCondition: Condition
    private lateinit var parameterValueCondition: Condition

    @Before
    fun setUp() {
        infixCondition = Condition.Companion.withPathInfix(INFIX)
        parameterNameCondition = Condition.Companion.withPathInfixAndQueryParameter(INFIX, PARAMETER_NAME)
        parameterValueCondition = Condition.Companion.withPathInfixAndQueryParameter(INFIX, PARAMETER_NAME, PARAMETER_VALUE)
    }

    @Test
    fun `has correct infix`() {
        assertThat(infixCondition.pathInfix).isEqualTo(INFIX)
    }

    @Test
    fun `has correct infix and query parameter name`() {
        assertThat(parameterNameCondition.pathInfix).isEqualTo(INFIX)
        assertThat(parameterNameCondition.queryParameterName).isEqualTo(PARAMETER_NAME)
    }

    @Test
    fun `has correct infix and query parameter name and value`() {
        assertThat(parameterValueCondition.pathInfix).isEqualTo(INFIX)
        assertThat(parameterValueCondition.queryParameterName).isEqualTo(PARAMETER_NAME)
        assertThat(parameterValueCondition.queryParameterValue).isEqualTo(PARAMETER_VALUE)
    }

    @Test
    fun `is name less than infix`() {
        assertThat(parameterNameCondition).isLessThan(infixCondition)
    }

    @Test
    fun `is name greater than value`() {
        assertThat(parameterNameCondition).isGreaterThan(parameterValueCondition)
    }

    @Test
    fun `is value less than infix`() {
        assertThat(parameterValueCondition).isLessThan(infixCondition)
    }
}