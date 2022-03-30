#### This is an example of using coroutines with Spring MVC (not webflux!)

`DemoController` uses https://github.com/joost-de-vries/spring-coroutine, but since that library 
was published to bintray, I copied the class `SpringCoroutineScope` and dependencies here.

The _/boomerang/{x}_ endpoint waits for `x` seconds waits for `x` seconds and returns `x` back.
If `x` is 0 then it throws an exception.

Looking at the implementation I suspected that, by reusing the same scope instance in a Spring bean,
if one of the requests fails it would cancel the parent job and other parallel or subsequent requests
would also fail.

This can be reproduced by changing back:
```kotlin
@Suppress("FunctionName")
fun SpringScope(dispatcher: CoroutineDispatcher = Dispatchers.Default, job: Job = SupervisorJob()): SpringScope =
    SpringScope(dispatcher + job)
```
to
```kotlin
@Suppress("FunctionName")
fun SpringScope(dispatcher: CoroutineDispatcher = Dispatchers.Default, job: Job = Job()): SpringScope =
    SpringScope(dispatcher + job)
```

Obviously, using a `SupervisorJob` fixes this.

Here is a use case that reproduces the aforementioned problem:
```shell
#running the app:
./gradlew clean run

#let's do some requests
curl http://localhost:8080/boomerang/1

curl http://localhost:8080/boomerang/10

#if we call it in less than 10 seconds, the previous request will also fail
curl http://localhost:8080/boomerang/0
#this also fails
curl http://localhost:8080/boomerang/10
```
