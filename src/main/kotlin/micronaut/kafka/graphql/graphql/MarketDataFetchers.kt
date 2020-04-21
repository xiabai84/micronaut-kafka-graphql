package micronaut.kafka.graphql.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import micronaut.kafka.graphql.model.Market
import micronaut.kafka.graphql.model.MarketInput
import micronaut.kafka.graphql.service.CreateMarketService
import micronaut.kafka.graphql.service.CurrentMarketStore
import javax.inject.Singleton

@Singleton
@SuppressWarnings("Duplicates")
class CreateMarketDataFetcher(private val createMarketService: CreateMarketService,
                              private val objectMapper: ObjectMapper) : DataFetcher<Market> {

    override fun get(env: DataFetchingEnvironment): Market {
        val marketInput =
                objectMapper.convertValue(env.getArgument("marketInput"), MarketInput::class.java)

        val market = Market(
            marketId = marketInput.marketId,
            currentStatus = marketInput.currentStatus,
            country = marketInput.country,
            zipcode = marketInput.zipcode
        )

        createMarketService.createMarket(id = market.marketId, market = market)
        return market
    }
}

@Singleton
@SuppressWarnings("Duplicates")
class AllMarketDataFetcher(private val currentMarketStore: CurrentMarketStore) : DataFetcher<List<Market>> {
    override fun get(env: DataFetchingEnvironment): List<Market> {
        return currentMarketStore.getAllMarkets()
    }
}