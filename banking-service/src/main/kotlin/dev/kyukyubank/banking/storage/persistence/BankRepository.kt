package dev.kyukyubank.banking.storage.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface BankRepository : JpaRepository<BankEntity, String>
