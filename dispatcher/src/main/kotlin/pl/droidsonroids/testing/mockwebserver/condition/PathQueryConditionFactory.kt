package pl.droidsonroids.testing.mockwebserver.condition

/**
 * Factory which can be used to create similar [PathQueryCondition]s sharing the same path prefix
 * so it does not need to be repeated each time when new [PathQueryCondition] is needed.
 * @param pathPrefix common path prefix
 * @constructor creates new factory with given prefix
 */
class PathQueryConditionFactory(private val pathPrefix: String) {
    /**
     * Creates condition with both <code>path</code>, <code>queryParameterName</code>
     * and <code>queryParameterValue</code>.
     * @param pathInfix path infix, may be empty
     * @param queryParameterName query parameter name <code>queryParameterName</code>
     * @param queryParameterValue query parameter value for given
     * @return a PathQueryCondition
     */
    fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String, queryParameterValue: String) =
            PathQueryCondition(pathPrefix + pathInfix, queryParameterName, queryParameterValue)

    /**
     * Creates condition with both <code>path</code>, <code>queryParameterName</code>.
     * @param pathInfix path infix, may be empty
     * @param queryParameterName query parameter name <code>queryParameterName</code>
     * @return a PathQueryCondition
     */
    fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String) =
            PathQueryCondition(pathPrefix + pathInfix, queryParameterName)

    /**
     * Creates condition with <code>path</code> only.
     * @param pathInfix path infix, may be empty
     * @return a PathQueryCondition
     */
    fun withPathInfix(pathInfix: String) =
            PathQueryCondition(pathPrefix + pathInfix)
}