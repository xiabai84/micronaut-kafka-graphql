package micronaut.kafka.graphql.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import micronaut.kafka.graphql.model.EventType
import micronaut.kafka.graphql.model.MarketEvent
import micronaut.kafka.graphql.model.MarketInput
import micronaut.kafka.graphql.model.Market
import micronaut.kafka.graphql.service.MarketMutationService
import java.util.*
import javax.inject.Singleton


@Singleton
@SuppressWarnings("Duplicates")
class MutationMarketEventDataFetcher(private val marketMutationService: MarketMutationService,
                                     private val objectMapper: ObjectMapper) : DataFetcher<String> {

    override fun get(env: DataFetchingEnvironment): String {
        val marketInput =
                objectMapper.convertValue(env.getArgument("marketInput"), MarketInput::class.java)

        val marketId = when (marketInput.eventType) {
            EventType.CREATE -> UUID.randomUUID().toString()

            else -> {
                if (marketInput.marketId.isNullOrEmpty())
                    throw IllegalArgumentException("Please provide a marketId for Update or Delete Event!")
                marketInput.marketId
            }
        }

        val event = MarketEvent(
            command = marketInput.eventType,
            marketId = marketId,
            payload = applyPayload(marketId = marketId, marketInput = marketInput)
        )

        marketMutationService.createMarketEvent(eventId = event.marketEventId, event = event)

        return marketId
    }

    private fun applyPayload (marketId: String, marketInput: MarketInput): Market? =
        when (marketInput.eventType) {
            EventType.CREATE, EventType.UPDATE -> {
                Market(
                        marketId = marketId,
                        currentStatus = marketInput.currentStatus,
                        country = marketInput.country,
                        zipcode = marketInput.zipcode
                )
            }
            else -> null
        }
}

