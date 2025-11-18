package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.common.enums.AccountCurrency
import java.math.BigDecimal

data class Balance(
    val balance: BigDecimal,
    val currency: AccountCurrency,
)