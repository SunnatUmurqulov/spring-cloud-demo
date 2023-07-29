package com.example.userpost

data class BaseMessage(val code: Int, val message: String?)
data class PostDto(
    val text: String,
    val userId: Long,
)

data class GetPost(
    val text: String?,
    val userId: Long,
) {
    companion object {
        fun toDto(post: Post): GetPost {
            return post.run {
                GetPost(text, userId)
            }
        }
    }
}

data class CommentDto(
    val text: String,
    val userId: Long,
    val postId: Long,
) {
    fun toEntity(post: Post) = Comments(post, userId, text)
}


data class GetOneComment(
    val text: String,
    val userId: Long,
    val postId: Post
) {

    companion object {
        fun toDto(comments: Comments): GetOneComment {
            return comments.run {
                GetOneComment(text, userId, post)
            }
        }
    }

}

data class LikedDto(
    val likedUserId:Long
)

data class ViewPostDto(
    val viewPostUserId:Long
)



