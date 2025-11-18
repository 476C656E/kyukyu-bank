package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.common.enums.AccountCurrency
import dev.kyukyubank.banking.common.enums.AccountStatus
import dev.kyukyubank.banking.common.enums.AccountType
import java.math.BigDecimal

data class NewAccount(
    val userId: Long,
    val accountPassword: String,
    val type: AccountType,
    val currency: AccountCurrency,
)