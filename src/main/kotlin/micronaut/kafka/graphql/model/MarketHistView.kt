package micronaut.kafka.graphql.model

data class MarketHistView(
    val marketEventList: MutableList<MarketEvent>
)