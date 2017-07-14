[![Javadocs](https://javadoc.io/badge/pl.droidsonroids.testing/mockwebserver-path-dispatcher.svg?color=brightgreen)](https://javadoc.io/doc/pl.droidsonroids.testing/mockwebserver-path-dispatcher)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids.testing/mockwebserver-path-dispatcher/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids.testing/mockwebserver-path-dispatcher)

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

### API

`PathQueryConditionFactory` - when you want to use common URL path prefix multiple times:

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

`PathQueryCondition` - when you want to match by path and optional query parameter:

```kotlin
fun pathQueryCondition() {
    val dispatcher = FixtureDispatcher()
    dispatcher.putResponse(PathQueryCondition("/prefix/infix", "param", "value"), "response_with_query_parameter_and_value")
    mockWebServer.setDispatcher(dispatcher)
    
}
```

`HttpUrlCondition` - when you want to match by some part of URL other than path or single query parameter:

```kotlin
fun httpUrlCondition() {
    val dispatcher = FixtureDispatcher()
    val condition = object : HttpUrlCondition() {
        override fun isUrlMatching(url: HttpUrl) = url.encodedUsername() == "foo"

        override fun compareTo(other: Condition) = 0
    }
    dispatcher.putResponse(condition , "response_for_foo")
    mockWebServer.setDispatcher(dispatcher)    
}
```

`Condition` - when you want to match by non-URL parts of the request e.g. headers:

```kotlin
fun condition() {
    val condition = object : Condition {
        override fun isRequestMatching(request: RecordedRequest)= request.getHeader("Content-Type") == "application/json"

        override fun compareTo(other: Condition) = 0
    }
    dispatcher.putResponse(condition , "json_response")   
}
```

### Download
For unit tests:
```gradle
testCompile 'pl.droidsonroids.testing:mockwebserver-path-dispatcher:1.0.0'
```
or for Android instrumentation tests:
```gradle
androidTestCompile 'pl.droidsonroids.testing:mockwebserver-path-dispatcher:1.0.0'
```

### License
Library uses MIT License. See [LICENSE](LICENSE) file.