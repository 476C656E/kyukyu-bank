package dev.kyukyubank.banking.storage.persistence

interface TransactionGroupRepositoryCustom {
    /**
     * 거래 그룹 시퀀스 번호를 생성한다.
     * @return 다음 시퀀스 번호
     */
    fun getNextTransactionGroupSequence(): Long
}
