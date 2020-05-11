package micronaut.kafka.graphql.model

import java.util.*

class MarketEvent (
        val marketEventId: String = UUID.randomUUID().toString(),
        val command: EventType,
        val marketId: String?,
        val payload: Market?,
        val timestamp: Long = System.currentTimeMillis()
)

enum class EventType {
    CREATE, UPDATE, DELETE
}