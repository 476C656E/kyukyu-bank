package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.BankStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Comment("은행")
@Entity
@Table(name = "bank")
class BankEntity(
    @field:Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Comment("은행 코드")
    @field:Column(name = "bank_code", nullable = false)
    val bankCode: String,

    @field:Comment("은행명")
    @field:Column(name = "bank_name", nullable = false)
    val bankName: String,

    @field:Comment("은행 상태")
    @field:Column(name = "bank_status", nullable = false)
    val status: BankStatus,

    @field:Comment("생성 일시")
    @field:Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @field:Comment("수정 일시")
    @field:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)