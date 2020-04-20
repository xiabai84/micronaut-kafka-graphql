package micronaut.kafka.graphql

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("micronaut.kafka.graphql")
                .mainClass(Application.javaClass)
                .start()
    }
}