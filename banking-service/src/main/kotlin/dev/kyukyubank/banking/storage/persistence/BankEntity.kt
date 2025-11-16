package dev.kyukyubank.banking.storage.persistence

import dev.kyukyubank.banking.common.enums.BankStatus
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
import org.hibernate.annotations.UpdateTimestamp
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
    @Enumerated(EnumType.STRING)
    @field:Column(name = "bank_status", nullable = false)
    val status: BankStatus,
) {
    @CreationTimestamp
    @Comment("생성 일시")
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
        private set

    @UpdateTimestamp
    @Comment("수정 일시")
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime
        private set
}