package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.AccountCurrency
import dev.kyukyubank.banking.common.enums.AccountStatus
import dev.kyukyubank.banking.common.enums.AccountType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("회원 계좌")
@Entity
@Table(name = "account")
class AccountEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Comment("유저 번호")
    @field:Column(name = "fk_account_user")
    val userId: Long,

    @field:Comment("계좌번호")
    @field:Column(name = "account_number", unique = true, nullable = false)
    val accountNumber: String,

    @field:Comment("계좌 비밀번호")
    @field:Column(name = "account_password", nullable = false)
    val accountPassword: String,

    @field:Comment("계좌 잔액")
    @field:Column(name = "account_balance", nullable = false)
    var balance: BigDecimal,

    @field:Comment("은행 코드")
    @field:Column(name = "account_bank_code", nullable = false)
    val bankCode: String,

    @field:Comment("계좌 유형")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_type", nullable = false)
    val type: AccountType,

    @field:Comment("계좌 통화")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_currency", nullable = false)
    val currency: AccountCurrency,

    @field:Comment("계좌 상태")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_status", nullable = false)
    var status: AccountStatus,
) {
    @CreationTimestamp
    @Comment("계좌 개설 일시")
    @Column(name = "account_opened_at", nullable = false, updatable = false)
    lateinit var openedAt: LocalDateTime
        private set

    @UpdateTimestamp
    @Comment("수정 일시")
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime
        private set
}