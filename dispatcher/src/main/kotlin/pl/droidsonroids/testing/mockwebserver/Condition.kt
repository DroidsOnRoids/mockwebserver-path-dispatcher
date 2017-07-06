package pl.droidsonroids.testing.mockwebserver

import okhttp3.HttpUrl

/**
 * Represents set of mocked request properties which have to match in received request.
 */
interface Condition : Comparable<Condition> {
    /**
     * TODO
     */
    fun isUrlMatching(url: HttpUrl): Boolean
}