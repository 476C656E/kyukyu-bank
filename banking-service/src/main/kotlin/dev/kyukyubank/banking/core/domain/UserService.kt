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

    @Transactional
    fun createUser(newUser: NewUser): Long {
        val saved = userRepository.save(
            UserEntity(
                accountId = newUser.accountId,
                password = newUser.password,
                name = newUser.name,
                nameEn = newUser.nameEn,
                dateOfBirth = newUser.dateOfBirth
            )
        )

        return saved.id
    }

    @Transactional
    fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        val found = userRepository.findById(userId).orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        return User(
            id = found.id,
            name = found.name,
        )
    }
}

