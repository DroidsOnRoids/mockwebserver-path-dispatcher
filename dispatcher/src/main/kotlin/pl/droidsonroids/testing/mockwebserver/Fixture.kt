package pl.droidsonroids.testing.mockwebserver

import okio.Buffer

internal class Fixture {

    var statusCode = 0
        internal set
    var body: String? = null
        internal set
    var bodyContent: BodyContent? = null
        internal set
    var headers: List<String> = emptyList()
        internal set
    var connectionFailure: Boolean = false
        internal set
    var timeoutFailure: Boolean = false
        internal set

    internal fun hasJsonBody() = body?.isPossibleJson() ?: false
}

sealed class BodyContent {

    data class Text(val content: String) : BodyContent()
    data class Json(val content: String) : BodyContent()
    data class Binary(val content: Buffer) : BodyContent()
}
