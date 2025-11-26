package dev.kyukyubank.xbank.core.support.api

import dev.kyukyubank.xbank.core.support.error.CoreException
import dev.kyukyubank.xbank.core.support.error.ErrorType
import dev.kyukyubank.xbank.core.support.response.XApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class XBankExceptionHandler {

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(e: CoreException): ResponseEntity<XApiResponse<Any>> {
        return ResponseEntity.status(e.errorType.status).body(
            XApiResponse.error(e.errorType, e.data)
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<XApiResponse<Any>> {
        return ResponseEntity.status(ErrorType.INVALID_REQUEST.status).body(
            XApiResponse.error(ErrorType.INVALID_REQUEST, e.message)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<XApiResponse<Any>> {
        e.printStackTrace()
        return ResponseEntity.status(ErrorType.DEFAULT_ERROR.status).body(
            XApiResponse.error(ErrorType.DEFAULT_ERROR, e.message)
        )
    }
}
