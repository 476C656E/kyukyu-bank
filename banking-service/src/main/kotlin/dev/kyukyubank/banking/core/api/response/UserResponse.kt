package dev.kyukyubank.banking.core.api.response

import dev.kyukyubank.banking.core.domain.User

data class UserResponse(
    val id: Long,
    val name: String,
) {

    companion object {
        fun of(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                name = user.name
            )
        }
    }
}
