package pl.droidsonroids.testing.mockwebserver

class Condition : Comparable<pl.droidsonroids.testing.mockwebserver.Condition> {
    internal lateinit var pathInfix: String
    internal var queryParameterName: String? = null
    internal var queryParameterValue: String? = null

    override fun compareTo(o: pl.droidsonroids.testing.mockwebserver.Condition) = when {
        score > o.score -> -1
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

        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String, queryParameterValue: String): pl.droidsonroids.testing.mockwebserver.Condition {
            val condition = pl.droidsonroids.testing.mockwebserver.Condition.Companion.withPathInfixAndQueryParameter(pathInfix, queryParameterName)
            condition.queryParameterValue = queryParameterValue
            return condition
        }

        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String): pl.droidsonroids.testing.mockwebserver.Condition {
            val condition = pl.droidsonroids.testing.mockwebserver.Condition.Companion.withPathInfix(pathInfix)
            condition.queryParameterName = queryParameterName
            return condition
        }

        fun withPathInfix(pathInfix: String): pl.droidsonroids.testing.mockwebserver.Condition {
            val condition = pl.droidsonroids.testing.mockwebserver.Condition()
            condition.pathInfix = pathInfix
            return condition
        }
    }
}
