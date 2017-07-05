package pl.droidsonroids.testing.mockwebserver

/**
 * Represents set of mocked request properties which have to match in received request.
 * Use one of the factory methods to instantiate it. <br>
 *     Condition consist of:
 *     <ul>
 *         <li><code>pathInfix</code> - required path infix, may be empty</li>
 *         <li><code>queryParameterName</code> - optional query parameter name</li>
 *         <li><code>queryParameterValue</code> - optional query parameter value, <code>queryParameterName</code> has to be provided</li>
 *     </ul>
 */
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
        /**
         * Creates condition with both <code>pathInfix</code>, <code>queryParameterName</code> and <code>queryParameterValue</code>.
         * @param pathInfix path infix, may be empty
         * @param queryParameterName query parameter name <code>queryParameterName</code>
         * @param queryParameterValue query parameter value for given
         * @return a Condition
         */
        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String, queryParameterValue: String) =
                Condition(pathInfix, queryParameterName, queryParameterValue)

        /**
         * Creates condition with both <code>pathInfix</code>, <code>queryParameterName</code>.
         * @param pathInfix path infix, may be empty
         * @param queryParameterName query parameter name <code>queryParameterName</code>
         * @return a Condition
         */
        fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String) =
                Condition(pathInfix, queryParameterName)

        /**
         * Creates condition with <code>pathInfix</code> only.
         * @param pathInfix path infix, may be empty
         * @return a Condition
         */
        fun withPathInfix(pathInfix: String) =
                Condition(pathInfix)
    }
}
