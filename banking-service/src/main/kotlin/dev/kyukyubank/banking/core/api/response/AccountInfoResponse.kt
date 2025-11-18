package dev.kyukyubank.banking.core.api.response

import dev.kyukyubank.banking.common.enums.AccountCurrency
import dev.kyukyubank.banking.common.enums.AccountStatus
import dev.kyukyubank.banking.common.enums.AccountType
import dev.kyukyubank.banking.core.domain.AccountInfo
import java.math.BigDecimal
import java.time.LocalDateTime

class AccountInfoResponse(
    val id: Long,
    val accountNumber: String,
    val type: AccountType,
    val status: AccountStatus,
    val openedAt: LocalDateTime,
) {
    companion object {
        fun of(accountInfo: AccountInfo): AccountInfoResponse {
            return AccountInfoResponse(
                id = accountInfo.id,
                accountNumber = accountInfo.accountNumber,
                type = accountInfo.type,
                status = accountInfo.status,
                openedAt = accountInfo.openedAt
            )
        }

        fun of(accountInfo: List<AccountInfo>): List<AccountInfoResponse> = accountInfo.map { of(it) }
    }
}