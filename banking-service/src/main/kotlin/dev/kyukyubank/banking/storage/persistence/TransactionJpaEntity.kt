package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("회원 거래")
@Entity
@Table
class TransactionJpaEntity(

    @field:Comment("ㅁㄴㅇㅁㅇ?")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Comment("거래 고유 식별자")
    @field:Column(unique = true, nullable = false)
    val transactionId: String,

    @field:Comment("출금된 계좌")
    @field:Column(nullable = false)
    val fromAccountId: Long,

    @field:Comment("송금 은행 코드")
    @field:Column(nullable = false)
    val toBankCode: String,

    @field:Comment("송금 계좌")
    @field:Column(nullable = false)
    val toAccountNumber: String,

    @field:Comment("거래 금액")
    @field:Column(nullable = false)
    val amount: BigDecimal,

    @field:Comment("거래 상태")
    @field:Column(nullable = false)
    val status: String,

    @field:Comment("거래 종류")
    @field:Column(nullable = false)
    val transactionType: TransactionType,

    @field:Comment("거래 발생 일시")
    @field:Column(nullable = false)
    val transactionDate: LocalDateTime = LocalDateTime.now()
)