package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.AccountCurrency
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Comment("현금자동입출기")
class ATMEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Comment("은행 코드")
    @field:Column(name = "bank_code", nullable = false)
    val bankCode: String,

    @field:Comment("계좌 번호")
    @field:Column(name = "account_number", nullable = false)
    val accountNumber: String,

    @field:Comment("입금자")
    @field:Column(name = "customer_name", nullable = false)
    val customerName: String,

    @field:Comment("입금 금액")
    @field:Column(name = "deposit_amount", nullable = false)
    val depositAmount: BigDecimal,

    @field:Comment("통화")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_currency", nullable = false)
    val currency: AccountCurrency,

    @field:Comment("거래 일시")
    @CreationTimestamp
    @field:Column(name = "transaction_date", nullable = false)
    val transactionDate: LocalDateTime = LocalDateTime.now()
)