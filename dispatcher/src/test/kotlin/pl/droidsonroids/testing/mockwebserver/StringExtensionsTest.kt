package pl.droidsonroids.testing.mockwebserver

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `throws on non-existent resource`() {
        val nonExistentResourceName = "non-existent"
        assertThatThrownBy { nonExistentResourceName.getResourceAsString() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining(nonExistentResourceName)
    }

    @Test
    fun `existent resource read as string`() {
        assertThat("dummy.txt".getResourceAsString()).isEqualTo("dummy")
    }

    @Test
    fun `json object is json`() {
        assertThat("{}".isPossibleJson()).isTrue
    }

    @Test
    fun `json array is json`() {
        assertThat("[]".isPossibleJson()).isTrue
    }

    @Test
    fun `non-trimmed json object is json`() {
        assertThat(" { } ".isPossibleJson()).isTrue
    }

    @Test
    fun `non-trimmed json array is json`() {
        assertThat(" [ ] ".isPossibleJson()).isTrue
    }

    @Test
    fun `non-matching braces are not json`() {
        assertThat("[}".isPossibleJson()).isFalse
        assertThat("{]".isPossibleJson()).isFalse
        assertThat("}{".isPossibleJson()).isFalse
        assertThat("}".isPossibleJson()).isFalse
        assertThat("]".isPossibleJson()).isFalse
        assertThat("{".isPossibleJson()).isFalse
    }

    @Test
    fun `garbage is not json`() {
        assertThat("".isPossibleJson()).isFalse
        assertThat(" ".isPossibleJson()).isFalse
        assertThat("test".isPossibleJson()).isFalse
        assertThat("{a".isPossibleJson()).isFalse
        assertThat("[a".isPossibleJson()).isFalse
        assertThat("a}".isPossibleJson()).isFalse
        assertThat("a[".isPossibleJson()).isFalse
    }

}