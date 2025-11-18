package dev.kyukyubank.banking.storage.persistence

interface AccountRepositoryCustom {
    fun getNextAccountNumberSequence(): Long
}
