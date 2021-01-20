# Core HTTP Server

Provide `HTTP Server` unit for `REST API` server

To use it, must add in your `build.gradle`

```gradle
  compile project(':core:httpserver')
```

## Static REST API

TBD

### Plain REST API

TBD

### REST with Eventbus

TBD

## Dynamic REST API

See [`Dynamic REST API` test](src/test/java/io.github.zero88.qwe/http/dynamic)

To implement `Dynamic REST API` function, it requires `gateway server` and `service supplier`. Currently,
support `HTTP service supplier` and `Eventbus service supplier`

- `Gateway`: It is HTTP server with dispatcher role for find and redirect request to appropriate `service supplier`
- `HTTP service supplier`: Provide a set of `HTTP REST API` then register them to `Gateway`
- `Eventbus service supplier`: Provide a set of `Eventbus Consumer` then register them to `Gateway`

The register task is implemented by [`:core:micro`](../micro/README.md). So, to use this feature, your
project `build.gradle` must declare dependency **`compile project(':core:micro')`**

### Gateway server

- Must add these dependencies in `build.gradle`

  ```gradle
  compile project(':core:httpserver')
  compile project(':core:micro')
  ```

- See [sample implementation](src/test/java/io.github.zero88.qwe/http/dynamic/mock/GatewayServer.java)

### Dynamic HTTP REST

- Must add these dependencies in `build.gradle`

  ```gradle
  compile project(':core:httpserver')
  compile project(':core:micro')
  ```

- See [sample implementation](src/test/java/io.github.zero88.qwe/http/dynamic/mock/HttpServiceServer.java)

### Dynamic Event Message REST

- Must add these dependencies in `build.gradle`. No need `httpserver`

  ```gradle
  compile project(':core:micro')
  ```

- See [sample implementation](src/test/java/io.github.zero88.qwe/http/dynamic/mock/EventMessageService.java)
