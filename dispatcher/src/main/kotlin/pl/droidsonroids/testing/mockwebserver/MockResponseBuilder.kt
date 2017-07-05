package pl.droidsonroids.testing.mockwebserver

import okhttp3.mockwebserver.MockResponse

internal open class MockResponseBuilder internal constructor(private val parser: ResourcesParser) {
    internal constructor() : this(ResourcesParser())

    internal open fun buildMockResponse(responseFixtureName: String): MockResponse {
        val fixture = parser.parseFrom(responseFixtureName)

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(fixture.statusCode)

        if (fixture.body != null) {
            mockResponse.setBody(fixture.body!!)
        }
        fixture.headers.forEach {
            mockResponse.addHeader(it)
        }

        return mockResponse
    }
}