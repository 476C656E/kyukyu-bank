package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.common.enums.AccountCurrency
import java.math.BigDecimal

data class AtmDeposit(
    val bankCode: String,
    val accountNumber: String,
    val customerName: String,
    val currency: AccountCurrency,
    val depositAmount: BigDecimal,
)
