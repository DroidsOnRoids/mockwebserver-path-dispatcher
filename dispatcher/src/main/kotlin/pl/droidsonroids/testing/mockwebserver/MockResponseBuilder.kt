package pl.droidsonroids.testing.mockwebserver

import okhttp3.mockwebserver.MockResponse

internal class MockResponseBuilder constructor(private val parser: ResourcesParser) : ResponseBuilder {
    constructor() : this(YamlResourcesParser())

    override fun buildMockResponse(responseFixtureName: String): MockResponse {
        val fixture = parser.parseFrom(responseFixtureName)

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(fixture.statusCode)

        fixture.body?.let(mockResponse::setBody)

        fixture.headers.forEach {
            mockResponse.addHeader(it)
        }

        return mockResponse
    }
}