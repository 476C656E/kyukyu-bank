package dev.kyukyubank.banking.storage.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<TransactionEntity, Long>
