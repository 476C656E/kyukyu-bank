package dev.kyukyubank.banking.core.domain

import java.math.BigDecimal

data class WithdrawTransaction(
    val accountId: Long,
    val amount: BigDecimal,
)
