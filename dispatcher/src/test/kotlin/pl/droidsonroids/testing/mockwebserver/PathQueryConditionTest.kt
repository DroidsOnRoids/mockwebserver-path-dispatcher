package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockito_kotlin.mock
import nl.jqno.equalsverifier.EqualsVerifier
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.condition.Condition
import pl.droidsonroids.testing.mockwebserver.condition.HTTPMethod
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryCondition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory

private const val INFIX = "/suffix"
private const val PARAMETER_NAME = "param"
private const val PARAMETER_VALUE = "value"
private const val PARAMETER_NAME2 = "param2"
private const val PARAMETER_VALUE2 = "value2"
private val PARAMETER_MAP_SINGLE = mapOf(PARAMETER_NAME to PARAMETER_VALUE)
private val PARAMETER_MAP_MULTIPLE_NULL = mapOf(PARAMETER_NAME to PARAMETER_VALUE, PARAMETER_NAME2 to null)
private val PARAMETER_MAP_MULTIPLE = mapOf(PARAMETER_NAME to PARAMETER_VALUE, PARAMETER_NAME2 to PARAMETER_VALUE2)

class PathQueryConditionTest {
    private lateinit var suffixPathQueryCondition: PathQueryCondition
    private lateinit var parameterNamePathQueryCondition: PathQueryCondition
    private lateinit var parameterValuePathQueryCondition: PathQueryCondition
    private lateinit var parameterMapPathQueryCondition: PathQueryCondition
    private lateinit var parameterMapNullPathQueryCondition: PathQueryCondition

    @Before
    fun setUp() {
        val factory = PathQueryConditionFactory("")
        suffixPathQueryCondition = factory.withPathSuffix(INFIX)
        parameterNamePathQueryCondition =
            factory.withPathSuffixAndQueryParameter(INFIX, PARAMETER_NAME)
        parameterValuePathQueryCondition =
            factory.withPathSuffixAndQueryParameter(INFIX, PARAMETER_NAME, PARAMETER_VALUE)
        parameterMapPathQueryCondition =
            factory.withPathSuffixAndQueryParameters(INFIX, PARAMETER_MAP_MULTIPLE)
        parameterMapNullPathQueryCondition =
            factory.withPathSuffixAndQueryParameters(INFIX, PARAMETER_MAP_MULTIPLE_NULL)
    }

    @Test
    fun `has correct suffix`() {
        assertThat(suffixPathQueryCondition.path).isEqualTo(INFIX)
    }

    @Test
    fun `has correct suffix and query parameter name`() {
        assertThat(parameterNamePathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterNamePathQueryCondition.queryParameters.keys.first()).isEqualTo(PARAMETER_NAME)
    }

    @Test
    fun `has correct suffix and query parameter name and value`() {
        assertThat(parameterValuePathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterValuePathQueryCondition.queryParameters.entries.first().key).isEqualTo(PARAMETER_NAME)
        assertThat(parameterValuePathQueryCondition.queryParameters.entries.first().value).isEqualTo(PARAMETER_VALUE)
    }

    @Test
    fun `has correct suffix and query parameter names and values`() {
        assertThat(parameterMapPathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterMapPathQueryCondition.queryParameters.keys).containsAll(
            setOf(
                PARAMETER_NAME,
                PARAMETER_NAME2
            )
        )
        assertThat(parameterMapPathQueryCondition.queryParameters.values).containsAll(
            setOf(
                PARAMETER_VALUE,
                PARAMETER_VALUE2
            )
        )
    }

    @Test
    fun `is name less than suffix`() {
        assertThat(parameterNamePathQueryCondition).isLessThan(suffixPathQueryCondition)
    }

    @Test
    fun `is name greater than value`() {
        assertThat(parameterNamePathQueryCondition).isGreaterThan(parameterValuePathQueryCondition)
    }

    @Test
    fun `is value less than suffix`() {
        assertThat(parameterValuePathQueryCondition).isLessThan(suffixPathQueryCondition)
    }

