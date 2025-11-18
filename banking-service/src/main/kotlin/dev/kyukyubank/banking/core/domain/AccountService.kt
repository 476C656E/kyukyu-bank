package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.common.enums.AccountStatus
import dev.kyukyubank.banking.common.enums.BankCode
import dev.kyukyubank.banking.common.utils.AccountNumberGenerator
import dev.kyukyubank.banking.core.support.OffsetLimit
import dev.kyukyubank.banking.core.support.Paging
import dev.kyukyubank.banking.core.support.error.CoreException
import dev.kyukyubank.banking.core.support.error.ErrorType
import dev.kyukyubank.banking.storage.persistence.AccountEntity
import dev.kyukyubank.banking.storage.persistence.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountNumberGenerator: AccountNumberGenerator,
) {
    @Transactional
    fun createAccount(newAccount: NewAccount): Long {
        val sequenceNumber = accountRepository.getNextAccountNumberSequence()
        val accountNumber = accountNumberGenerator.generate(newAccount.type, sequenceNumber)

        if (accountRepository.existsByAccountNumber(accountNumber)) {
           throw CoreException(ErrorType.DUPLICATE_ACCOUNT_NUMBER)
        }

        val saved = accountRepository.save(
            AccountEntity(
                userId = newAccount.userId,
                accountNumber = accountNumber,
                accountPassword = newAccount.accountPassword,
                bankCode = BankCode.KYUKYU_BANK.code,
                type = newAccount.type,
                currency = newAccount.currency,
                status = AccountStatus.ACTIVE,
                balance = BigDecimal.ZERO,
            )
        )

        return saved.id
    }

    @Transactional
    fun deposit(transaction: DepositTransaction): ResultTransaction {
        require(transaction.amount > BigDecimal.ZERO) { "입금액은 0보다 커야해요."}
        val account = accountRepository.findById(transaction.accountId).orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        if (account.status != AccountStatus.ACTIVE)  {
            throw CoreException(ErrorType.INACTIVE_ACCOUNT)
        }

        account.changeBalance(transaction.amount)

        return ResultTransaction(
            account.id,
            balance = account.balance
        )
    }

    @Transactional
    fun withdraw(transaction: WithdrawTransaction): ResultTransaction {
        require(transaction.amount > BigDecimal.ZERO) { "출금액은 0보다 커야해요." }
        val account = accountRepository.findById(transaction.accountId).orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        if (account.status != AccountStatus.ACTIVE) {
            throw CoreException(ErrorType.INACTIVE_ACCOUNT)
        }

        if (account.balance < transaction.amount) {
            throw CoreException(ErrorType.INSUFFICIENT_BALANCE)
        }
        account.changeBalance(-transaction.amount)

        return ResultTransaction(
            account.id,
            balance = account.balance
        )
    }

    @Transactional(readOnly = true)
    fun getBalance(userId: Long, accountId: Long): BigDecimal = accountRepository.findByUserIdAndId(userId, accountId).balance

    @Transactional(readOnly = true)
    fun getAccount(userId: Long, accountId: Long): AccountInfo {
        val account = accountRepository.findByUserIdAndId(userId, accountId)

        return AccountInfo(
            id = account.id,
            accountNumber = account.accountNumber,
            type = account.type,
            status = account.status,
            openedAt = account.openedAt
        )
    }

    @Transactional(readOnly = true)
    fun getAccounts(userId: Long, offsetLimit: OffsetLimit): Paging<AccountInfo> {
        val accounts = accountRepository.findByUserId(userId, offsetLimit.toPageable())
            .map { AccountInfo(it.id, it.accountNumber, it.type, it.status, it.openedAt) }

        if (accounts.isEmpty) return Paging(emptyList(), false)

        return Paging(
            accounts.content.map { account ->
                AccountInfo(
                    id = account.id,
                    accountNumber = account.accountNumber,
                    type = account.type,
                    status = account.status,
                    openedAt = account.openedAt
                )
            },
            hasNext = accounts.hasNext()
        )

    }

}