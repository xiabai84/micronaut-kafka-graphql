package micronaut.kafka.graphql.util

import micronaut.kafka.graphql.model.Market
import micronaut.kafka.graphql.model.MarketEvent
import micronaut.kafka.graphql.service.CURRENT_MARKET_STORE
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.ValueTransformerWithKey
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore


fun updateCurrentMarket(): ValueTransformerWithKeySupplier<String, MarketEvent, KeyValue<String, MarketEvent>> =
    ValueTransformerWithKeySupplier {
        object: ValueTransformerWithKey<String, MarketEvent, KeyValue<String, MarketEvent>> {
            private lateinit var context: ProcessorContext
            private lateinit var store: KeyValueStore<String, Market>

            @Suppress("UNCHECKED_CAST")
            override fun init(context: ProcessorContext) {
                this.context = context
                this.store = context.getStateStore(CURRENT_MARKET_STORE) as KeyValueStore<String, Market>
            }

            override fun transform(key: String, value: MarketEvent): KeyValue<String, MarketEvent>? {
                val existingKey: Market? = store.get(key)
                return when {
                    existingKey == null || existingKey != value.payload -> {
                        store.put(key, value.payload)
                        KeyValue.pair(key, value)
                    }
                    else -> null
                }
            }

            override fun close() {}
        }
    }


