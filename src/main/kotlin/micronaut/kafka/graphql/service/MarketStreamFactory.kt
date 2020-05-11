package micronaut.kafka.graphql.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import micronaut.kafka.graphql.model.Market
import micronaut.kafka.graphql.model.MarketEvent
import micronaut.kafka.graphql.model.MarketHistView
import micronaut.kafka.graphql.util.updateCurrentMarket
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.state.Stores
import javax.inject.Named
import javax.inject.Singleton

const val MARKET_EVENT_TOPIC = "market-event-store"
const val CURRENT_MARKET_STORE = "current-market-statestore"
const val MARKET_HIST_STORE = "market-hist-statestore"
const val MARKET_APP_ID = "market-stream-app"

@Factory
class MarketStream {

    @Singleton
    @Named(MARKET_APP_ID)
    fun buildMarketStream(builder: ConfiguredStreamBuilder, objectMapper: ObjectMapper): KStream<String, MarketEvent?> {

        builder.configuration[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE
        builder.configuration[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        builder.configuration[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = true

        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(CURRENT_MARKET_STORE),
                Serdes.StringSerde(),
                JsonSerde(objectMapper, Market::class.java)
            )
        )

        val marketHistStore = Stores.inMemoryKeyValueStore(MARKET_HIST_STORE)

        val stream = builder
            .stream(MARKET_EVENT_TOPIC,
                Consumed.with(Serdes.String(), JsonSerde(objectMapper, MarketEvent::class.java)))

        val groupedMarketEvent = stream
            .groupBy(
                { _, value -> value.marketId },
                Grouped.with(
                    Serdes.String(),
                    JsonSerde(objectMapper, MarketEvent::class.java)))

        groupedMarketEvent
            .reduce { v1, v2 -> if (v1.timestamp <= v2.timestamp) v2 else v1 }
            .transformValues(updateCurrentMarket(), CURRENT_MARKET_STORE)

        groupedMarketEvent
            .aggregate(
                { MarketHistView(marketEventList = mutableListOf()) },
                { _: String, value: MarketEvent, aggregate: MarketHistView ->
                    aggregate.marketEventList.add(value)
                    aggregate
                },
                Materialized.`as`<String, MarketHistView>(marketHistStore)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerde(objectMapper, MarketHistView::class.java))
            )

        return stream
    }
}