package dev.kyukyubank.xbank.core.api

import dev.kyukyubank.xbank.core.api.request.DepositRequest
import dev.kyukyubank.xbank.core.api.response.DepositResponse
import dev.kyukyubank.xbank.core.service.XBankService
import dev.kyukyubank.xbank.core.support.response.XApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transfer")
class XBankController(
    private val xBankService: XBankService
) {

    @PostMapping("/deposit")
    fun deposit(@RequestBody request: DepositRequest): XApiResponse<DepositResponse> {
        val result = xBankService.deposit(request.toDomain())
        
        return XApiResponse.success(
            DepositResponse.from(result)
        )
    }
}