package dev.kyukyubank.banking.core.domain

import java.math.BigDecimal

data class DepositTransaction(
    val accountId: Long,
    val amount: BigDecimal,
)
