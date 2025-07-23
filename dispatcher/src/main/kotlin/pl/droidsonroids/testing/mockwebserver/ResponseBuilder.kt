package pl.droidsonroids.testing.mockwebserver

import mockwebserver3.MockResponse

internal interface ResponseBuilder {
    fun buildMockResponse(
        responseFixtureName: String,
        bodyContentTransformer: BodyContentTransformer? = null,
    ): MockResponse
}
