# micronaut-kafka-graphql

## About this project
This program demonstrates mainly the interaction between Kafka and GraphQL within Micronaut Framework. By running this project it will provide two GraphQL interfaces for writing (mutation) and reading (query) data from Kafka Topic.

## Project structure
The project is structured as below. There are three main modules under src/main/kotlin/micronaut/kafka.


**graphql** contains:

* one GraphQLFactory class, which is responsble for registering GraphQL interfaces

* one or more implementation classes of GraphQL interfaces, which extend DataFetcher classes

**service** contins Kafka related backend implementation classes, which are normally the Kafka Streams, Statestore and common Kafka Producer/Consumer.

**model** stores data classes for the whole program

```
├── Dockerfile
├── README.md
└── src
    ├── main
    │   ├── kotlin
    │   │   └── micronaut
    │   │       └── kafka
    │   │           └── graphql
    │   │               ├── Application.kt
    │   │               ├── graphql
    │   │               │   ├── GraphQLFactory.kt
    │   │               │   └── MarketDataFetchers.kt
    │   │               ├── model
    │   │               │   ├── Market.kt
    │   │               │   └── MarketInput.kt
    │   │               └── service
    │   │                   ├── CreateMarketService.kt
    │   │                   ├── CurrentMarketStore.kt
    │   │                   └── MarketStream.kt
    │   └── resources
    │       ├── application.yml
    │       ├── logback.xml
    │       └── schema.graphqls
    └── test
        └── kotlin
            ├── io
            │   └── kotlintest
            │       └── provided
            │           └── ProjectConfig.kt
            └── micronaut
                └── kafka
                    └── graphql
```
