package dev.kyukyubank.banking.core.api.request

import dev.kyukyubank.banking.core.domain.DepositTransaction
import java.math.BigDecimal

data class DepositRequest(
    val accountId: Long,
    val amount: BigDecimal,
) {
    fun toDepositTransaction(): DepositTransaction {
        return DepositTransaction(
            accountId = this.accountId,
            amount = this.amount
        )
    }
}
