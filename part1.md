# Non-blocking I/O

First, go into the `latencyserver` directory and run

`./gradlew bootRun`

to spin up the latency server on port `9988`. This is a simple server only providing a `GET` endpoint on `/`, which
simply will accept a request, sleep for 2s and then return a greeting along which thread that handled the request.
Something like this:

`Hi from http-nio-9988-exec-7`

## The demos

All demos reside in the `client` directory, all execute against the latency server. Feel free to check out the code. All
tests are supposed to finish within ten seconds. If not, an exception is thrown. At the end of each test run, the total
execution time is printed (expect some overhead). The application (`ClientApplication`) will not run without a profile.
Each demo has a corresponding profile which you must specify when running.

### Single thread demo

This one executes three sequential requests against the latency server.

To run: 

```
./gradlew bootRun --args='--spring.profiles.active=single-thread'
```

_Example output:_

```
[main] se.cygni.cts.demo.SingleThread : [Hi from http-nio-9988-exec-1]
[main] se.cygni.cts.demo.SingleThread : [Hi from http-nio-9988-exec-2]
[main] se.cygni.cts.demo.SingleThread : [Hi from http-nio-9988-exec-3]
[main] se.cygni.cts.demo.SingleThread : Exec time: 6.101132841 s.
```

### Multithread demo 1 - thread pool of 20 threads, 20 requests

We scale using a 1:1 ratio between number of available threads and number of requests.

To run: 

```
./gradlew bootRun --args='--spring.profiles.active=multi-thread-1'
```

_Example output:_

```
[ool-1-thread-19] se.cygni.cts.demo.MultiThread1 : [Hi from http-nio-9988-exec-7]
[pool-1-thread-9] se.cygni.cts.demo.MultiThread1 : [Hi from http-nio-9988-exec-20]
[pool-1-thread-7] se.cygni.cts.demo.MultiThread1 : [Hi from http-nio-9988-exec-9]
[           main] se.cygni.cts.demo.MultiThread1 : Exec time: 2.045328493 s.
```

### Multithread demo 2 - thread pool of 5 threads, 20 requests

We scale using a 1:4 ratio between number of available threads and number of requests.

To run: 

```
./gradlew bootRun --args='--spring.profiles.active=multi-thread-2'
```

_Example output:_

```
[pool-1-thread-1] se.cygni.cts.demo.MultiThread2 : [Hi from http-nio-9988-exec-9]
[pool-1-thread-4] se.cygni.cts.demo.MultiThread2 : [Hi from http-nio-9988-exec-6]
[pool-1-thread-3] se.cygni.cts.demo.MultiThread2 : [Hi from http-nio-9988-exec-8]
[           main] se.cygni.cts.demo.MultiThread2 : Exec time: 8.124126323 s.
```

### Reactive demo – single thread (forced), 200 requests

Using a custom setting for demo purposes, `WebClient` is limited to only one thread. The purpose of this is simply to
show the power of the event loop model when dealing with high latency I/O and in this case negligible CPU work. *Please
note* that there is really no reason to use `WebClient` customized this way in real life, let `WebClient/Netty` scale up
and down as it sees fit.

To run: 

```
./gradlew bootRun --args='--spring.profiles.active=reactive'
```

_Example output:_

```
[ntLoopGroup-2-1] r.n.http.client.HttpClientOperations : [id:2b6b8fe3-1, L:/127.0.0.1:63443 - R:localhost/127.0.0.1:9988] Received response (auto-read:false) : [Content-Type=text/plain;charset=UTF-8, Date=Tue, 28 Sep 2021 06:56:06 GMT, content-length=30]
[ntLoopGroup-2-1] r.n.http.client.HttpClientOperations : [id:2b6b8fe3-1, L:/127.0.0.1:63443 - R:localhost/127.0.0.1:9988] Received last HTTP packet
[ntLoopGroup-2-1] se.cygni.cts.demo.Reactive           : Hi from http-nio-9988-exec-197
[           main] se.cygni.cts.demo.Reactive           : Exec time: 2.450435445 s.
```

Note that we have `DEBUG` set for Netty here, it's just to show that both the HTTP-traffic and printing of the response
etc. is handled on the same thread (the `main` thread is still used for timing of the test).
