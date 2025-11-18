package dev.kyukyubank.banking.storage.persistence

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class TransactionGroupRepositoryImpl(
    private val entityManager: EntityManager
) : TransactionGroupRepositoryCustom {

    @Transactional
    override fun getNextTransactionGroupSequence(): Long {
        // 시퀀스 테이블에 새 행 삽입
        entityManager.createNativeQuery("INSERT INTO transaction_group_sequence VALUES ()")
            .executeUpdate()

        // 마지막으로 생성된 ID 조회
        return entityManager.createNativeQuery("SELECT LAST_INSERT_ID()")
            .singleResult as Long
    }
}
