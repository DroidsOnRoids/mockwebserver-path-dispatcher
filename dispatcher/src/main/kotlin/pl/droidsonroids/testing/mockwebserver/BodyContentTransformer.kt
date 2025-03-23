package pl.droidsonroids.testing.mockwebserver

/**
 * Transforms the response body content based before returning it to the client.
 * @since 1.2.0
 */
fun interface BodyContentTransformer {

    fun transform(bodyContent: BodyContent): BodyContent
}
