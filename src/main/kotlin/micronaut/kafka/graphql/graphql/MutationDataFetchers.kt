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

        val marketId = marketInput.marketId?: UUID.randomUUID().toString()

        val event = MarketEvent(
            command = marketInput.eventType,
            marketId = marketId,
            payload = applyPayload(
                marketId = marketId,
                eventType = marketInput.eventType,
                marketInput = marketInput
            )
        )

        marketMutationService.createMarketEvent(eventId = event.marketEventId, event = event)

        return marketId
    }

    private fun applyPayload (marketId: String, eventType: EventType, marketInput: MarketInput): Market? {

        return when(marketInput.eventType) {
            EventType.CREATE -> {
                 Market(
                    marketId = UUID.randomUUID().toString(),
                    currentStatus = marketInput.currentStatus,
                    country = marketInput.country,
                    zipcode = marketInput.zipcode
                )
            }

            EventType.UPDATE -> {
                if (marketInput.marketId.isNullOrEmpty())
                    throw IllegalArgumentException("Please provide a marketId for Update Event!")

                Market(
                        marketId = marketInput.marketId,
                        currentStatus = marketInput.currentStatus,
                        country = marketInput.country,
                        zipcode = marketInput.zipcode
                )
            }

            else -> null
        }
    }
}

