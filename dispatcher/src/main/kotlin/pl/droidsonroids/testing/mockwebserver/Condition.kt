package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl

/**
 * Represents set of mocked request properties which have to match received request.
 * Conditions are checked by <code>FixtureDispatcher</code> in order defined by values returned from <code>compareTo</code>.
 */
interface Condition : Comparable<Condition> {
    /**
     * @param TODO TODO
     * @return <code>true</code> if an argument matches this condition, <code>false</code> otherwise
     */
    fun isUrlMatching(url: HttpUrl): Boolean
}