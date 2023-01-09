package pl.droidsonroids.testing.mockwebserver

internal class Fixture {

    var statusCode = 0
        internal set
    var body: String? = null
        internal set
    var headers: List<String> = emptyList()
        internal set
    var connectionFailure: Boolean = false
        internal set
    var timeoutFailure: Boolean = false
        internal set

    internal fun hasJsonBody() = body?.isPossibleJson() ?: false
}
