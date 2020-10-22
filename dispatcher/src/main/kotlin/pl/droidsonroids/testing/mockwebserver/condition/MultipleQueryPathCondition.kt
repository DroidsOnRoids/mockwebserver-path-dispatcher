package pl.droidsonroids.testing.mockwebserver.condition

import okhttp3.HttpUrl
import kotlin.contracts.ExperimentalContracts

/**
 * A [Condition] matching by URL path, optional query parameter name and optional query parameter value for that name.
 * Instances are sorted from least to most general e.g. one containing query parameter name comes before
 * another containing path only.
 * @property path URL path, required
 * @property queryParameters map of parameter key to value, optional
 */
data class MultipleQueryPathCondition(
    internal val path: String,
    internal val queryParameters: Map<String, String>? = null
) : HttpUrlCondition() {

    constructor(
        path: String,
        vararg queryParameters: Pair<String, String>
    ) : this(path, queryParameters.toMap())

    override fun compareTo(other: Condition) = when {
        other is MultipleQueryPathCondition && score > other.score -> -1
        other == this -> 0
        else -> 1
    }

    @ExperimentalContracts
    override fun isUrlMatching(url: HttpUrl?): Boolean {
        if (url?.encodedPath == path) {
            return when (queryParameters) {
                null -> true
                else -> hasQueryParameters(url)
            }
        }
        return false
    }

    private fun hasQueryParameters(url: HttpUrl): Boolean {
        if (queryParameters == null) {
            return false
        }
        return url.queryParameterNames.all { parameterName ->
            val requestQueryParameterValue = url.queryParameter(parameterName)
            queryParameters.containsKey(parameterName) && queryParameters[parameterName] == requestQueryParameterValue
        }
    }

    private val score: Int
        get() {
            if (queryParameters == null || queryParameters.keys.isNullOrEmpty()) {
                return 0
            } else if (queryParameters.values.isNullOrEmpty()) {
                return 1
            }
            return 2
        }
}