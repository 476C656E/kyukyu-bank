package dev.kyukyubank.banking.core.api.request

import dev.kyukyubank.banking.core.domain.Login

data class LoginRequest(
    val accountId: String,
    val password: String
) {
    fun toLogin(): Login {
        return Login(
            accountId = accountId,
            password = password
        )
    }
}
