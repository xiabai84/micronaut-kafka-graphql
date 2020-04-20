package micronaut.kafka.graphql.backend.kafka.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

import javax.inject.Singleton


@Singleton
class CreateMarketDataFetchers : DataFetcher<String> {

    override fun get(env: DataFetchingEnvironment): String {
        var name = env.getArgument<String>("name")
        if (name == null || name.trim().isEmpty()) {
            name = "World"
        }
        return "Hello $name!"
    }
}