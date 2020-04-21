package micronaut.kafka.graphql.service

import io.micronaut.configuration.kafka.streams.InteractiveQueryService
import micronaut.kafka.graphql.model.Market
import org.apache.kafka.streams.state.QueryableStoreTypes
import javax.inject.Singleton

@Singleton
class CurrentMarketStore(private val interactiveQueryService: InteractiveQueryService) {

    fun getAllMarkets(): List<Market> {
        val marketStore = interactiveQueryService
                .getQueryableStore(CURRENT_MARKET_STORE, QueryableStoreTypes.keyValueStore<String, Market>())
        return marketStore
                .map { kvStore -> kvStore.all().asSequence().map { v -> v.value }.toList() }
                .orElse( emptyList<Market>())
    }
}