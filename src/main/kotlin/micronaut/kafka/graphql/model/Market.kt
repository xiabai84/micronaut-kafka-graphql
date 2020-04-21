package micronaut.kafka.graphql.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class Market (
    var marketId: String,
    val currentStatus: String,
    val country: String,
    val zipcode: String,
    val timestamp: Long = System.currentTimeMillis()
)