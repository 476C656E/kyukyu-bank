package dev.kyukyubank.xbank.core.api.response

import dev.kyukyubank.xbank.core.domain.DepositResult
import java.math.BigDecimal
import java.time.LocalDateTime

data class DepositResponse(
    val txId: String,
    val balance: BigDecimal,
    val date: LocalDateTime
) {
    companion object {
        fun from(result: DepositResult): DepositResponse {
            return DepositResponse(
                txId = result.transactionId,
                balance = result.balanceAfter,
                date = result.processedAt
            )
        }
    }
}
