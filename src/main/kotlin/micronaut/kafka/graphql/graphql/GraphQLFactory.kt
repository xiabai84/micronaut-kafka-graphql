package micronaut.kafka.graphql.graphql

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
    fun graphQL(resourceResolver: ResourceResolver,
                createMarketDataFetcher: CreateMarketDataFetcher,
                allMarketDataFetcher: AllMarketDataFetcher ): GraphQL {

        val schemaParser = SchemaParser()
        val schemaGenerator = SchemaGenerator()
        val typeRegistry = TypeDefinitionRegistry()

        typeRegistry.merge(schemaParser.parse(BufferedReader(InputStreamReader(
                resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()))))

        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Mutation") { typeWiring -> typeWiring
                        .dataFetcher("createMarket", createMarketDataFetcher)
                }
                .type("Query") { typeWiring -> typeWiring
                        .dataFetcher("allMarkets", allMarketDataFetcher)
                }
                .build()

        val graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)

        return GraphQL.newGraphQL(graphQLSchema).build()
    }
}