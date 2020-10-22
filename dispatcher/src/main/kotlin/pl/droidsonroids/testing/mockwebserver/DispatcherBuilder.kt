package pl.droidsonroids.testing.mockwebserver

import pl.droidsonroids.testing.mockwebserver.condition.MultipleQueryPathCondition

class DispatcherBuilder {

    companion object {
        private const val ERROR_FILE = "error"
    }

    private val dispatcher: FixtureDispatcher = FixtureDispatcher()

    /**
     * Example usage:
     * <code>
     *     put(
     *        "/topevents/scoreboard/main" to "top_events_scoreboard",
     *        // ^^^ the URL path                ^^^ the file you are pointing to
     *        mapOf("groupId" to "")
     *        // ^^^ the request parameters
     *     )
     * </code>
     */
    fun put(
        pathAndFile: Pair<String, String>,
        queryMap: Map<String, String>? = null
    ): FixtureDispatcher {
        dispatcher.putResponse(
            MultipleQueryPathCondition(pathAndFile.first, queryMap),
            pathAndFile.second
        )
        return dispatcher
    }

    /**
     * For when you want to return a simple 404 error response
     */
    fun error(
        pathAndFile: Pair<String, String?>,
        queryMap: Map<String, String>? = null
    ): FixtureDispatcher {
        dispatcher.putResponse(
            MultipleQueryPathCondition(pathAndFile.first, queryMap),
            pathAndFile.second ?: ERROR_FILE
        )
        return dispatcher
    }

    fun error(paths: List<String>): FixtureDispatcher {
        paths.forEach { error(it to null) }
        return dispatcher
    }
}

fun getDispatcher(
    func: DispatcherBuilder.() -> FixtureDispatcher
): FixtureDispatcher = DispatcherBuilder().run { func() }
