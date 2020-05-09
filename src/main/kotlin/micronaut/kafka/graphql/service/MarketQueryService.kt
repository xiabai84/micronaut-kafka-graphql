package micronaut.kafka.graphql.service

import io.micronaut.configuration.kafka.streams.InteractiveQueryService
import micronaut.kafka.graphql.model.Market
import micronaut.kafka.graphql.model.MarketHistView
import org.apache.kafka.streams.state.QueryableStoreTypes
import javax.inject.Singleton

@Singleton
class MarketQueryService(private val interactiveQueryService: InteractiveQueryService) {

    fun getAllCurrentMarkets(): List<Market> {
        val marketStore = interactiveQueryService
                .getQueryableStore(CURRENT_MARKET_STORE, QueryableStoreTypes.keyValueStore<String, Market>())
        return marketStore
                .map { kvStore -> kvStore.all().asSequence().map { v -> v.value }.toList() }
                .orElse( emptyList<Market>())
    }

    fun getMarketHistById(marketId: String): List<Market?> {
        val allHistMarket = interactiveQueryService
                .getQueryableStore(MARKET_HIST_STORE, QueryableStoreTypes.keyValueStore<String, MarketHistView>())
        return allHistMarket
            .map { kvStore -> kvStore[marketId] }
            .map { v ->
                v.marketEventList.sortBy { e -> e.timestamp }
                v.marketEventList.map { e -> e.payload }
            }
            .orElse( emptyList() )
    }
}