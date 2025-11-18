package dev.kyukyubank.banking.common.util

import dev.kyukyubank.banking.common.enums.AccountType
import dev.kyukyubank.banking.common.enums.BankCode
import org.springframework.stereotype.Component

@Component("accountNumberGenerator")
object AccountNumberGenerator {
    // 계좌 타입과 시퀀스 번호를 기반으로 계좌번호를 생성한다.
    fun generate(accountType: AccountType, sequenceNumber: Long): String {
        val branchCode = BankCode.KYUKYU_BANK.code
        val productCode = accountType.productCode
        val serialNumber = sequenceNumber.toString().padStart(6, '0')

        return "$branchCode-$productCode-$serialNumber"
    }

    // 하이픈이 제거된 12자리 계좌번호를 생성한다.
    fun generateWithoutHyphen(accountType: AccountType, sequenceNumber: Long): String {
        return generate(accountType, sequenceNumber).replace("-", "")
    }
}
