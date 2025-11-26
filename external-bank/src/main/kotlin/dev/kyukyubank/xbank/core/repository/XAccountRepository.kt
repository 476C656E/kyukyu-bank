package dev.kyukyubank.xbank.core.repository

import dev.kyukyubank.xbank.core.domain.XAccount
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

@Repository
class XAccountRepository {
    // 메모리 DB 역할
    private val store = ConcurrentHashMap<String, XAccount>()

    fun save(account: XAccount): XAccount {
        store[account.accountNumber] = account
        return account
    }

    fun findByAccountNumber(accountNumber: String): XAccount? {
        return store[accountNumber]
    }
    
    fun findByAccountNumberOrSave(accountNumber: String, defaultName: String): XAccount {
        return store.computeIfAbsent(accountNumber) {
            XAccount(it, defaultName, BigDecimal("1000000000"))
        }
    }
}
