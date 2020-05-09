package micronaut.kafka.graphql.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class MarketInput (
    val marketId: String?,
    val currentStatus: String,
    val country: String,
    val zipcode: String,
    val eventType: EventType
)