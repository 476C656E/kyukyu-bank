package dev.kyukyubank.banking.core.api.request

import dev.kyukyubank.banking.common.enums.AccountCurrency
import dev.kyukyubank.banking.common.enums.AccountType
import dev.kyukyubank.banking.core.domain.NewAccount

data class CreateAccountRequest(
    val userId: Long,
    val accountPassword: String,
    val type: AccountType,
    val currency: AccountCurrency,
) {
    fun toAccount(): NewAccount {
        return NewAccount(
            userId = this.userId,
            accountPassword = this.accountPassword,
            type = this.type,
            currency = this.currency
        )
    }
}
