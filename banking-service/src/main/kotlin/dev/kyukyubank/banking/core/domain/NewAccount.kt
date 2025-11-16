package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.common.enums.AccountCurrency
import dev.kyukyubank.banking.common.enums.AccountStatus
import dev.kyukyubank.banking.common.enums.AccountType
import java.math.BigDecimal

data class NewAccount(
    val accountNumber: String,
    val accountPassword: String,
    val bankCode: String,
    val type: AccountType,
    val balance: BigDecimal,
    val currency: AccountCurrency,
    val status: AccountStatus,
)