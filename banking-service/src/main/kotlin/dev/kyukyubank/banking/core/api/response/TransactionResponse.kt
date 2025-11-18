package dev.kyukyubank.banking.core.api.response

import java.math.BigDecimal

data class TransactionResponse(
    val accountId: Long,
    val balance: BigDecimal,
) {
    companion object {
        fun of(accountId: Long, balance: BigDecimal): TransactionResponse {
            return TransactionResponse(
                accountId = accountId,
                balance = balance
            )
        }
    }
}
