package pl.droidsonroids.testing.mockwebserver.condition

import okhttp3.HttpUrl

/**
 * A [Condition] matching by URL path, optional query parameter name and optional query parameter value for that name.
 * Instances are sorted from least to most general e.g. one containing query parameter name comes before
 * another containing path only.
 * @property path URL path, required
 * @property queryParameterName query parameter name, optional
 * @property queryParameterName query parameter value for given name, optional
 */
data class PathQueryCondition(
    internal val path: String,
    internal val queryParameterName: String? = null,
    internal val queryParameterValue: String? = null
) : HttpUrlCondition() {

    override fun compareTo(other: Condition) = when {
        other is PathQueryCondition && score > other.score -> -1
        other == this -> 0
        else -> 1
    }

    override fun isUrlMatching(url: HttpUrl): Boolean {
        val requestQueryParameterNames = url.queryParameterNames
        if (url.encodedPath == path) {
            when {
                queryParameterName == null -> return true
                requestQueryParameterNames.contains(queryParameterName) -> {
                    val requestQueryParameterValue = url.queryParameter(queryParameterName)
                    if (queryParameterValue == null || queryParameterValue == requestQueryParameterValue) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private val score: Int
        get() {
            if (queryParameterName == null) {
                return 0
            } else if (queryParameterValue == null) {
                return 1
            }
            return 2
        }

}
