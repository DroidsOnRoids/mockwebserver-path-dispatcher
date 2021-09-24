package pl.droidsonroids.testing.mockwebserver.condition

import okhttp3.HttpUrl
import okhttp3.mockwebserver.RecordedRequest

/**
 * A Condition which matches HttpUrls only, they are extracted from request so that only
 * [isUrlMatching] needs to implemented.
 * Requests without <code>requestUrl</code>s are excluded from matching.
 */
abstract class HttpUrlCondition : Condition {
    internal open val httpMethod: HTTPMethod = HTTPMethod.ANY

    override fun isRequestMatching(request: RecordedRequest): Boolean {
        if (httpMethod != HTTPMethod.ANY && request.method != httpMethod.name) {
            return false
        }

        return when (val url = request.requestUrl) {
            null -> false
            else -> isUrlMatching(url)
        }
    }

    /**
     * @param url a URL extracted from request, never null
     * @return <code>true</code> if an argument matches this condition, <code>false</code> otherwise
     */
    abstract fun isUrlMatching(url: HttpUrl): Boolean
}