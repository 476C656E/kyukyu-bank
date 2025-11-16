package dev.kyukyubank.banking.core.support.error

class CoreException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)
