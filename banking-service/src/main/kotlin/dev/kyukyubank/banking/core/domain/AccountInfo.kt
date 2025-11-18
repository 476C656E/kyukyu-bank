package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.common.enums.AccountStatus
import dev.kyukyubank.banking.common.enums.AccountType
import java.time.LocalDateTime

class AccountInfo(
    val id: Long,
    val accountNumber: String,
    val type: AccountType,
    val status: AccountStatus,
    val openedAt: LocalDateTime,
)