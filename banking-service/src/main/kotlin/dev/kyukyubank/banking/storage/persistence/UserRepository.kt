package dev.kyukyubank.banking.storage.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserJpaEntity, Long> {

}
