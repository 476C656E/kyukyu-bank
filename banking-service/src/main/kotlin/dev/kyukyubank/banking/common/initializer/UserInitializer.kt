package dev.kyukyubank.banking.common.initializer

import dev.kyukyubank.banking.storage.persistence.UserEntity
import dev.kyukyubank.banking.storage.persistence.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserInitializer(
    private val userRepository: UserRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if (userRepository.count() == 0L) {
            val user = UserEntity(
                ulid = "01ARZ3NDEKTSV4RRFFQ69G5FAV",
                accountId = "test",
                password = "asd123",
                name = "치와와",
                nameEn = "Chihuahua",
                dateOfBirth = LocalDate.of(1988, 8, 8)
            )
            userRepository.save(user)
        }
    }
}
