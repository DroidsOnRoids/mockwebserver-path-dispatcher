@file:JvmName("StringUtils")

package pl.droidsonroids.testing.mockwebserver

internal fun String.getResourceAsString(): String {
    val loader = Thread.currentThread().contextClassLoader
    return loader.getResource(this)?.readText() ?: throw IllegalArgumentException("Invalid path: $this")
}

internal fun String.isPossibleJson(): Boolean {
    val firstOrganicChar = trimStart().firstOrNull()
    val lastOrganicChar = trimEnd().lastOrNull()
    return (firstOrganicChar == '{' && lastOrganicChar == '}') || (firstOrganicChar == '[' && lastOrganicChar == ']')
}