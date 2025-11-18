package dev.kyukyubank.banking.common.util

import dev.kyukyubank.banking.common.enums.AccountType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
        assertEquals("1004-0001-0001", accountNumber)
    }

    @Test
    @DisplayName("적금 계좌번호 생성 - 정상")
    fun `적금 계좌번호를 정상적으로 생성한다`() {
        // given
        val accountType = AccountType.SAVING
        val sequenceNumber = 1234L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        assertEquals("1004-0002-1234", accountNumber)
    }

    @Test
    @DisplayName("대출 계좌번호 생성 - 정상")
    fun `대출 계좌번호를 정상적으로 생성한다`() {
        // given
        val accountType = AccountType.LOAN
        val sequenceNumber = 9999L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        assertEquals("1004-0003-9999", accountNumber)
    }

    @ParameterizedTest
    @CsvSource(
        "1, 0001",
        "10, 0010",
        "100, 0100",
        "1000, 1000",
        "9999, 9999"
    )
    @DisplayName("시퀀스 번호 4자리 패딩 테스트")
    fun `시퀀스 번호를 4자리로 패딩한다`(sequenceNumber: Long, expectedSerial: String) {
        // given
        val accountType = AccountType.DEPOSIT

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        val serialNumber = accountNumber.split("-").last()
        assertEquals(expectedSerial, serialNumber)
    }

    @Test
    @DisplayName("계좌번호 형식 검증 - 하이픈 포함")
    fun `하이픈이 포함된 올바른 형식의 계좌번호를 생성한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val sequenceNumber = 1L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        val parts = accountNumber.split("-")
        assertEquals(3, parts.size, "하이픈으로 구분된 3개의 파트가 있어야 합니다")
        assertEquals("1004", parts[0], "첫 번째 파트는 지점코드(1004)여야 합니다")
        assertEquals(4, parts[0].length, "지점코드는 4자리여야 합니다")
        assertEquals(4, parts[1].length, "상품코드는 4자리여야 합니다")
        assertEquals(4, parts[2].length, "일련번호는 4자리여야 합니다")
    }

    @Test
    @DisplayName("하이픈 제거된 계좌번호 생성 - 정상")
    fun `하이픈이 제거된 계좌번호를 생성한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val sequenceNumber = 1L

        // when
        val accountNumber = AccountNumberGenerator.generateWithoutHyphen(accountType, sequenceNumber)

        // then
        assertEquals("100400010001", accountNumber)
        assertEquals(12, accountNumber.length, "하이픈 제거된 계좌번호는 12자리여야 합니다")
        assertFalse(accountNumber.contains("-"), "하이픈이 포함되어서는 안 됩니다")
    }

    @Test
    @DisplayName("하이픈 제거된 계좌번호 - 모든 계좌 타입 검증")
    fun `모든 계좌 타입에 대해 하이픈이 제거된 계좌번호를 생성한다`() {
        // given & when & then
        assertEquals(
            "100400010001",
            AccountNumberGenerator.generateWithoutHyphen(AccountType.DEPOSIT, 1)
        )
        assertEquals(
            "100400020001",
            AccountNumberGenerator.generateWithoutHyphen(AccountType.SAVING, 1)
        )
        assertEquals(
            "100400030001",
            AccountNumberGenerator.generateWithoutHyphen(AccountType.LOAN, 1)
        )
    }

    @Test
    @DisplayName("큰 시퀀스 번호 처리")
    fun `큰 시퀀스 번호를 정상적으로 처리한다`() {
        // given
        val accountType = AccountType.DEPOSIT
        val sequenceNumber = 999999L

        // when
        val accountNumber = AccountNumberGenerator.generate(accountType, sequenceNumber)

        // then
        // 4자리를 넘어가는 경우에도 정상 처리되어야 한다.
        assertEquals("1004-0001-999999", accountNumber)
    }
}
