package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.util.*

/**
 * The dispatcher using conditional fixture response mapping.
 * Use <code>putResponse(condition, responseFixtureName)</code> to create mappings.
 */
class FixtureDispatcher internal constructor(private val pathPrefix: String,
                                             private val responseBuilder: ResponseBuilder) : Dispatcher() {
    /**
     * Creates new dispatcher with optional path prefix.
     * Common prefix for all the mappings can be added here instead of prepending it in all the Conditions.
     * @param pathPrefix optional path prefix (pass empty String if not needed)
     */
    constructor(pathPrefix: String) : this(pathPrefix, MockResponseBuilder())

    private val responses: MutableMap<Condition, String> = TreeMap()

    @Throws(IllegalArgumentException::class)
    override fun dispatch(request: RecordedRequest) = dispatch(request.requestUrl)

    internal fun dispatch(url: HttpUrl): MockResponse {
        val requestPathSuffix = url.encodedPath().removePrefix(pathPrefix)
        val requestQueryParameterNames = url.queryParameterNames()

        responses.forEach { (condition, fixture) ->
            if (requestPathSuffix.startsWith(condition.pathInfix)) {
                if (condition.queryParameterName == null) {
                    return responseBuilder.buildMockResponse(fixture)
                } else if (requestQueryParameterNames.contains(condition.queryParameterName)) {
                    val requestQueryParameterValue = url.queryParameter(condition.queryParameterName)
                    if (condition.queryParameterValue == null || condition.queryParameterValue == requestQueryParameterValue) {
                        return responseBuilder.buildMockResponse(fixture)
                    }
                }
            }
        }
        throw IllegalArgumentException("Unexpected request: $url")
    }

    /**
     * Maps given <code>condition</code> to fixture named <code>responseFixtureName</code>.
     * Existing mapping of the same <code>condition</code> is overwritten.
     */
    fun putResponse(condition: Condition, responseFixtureName: String) {
        responses.put(condition, responseFixtureName)
    }

}
