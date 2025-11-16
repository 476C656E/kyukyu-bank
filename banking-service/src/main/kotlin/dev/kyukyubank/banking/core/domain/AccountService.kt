package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.storage.persistence.AccountEntity
import dev.kyukyubank.banking.storage.persistence.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun createAccount(userId: Long, newAccount: NewAccount): Long {
        val saved = accountRepository.save(
            AccountEntity(
                userId = userId,
                accountNumber = newAccount.accountNumber,
                accountPassword = newAccount.accountPassword,
                bankCode = newAccount.bankCode,
                type = newAccount.type,
                currency = newAccount.currency,
                status = newAccount.status,
                balance = newAccount.balance,
                )
        )

        return saved.id
    }

    @Transactional
    fun deleteAccount(accountId: Long) {
        accountRepository.deleteById(accountId)
    }

    @Transactional(readOnly = true)
    fun getBalance(accountId: Long): BigDecimal = accountRepository.findById(accountId).get().balance
}