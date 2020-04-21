package micronaut.kafka.graphql.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import micronaut.kafka.graphql.model.Market
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.state.Stores
import javax.inject.Named
import javax.inject.Singleton

const val MARKET_EVENT_TOPIC = "market-event-store"
const val CURRENT_MARKET_STORE = "current-market-store"
const val MARKET_APP_ID = "market-stream"

@Factory
class MarketStream {

    @Singleton
    @Named(MARKET_APP_ID)
    fun buildMarketStream(builder: ConfiguredStreamBuilder, objectMapper: ObjectMapper): KStream<String, Market>? {
        val marketStore = Stores.inMemoryKeyValueStore(CURRENT_MARKET_STORE)

        builder.configuration[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE
        builder.configuration[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        builder.configuration[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = true

        val stream = builder.stream(MARKET_EVENT_TOPIC,
                Consumed.with(Serdes.String(), JsonSerde(objectMapper, Market::class.java)))

        stream.groupBy(
            { _, value -> value.marketId },
            Grouped.with(
                Serdes.String(),
                JsonSerde(objectMapper, Market::class.java)))
            .reduce(
                {value1, value2 ->
                    if(value1.timestamp <= value2.timestamp) {
                        return@reduce value2
                    } else {
                        return@reduce value1
                    }
                },
                Materialized
                    .`as`<String, Market>(marketStore)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerde(objectMapper, Market::class.java))
            )
            .toStream()
            .print(Printed.toSysOut())

        return stream
    }
}