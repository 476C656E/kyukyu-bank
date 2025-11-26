package dev.kyukyubank.xbank.core.support.response

import dev.kyukyubank.xbank.core.support.error.ErrorMessage
import dev.kyukyubank.xbank.core.support.error.ErrorType

data class XApiResponse<T> private constructor(
    val result: ResultType,
    val data: T? = null,
    val error: ErrorMessage? = null 
) {
    companion object {
        fun <T> success(data: T): XApiResponse<T> {
            return XApiResponse(result = ResultType.SUCCESS, data = data, error = null)
        }

        fun <T> error(errorType: ErrorType, errorData: Any? = null): XApiResponse<T> {
            return XApiResponse(
                result = ResultType.ERROR, 
                data = null, 
                error = ErrorMessage(errorType, errorData)
            )
        }
    }
}
