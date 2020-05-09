package micronaut.kafka.graphql.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import micronaut.kafka.graphql.model.Market
import micronaut.kafka.graphql.service.MarketQueryService
import javax.inject.Singleton

@Singleton
@SuppressWarnings("Duplicates")
class AllCurrentMarketDataFetcher(private val marketQueryService: MarketQueryService) : DataFetcher<List<Market>> {
    override fun get(env: DataFetchingEnvironment): List<Market> {
        return marketQueryService.getAllCurrentMarkets()
    }
}

@Singleton
@SuppressWarnings("Duplicates")
class MarketHistDataFetcher(private val marketQueryService: MarketQueryService) : DataFetcher<List<Market?>> {
    override fun get(env: DataFetchingEnvironment): List<Market?> {
        val marketId = env.getArgument<String>("marketid")
        return marketQueryService.getMarketHistById(marketId = marketId)
    }
}