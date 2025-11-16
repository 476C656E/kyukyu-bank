package dev.kyukyubank.banking.storage.persistence

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Comment("회원 정보")
@Entity
@Table(name = "user")
class UserEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Comment("회원 고유번호")
    val id: Long = 0,

    @field:Comment("회원 계정 아이디")
    @field:Column(name = "account_id", unique = true, nullable = false)
    val accountId: String,

    @field:Comment("회원 계정 비밀번호")
    @field:Column(name = "password", nullable = false)
    val password: String,

    @field:Comment("회원 이름")
    @field:Column(name = "name", nullable = false)
    val name: String,

    @field:Comment("회원 영문 이름")
    @field:Column(name = "name_en", nullable = false)
    val nameEn: String,

    @field:Comment("회원 생년월일")
    @field:Column(name = "date_of_birth", nullable = false)
    val dateOfBirth: LocalDate,
) {
    @CreationTimestamp
    @Comment("회원 가입 날짜")
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
        private set

    @UpdateTimestamp
    @Comment("회원 정보 변경 날짜")
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime
        private set
}
