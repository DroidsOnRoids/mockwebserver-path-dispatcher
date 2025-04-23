package pl.droidsonroids.testing.mockwebserver

import okio.Buffer

internal class Fixture {

    var statusCode = 0
    var body: String? = null
    var bodyContent: BodyContent? = null
    var headers: List<String> = emptyList()
    var connectionFailure: Boolean = false
    var timeoutFailure: Boolean = false

    internal fun hasJsonBody() = body?.isPossibleJson() ?: false
}

sealed class BodyContent {

    data class Text(val content: String) : BodyContent()
    data class Json(val content: String) : BodyContent()
    data class Binary(val content: Buffer) : BodyContent()
}
