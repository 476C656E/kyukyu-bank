package dev.kyukyubank.banking.common.enums

// 거래 기입 타입 (복식부기)
enum class EntryType {
    // DEBIT: 차변 (출금, 자산 증가)
    DEBIT,

    // CREDIT: 대변 (입금, 자산 감소)
    CREDIT
}
