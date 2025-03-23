package pl.droidsonroids.testing.mockwebserver

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy

internal class MockResponseBuilder constructor(private val parser: ResourcesParser) :
    ResponseBuilder {
    constructor() : this(YamlResourcesParser())

    override fun buildMockResponse(
        responseFixtureName: String,
        bodyContentTransformer: BodyContentTransformer?,
    ): MockResponse {
        val fixture = parser.parseFrom(responseFixtureName)

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(fixture.statusCode)

        fixture.headers.forEach {
            mockResponse.addHeader(it)
        }

        val bodyContent = fixture.bodyContent?.let { bodyContent ->
            bodyContentTransformer?.transform(bodyContent) ?: bodyContent
        }
        when (bodyContent) {
            is BodyContent.Text -> {
                mockResponse.addHeader("Content-Type: text/plain")
                mockResponse.setBody(bodyContent.content)
            }
            is BodyContent.Json -> {
                mockResponse.addHeader("Content-Type: application/json")
                mockResponse.setBody(bodyContent.content)
            }
            is BodyContent.Binary -> mockResponse.setBody(bodyContent.content)
            null -> Unit
        }

        when {
            fixture.connectionFailure -> {
                mockResponse.socketPolicy = SocketPolicy.DISCONNECT_AT_START
            }

            fixture.timeoutFailure -> {
                mockResponse.socketPolicy = SocketPolicy.NO_RESPONSE
            }
        }

        return mockResponse
    }
}
