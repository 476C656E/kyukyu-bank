package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.EntryType
import dev.kyukyubank.banking.common.enums.TransactionStatus
import dev.kyukyubank.banking.common.enums.TransferCategory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("거래 원장")
@Entity
@Table(
    name = "transactions",
    indexes = [
        Index(name = "idx_account_id", columnList = "account_id"),
        Index(name = "idx_transaction_group_id", columnList = "transaction_group_id"),
        Index(name = "idx_transaction_date", columnList = "transaction_date"),
        Index(name = "idx_status", columnList = "status")
    ]
)
class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Comment("거래 번호")
    val id: Long = 0,

    // 당행 송금의 경우 복식부기
    @field:Comment("거래 그룹 ID")
    @field:Column(name = "transaction_group_id", nullable = false, updatable = false, length = 100)
    val transactionGroupId: String,

    @field:Comment("계좌 ID")
    @field:Column(name = "account_id", nullable = false, updatable = false)
    val accountId: Long,

    // 차변 대변 유형
    @Enumerated(EnumType.STRING)
    @field:Comment("기입 유형")
    @field:Column(name = "entry_type", nullable = false, updatable = false, length = 20)
    val entryType: EntryType,

    @field:Comment("거래 금액")
    @field:Column(name = "amount", nullable = false, updatable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @field:Comment("송금 구분")
    @field:Column(name = "transfer_category", nullable = false, updatable = false, length = 20)
    val transferCategory: TransferCategory,

    @Enumerated(EnumType.STRING)
    @field:Comment("거래 상태")
    @field:Column(name = "status", nullable = false, length = 20)
    val status: TransactionStatus,

    // 타행 송금
    @field:Comment("상대방 은행 코드")
    @field:Column(name = "counter_bank_code", updatable = false, length = 10)
    val counterBankCode: String,

    // 타행 송금
    @field:Comment("상대방 계좌번호")
    @field:Column(name = "counter_account_number", updatable = false, length = 50)
    val counterAccountNumber: String,

    @field:Comment("상대방 계좌 ID (내부 송금시)")
    @field:Column(name = "counter_account_id", updatable = false)
    val counterAccountId: Long,

    @field:Comment("거래 전 잔액")
    @field:Column(name = "balance_before", nullable = false, updatable = false, precision = 19, scale = 2)
    val balanceBefore: BigDecimal,

    @field:Comment("거래 후 잔액")
    @field:Column(name = "balance_after", nullable = false, updatable = false, precision = 19, scale = 2)
    val balanceAfter: BigDecimal,

    @field:Comment("실패 사유")
    @field:Column(name = "failure_reason", updatable = false, length = 500)
    val failureReason: String,

    @field:Comment("거래 메모")
    @field:Column(name = "memo", updatable = false, length = 200)
    val memo: String
) {
    @CreationTimestamp
    @Comment("거래 발생 일시")
    @Column(name = "transaction_date", nullable = false, updatable = false)
    lateinit var transactionDate: LocalDateTime
        private set
}