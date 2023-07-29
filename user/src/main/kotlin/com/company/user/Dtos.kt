package com.company.user

data class BaseMessage(val code: Int, val message: String?)

data class UserDto(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
    val email: String
)
{
    companion object {
        fun toDto(user: User): UserDto {
            return user.run {
                UserDto(firstName, lastName, username, password, email)
            }
        }
    }
}

data class SubscriptionDto(
    val userId:Long
)

data class ApiResponse(
    val message: String,
    val success:Boolean
)

