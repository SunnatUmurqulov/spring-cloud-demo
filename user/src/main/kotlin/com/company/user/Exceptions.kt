package com.company.user

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

sealed class UserServiceException(message: String? = null) : RuntimeException(message) {
    abstract fun errorType(): ErrorCode

    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource, vararg array: Any?): BaseMessage {
        return BaseMessage(
            errorType().code,
            errorMessageSource.getMessage(
                errorType().toString(),
                array,
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}

class UserNotFoundException(val userId: Long) : UserServiceException() {

    override fun errorType() = ErrorCode.USER_NOT_FOUND
}
class UsernameExistsException(val username: String) : UserServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.USER_NAME_EXISTS
}

class UserEmailExistsException(val email: String) : UserServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.USER_EMAIL_EXISTS
}

class SubscriptionNotCreated(val userId: Long) : UserServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.SUBSCRIPTION_NOT_CREATED
}

class UserEmailNotFoundException(val emailCode:String,val email: String) : UserServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.USER_EMAIL_NOT_FOUND
}

class EmailValidException(val email: String) : UserServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.EMAIL_IS_INCORRECT
}

