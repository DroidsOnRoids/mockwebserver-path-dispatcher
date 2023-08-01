package pl.droidsonroids.testing.mockwebserver.condition

import okhttp3.HttpUrl

/**
 * A [Condition] matching by URL path and a [Map] of query parameter name/value pairs.
 * Instances are sorted from least to most general, based on the number of specified query parameter names and
 * the corresponding non-null values.
 * @property path URL path, required
 * @property httpMethod [HTTPMethod] to map to for the given condition, optional
 * @property queryParameters [Map] query parameter name/value pairs, optional, defaulting to an empty map
 */
data class PathQueryCondition(
    internal val path: String,
    override val httpMethod: HTTPMethod,
    internal val queryParameters: Map<String, String?> = emptyMap()
) : HttpUrlCondition() {
    constructor(path: String, queryParameters: Map<String, String?>) :
            this(path, HTTPMethod.ANY, queryParameters)

    /**
     *
     * A [Condition] matching by URL path, optional query parameter name and optional query parameter value for that name.
     * Instances are sorted from least to most general e.g. one containing query parameter name comes before
     * another containing path only.
     * @property path URL path, required
     * @property httpMethod [HTTPMethod] to map to for the given condition, optional
     * @property queryParameterName query parameter name, optional
     * @property queryParameterName query parameter value for given name, optional
     */
    constructor(path: String, httpMethod: HTTPMethod, queryParameterName: String, queryParameterValue: String? = null) :
            this(path, httpMethod, mapOf(queryParameterName to queryParameterValue))

    constructor(path: String, queryParameterName: String, queryParameterValue: String? = null) :
            this(path, HTTPMethod.ANY, mapOf(queryParameterName to queryParameterValue))

    override fun compareTo(other: Condition) = when {
        other == this -> 0
        other is PathQueryCondition && httpMethod == HTTPMethod.ANY && httpMethod != other.httpMethod -> -1
        other is PathQueryCondition && score == other.score -> path.compareTo(other.path)
        other is PathQueryCondition && score > other.score -> -1
        else -> 1
    }

    override fun isUrlMatching(url: HttpUrl): Boolean {
        if (url.encodedPath == path) {
            when {
                queryParameters.isEmpty() -> return true
                queryParameters.keys.all { it in url.queryParameterNames } -> {
                    return queryParameters.all {
                        it.value == null || it.value == url.queryParameter(it.key)
                    }
                }
            }
        }
        return false
    }

    private val score: Int
        get() = queryParameters.keys.count() +
                queryParameters.values.count { !it.isNullOrEmpty() }
}
