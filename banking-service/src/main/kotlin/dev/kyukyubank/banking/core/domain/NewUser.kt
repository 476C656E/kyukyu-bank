package dev.kyukyubank.banking.core.domain

import java.time.LocalDate

data class NewUser(
    val accountId: String,
    val password: String,
    val name: String,
    val nameEn: String,
    val dateOfBirth: LocalDate
)
