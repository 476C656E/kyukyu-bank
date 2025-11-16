package dev.kyukyubank.banking.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "뭔가 잘못됐어요. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, "요청을 다시 확인해주세요.", LogLevel.INFO),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "로그인에 실패했어요. 계정 정보를 다시 확인해주세요.", LogLevel.INFO),
    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST, ErrorCode.E404, "해당 데이터를 찾을 수 없어요.", LogLevel.ERROR),
}