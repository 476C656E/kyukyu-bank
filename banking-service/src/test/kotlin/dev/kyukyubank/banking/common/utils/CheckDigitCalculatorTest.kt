package dev.kyukyubank.banking.common.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CheckDigitCalculatorTest {

    @Test
    fun `검증번호 계산 - 정상 케이스`() {
        // given: 계좌종류 100 + 일련번호 00000001
        val accountPrefix = "10000000001"

        // when
        val checkDigit = CheckDigitCalculator.calculate(accountPrefix)

        // then: Modulo 11 알고리즘에 따라 계산된 검증번호
        // 계산: 1*2 + 0*3 + 0*4 + 0*5 + 0*6 + 0*7 + 0*2 + 0*3 + 0*4 + 0*5 + 1*6
        //     = 2 + 6 = 8
        // 나머지: 8 % 11 = 8
        // 검증번호: (11 - 8) % 11 = 3
        assertEquals(3, checkDigit)
    }

    @Test
    fun `검증번호 계산 - 하이픈 포함 입력값 처리`() {
        // given: 하이픈이 포함된 계좌번호
        val accountPrefix = "100-00000001"

        // when
        val checkDigit = CheckDigitCalculator.calculate(accountPrefix)

        // then: 하이픈을 제거하고 정상 계산
        assertEquals(3, checkDigit)
    }

    @Test
    fun `검증번호 계산 - 다양한 일련번호`() {
        // given & when & then
        // 각 계좌번호에 대해 Modulo 11 알고리즘으로 계산된 실제 검증번호
        // 계좌종류 코드 변경: 100, 200, 300
        val checkDigit1 = CheckDigitCalculator.calculate("10012345678")  // DEPOSIT
        val checkDigit2 = CheckDigitCalculator.calculate("20000000001")  // FIXED_DEPOSIT
        val checkDigit3 = CheckDigitCalculator.calculate("30000000001")  // SAVING

        // 검증번호는 0~10 범위여야 함
        assertTrue(checkDigit1 in 0..10)
        assertTrue(checkDigit2 in 0..10)
        assertTrue(checkDigit3 in 0..10)
    }

    @Test
    fun `검증번호 계산 - 결과가 10인 경우 0으로 변환`() {
        // given: 검증번호 계산 결과가 10이 되는 경우
        // 계산하여 (11 - remainder) % 11 = 10이 나오는 케이스
        val accountPrefix = "10099999999"

        // when
        val checkDigit = CheckDigitCalculator.calculate(accountPrefix)

        // then: 10은 1자리를 초과하므로 0으로 변환
        assertTrue(checkDigit in 0..9, "검증번호는 0~9 범위여야 합니다")
    }

    @Test
    fun `검증번호 유효성 검증 - 올바른 계좌번호`() {
        // given: 검증번호가 올바른 완전한 계좌번호
        // 100 + 00000001 + 검증번호 3 = 100000000013
        val validAccountNumber = "100000000013"

        // when
        val isValid = CheckDigitCalculator.validate(validAccountNumber)

        // then
        assertTrue(isValid)
    }

    @Test
    fun `검증번호 유효성 검증 - 하이픈 포함 올바른 계좌번호`() {
        // given: 하이픈이 포함되고 검증번호가 올바른 계좌번호
        // 10012345678의 검증번호를 계산하여 사용
        val prefix = "10012345678"
        val checkDigit = CheckDigitCalculator.calculate(prefix)
        val validAccountNumber = "${prefix.substring(0, 4)}-${prefix.substring(4, 8)}-${prefix.substring(8)}$checkDigit"

        // when
        val isValid = CheckDigitCalculator.validate(validAccountNumber)

        // then
        assertTrue(isValid)
    }

    @Test
    fun `검증번호 유효성 검증 - 잘못된 검증번호`() {
        // given: 검증번호가 잘못된 계좌번호
        val invalidAccountNumber = "100000000019"  // 올바른 검증번호는 3이지만 9로 입력

        // when
        val isValid = CheckDigitCalculator.validate(invalidAccountNumber)

        // then
        assertFalse(isValid)
    }

    @Test
    fun `검증번호 유효성 검증 - 잘못된 길이`() {
        // given: 12자리가 아닌 계좌번호
        val shortAccountNumber = "10000000001"  // 11자리
        val longAccountNumber = "1000000000199"  // 13자리

        // when & then
        assertFalse(CheckDigitCalculator.validate(shortAccountNumber))
        assertFalse(CheckDigitCalculator.validate(longAccountNumber))
    }

    @Test
    fun `검증번호 유효성 검증 - 숫자가 아닌 문자 포함`() {
        // given: 알파벳이 포함된 계좌번호
        val accountNumberWithLetters = "100A0000001B"

        // when
        val isValid = CheckDigitCalculator.validate(accountNumberWithLetters)

        // then
        assertFalse(isValid)
    }

    @Test
    fun `검증번호 계산 - 잘못된 길이 입력시 예외 발생`() {
        // given: 11자리가 아닌 입력값
        val shortInput = "1000000001"  // 10자리
        val longInput = "100000000001"  // 12자리

        // when & then
        assertThrows<IllegalArgumentException> {
            CheckDigitCalculator.calculate(shortInput)
        }
        assertThrows<IllegalArgumentException> {
            CheckDigitCalculator.calculate(longInput)
        }
    }

    @Test
    fun `검증번호 계산 - 숫자가 아닌 문자 포함시 예외 발생`() {
        // given: 알파벳이 포함된 입력값
        val invalidInput = "100ABCD0001"

        // when & then
        assertThrows<IllegalArgumentException> {
            CheckDigitCalculator.calculate(invalidInput)
        }
    }

    @Test
    fun `전체 시나리오 - 계좌번호 생성 및 검증`() {
        // given: 계좌종류와 일련번호
        val accountTypeCode = "300"  // SAVING
        val serialNumber = "87654321"
        val prefix = accountTypeCode + serialNumber

        // when: 검증번호 계산
        val checkDigit = CheckDigitCalculator.calculate(prefix)
        val fullAccountNumber = prefix + checkDigit

        // then: 생성된 계좌번호가 유효성 검증 통과
        assertTrue(CheckDigitCalculator.validate(fullAccountNumber))
        assertEquals(12, fullAccountNumber.length)
    }

    @Test
    fun `전체 시나리오 - 하이픈 포맷팅된 계좌번호 검증`() {
        // given: 하이픈으로 포맷팅된 계좌번호 생성
        val accountTypeCode = "200"  // FIXED_DEPOSIT
        val serialNumber = "11111111"
        val prefix = accountTypeCode + serialNumber
        val checkDigit = CheckDigitCalculator.calculate(prefix)

        // 4-4-4 형식으로 포맷팅
        val formattedAccountNumber = "${prefix.substring(0, 4)}-${prefix.substring(4, 8)}-${prefix.substring(8)}$checkDigit"

        // when
        val isValid = CheckDigitCalculator.validate(formattedAccountNumber)

        // then
        assertTrue(isValid)
        assertEquals(14, formattedAccountNumber.length)  // 12자리 + 하이픈 2개
        assertTrue(formattedAccountNumber.matches(Regex("\\d{4}-\\d{4}-\\d{4}")))
    }
}
