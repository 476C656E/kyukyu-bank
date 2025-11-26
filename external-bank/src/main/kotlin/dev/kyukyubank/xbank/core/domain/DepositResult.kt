package dev.kyukyubank.xbank.core.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class DepositResult(
    val transactionId: String,
    val resultCode: String,
    val balanceAfter: BigDecimal,
    val processedAt: LocalDateTime
)
