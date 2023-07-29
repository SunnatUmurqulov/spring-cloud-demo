package com.company.user

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    USER_NAME_EXISTS(101),
    USER_EMAIL_EXISTS(102),
    SUBSCRIPTION_NOT_CREATED(103),
    USER_EMAIL_NOT_FOUND(104),
    EMAIL_IS_INCORRECT(105)
}