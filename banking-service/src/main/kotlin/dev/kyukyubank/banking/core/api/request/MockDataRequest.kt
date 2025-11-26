package dev.kyukyubank.banking.core.api.request

data class MockDataRequest(
    val totalCount: Long = 1000,
    val method: String = "JPA" // "JPA" or "TSV"
)
