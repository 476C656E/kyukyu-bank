package dev.kyukyubank.banking.common.enums

// 거래 기입 타입 (복식부기)
enum class EntryType {
    // DEBIT: 차변 (은행 부채 감소 -> 고객 출금)
    DEBIT,

    // CREDIT: 대변 (은행 부채 증가 -> 고객 입금)
    CREDIT
}
