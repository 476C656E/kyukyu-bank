package dev.kyukyubank.xbank.core.api.request

import dev.kyukyubank.xbank.core.domain.Deposit
import dev.kyukyubank.xbank.core.domain.SimulationOptions
import java.math.BigDecimal

data class DepositRequest(
    val receiverAccountNumber: String,
    val amount: BigDecimal,
    val senderName: String,
    val simulation: SimulationOptions
) {
    fun toDomain(): Deposit {
        require(amount > BigDecimal.ZERO) { "입금 금액은 0보다 커야 합니다." }

        return Deposit(
            receiverAccountNumber = receiverAccountNumber,
            amount = amount,
            senderName = senderName,
            simulation = simulation
        )
    }
}