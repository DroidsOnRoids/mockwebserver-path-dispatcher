[![Javadocs](https://javadoc.io/badge/pl.droidsonroids.testing/mockwebserver-path-dispatcher.svg?color=blue)](https://javadoc.io/doc/pl.droidsonroids.testing/mockwebserver-path-dispatcher)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids.testing/mockwebserver-path-dispatcher/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids.testing/mockwebserver-path-dispatcher)

MockWebServer path dispatcher
=============

A helper for dispatching MockWebServer responses. It allows to easily mock responses with data
stored in YAML files in `resources/fixtures/` directory

### Motivation

- YAML used to store responses for more readability (compared to
  bare [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver))
- Android compatible (unlike [MockWebServer+](https://github.com/orhanobut/mockwebserverplus))
- Concise dispatching logic implementation

### Example

Code with MockWebServer path dispatcher:

```kotlin
fun pathCondition() {
    val dispatcher = FixtureDispatcher()
    // match all URLs with path starting with /prefix/ e.g. http://example.test/prefix/
    val factory = PathQueryConditionFactory("/prefix/")
    // match all URLs with path ending with "suffix" and return response from fixtures/body_text_path.yaml
    dispatcher.putResponse(factory.withPathSuffix("suffix"), "body_text_path")
    dispatcher.putResponse(factory.withPathSuffix("another_suffix"), "json_object")
    mockWebServer.setDispatcher(dispatcher)
}
```

Example YAML file at `resources/fixtures/json_object.yaml`:

```yaml
statusCode : 200
headers:
- 'Content-Type: application/json'
body: >
    {
      "test": null
    }
```

Instead of defining body in yaml directly you can specify relative path to file with body.
There are 3 different types of files supported: json, text, and binary.

### JSON

By default the file is read as a JSON file. Id you don't specify the `Content-Type` it defaults to 
`application/json` and creates a `BodyContent.Json`:

```yaml
statusCode : 404
headers:
- "Vary: Accept-Encoding"
body: body_json.txt
```
### Plain text

To create plain text responses, set the `Content-Type` to `text/plain`. The body will be an instance
of `BodyContent.Text`:

```yaml
statusCode : 404
headers:
- 'Content-Type: text/plain'
- "Vary: Accept-Encoding"
body: body.txt
```

### Binary

To create binary responses, set the `Content-Type` to anythin different than `application-json` or 
`text/plain`. The body will be an instance of `BodyContent.Binary`:

```yaml
statusCode : 404
headers:
- 'Content-Type: image/jpeg'
- "Vary: Accept-Encoding"
body: image.jpeg
```

### Connection issues simulation

You can force the request to fail by setting `connectionFailure` to `true`:

```yaml
statusCode : 200
connectionFailure: true
```

Alternatively, you can specify not getting a response by simulating a timeout with `timeoutFailure` to `true`:

```yaml
statusCode : 200
timeoutFailure: true
```

### MockWebServer without this library

Code without MockWebServer path dispatcher:

```kotlin
fun bareMockWebServer() {
    val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.requestUrl.encodedPath()
            if (path == "/prefix/suffix") {
                return MockResponse()
                        .setResponseCode(404)
                        .addHeader("Content-Type", "text/plain")
                        .addHeader("Vary", "Accept-Encoding")
                        .setBody("""{"test"}""")
            } else if (path == "/prefix/another_suffix") {
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

See more examples
at [FunctionalTest.kt](dispatcher/src/test/kotlin/pl/droidsonroids/testing/mockwebserver/FunctionalTest.kt)

### API

`FixtureDispatcher` - when you want conditional fixture response mapping and enqueuing:

```kotlin
fun factory() {
    val dispatcher = FixtureDispatcher()
    val factory = PathQueryConditionFactory("/prefix/")
    // bar will be served for first matching requests
    dispatcher.enqueue(factory.withPathSuffix("suffix"), "bar")
    // bar will be served for second matching requests
    dispatcher.enqueue(factory.withPathSuffix("suffix"), "baz")
    // foo will be served by default (if there is nothing enqueued) for subsequent matching requests
    dispatcher.putResponse(factory.withPathSuffix("suffix"), "foo")    
    // qux will be served by default when there are no matching requests
    dispatcher.setFallbackResponse("qux")    
    mockWebServer.setDispatcher(dispatcher)
}
```

`PathQueryConditionFactory` - when you want to use common URL path prefix multiple times:

```kotlin
fun factory() {
    val dispatcher = FixtureDispatcher()
    val factory = PathQueryConditionFactory("/prefix/")
    dispatcher.putResponse(factory.withPathSuffix("suffix"), "queryless_response")
    // match all URLs with path ending with "suffix" and have "param" with any value as query parameter e.g. http://example.test/prefix/user/suffix?param
    dispatcher.putResponse(factory.withPathSuffixAndQueryParameter("suffix", "param"), "response_with_query_parameter")
    // match all URLs with path ending with "suffix" and have "param" with "value" as query parameter e.g. http://example.test/prefix/user/suffix?param=value
    dispatcher.putResponse(factory.withPathSuffixAndQueryParameter("suffix", "param", "value"), "response_with_query_parameter_and_value")
    // match all URLs with path ending with "suffix" and have multiple parameter name/value pairs e.g.http://example.test/prefix/user/suffix?param=value&param2=value2
    dispatcher.putResponse(
        factory.withPathSuffixAndQueryParameters("suffix", mapOf("param" to "value", "param2" to "value2")),
        "response_with_multiple_query_parameters"
    )
    mockWebServer.setDispatcher(dispatcher)
}
```

`PathQueryCondition` - when you want to match by path and optional query parameter:

```kotlin
fun pathQueryCondition() {
    val dispatcher = FixtureDispatcher()
    dispatcher.putResponse(PathQueryCondition("/prefix/suffix", "param", "value"), "response_with_query_parameter_and_value")
    mockWebServer.setDispatcher(dispatcher)
    
}
```
Also supports a map of multiple query parameters:

```kotlin
fun pathQueryConditions() {
    val dispatcher = FixtureDispatcher()
    dispatcher.putResponse(PathQueryCondition("/prefix/suffix", mapOf("param" to "value", "param2" to "value2")), "response_with_query_parameters_and_values")
    mockWebServer.setDispatcher(dispatcher)
}
```

`HttpUrlCondition` - when you want to match by some part of URL other than path or single query
parameter:

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

`BodyContentTransformer` - when you want to modify the response before being returned to the user 
you can create a transformer and set it to the dispatcher. The transformer is very useful when you 
want to replace some urls (i.e. images) to your mock server url so they go through the dispatcher 
and you can then be able to mock the responses and load fake image or files:

```kotlin
fun transformResponseUrls() {
    val mockServerUrl = … // Get the url from your MockWebServer
    dispatcher.setBodyContentTransformerResponse{ bodyContent ->
      if (bodyContent is BodyContent.Json) {
        bodyContent.copy(
          bodyContent.content.replace("https://my.domain/", mockServerUrl)
        )
      } else {
        bodyContent
      }
    }   
}
```

### Download

For unit tests:

```gradle
testImplementation 'pl.droidsonroids.testing:mockwebserver-path-dispatcher:1.2.0'
```

or for Android instrumentation tests:

```gradle
androidTestImplementation 'pl.droidsonroids.testing:mockwebserver-path-dispatcher:1.2.0'
```

### License

Library uses the MIT License. See [LICENSE](LICENSE) file.
