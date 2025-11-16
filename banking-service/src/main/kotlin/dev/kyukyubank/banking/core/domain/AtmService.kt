package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.storage.persistence.ATMEntity
import dev.kyukyubank.banking.storage.persistence.ATMRepository
import org.springframework.stereotype.Service

@Service
class AtmService(
    private val atmRepository: ATMRepository,
) {
    fun deposit(deposit: AtmDeposit) : Long {
        val deposit = atmRepository.save(
            ATMEntity(
                bankCode = deposit.bankCode,
                accountNumber = deposit.accountNumber,
                customerName = deposit.customerName,
                currency = deposit.currency,
                depositAmount = deposit.depositAmount
            )
        )

        return deposit.id
    }
}
