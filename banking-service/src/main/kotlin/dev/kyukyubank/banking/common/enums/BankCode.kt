package dev.kyukyubank.banking.common.enums

enum class BankCode(val code: String, val bankName: String){
    KYUKYU_BANK("002", "큐큐 은행"),
    XBANK(code = "101", "엑스 은행")
}