package pl.droidsonroids.testing.mockwebserver

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import pl.droidsonroids.testing.mockwebserver.condition.Condition
import pl.droidsonroids.testing.mockwebserver.condition.HttpUrlCondition
import pl.droidsonroids.testing.mockwebserver.condition.MultipleQueryPathCondition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryCondition
import java.util.*

/**
 * The dispatcher using conditional fixture response mapping and enqueuing.
 * Use [putResponse] to create persistent mappings and [enqueue] for one-shots.
 * Enqueued fixtures takes precedence for the same conditions.
 */
class FixtureDispatcher internal constructor(private val responseBuilder: ResponseBuilder) : Dispatcher() {
    /**
     * Creates new dispatcher.
     * Common prefix for all the mappings can be added here instead of prepending it in all the [Condition]s.
     */
    constructor() : this(MockResponseBuilder())

    private val constantResponses: MutableMap<Condition, String> = TreeMap()
    private val queuedResponses: MutableMap<Condition, Deque<String>> = TreeMap()

    /**
     * Called by MockWebServer to determine which response should be returned for given request
     * @param request request from MockWebServer
     * @throws IllegalArgumentException when no requests can be matched
     */
    @Throws(IllegalArgumentException::class)
    override fun dispatch(request: RecordedRequest): MockResponse {
        queuedResponses.forEach { (condition, fixtures) ->
            if (condition.isRequestMatching(request)) {
                val fixture = fixtures.removeFirst()
                if (fixtures.isEmpty()) {
                    queuedResponses.remove(condition)
                }
                return responseBuilder.buildMockResponse(fixture)
            }
        }

        constantResponses.forEach { (condition, fixture) ->
            if (condition.isRequestMatching(request)) {
                return responseBuilder.buildMockResponse(fixture)
            }
        }
        throw IllegalArgumentException("Unexpected request: $request")
    }

    /**
     * Maps given <code>condition</code> to fixture named <code>responseFixtureName</code>.
     * Existing mapping of the same <code>condition</code> is overwritten.
     * Note that if there is any fixture [enqueued][enqueue] for the same <code>condition</code>
     * it will be used first.
     * @return previous mapping or <code>null</code> if there is no such
     */
    fun putResponse(condition: Condition, responseFixtureName: String) =
        constantResponses.put(condition, responseFixtureName)

    /**
     * Enqueues given <code>responseFixtureName</code> to be returned for request matching
     * given <code>condition</code>. Each enqueued fixture will be returned at most once however,
     * more of them can be enqueued for the same condition.
     */
    fun enqueue(condition: PathQueryCondition, responseFixtureName: String) =
        enqueue(condition as HttpUrlCondition, responseFixtureName)

    fun enqueue(
        pathAndResponse: Pair<String, String>,
        vararg queryParameterNameAndValue: Pair<String, String>
    ) {
        val condition = MultipleQueryPathCondition(
            pathAndResponse.first,
            *queryParameterNameAndValue
        )
        enqueue(condition, pathAndResponse.second)
    }

    fun enqueue(pathAndResponse: Pair<String, String>, queryParameters: Map<String, String>) {
        val condition = MultipleQueryPathCondition(
            pathAndResponse.first,
            queryParameters
        )
        enqueue(condition, pathAndResponse.second)
    }

    private fun enqueue(condition: HttpUrlCondition, responseName: String) {
        val fixtures = queuedResponses[condition] ?: ArrayDeque()
        fixtures += responseName
        queuedResponses[condition] = fixtures
    }
}
