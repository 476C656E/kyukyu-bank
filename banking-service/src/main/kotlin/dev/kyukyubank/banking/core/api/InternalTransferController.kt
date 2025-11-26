package dev.kyukyubank.banking.core.api

import dev.kyukyubank.banking.core.api.request.MockDataRequest
import dev.kyukyubank.banking.core.service.MockDataService
import dev.kyukyubank.banking.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/internal")
class InternalTransferController(
    private val mockDataService: MockDataService
) {

    @PostMapping("/mock-data")
    fun createMockData(@RequestBody request: MockDataRequest): ApiResponse<String> {
        val timeMillis = if (request.method.uppercase() == "TSV") {
            mockDataService.generateByTsv(request.totalCount)
        } else {
            mockDataService.generateByJpa(request.totalCount)
        }
        
        return ApiResponse.success(
            "[${request.method}] ${request.totalCount}건 처리 완료 (소요시간: ${timeMillis}ms)"
        )
    }
}
