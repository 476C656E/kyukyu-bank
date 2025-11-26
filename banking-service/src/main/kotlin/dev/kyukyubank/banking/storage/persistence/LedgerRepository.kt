package dev.kyukyubank.banking.storage.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LedgerRepository : JpaRepository<LedgerEntity, Long> {
    fun findAllByAccountIdOrderByIdDesc(accountId: Long, pageable: Pageable): Page<LedgerEntity>
}
