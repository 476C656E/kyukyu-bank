package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.EntryType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("계좌 원장")
@Entity
@Table(
    name = "ledger",
    indexes = [
        Index(name = "idx_ledger_account_id", columnList = "account_id"),
        Index(name = "idx_ledger_created_at", columnList = "created_at")
    ]
)
class LedgerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Comment("원장 ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    @field:Comment("관련 거래 ID")
    val transaction: TransactionEntity,

    @field:Comment("대상 계좌 ID (당행 계좌)")
    @field:Column(name = "account_id", nullable = false)
    val accountId: Long,

    @Enumerated(EnumType.STRING)
    @field:Comment("기입 유형 복식부기 (차변/대변)")
    @field:Column(name = "entry_type", nullable = false, length = 20)
    val entryType: EntryType,

    @field:Comment("변동 금액")
    @field:Column(name = "amount", nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @field:Comment("변동 후 잔액")
    @field:Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    val balanceAfter: BigDecimal,

    @field:Comment("적요")
    @field:Column(name = "memo", length = 200)
    val memo: String?
) {
    @CreationTimestamp
    @Comment("기록 일시")
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
        private set
}
