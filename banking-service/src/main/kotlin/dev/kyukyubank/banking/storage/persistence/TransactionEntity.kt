package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.TransactionStatus
import dev.kyukyubank.banking.common.enums.TransferCategory
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("거래 이력")
@Entity
@Table(
    name = "transaction",
    indexes = [
        Index(name = "idx_tx_date", columnList = "transaction_date"),
        Index(name = "idx_tx_status", columnList = "status")
    ]
)
class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Comment("거래 ID")
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:Comment("송금 구분 (당행/타행)")
    @field:Column(name = "transfer_category", nullable = false, length = 20)
    val transferCategory: TransferCategory,

    // 요청자 정보 (보내는 사람)
    @field:Comment("보내는 사람 계좌 ID (당행인 경우)")
    @field:Column(name = "sender_account_id", nullable = true)
    val senderAccountId: Long?,

    @field:Comment("보내는 사람 이름")
    @field:Column(name = "sender_name", length = 100)
    val senderName: String,

    // 수취인 정보
    @field:Comment("수취인 은행 코드")
    @field:Column(name = "receiver_bank_code", length = 10)
    val receiverBankCode: String,

    @field:Comment("수취인 계좌번호")
    @field:Column(name = "receiver_account_number", length = 50)
    val receiverAccountNumber: String,

    @field:Comment("수취인 계좌 ID (당행인 경우)")
    @field:Column(name = "receiver_account_id", nullable = true)
    val receiverAccountId: Long?,

    @field:Comment("수취인 이름")
    @field:Column(name = "receiver_name", length = 100)
    val receiverName: String,

    // 거래 금액 및 상태
    @field:Comment("거래 금액")
    @field:Column(name = "amount", nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @field:Comment("거래 상태")
    @field:Column(name = "status", nullable = false, length = 20)
    var status: TransactionStatus,

    @field:Comment("실패 사유")
    @field:Column(name = "failure_reason", length = 500)
    var failureReason: String? = null

) {
    @CreationTimestamp
    @Comment("거래 발생 일시")
    @Column(name = "transaction_date", nullable = false, updatable = false)
    lateinit var transactionDate: LocalDateTime
        private set
}