package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl

/**
 * TODO
 */
class PathQueryCondition(internal val path: String,
                         internal val queryParameterName: String? = null,
                         internal val queryParameterValue: String? = null) : Condition {

    override fun compareTo(other: Condition) = when {
        other is PathQueryCondition && score > other.score -> -1
        other !is PathQueryCondition -> 0
        else -> 1
    }

    override fun isUrlMatching(url: HttpUrl): Boolean {
        val requestQueryParameterNames = url.queryParameterNames()
        if (url.encodedPath() == path) {
            if (requestQueryParameterNames.contains(queryParameterName)) {
                val requestQueryParameterValue = url.queryParameter(queryParameterName)
                if (queryParameterValue == null || queryParameterValue == requestQueryParameterValue) {
                    return true
                }
            } else if (queryParameterName == null) {
                return true
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
