package micronaut.kafka.graphql.service

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import micronaut.kafka.graphql.model.MarketEvent

@KafkaClient
interface MarketMutationService {
    @Topic(MARKET_EVENT_TOPIC)
    fun createMarketEvent(@KafkaKey eventId: String, event: MarketEvent)
}