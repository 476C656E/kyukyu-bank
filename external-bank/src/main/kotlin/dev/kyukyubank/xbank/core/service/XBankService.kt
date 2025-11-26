package dev.kyukyubank.xbank.core.service

import dev.kyukyubank.xbank.core.domain.Deposit
import dev.kyukyubank.xbank.core.domain.DepositResult
import dev.kyukyubank.xbank.core.domain.SimulationOptions
import dev.kyukyubank.xbank.core.repository.XAccountRepository
import dev.kyukyubank.xbank.core.support.error.CoreException
import dev.kyukyubank.xbank.core.support.error.ErrorType
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@Service
class XBankService(
    private val xAccountRepository: XAccountRepository
) {
    fun deposit(command: Deposit): DepositResult {
        val options = command.simulation
        
        // 1. 지연 시뮬레이션
        val delay = options.delayMs ?: 0L
        if (delay > 0) {
            try {
                Thread.sleep(delay)
                println("지연 테스트 중이에요. ${delay}ms 뒤에 처리할게요.")
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        // 2. 실패 시뮬레이션 (옵션 없으면 기본 10%)
        val failureRate = options.failureRate ?: 10
        
        if (Random.nextInt(100) < failureRate) {
            println("설정된 확률($failureRate%) 때문에 일부러 에러를 냈어요.")
            throw CoreException(ErrorType.SIMULATED_FAILURE, "확률: $failureRate%")
        }

        // 3. 로직 수행
        val account = xAccountRepository.findByAccountNumberOrSave(command.receiverAccountNumber, "가상 고객")

        synchronized(account) {
            account.balance = account.balance.add(command.amount)
        }

        val txId = UUID.randomUUID().toString().replace("-", "").uppercase()
        val now = LocalDateTime.now()

        println("${command.senderName}님이 ${account.name}님에게 ${command.amount}원을 입금했어요. (TxID: $txId)")
        
        return DepositResult(
            transactionId = txId,
            resultCode = "0000",
            balanceAfter = account.balance,
            processedAt = now
        )
    }
}