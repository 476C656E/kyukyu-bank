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
    @field:Comment("회원 고유번호")
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Comment("회원 계정 아이디")
    @field:Column(unique = true, nullable = false)
    val accountId: String,

    @field:Comment("회원 계정 비밀번호")
    @field:Column(nullable = false)
    val password: String,

    @field:Comment("회원 이름")
    @field:Column(nullable = false)
    val name: String,

    @field:Comment("회원 영문 이름")
    @field:Column(nullable = false)
    val nameEn: String,

    @field:Comment("회원 생년월일")
    @field:Column(nullable = false)
    val dateOfBirth: LocalDate,

    @CreationTimestamp
    @field:Comment("회원 가입 날짜")
    @field:Column("created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @field:Comment("회원 정보 변경 날짜")
    @field:Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
