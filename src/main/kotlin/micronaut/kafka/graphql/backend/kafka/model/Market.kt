package micronaut.kafka.graphql.backend.kafka.model

import io.micronaut.core.annotation.Introspected
import java.util.*

@Introspected
data class Market (
    val marketId: String = UUID.randomUUID().toString(),
    val marketType: String,
    val country: String,
    val zipCode: String
)