package dev.kyukyubank.banking.core.api

import dev.kyukyubank.banking.core.api.request.LoginRequest
import dev.kyukyubank.banking.core.api.response.UserResponse
import dev.kyukyubank.banking.core.domain.UserService
import dev.kyukyubank.banking.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<Any> {
        userService.login(request.toLogin())

        return ApiResponse.success()
    }
}
