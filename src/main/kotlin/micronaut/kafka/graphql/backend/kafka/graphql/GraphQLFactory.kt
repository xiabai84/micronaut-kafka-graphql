package micronaut.kafka.graphql.backend.kafka.graphql

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Singleton


@SuppressWarnings("Duplicates")
@Factory
class GraphQLFactory {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver, createMarketDataFetcher: CreateMarketDataFetchers): GraphQL { // <2>

        val schemaParser = SchemaParser()
        val schemaGenerator = SchemaGenerator()

        val typeRegistry = TypeDefinitionRegistry();
        typeRegistry.merge(schemaParser.parse(BufferedReader(InputStreamReader(
                resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()))))

        // Create the runtime wiring.
        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Mutation") { typeWiring ->
                    typeWiring.dataFetcher("createMarket", createMarketDataFetcher)
                }
                .build()

        // Create the executable schema.
        val graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)

        // Return the GraphQL bean.
        return GraphQL.newGraphQL(graphQLSchema).build()
    }
}