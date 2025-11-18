package dev.kyukyubank.banking.common.utils

import dev.kyukyubank.banking.common.enums.AccountType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("계좌번호 생성기 테스트")
class AccountNumberGeneratorTest {

    @Test
    @DisplayName("입출금 계좌번호 생성 - 정상")
    fun `입출금 계좌번호를 정상적으로 생성한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val sequenceNumber = 1L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        // 형식: 100(계좌종류) + 00000001(일련번호) + X(검증번호) = 10000000001X
        assertTrue(accountNumber.startsWith("100"), "입출금 계좌는 100으로 시작해야 합니다")
        assertEquals(12, accountNumber.length, "계좌번호는 12자리여야 합니다")
        assertTrue(accountNumber.all { it.isDigit() }, "모든 문자가 숫자여야 합니다")
    }

    @Test
    @DisplayName("적금 계좌번호 생성 - 정상")
    fun `적금 계좌번호를 정상적으로 생성한다`() {
        // given
        val accountType = AccountType.SAVING
        val sequenceNumber = 12345678L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        // 형식: 300(계좌종류) + 12345678(일련번호) + X(검증번호) = 30012345678X
        assertTrue(accountNumber.startsWith("300"), "적금 계좌는 300으로 시작해야 합니다")
        assertEquals(12, accountNumber.length)
        assertTrue(accountNumber.all { it.isDigit() })
    }

    @Test
    @DisplayName("정기예금 계좌번호 생성 - 정상")
    fun `정기예금 계좌번호를 정상적으로 생성한다`() {
        // given
        val accountType = AccountType.FIXED_DEPOSIT
        val sequenceNumber = 99999999L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        // 형식: 200(계좌종류) + 99999999(일련번호) + X(검증번호) = 20099999999X
        assertTrue(accountNumber.startsWith("200"), "정기예금 계좌는 200으로 시작해야 합니다")
        assertEquals(12, accountNumber.length)
        assertTrue(accountNumber.all { it.isDigit() })
    }

    @ParameterizedTest
    @CsvSource(
        "1, 00000001",
        "10, 00000010",
        "100, 00000100",
        "1000, 00001000",
        "10000, 00010000",
        "100000, 00100000",
        "1000000, 01000000",
        "10000000, 10000000",
        "99999999, 99999999"
    )
    @DisplayName("시퀀스 번호 8자리 패딩 테스트")
    fun `시퀀스 번호를 8자리로 패딩한다`(sequenceNumber: Long, expectedSerial: String) {
        // given
        val accountType = AccountType.DEPOSIT

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then: 계좌번호에서 일련번호 부분 추출 (계좌종류 3자리 이후 8자리)
        val serialNumber = accountNumber.substring(3, 11)
        assertEquals(expectedSerial, serialNumber, "일련번호는 8자리로 패딩되어야 합니다")
    }

    @Test
    @DisplayName("계좌번호 생성 - 기본 형식")
    fun `계좌번호는 기본적으로 하이픈 없이 생성된다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val sequenceNumber = 1L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        assertEquals(12, accountNumber.length, "계좌번호는 12자리여야 합니다")
        assertFalse(accountNumber.contains("-"), "하이픈이 포함되어서는 안 됩니다")
        assertTrue(accountNumber.all { it.isDigit() }, "모든 문자가 숫자여야 합니다")
    }

    @Test
    @DisplayName("모든 계좌 타입 - 계좌번호 생성 검증")
    fun `모든 계좌 타입에 대해 계좌번호를 생성한다`() {
        // given & when
        val depositAccount = AccountNumberGenerator.generate(AccountType.DEPOSIT, 1)
        val savingAccount = AccountNumberGenerator.generate(AccountType.SAVING, 1)
        val fixedDepositAccount = AccountNumberGenerator.generate(AccountType.FIXED_DEPOSIT, 1)

        // then
        assertTrue(depositAccount.startsWith("100"), "입출금 계좌는 100으로 시작")
        assertTrue(savingAccount.startsWith("300"), "적금 계좌는 300으로 시작")
        assertTrue(fixedDepositAccount.startsWith("200"), "정기예금 계좌는 200으로 시작")

        // 모두 12자리
        assertEquals(12, depositAccount.length)
        assertEquals(12, savingAccount.length)
        assertEquals(12, fixedDepositAccount.length)
    }

    @Test
    @DisplayName("검증번호 유효성 테스트")
    fun `생성된 계좌번호는 검증번호 유효성 검사를 통과한다`() {
        // given
        val accountTypes = listOf(AccountType.DEPOSIT, AccountType.SAVING, AccountType.FIXED_DEPOSIT, AccountType.FOREIGN_CURRENCY)
        val sequenceNumbers = listOf(1L, 100L, 12345L, 1234567L, 99999999L)

        // when & then
        for (accountType in accountTypes) {
            for (sequenceNumber in sequenceNumbers) {
                val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)
                assertTrue(
                    AccountNumberGenerator.validate(accountNumber),
                    "계좌번호 $accountNumber 는 검증을 통과해야 합니다"
                )
            }
        }
    }

    @Test
    @DisplayName("검증번호 유효성 테스트 - 생성된 계좌번호")
    fun `생성된 계좌번호는 검증번호 유효성 검사를 통과한다_단일`() {
        // given
        val accountNumber = AccountNumberGenerator.generate(AccountType.DEPOSIT, 123456L)

        // when
        val isValid = AccountNumberGenerator.validate(accountNumber)

        // then
        assertTrue(isValid, "생성된 계좌번호는 검증을 통과해야 합니다")
    }

    @Test
    @DisplayName("계좌번호 구조 검증")
    fun `계좌번호는 계좌종류_일련번호_검증번호 순서로 구성된다`() {
        // given
        val accountType = AccountType.SAVING
        val sequenceNumber = 87654321L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        assertEquals("300", accountNumber.substring(0, 3), "처음 3자리는 계좌종류")
        assertEquals("87654321", accountNumber.substring(3, 11), "다음 8자리는 일련번호")
        assertEquals(1, accountNumber.substring(11).length, "마지막 1자리는 검증번호")
    }

    @Test
    @DisplayName("범위 초과 일련번호 - 예외 발생")
    fun `일련번호가 최대값을 초과하면 예외가 발생한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val invalidSequenceNumber = 100000000L  // 9자리 (최대 8자리 초과)

        // when & then
        assertThrows<IllegalArgumentException> {
            AccountNumberGenerator.generate(accountType, invalidSequenceNumber)
        }
    }

    @Test
    @DisplayName("범위 미달 일련번호 - 예외 발생")
    fun `일련번호가 최소값 미만이면 예외가 발생한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val invalidSequenceNumber = 0L  // 최소값은 1

        // when & then
        assertThrows<IllegalArgumentException> {
            AccountNumberGenerator.generate(accountType, invalidSequenceNumber)
        }
    }

    @Test
    @DisplayName("경계값 테스트 - 최소 일련번호")
    fun `최소 일련번호로 계좌번호를 생성한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val minSequenceNumber = 1L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, minSequenceNumber)

        // then
        assertTrue(accountNumber.contains("00000001"), "최소 일련번호는 00000001")
        assertTrue(AccountNumberGenerator.validate(accountNumber))
    }

    @Test
    @DisplayName("경계값 테스트 - 최대 일련번호")
    fun `최대 일련번호로 계좌번호를 생성한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val maxSequenceNumber = 99999999L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, maxSequenceNumber)

        // then
        assertTrue(accountNumber.contains("99999999"), "최대 일련번호는 99999999")
        assertTrue(AccountNumberGenerator.validate(accountNumber))
    }

    @Test
    @DisplayName("전체 시나리오 - 계좌번호 생성 및 검증")
    fun `계좌번호 생성부터 검증까지 전체 시나리오`() {
        // given: 다양한 케이스
        val testCases = listOf(
            Triple(AccountType.DEPOSIT, 1L, "입출금 계좌 최소 일련번호"),
            Triple(AccountType.SAVING, 12345678L, "적금 계좌 중간 일련번호"),
            Triple(AccountType.FIXED_DEPOSIT, 99999999L, "정기예금 계좌 최대 일련번호"),
            Triple(AccountType.FOREIGN_CURRENCY, 50000000L, "외화 계좌 중간 일련번호")
        )

        for ((accountType, sequenceNumber, description) in testCases) {
            // when: 계좌번호 생성
            val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

            // then: 검증
            assertTrue(AccountNumberGenerator.validate(accountNumber), "$description - 검증 실패")
        }
    }
}
