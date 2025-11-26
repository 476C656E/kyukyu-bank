package dev.kyukyubank.xbank.core.initializer

import dev.kyukyubank.xbank.core.domain.XAccount
import dev.kyukyubank.xbank.core.repository.XAccountRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class SettlementInitializer(
    private val repository: XAccountRepository
) : ApplicationRunner {

    companion object {
        const val SETTLEMENT_ACCOUNT_NO = "SETTLEMENT-KYUKYU"
    }

    override fun run(args: ApplicationArguments?) {
        val account = XAccount(
            accountNumber = SETTLEMENT_ACCOUNT_NO,
            name = "큐큐뱅크 정산용 계좌",
            balance = BigDecimal.ZERO
        )
        repository.save(account)
        println("큐큐뱅크와의 정산을 위한 법인 계좌를 만들었어요. 계좌번호: $SETTLEMENT_ACCOUNT_NO")
    }
}
