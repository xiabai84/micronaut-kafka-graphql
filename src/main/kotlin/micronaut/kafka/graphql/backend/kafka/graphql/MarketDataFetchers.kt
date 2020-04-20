package micronaut.kafka.graphql.backend.kafka.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import micronaut.kafka.graphql.backend.kafka.MarketClient
import micronaut.kafka.graphql.backend.kafka.model.Market
import java.util.*

import javax.inject.Singleton


@Singleton
class CreateMarketDataFetchers(private val marketClient: MarketClient) : DataFetcher<Market> {

    override fun get(env: DataFetchingEnvironment): Market {
        val market = env.getArgument<Market>("market")
        val markedId = UUID.randomUUID().toString()
        marketClient.sendMarket(id = markedId, market = market)
        return market
    }
}