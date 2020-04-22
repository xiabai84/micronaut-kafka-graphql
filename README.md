# micronaut-kafka-graphql

## About this project
This program demonstrates mainly the interaction between Kafka and GraphQL within Micronaut Framework. By running this project it will provide two GraphQL interfaces for writing (mutation) and reading (query) data from Kafka. There is also a Kafka Streams service inside the program, which will update the Market record to current state by providing the same marketId (see model Market). 

All of changes over GraphQL mutation will finally persist in Kafka Topic market-event-store. You can review them by using kafka-console-consumer command line tool.

## Project structure
The project is structured as below. There are three main modules under src/main/kotlin/micronaut/kafka.


**graphql** contains:

* exactly one GraphQLFactory class, which is responsble for registering GraphQL interfaces

* one or more implementation classes of GraphQL interfaces, which are extensions of DataFetcher classes. It is responsble for binding GraphQL with custom backend service

**service** contins Kafka related backend implementation classes, which are normally the Kafka Streams, Statestore and common Kafka Producer/Consumer.

**model** stores data classes for the whole program

Under **src/mainresources** folder there are two important configuration files:

* application.yml contains the configuration for whole micronaut backend including Kafka service
* schema.graphqls contains GraphQL schema definition, which links GraphQL interface together with GraphQLFactory class

```
├── ...
├── build.gradle
├── gradle.properties
├── gradlew
├── micronaut-cli.yml
├── settings.gradle
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

## Running the Application
First you need a running local Kafka cluster. There are multiple installation options, you can find them in the link below: https://docs.confluent.io/current/quickstart/index.html

After the installation you can use confluent-cli tool to start local Kafka broker and zookeeper:
```
$ confluent start kafka
```

Create a predefined Kafka Topic for receiving data from Kafka Producer:
```
$ kafka-topics --create --zookeeper localhost:2181 --topic market-event-store --partitions 10 --replication-factor 1
```

Open terminal & clone my project from Github:
```
$ git clone https://github.com/xiabai84/micronaut-kafka-graphql.git
```
Then switch to project root directory and perform:
```
$ ./gradlew run
```

For validation scope you can also use console-consumer:
```
$ kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic market-event-store
```

## GraphQL Example
Open web browser via http://localhost:8080/graphiql

Perform a mutation GraphQL command over graphiql:
```
mutation {
  createMarket(marketInput: {
    marketId: "id-1", currentStatus: "closed", country: "china", zipcode:"130000"
  }) {
    marketId
    currentStatus
    country
    zipcode
    timestamp
  }
}
```

Perform a query GraphQL command over graphiql:
```
query {
  allMarkets {
    marketId
    currentStatus
    country
    zipcode
    timestamp
  }
}
```

