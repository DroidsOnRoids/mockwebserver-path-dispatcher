package pl.droidsonroids.testing.mockwebserver

internal class Fixture {

    var statusCode = 0
        internal set
    var body: String? = null
        internal set
    var headers: List<String> = emptyList()
        internal set

    internal fun hasJsonBody() = body?.isPossibleJson() ?: false

}
