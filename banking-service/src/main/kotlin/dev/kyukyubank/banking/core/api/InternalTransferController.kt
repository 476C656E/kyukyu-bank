package dev.kyukyubank.banking.core.api

import dev.kyukyubank.banking.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

// In a real implementation, the request body would be mapped to a DTO class.
// data class InternalTransferRequest(val fromAccountId: Long, val toAccountId: Long, val amount: java.math.BigDecimal)

@RestController
@RequestMapping("/api/v1")
class InternalTransferController {

    /**
     * 자신의 계좌로 돈을 이체합니다. (내부 이체)
     */
    @PostMapping("/transfers/internal")
    fun internalTransfer(
        @RequestBody request: Map<String, Any> // Using a Map to avoid creating a DTO file for now
    ): ApiResponse<Map<String, Any>> {
        // In a real implementation, you would deserialize to a DTO and call a service.
        // For this simple implementation, we just return a dummy success response.

        val dummyResponse = mapOf(
            "transactionId" to 1L,
            "fromAccountId" to request["fromAccountId"]!!,
            "toAccountId" to request["toAccountId"]!!,
            "amount" to request["amount"]!!,
            "status" to "SUCCESS",
            "transactionType" to "INTERNAL_TRANSFER",
            "transactionDate" to LocalDateTime.now().toString()
        )
        return ApiResponse.success(dummyResponse)
    }
}