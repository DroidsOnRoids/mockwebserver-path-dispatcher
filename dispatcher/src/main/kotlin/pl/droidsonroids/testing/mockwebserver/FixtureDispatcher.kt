package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.util.*

class FixtureDispatcher internal constructor(private val pathPrefix: String, private val responseBuilder: MockResponseBuilder) : Dispatcher() {
    constructor(pathPrefix: String) : this(pathPrefix, MockResponseBuilder())

    private val responses: MutableMap<Condition, String> = TreeMap()

    override fun dispatch(request: RecordedRequest): MockResponse {
        return dispatch(request.requestUrl)
    }

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

    fun addResponse(condition: Condition, responseFixtureName: String) {
        responses.put(condition, responseFixtureName)
    }

}
