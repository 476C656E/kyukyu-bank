package dev.kyukyubank.banking.common.utils

import dev.kyukyubank.banking.common.enums.AccountType
import org.springframework.stereotype.Component

@Component("accountNumberGenerator")
object AccountNumberGenerator {

    // 계좌 타입과 시퀀스 번호를 기반으로 12자리 계좌번호를 생성한다. (DB 저장용)
    fun generate(accountType: AccountType, sequenceNumber: Long): String {
        require(sequenceNumber in 1..99999999) {
            "일련번호는 1부터 99999999 사이여야 해요: $sequenceNumber"
        }

        // 계좌종류 코드 (3자리) + 일련번호 (8자리)
        val accountTypeCode = accountType.accountTypeCode
        val serialNumber = sequenceNumber.toString().padStart(8, '0')
        val prefix = accountTypeCode + serialNumber

        // 검증번호 계산
        val checkDigit = CheckDigitCalculator.calculate(prefix)

        // 전체 12자리 계좌번호
        return prefix + checkDigit
    }

    // 계좌번호의 유효성을 검증한다.
    fun validate(accountNumber: String): Boolean {
        return CheckDigitCalculator.validate(accountNumber)
    }
}
