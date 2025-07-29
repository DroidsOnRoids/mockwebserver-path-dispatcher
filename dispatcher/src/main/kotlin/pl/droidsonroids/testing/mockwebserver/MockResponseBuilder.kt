package pl.droidsonroids.testing.mockwebserver

import mockwebserver3.MockResponse
import mockwebserver3.SocketEffect

internal class MockResponseBuilder constructor(private val parser: ResourcesParser) :
    ResponseBuilder {
    constructor() : this(YamlResourcesParser())

    override fun buildMockResponse(
        responseFixtureName: String,
        bodyContentTransformer: BodyContentTransformer?,
    ): MockResponse {
        val fixture = parser.parseFrom(responseFixtureName)

        val mockResponseBuilder = MockResponse.Builder()
        mockResponseBuilder.code(fixture.statusCode)

        fixture.headers.forEach {
            mockResponseBuilder.addHeader(it)
        }

        val bodyContent = fixture.bodyContent?.let { bodyContent ->
            bodyContentTransformer?.transform(bodyContent) ?: bodyContent
        }
        when (bodyContent) {
            is BodyContent.Text -> {
                mockResponseBuilder.addHeader("Content-Type: text/plain")
                mockResponseBuilder.body(bodyContent.content)
            }
            is BodyContent.Json -> {
                mockResponseBuilder.addHeader("Content-Type: application/json")
                mockResponseBuilder.body(bodyContent.content)
            }
            is BodyContent.Binary -> mockResponseBuilder.body(bodyContent.content)
            null -> Unit
        }

        when {
            fixture.connectionFailure -> {
                mockResponseBuilder.onRequestStart(SocketEffect.CloseSocket())
            }

            fixture.timeoutFailure -> {
                mockResponseBuilder.onResponseStart(SocketEffect.Stall)
            }
        }

        return mockResponseBuilder.build()
    }
}
