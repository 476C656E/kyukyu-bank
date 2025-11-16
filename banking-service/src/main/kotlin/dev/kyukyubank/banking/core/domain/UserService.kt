package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.core.support.error.CoreException
import dev.kyukyubank.banking.core.support.error.ErrorType
import dev.kyukyubank.banking.storage.persistence.UserEntity
import dev.kyukyubank.banking.storage.persistence.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        val found = userRepository.findById(userId).orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        return User(
            id = found.id,
            name = found.name,
        )
    }

    @Transactional(readOnly = true)
    fun login(login: Login): User {
        val user = userRepository.findByAccountId(login.accountId)
            ?: throw CoreException(ErrorType.UNAUTHORIZED)

        if (user.password != login.password) {
            throw CoreException(ErrorType.UNAUTHORIZED)
        }

        return User(
            id = user.id,
            name = user.name
        )
    }
}

