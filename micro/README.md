# Core Microservice

It provides:

- `Local service discovery` Helps to discover service that run in one java process. It is suitable for using
  in `IoT device`
- `Cluster service discovery` Helps to discover service that run in cluster mode. It is suitable for using
  in `Cloud/bare meta server`
- `Circuit breaker` keeps track of the number of failures and opens the circuit when a threshold is reached when calling
  service and retry an invocation if it's failure
