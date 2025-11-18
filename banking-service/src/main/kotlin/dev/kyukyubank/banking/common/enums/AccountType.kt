package dev.kyukyubank.banking.common.enums

enum class AccountType(
    val accountTypeCode: String
) {
    DEPOSIT("100"),           // 입출금
    FIXED_DEPOSIT("200"),     // 정기예금
    SAVING("300"),            // 적금
    FOREIGN_CURRENCY("400")   // 외화
}