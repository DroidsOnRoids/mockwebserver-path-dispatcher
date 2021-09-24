package pl.droidsonroids.testing.mockwebserver.condition

/**
 * Factory which can be used to create similar [PathQueryCondition]s sharing the same path prefix
 * so it does not need to be repeated each time when new [PathQueryCondition] is needed.
 * @param pathPrefix optional common path prefix, empty by default
 * @constructor creates new factory with given prefix
 */
class PathQueryConditionFactory constructor(private val pathPrefix: String = "") {
    /**
     * Creates condition with both <code>path</code>, <code>queryParameterName</code>
     * and <code>queryParameterValue</code>.
     * @param pathSuffix path suffix, may be empty
     * @param queryParameterName query parameter name <code>queryParameterName</code>
     * @param queryParameterValue query parameter value for given
     * @return a PathQueryCondition
     * @since 1.1.0
     */
    @JvmOverloads
    fun withPathSuffixAndQueryParameter(
        pathSuffix: String,
        queryParameterName: String,
        queryParameterValue: String? = null,
        httpMethod: HTTPMethod = HTTPMethod.ANY,
    ) =
        PathQueryCondition(
            pathPrefix + pathSuffix,
            httpMethod,
            queryParameterName,
            queryParameterValue
        )

    /**
     * Creates condition with <code>path</code> only.
     * @param pathSuffix path suffix, may be empty
     * @return a PathQueryCondition
     * @since 1.1.0
     */
    fun withPathSuffix(
        pathSuffix: String,
        httpMethod: HTTPMethod = HTTPMethod.ANY,
    ) =
        PathQueryCondition(
            pathPrefix + pathSuffix,
            httpMethod,
        )

    /**
     * Creates condition with <code>path</code> only.
     * @param path the path
     * @return a PathQueryCondition
     */
    fun withPath(
        path: String,
        httpMethod: HTTPMethod = HTTPMethod.ANY,
    ) =
        PathQueryCondition(
            path,
            httpMethod,
        )

    /**
     * Creates condition with both <code>path</code>, <code>queryParameterName</code>
     * and <code>queryParameterValue</code>.
     * @see PathQueryConditionFactory.withPathSuffixAndQueryParameter
     * @param pathInfix path suffix, may be empty
     * @param queryParameterName query parameter name <code>queryParameterName</code>
     * @param queryParameterValue query parameter value for given
     * @return a PathQueryCondition
     */
    @Deprecated(
        "Infix renamed to suffix",
        replaceWith = ReplaceWith("withPathSuffixAndQueryParameter")
    )
    fun withPathInfixAndQueryParameter(
        pathInfix: String,
        queryParameterName: String,
        queryParameterValue: String,
        httpMethod: HTTPMethod = HTTPMethod.ANY,
    ) =
        withPathSuffixAndQueryParameter(
            pathInfix,
            queryParameterName,
            queryParameterValue,
            httpMethod
        )

    /**
     * Creates condition with both <code>path</code>, <code>queryParameterName</code>.
     * @see PathQueryConditionFactory.withPathSuffixAndQueryParameter
     * @param pathInfix path suffix, may be empty
     * @param queryParameterName query parameter name <code>queryParameterName</code>
     * @return a PathQueryCondition
     */
    @Deprecated(
        "Infix renamed to suffix",
        replaceWith = ReplaceWith("withPathSuffixAndQueryParameter")
    )
    fun withPathInfixAndQueryParameter(pathInfix: String, queryParameterName: String) =
        withPathSuffixAndQueryParameter(pathInfix, queryParameterName)

    /**
     * Creates condition with <code>path</code> only.
     * @see PathQueryConditionFactory.withPathSuffix
     * @param pathInfix path suffix, may be empty
     * @return a PathQueryCondition
     */
    @Deprecated("Infix renamed to suffix", replaceWith = ReplaceWith("withPathSuffix"))
    fun withPathInfix(pathInfix: String) =
        withPathSuffix(pathInfix)
}