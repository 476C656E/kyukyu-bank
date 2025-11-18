package dev.kyukyubank.banking.common.utils

import dev.kyukyubank.banking.storage.persistence.TransactionGroupRepositoryCustom
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom

// 거래 그룹 ID 생성기
@Component("transactionGroupIdGenerator")
class TransactionGroupIdGenerator(
    private val transactionGroupRepository: TransactionGroupRepositoryCustom
) {
    companion object {
        private const val PREFIX = "TXN"
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    // 거래 그룹 ID를 생성한다.
    fun generate(): String {
        val date = LocalDate.now().format(DATE_FORMATTER)
        val sequence = transactionGroupRepository.getNextTransactionGroupSequence()
        val random = ThreadLocalRandom.current().nextInt(1000, 9999)

        val sequenceStr = sequence.toString().padStart(6, '0')

        return "$PREFIX-$date-$sequenceStr-$random"
    }

    // 하이픈이 제거된 거래 그룹 ID를 생성한다.
    fun generateWithoutHyphen(): String {
        return generate().replace("-", "")
    }
}