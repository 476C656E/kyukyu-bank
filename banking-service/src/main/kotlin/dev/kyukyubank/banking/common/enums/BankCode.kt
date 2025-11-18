package dev.kyukyubank.banking.common.enums

enum class BankCode(val code: String, val bankName: String){
    BANK_OF_EARTH(code = "001", "지구은행"),
    KYUKYU_BANK("1004", "큐큐은행"),
    Y_BANK(code = "1009", "와이은행"),
    X_BANK(code = "1010", "엑스은행"),
    Z_BANK(code = "1011", "제트은행"),
}