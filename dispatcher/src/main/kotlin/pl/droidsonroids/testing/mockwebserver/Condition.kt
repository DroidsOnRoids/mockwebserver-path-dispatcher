package pl.droidsonroids.testing.mockwebserver

class Condition(internal val pathInfix: String,
                internal val queryParameterName: String? = null,
                internal val queryParameterValue: String? = null) : Comparable<Condition> {

    override fun compareTo(other: Condition) = when {
        score > other.score -> -1
        else -> 1
    }

    private val score: Int
        get() {
            if (queryParameterName == null) {
                return 0
            } else if (queryParameterValue == null) {
                return 1
            }
            return 2
        }

    companion object {
        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String, queryParameterValue: String) =
                Condition(pathInfix, queryParameterName, queryParameterValue)

        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String) =
                Condition(pathInfix, queryParameterName)

        fun withPathInfix(pathInfix: String) =
                Condition(pathInfix)
    }
}
