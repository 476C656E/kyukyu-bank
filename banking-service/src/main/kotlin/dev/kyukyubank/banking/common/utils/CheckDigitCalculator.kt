package dev.kyukyubank.banking.common.utils

import org.springframework.stereotype.Component

// Modulo 연산을 사용하여 계좌번호 검증번호를 계산하는 유틸리티
@Component
object CheckDigitCalculator {

    // 11 Modulo 연산의 가중치 (오른쪽부터 왼쪽으로 적용)
    private val WEIGHTS = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6)

    // 계좌번호의 앞 11자리(YYYZ-ZZZZ-ZZZ)를 기반으로 검증번호를 계산한다
    fun calculate(accountNumberPrefix: String): Int {
        // 하이픈 제거 및 유효성 검사
        val digits = accountNumberPrefix.replace("-", "")
        require(digits.length == 11) { "계좌번호는 11자리여야 해요. 입력값: $accountNumberPrefix" }
        require(digits.all { it.isDigit() }) { "계좌번호는 숫자만 포함해야 해요. 입력값: $accountNumberPrefix" }

        // 오른쪽부터 왼쪽으로 가중치 적용하여 합산한다
        var sum = 0
        for (i in digits.indices) {
            val digit = digits[digits.length - 1 - i].digitToInt()
            sum += digit * WEIGHTS[i]
        }

        // 11 Modulo
        val remainder = sum % 11
        val checkDigit = (11 - remainder) % 11

        // 10이면 0으로 변환 (1자리 숫자로 유지)
        return if (checkDigit == 10) 0 else checkDigit
    }

    // 계좌번호 전체(검증번호 포함)의 유효성을 검증한다.
    fun validate(fullAccountNumber: String): Boolean {
        // 하이픈 제거
        val withoutHyphen = fullAccountNumber.replace("-", "")

        // 길이 검증
        if (withoutHyphen.length != 12) return false

        // 숫자만 포함하는지 검증
        if (!withoutHyphen.all { it.isDigit() }) return false

        // 앞 11자리와 검증번호 분리
        val prefix = withoutHyphen.substring(0, 11)
        val providedCheckDigit = withoutHyphen.last().digitToInt()

        // 계산된 검증번호와 제공된 검증번호 비교
        val calculatedCheckDigit = calculate(prefix)

        return calculatedCheckDigit == providedCheckDigit
    }
}
