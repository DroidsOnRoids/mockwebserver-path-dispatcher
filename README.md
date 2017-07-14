MockWebServer path dispatcher
=============

A helper for dispatching MockWebServer responses.


### Motivation

- YAML used to store responses for more readability (compared to bare [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver))
- Android compatible (unlike [MockWebServer+](https://github.com/orhanobut/mockwebserverplus))
- Concise dispatching logic implementation

### Example

Code with MockWebServer path dispatcher
```kotlin
fun pathCondition() {
    val dispatcher = FixtureDispatcher()
    val factory = PathQueryConditionFactory("/prefix/")
    dispatcher.putResponse(factory.withPathInfix("infix"), "body_path")
    dispatcher.putResponse(factory.withPathInfix("another_infix"), "json_object")
    mockWebServer.setDispatcher(dispatcher)
}
```
Code without MockWebServer path dispatcher
```kotlin
fun bareMockWebServer() {
    val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.requestUrl.encodedPath()
            if (path == "/prefix/infix") {
                return MockResponse()
                        .setResponseCode(404)
                        .addHeader("Content-Type", "text/plain")
                        .addHeader("Vary", "Accept-Encoding")
                        .setBody("""{"test"}""")
            } else if (path == "/prefix/another_infix") {
                return MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("{\n  \"test\": null\n}")
            }
            throw IllegalArgumentException("Unexpected request: $request")
        }
    }
    mockWebServer.setDispatcher(dispatcher)
}
```
###API

Conditions

```kotlin
fun factory() {
    val dispatcher = FixtureDispatcher()
    val factory = PathQueryConditionFactory("/prefix/")
    dispatcher.putResponse(factory.withPathInfix("infix"), "queryless_response")
    dispatcher.putResponse(factory.withPathInfixAndQueryParameter("infix", "param"), "response_with_query_parameter")
    dispatcher.putResponse(factory.withPathInfixAndQueryParameter("infix", "param", "value"), "response_with_query_parameter_and_value")
    mockWebServer.setDispatcher(dispatcher)
    
}
```

```kotlin
fun pathQueryCondition() {
    val dispatcher = FixtureDispatcher()
    dispatcher.putResponse(PathQueryCondition("/prefix/infix", "param", "value"), "response_with_query_parameter_and_value")
    mockWebServer.setDispatcher(dispatcher)
    
}
```

```kotlin
fun httpUrlCondition() {
    val dispatcher = FixtureDispatcher()
    val factory = PathQueryConditionFactory("/prefix/")
    dispatcher.putResponse(factory.withPathInfix("infix"), "queryless_response")
    dispatcher.putResponse(factory.withPathInfixAndQueryParameter("another_infix", "param"), "json_object")
    dispatcher.putResponse(factory.withPathInfixAndQueryParameter("another_infix", "param", "value"), "json_object")
    mockWebServer.setDispatcher(dispatcher)
    
}
```