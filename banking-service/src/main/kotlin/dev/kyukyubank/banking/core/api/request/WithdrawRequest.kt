package dev.kyukyubank.banking.core.api.request

import dev.kyukyubank.banking.core.domain.WithdrawTransaction
import java.math.BigDecimal

data class WithdrawRequest(
    val accountId: Long,
    val amount: BigDecimal,
) {
    fun toWithdrawTransaction(): WithdrawTransaction {
        return WithdrawTransaction(
            accountId = this.accountId,
            amount = this.amount
        )
    }
}
