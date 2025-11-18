package dev.kyukyubank.banking.core.domain

import java.math.BigDecimal

class ResultTransaction (
    val accountId: Long,
    val balance: BigDecimal
)