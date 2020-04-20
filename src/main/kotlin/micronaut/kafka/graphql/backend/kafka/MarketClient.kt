package micronaut.kafka.graphql.backend.kafka

import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import micronaut.kafka.graphql.backend.kafka.model.Market

interface MarketClient {
    @Topic("market-event-store")
    fun sendMarket(@KafkaKey id: String, market: Market)
}