    @Test
    fun `is map less than suffix`() {
        assertThat(parameterMapPathQueryCondition).isLessThan(suffixPathQueryCondition)
    }

    @Test
    fun `is map less than name`() {
        assertThat(parameterMapPathQueryCondition).isLessThan(parameterNamePathQueryCondition)
    }

    @Test
    fun `is map less than value`() {
        assertThat(parameterMapPathQueryCondition).isLessThan(parameterValuePathQueryCondition)
    }

    @Test
    fun `is map less than map null`() {
        assertThat(parameterMapPathQueryCondition).isLessThan(parameterMapNullPathQueryCondition)
    }

    @Test
    fun `is unrelated condition not equal to path query condition`() {
        assertThat(parameterValuePathQueryCondition as Condition).isNotEqualByComparingTo(mock())
    }

    @Test
    fun `url with different path does not match`() {
        val url = "http://test.test".toHttpUrl()
        assertThat(suffixPathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with different query does not match`() {
        val url = "http://test.test?another".toHttpUrl()
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with equal suffix and no query parameter matches`() {
        val url = "http://test.test/suffix".toHttpUrl()
        assertThat(suffixPathQueryCondition.isUrlMatching(url)).isTrue
    }

    @Test
    fun `url with suffix and query parameter name matches`() {
        val url = "http://test.test/suffix?param".toHttpUrl()
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isTrue
    }

    @Test
    fun `url with equal suffix and different query parameter name does not match`() {
        val url = "http://test.test/suffix?param2".toHttpUrl()
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with suffix, query parameter name and value matches`() {
        val url = "http://test.test/suffix?param=value".toHttpUrl()
        assertThat(parameterValuePathQueryCondition.isUrlMatching(url)).isTrue
    }

    @Test
    fun `url with equal suffix, query parameter name with different value does not match`() {
        val url = "http://test.test/suffix?param=value2".toHttpUrl()
        assertThat(parameterValuePathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with equal suffix, query parameter name and value does not match mapped`() {
        PathQueryCondition("/suffix", "param", "value")
        val url = "http://test.test/suffix?param=value".toHttpUrl()
        assertThat(parameterMapPathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with equal suffix, query parameter names and values matches`() {
        val url = "http://test.test/suffix?param=value&param2=value2".toHttpUrl()
        assertThat(parameterMapPathQueryCondition.isUrlMatching(url)).isTrue
    }

    @Test
    fun `url with equal suffix, first query parameter pair, different second parameter name does not match`() {
        val url = "http://test.test/suffix?param=value&param3".toHttpUrl()
        assertThat(parameterMapPathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with equal suffix, first query parameter pair and second parameter name, different second parameter value does not match`() {
        val url = "http://test.test/suffix?param=value&param2=value3".toHttpUrl()
        assertThat(parameterMapPathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with equal suffix, first query parameter pair and second parameter name, null second parameter value does not match`() {
        val url = "http://test.test/suffix?param=value&param2".toHttpUrl()
        assertThat(parameterMapPathQueryCondition.isUrlMatching(url)).isFalse
    }

    @Test
    fun `url with equal suffix, first query parameter pair and second parameter name match when second parameter value null`() {
        val url = "http://test.test/suffix?param=value&param2=value3".toHttpUrl()
        assertThat(parameterMapNullPathQueryCondition.isUrlMatching(url)).isTrue
    }

    @Test
    fun `equals and hashCode match contract`() {
        EqualsVerifier
            .forClass(PathQueryCondition::class.java)
            .verify()
    }

    @Test
    fun `alternate constructors yield equivalent results for map`() {
        val withMethod = PathQueryCondition(INFIX, HTTPMethod.ANY, PARAMETER_MAP_MULTIPLE)
        val withoutMethod = PathQueryCondition(INFIX, PARAMETER_MAP_MULTIPLE)
        assertThat(withMethod.queryParameters).containsAllEntriesOf(withoutMethod.queryParameters)
    }

    @Test
    fun `alternate constructors yield equivalent results for parameter name`() {
        val withMethod = PathQueryCondition(INFIX, HTTPMethod.ANY, PARAMETER_NAME)
        val withoutMethod = PathQueryCondition(INFIX, PARAMETER_NAME)
        assertThat(withMethod.queryParameters).containsAllEntriesOf(withoutMethod.queryParameters)
    }

    @Test
    fun `alternate constructors yield equivalent results for name and value`() {
        val withMethod = PathQueryCondition(INFIX, HTTPMethod.ANY, PARAMETER_NAME, PARAMETER_VALUE)
        val withoutMethod = PathQueryCondition(INFIX, PARAMETER_NAME, PARAMETER_VALUE)
        assertThat(withMethod.queryParameters).containsAllEntriesOf(withoutMethod.queryParameters)
    }

    @Test
    fun `compareTo should return 0 when conditions are equals without parameters`() {
        assertThat(suffixPathQueryCondition.compareTo(suffixPathQueryCondition)).isEqualTo(0)
    }

    @Test
    fun `compareTo should return 0 when conditions are equals with parameter name`() {
        assertThat(parameterNamePathQueryCondition.compareTo(parameterNamePathQueryCondition))
            .isEqualTo(0)
    }

    @Test
    fun `compareTo should return 0 when conditions are equals with parameter value`() {
        assertThat(parameterValuePathQueryCondition.compareTo(parameterValuePathQueryCondition))
            .isEqualTo(0)
    }

    @Test
    fun `compareTo should return a number lower than 0 when conditions are different with the same score and the first condition path is lower than the second`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc")
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffix("/cba")

        assertThat(firstCondition.compareTo(secondCondition))
            .isLessThan(-1)
    }

    @Test
    fun `compareTo should return a number higher than 0 when conditions are different with the same score and the first condition path is higher than the second`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffix("/cba")
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffix("/acb")

        assertThat(firstCondition.compareTo(secondCondition))
            .isGreaterThan(0)
    }

    @Test
    fun `compareTo should return -1 when the first condition has ANY http method and the second is different`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc", HTTPMethod.ANY)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc", HTTPMethod.GET)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(-1)
    }

    @Test
    fun `compareTo should return -1 when the first condition has a parameter name and the second one does not`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc")

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(-1)
    }

    @Test
    fun `compareTo should return -1 when the first condition has a parameter value and the second one does not`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME, PARAMETER_VALUE)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc")

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(-1)
    }

    @Test
    fun `compareTo should return -1 when the first condition has a parameter value and the second one has a parameter name`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME, PARAMETER_VALUE)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(-1)
    }

    @Test
    fun `compareTo should return 1 when the first condition does not have a parameter and the second one has a parameter name`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc")
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return 1 when the first condition has a parameter name and the second one has a parameter value`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME, PARAMETER_VALUE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return 1 when the first condition does not have a parameter and the second one has a parameter value`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc")
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME, PARAMETER_VALUE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return 1 when the first condition does not have a parameter and the second one has a parameter value map`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffix("/abc")
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return 1 when the first condition has a parameter name and the second one has a parameter value map`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return 1 when the first condition has a parameter name and value and the second one has a parameter value map with multiple pairs`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME, PARAMETER_VALUE)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return 0 when the first condition has a parameter name and value and the second one has a parameter value map with matching parameters`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameter("/abc", PARAMETER_NAME, PARAMETER_VALUE)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_SINGLE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(0)
    }

    @Test
    fun `compareTo should return 1 when the first condition has a parameter value map with a null value and the second has a parameter value map with non-null values`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE_NULL)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(1)
    }

    @Test
    fun `compareTo should return -1 when the first condition has a parameter value map with non-null values and the second has a map with a null value`() {
        val firstCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE)
        val secondCondition = PathQueryConditionFactory()
            .withPathSuffixAndQueryParameters("/abc", PARAMETER_MAP_MULTIPLE_NULL)

        assertThat(firstCondition.compareTo(secondCondition))
            .isEqualTo(-1)
    }
}
