package dev.kyukyubank.banking.core.support.response

data class PagingResponse<T>(
    val content: List<T>,
    val hasNext: Boolean,
)
