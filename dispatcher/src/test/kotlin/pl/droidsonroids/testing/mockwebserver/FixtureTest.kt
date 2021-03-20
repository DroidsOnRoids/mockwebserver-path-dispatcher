package pl.droidsonroids.testing.mockwebserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class FixtureTest {
    private lateinit var fixture: Fixture

    @Before
    fun setUp() {
        fixture = Fixture()
    }

    @Test
    fun `status code is zero by default`() {
        assertThat(fixture.statusCode).isZero
    }

    @Test
    fun `headers are empty by default`() {
        assertThat(fixture.headers).isEmpty()
    }

    @Test
    fun `has no body by default`() {
        assertThat(fixture.body).isNull()
    }

    @Test
    fun `missing body is not json`() {
        assertThat(fixture.hasJsonBody()).isFalse
    }

    @Test
    fun `empty body is not json`() {
        fixture.body = ""
        assertThat(fixture.hasJsonBody()).isFalse
    }

    @Test
    fun `textual body is not json`() {
        fixture.body = "test"
        assertThat(fixture.hasJsonBody()).isFalse
    }


    @Test
    fun `json object body is not json`() {
        fixture.body = "{}"
        assertThat(fixture.hasJsonBody()).isTrue
    }

}