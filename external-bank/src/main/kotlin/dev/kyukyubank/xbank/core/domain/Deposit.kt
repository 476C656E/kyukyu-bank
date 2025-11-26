package dev.kyukyubank.xbank.core.domain

import java.math.BigDecimal

data class Deposit(
    val receiverAccountNumber: String,
    val amount: BigDecimal,
    val senderName: String,
    val simulation: SimulationOptions
)
