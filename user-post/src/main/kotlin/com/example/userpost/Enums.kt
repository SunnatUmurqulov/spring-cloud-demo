package com.example.userpost

enum class ErrorCode(val code: Int){
    USER_NOT_FOUND(300),
    POST_NOT_FOUND(301),
    COMMENT_NOT_FOUND(302),
    EXISTS_POST_LIKED_USER(303)
}