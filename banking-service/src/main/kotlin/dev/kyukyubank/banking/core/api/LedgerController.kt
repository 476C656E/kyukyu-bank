package dev.kyukyubank.banking.core.api

import dev.kyukyubank.banking.core.api.response.LedgerResponse
import dev.kyukyubank.banking.core.service.LedgerService
import dev.kyukyubank.banking.core.support.response.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ledgers")
class LedgerController(
    private val ledgerService: LedgerService
) {

    @GetMapping
    fun getLedgers(
        @RequestParam accountId: Long,
        @PageableDefault(size = 20, page = 0) pageable: Pageable
    ): ApiResponse<Page<LedgerResponse>> {
        val result = ledgerService.getLedgers(accountId, pageable)
        return ApiResponse.success(result)
    }
}
