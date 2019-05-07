OutOfMemory Bug on AppEngine
============================

# What happens?

1. A request fails with an OutOfMemoryError while deserializing
   a response from MemCache. The Error is caught, and the request
   completes with a 500 status code.

2. The instance continues to serve requests, but if any of the requests
   invoke an AppEngine API RPC request (e.g. Datastore or Memcache), the
   RPC request fails with `java.nio.channels.ClosedSelectorException`

3. All other requests to the instance that do not make Datastore or Memcache
   RPC request succeed.

4. Depending on the percentage of requests to the affected instance that
   make RPC requests (and fail), the load balancer may keep the affected
   instance alive indefinitely.

# How to reproduce

Still not clear what combination of events lead to the affected state.

I have been able to trigger the state a few times using this dummy app.

To attempt:

1. Visit https://activityinfo-gae-oom-bug.appspot.com/start to populate
   memcache with a set of large key/values pairs

2. Run StressTest.

