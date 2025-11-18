package dev.kyukyubank.banking.storage.persistence

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AccountRepositoryImpl(
    private val entityManager: EntityManager
) : AccountRepositoryCustom {

    @Transactional
    override fun getNextAccountNumberSequence(): Long {
        // 시퀀스 테이블에 새 레코드 삽입
        entityManager.createNativeQuery("INSERT INTO account_number_sequence VALUES ()")
            .executeUpdate()

        // 마지막 삽입된 ID 반환
        return entityManager.createNativeQuery("SELECT LAST_INSERT_ID()")
            .singleResult as Long
    }
}
