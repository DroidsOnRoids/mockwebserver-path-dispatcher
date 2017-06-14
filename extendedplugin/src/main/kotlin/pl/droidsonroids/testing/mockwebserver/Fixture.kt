package pl.droidsonroids.testing.mockwebserver

/**
 * A value container that holds all information about the fixture file.
 */
internal class Fixture {

    var statusCode = 0
        internal set
    var body: String? = null
        internal set
    var headers: List<String> = emptyList()
        internal set

    internal fun hasJsonBody(): Boolean {
        return (body?.startsWith("{") ?: false || body?.startsWith("[") ?: false)
    }

}
