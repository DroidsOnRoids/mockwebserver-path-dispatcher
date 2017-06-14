package pl.droidsonroids.testing.mockwebserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.Condition

private const val INFIX = "test"
private const val PARAMETER_NAME = "param"
private const val PARAMETER_VALUE = "value"

class ConditionTest {

    private lateinit var infixCondition: pl.droidsonroids.testing.mockwebserver.Condition

    private lateinit var parameterNameCondition: pl.droidsonroids.testing.mockwebserver.Condition

    private lateinit var parameterValueCondition: pl.droidsonroids.testing.mockwebserver.Condition

    @org.junit.Before
    fun setUp() {
        infixCondition = pl.droidsonroids.testing.mockwebserver.Condition.Companion.withPathInfix(INFIX)
        parameterNameCondition = pl.droidsonroids.testing.mockwebserver.Condition.Companion.withPathInfixAndQueryParameter(INFIX, PARAMETER_NAME)
        parameterValueCondition = pl.droidsonroids.testing.mockwebserver.Condition.Companion.withPathInfixAndQueryParameter(INFIX, PARAMETER_NAME, PARAMETER_VALUE)
    }

    @org.junit.Test
    fun `has correct infix`() {
        assertThat(infixCondition.pathInfix).isEqualTo(pl.droidsonroids.testing.mockwebserver.INFIX)
    }

    @org.junit.Test
    fun `has correct infix and query parameter name`() {
        assertThat(parameterNameCondition.pathInfix).isEqualTo(pl.droidsonroids.testing.mockwebserver.INFIX)
        assertThat(parameterNameCondition.queryParameterName).isEqualTo(pl.droidsonroids.testing.mockwebserver.PARAMETER_NAME)
    }

    @org.junit.Test
    fun `has correct infix and query parameter name and value`() {
        assertThat(parameterValueCondition.pathInfix).isEqualTo(pl.droidsonroids.testing.mockwebserver.INFIX)
        assertThat(parameterValueCondition.queryParameterName).isEqualTo(pl.droidsonroids.testing.mockwebserver.PARAMETER_NAME)
        assertThat(parameterValueCondition.queryParameterValue).isEqualTo(pl.droidsonroids.testing.mockwebserver.PARAMETER_VALUE)
    }

    @org.junit.Test
    fun `is name less than infix`() {
        assertThat(parameterNameCondition).isLessThan(infixCondition)
    }

    @org.junit.Test
    fun `is name greater than value`() {
        assertThat(parameterNameCondition).isGreaterThan(parameterValueCondition)
    }

    @org.junit.Test
    fun `is value less than infix`() {
        assertThat(parameterValueCondition).isLessThan(infixCondition)
    }
}