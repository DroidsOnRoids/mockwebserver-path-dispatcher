package pl.droidsonroids.testing.mockwebserver

class FixtureDispatcher (protected val urlInfix: String) : okhttp3.mockwebserver.Dispatcher() {
    internal var responseBuilder = MockResponseBuilder()

    protected val responses: MutableMap<Condition, String> = java.util.TreeMap()

    override fun dispatch(request: okhttp3.mockwebserver.RecordedRequest): okhttp3.mockwebserver.MockResponse {
        val url = request.requestUrl

        val query = request.body.readUtf8()
        return dispatch(request.path, query)
    }

    internal fun dispatch(path: String, query: String): okhttp3.mockwebserver.MockResponse {
        val partialInfix = path.replaceFirst(urlInfix.toRegex(), "")
        responses.forEach { (condition, fixture) ->
            if (partialInfix.startsWith(condition.pathInfix)) {
                if (condition.queryParameterName == null) {
                    return responseBuilder.buildMockResponse(fixture)
                } else {
                    val queryUri = android.net.Uri.Builder().encodedQuery(query).build()
                    val queryParameterValue = queryUri.getQueryParameter(condition.queryParameterName)
                    if (condition.queryParameterValue == null || condition.queryParameterValue == queryParameterValue) {
                        return responseBuilder.buildMockResponse(fixture)
                    }
                }
            }
        }
        throw IllegalArgumentException("Unexpected request path: $path")
    }

    fun addResponse(condition: Condition, responseFixtureName: String) {
        responses.put(condition, responseFixtureName)
    }

}
