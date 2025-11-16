package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("회원 거래")
@Entity
@Table(name = "transactions")
class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Comment("거래 번호")
    val id: Long = 0,

    @field:Comment("거래 고유 식별자")
    @field:Column(name = "transaction_id", unique = true, nullable = false)
    val transactionId: String,

    @field:Comment("출금된 계좌 ID")
    @field:Column(name = "from_account_id", nullable = false)
    val fromAccountId: Long,

    @field:Comment("입금 은행 코드")
    @field:Column(name = "to_bank_code", nullable = false)
    val toBankCode: String,

    @field:Comment("입금 계좌 번호")
    @field:Column(name = "to_account_number", nullable = false)
    val toAccountNumber: String,

    @field:Comment("거래 금액")
    @field:Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @field:Comment("거래 상태")
    @field:Column(name = "status", nullable = false)
    val status: String,

    @Enumerated(EnumType.STRING)
    @field:Comment("거래 종류")
    @field:Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,
) {
    @CreationTimestamp
    @Comment("거래 발생 일시")
    @Column(name = "transaction_date", nullable = false, updatable = false)
    lateinit var transactionDate: LocalDateTime
        private set
}