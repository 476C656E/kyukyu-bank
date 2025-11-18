package dev.kyukyubank.banking.core.api

import dev.kyukyubank.banking.core.api.request.CreateAccountRequest
import dev.kyukyubank.banking.core.api.request.DepositRequest
import dev.kyukyubank.banking.core.api.request.WithdrawRequest
import dev.kyukyubank.banking.core.api.response.AccountInfoResponse
import dev.kyukyubank.banking.core.api.response.TransactionResponse
import dev.kyukyubank.banking.core.domain.AccountService
import dev.kyukyubank.banking.core.support.OffsetLimit
import dev.kyukyubank.banking.core.support.response.ApiResponse
import dev.kyukyubank.banking.core.support.response.PagingResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping("/api/accounts")
    fun createAccount(
        @RequestBody request: CreateAccountRequest
    ) : ApiResponse<Any> {
        accountService.createAccount(request.toAccount())

        return ApiResponse.success()
    }

    @PostMapping("/api/accounts/deposit")
    fun deposit(
        @RequestBody request: DepositRequest
    ): ApiResponse<TransactionResponse> {
        val deposit = accountService.deposit(request.toDepositTransaction())

        return ApiResponse.success(TransactionResponse.of(deposit.accountId, deposit.balance))
    }

    @PostMapping("/api/accounts/withdraw")
    fun withdraw(
        @RequestBody request: WithdrawRequest
    ): ApiResponse<TransactionResponse> {
        val withdraw = accountService.withdraw(request.toWithdrawTransaction())

        return ApiResponse.success(TransactionResponse.of(withdraw.accountId, withdraw.balance))
    }

    @GetMapping("/api/accounts/{accountId}")
    fun getAccount(
        @RequestParam userId: Long,
        @PathVariable accountId: Long,
    ) : ApiResponse<AccountInfoResponse>{

        val account = accountService.getAccount(userId, accountId)

        return ApiResponse.success(AccountInfoResponse.of(account))
    }

    @GetMapping("/api/accounts")
    fun getAccounts(
        @RequestParam userId: Long,
        @RequestParam offset: Int,
        @RequestParam limit: Int,
    ) : ApiResponse<PagingResponse<AccountInfoResponse>> {
        val accounts = accountService.getAccounts(userId, OffsetLimit(offset, limit))

        return ApiResponse.success(PagingResponse(AccountInfoResponse.of(accounts.content), accounts.hasNext))
    }

    @GetMapping("/api/accounts/{accountId}/balance")
    fun getAccountBalance(
        @RequestParam userId: Long,
        @PathVariable accountId: Long
    ) : ApiResponse<TransactionResponse> {
        val balance = accountService.getBalance(userId, accountId)

        return ApiResponse.success(TransactionResponse.of(accountId, balance))
    }
}