package dev.kyukyubank.banking.common.enums

enum class TransferCategory {
    // 당행 송금
    INTERNAL,

    // 타행 송금
    EXTERNAL,

    // 입금
    DEPOSIT,

    // 출금
    WITHDRAWAL
}
