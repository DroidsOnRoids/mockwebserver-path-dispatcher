package pl.droidsonroids.testing.mockwebserver

internal interface ResourcesParser {
    fun parseFrom(fileName: String): Fixture
}