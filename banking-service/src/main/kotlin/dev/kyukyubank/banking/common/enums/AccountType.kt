package dev.kyukyubank.banking.common.enums

enum class AccountType(val productCode: String) {
    DEPOSIT("0001"),
    SAVING("0002"),
    LOAN("0003")
}