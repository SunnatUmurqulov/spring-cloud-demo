package com.example.userpost

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

sealed class PostServiceException(message: String? = null) : RuntimeException(message) {
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

class UserNotFoundException(val userId: Long) : PostServiceException() {
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class PostNotFoundException(val id: Long):PostServiceException(){
    override fun errorType() = ErrorCode.POST_NOT_FOUND
}

class CommentNotFoundException(val id: Long):PostServiceException(){
    override fun errorType() = ErrorCode.POST_NOT_FOUND
}

class LikedExistsUserIdException(val userId: Long):PostServiceException(){
    override fun errorType() = ErrorCode.EXISTS_POST_LIKED_USER
}