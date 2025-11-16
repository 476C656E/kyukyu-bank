package dev.kyukyubank.banking.core.api.request

import dev.kyukyubank.banking.core.domain.NewUser
import java.time.LocalDate

data class CreateUserRequest(
    val accountId: String,
    val password: String,
    val name: String,
    val nameEn: String,
    val dateOfBirth: LocalDate
) {
    fun toUser() = NewUser(accountId, password, name, nameEn, dateOfBirth)
}
