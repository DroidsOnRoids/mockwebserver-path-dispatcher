package pl.droidsonroids.testing.mockwebserver

import okhttp3.mockwebserver.MockResponse

internal interface ResponseBuilder {
    fun buildMockResponse(responseFixtureName: String): MockResponse
}