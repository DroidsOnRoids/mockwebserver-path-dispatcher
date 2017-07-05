package pl.droidsonroids.testing.mockwebserver

class Condition : Comparable<Condition> {
    internal lateinit var pathInfix: String
    internal var queryParameterName: String? = null
    internal var queryParameterValue: String? = null

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

        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String, queryParameterValue: String): Condition {
            val condition = Condition.Companion.withPathInfixAndQueryParameter(pathInfix, queryParameterName)
            condition.queryParameterValue = queryParameterValue
            return condition
        }

        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String): Condition {
            val condition = Condition.Companion.withPathInfix(pathInfix)
            condition.queryParameterName = queryParameterName
            return condition
        }

        fun withPathInfix(pathInfix: String): Condition {
            val condition = Condition()
            condition.pathInfix = pathInfix
            return condition
        }
    }
}
