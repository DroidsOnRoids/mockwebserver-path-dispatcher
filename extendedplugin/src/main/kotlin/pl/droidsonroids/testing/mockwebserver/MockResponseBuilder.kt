package pl.droidsonroids.testing.mockwebserver

internal open class MockResponseBuilder {
    internal var parser = ResourcesParser()

    internal open fun buildMockResponse(responseFixtureName: String): okhttp3.mockwebserver.MockResponse {
        val fixture = parser.parseFrom(responseFixtureName)

        val mockResponse = okhttp3.mockwebserver.MockResponse()
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