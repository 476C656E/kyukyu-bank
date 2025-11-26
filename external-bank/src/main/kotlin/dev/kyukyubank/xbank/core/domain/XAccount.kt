package dev.kyukyubank.xbank.core.domain

import java.math.BigDecimal

data class XAccount(
    val accountNumber: String,
    val name: String,
    var balance: BigDecimal
)
