package pl.droidsonroids.testing.mockwebserver.condition

import okhttp3.mockwebserver.RecordedRequest

/**
 * Represents set of mocked request properties which have to match received request.
 * Conditions are checked by <code>FixtureDispatcher</code> in order defined by values returned from <code>compareTo</code>.
 */
interface Condition : Comparable<Condition> {
    /**
     * Checks whether request from MockWebServer matches this condition.
     * @param request request from MockWebServer
     * @return <code>true</code> if an argument matches this condition, <code>false</code> otherwise
     */
    fun isRequestMatching(request: RecordedRequest): Boolean
}