package dev.kyukyubank.xbank.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "X-Bank 내부 오류가 발생했습니다.", LogLevel.ERROR),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 요청입니다.", LogLevel.INFO),
    
    // 시뮬레이션 전용 에러
    SIMULATED_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "시뮬레이션된 강제 실패입니다.", LogLevel.ERROR)
}
