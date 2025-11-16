package dev.kyukyubank.banking.core.api

import dev.kyukyubank.banking.core.api.request.CreateUserRequest
import dev.kyukyubank.banking.core.api.response.UserResponse
import dev.kyukyubank.banking.core.domain.UserService
import dev.kyukyubank.banking.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {

    @PostMapping("/api/users")
    fun createUser(
        @RequestBody request: CreateUserRequest
    ) : ApiResponse<Any> {
        userService.createUser(request.toUser())

        return ApiResponse.success()
    }

    @DeleteMapping("/api/users")
    fun deleteUser(userId: Long) : ApiResponse<Any> {
        userService.deleteUser(userId)

        return ApiResponse.success()
    }

    @GetMapping("/api/users/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ) : ApiResponse<UserResponse> {
        val user = userService.getUser(userId)

        return ApiResponse.success(UserResponse.of(user))
    }
}