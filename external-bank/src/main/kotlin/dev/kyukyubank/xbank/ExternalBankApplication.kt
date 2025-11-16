package dev.kyukyubank.xbank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExternalBankApplication

fun main(args: Array<String>) {
    runApplication<ExternalBankApplication>(*args)
}
