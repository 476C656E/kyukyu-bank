package dev.kyukyubank.banking.storage.persistence

import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable

interface AccountRepository : JpaRepository<AccountEntity, Long>, AccountRepositoryCustom {
    fun existsByAccountNumber(accountNumber: String): Boolean

    fun findByUserId(userId: Long, slice: Pageable): Slice<AccountEntity>

    fun findByUserIdAndId(userId: Long, accountId: Long): AccountEntity
}
