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
class FixtureDispatcher internal constructor(private val responseBuilder: ResponseBuilder) : Dispatcher() {
    /**
     * Creates new dispatcher.
     * Common prefix for all the mappings can be added here instead of prepending it in all the Conditions.
     */
    constructor() : this(MockResponseBuilder())

    private val responses: MutableMap<Condition, String> = TreeMap()

    @Throws(IllegalArgumentException::class)
    override fun dispatch(request: RecordedRequest) = dispatch(request.requestUrl)

    internal fun dispatch(url: HttpUrl): MockResponse {
        responses.forEach { (condition, fixture) ->
            if (condition.isUrlMatching(url)) {
                return responseBuilder.buildMockResponse(fixture)
            }
        }
        throw IllegalArgumentException("Unexpected request: $url")
    }

    /**
     * Maps given <code>condition</code> to fixture named <code>responseFixtureName</code>.
     * Existing mapping of the same <code>condition</code> is overwritten.
     * @return previous mapping or null if there is no such
     */
    fun putResponse(condition: Condition, responseFixtureName: String) =
        responses.put(condition, responseFixtureName)

}
