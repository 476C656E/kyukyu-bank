package dev.kyukyubank.banking.core.support

data class Paging<T>(
    val content: List<T>,
    val hasNext: Boolean,
)
