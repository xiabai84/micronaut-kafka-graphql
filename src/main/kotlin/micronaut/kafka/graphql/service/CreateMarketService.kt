package micronaut.kafka.graphql.service

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import micronaut.kafka.graphql.model.Market

@KafkaClient
interface CreateMarketService {
    @Topic(MARKET_EVENT_TOPIC)
    fun createMarket(@KafkaKey id: String, market: Market)
}