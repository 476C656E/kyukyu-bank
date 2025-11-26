package dev.kyukyubank.banking.core.service

import dev.kyukyubank.banking.core.api.response.LedgerResponse
import dev.kyukyubank.banking.storage.persistence.LedgerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LedgerService(
    private val ledgerRepository: LedgerRepository
) {
    @Transactional(readOnly = true)
    fun getLedgers(accountId: Long, pageable: Pageable): Page<LedgerResponse> {
        return ledgerRepository.findAllByAccountIdOrderByIdDesc(accountId, pageable)
            .map { LedgerResponse.from(it) }
    }
}
