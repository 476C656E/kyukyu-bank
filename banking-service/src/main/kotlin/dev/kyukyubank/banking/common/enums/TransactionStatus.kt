package dev.kyukyubank.banking.common.enums

// 거래 상태
enum class TransactionStatus {
    // 처리중
    PENDING,

    // 성공
    SUCCESS,

    // 실패
    FAILED,

    // 취소
    CANCELLED
}