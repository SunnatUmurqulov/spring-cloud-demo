package com.example.userpost

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import javax.persistence.EntityManager


@FeignClient(name = "user")
interface UserService {
    @PostMapping("/internal/exists/{id}")
    fun existsById(@PathVariable id: Long): Boolean
}

interface PostService {
    fun createPost(dto: PostDto)
    fun update(id: Long, dto: PostDto)
    fun getPost(id: Long): GetPost
    fun getAll(pageable: Pageable): Page<GetPost>
    fun delete(id: Long)
    fun like(postId: Long,dto: LikedDto)
    fun viewedPost(postId: Long, dto: ViewPostDto)
}

interface CommentsService {
    fun create(dto: CommentDto)
    fun update(commentId: Long, dto: CommentDto)
    fun getOne(id: Long): GetOneComment
    fun getAll(pageable: Pageable): Page<GetOneComment>
    fun delete(id: Long)
}

@Service
class PostServiceImpl(
    private val userService: UserService,
    private val postRepository: PostRepository,
    private val likedUserRepository: LikedUserRepository,
    private val viewPostRepository: ViewPostRepository
) : PostService {
    override fun createPost(dto: PostDto) {
        if (!userService.existsById(dto.userId)) throw UserNotFoundException(dto.userId)
        dto.run {
            val post = Post(text, userId)
            postRepository.save(post)
        }

    }

    override fun update(id: Long, dto: PostDto) {
        val post = postRepository.findPostByIdAndDeletedFalseAndUserId(id, dto.userId)
            ?: throw PostNotFoundException(id)
        post.text = dto.text
        postRepository.save(post)
    }

    override fun getPost(id: Long): GetPost {
        return postRepository.findByIdAndDeletedFalse(id)?.let { GetPost.toDto(it) }
            ?: throw PostNotFoundException(id)
    }

    override fun getAll(pageable: Pageable): Page<GetPost> {
        return postRepository.findAllNotDeleted(pageable).map { GetPost.toDto(it) }
    }

    override fun delete(id: Long) {
        postRepository.trash(id) ?: throw PostNotFoundException(id)
    }

    override fun like(postId: Long,dto: LikedDto) {
        if (!userService.existsById(dto.likedUserId)){
            throw UserNotFoundException(dto.likedUserId)
        }

        val post = postRepository.findByIdAndDeletedFalse(postId)
            ?:throw PostNotFoundException(postId)

        if (likedUserRepository.existsByPostIdAndUserIdAndDeletedFalse(postId,dto.likedUserId))
            throw LikedExistsUserIdException(dto.likedUserId)


        val likes = Likes(post, dto.likedUserId)
        likedUserRepository.save(likes)

    }

    override fun viewedPost(postId: Long, dto: ViewPostDto) {
        if (!userService.existsById(dto.viewPostUserId)){
            throw UserNotFoundException(dto.viewPostUserId)
        }

        val post = postRepository.findByIdAndDeletedFalse(postId)
            ?:throw PostNotFoundException(postId)

        val viewPost = ViewPost(post, dto.viewPostUserId)
        viewPostRepository.save(viewPost)
        TODO("tugallanmagan")
    }

}


@Service
class CommentsServiceImpl(
    private val userService: UserService,
    private val commentsRepository: CommentsRepository,
    private val postRepository: PostRepository,
    private val entityManager: EntityManager,
) : CommentsService {
    override fun create(dto: CommentDto) {
        dto.run {
            val post = postId.let {
                postRepository.existsByIdAndDeletedFalse(it).runIfFalse {
                    throw PostNotFoundException(it)
                }
                entityManager.getReference(Post::class.java, it)
            }
            commentsRepository.save(toEntity(post))
        }
    }

    override fun update(commentId: Long, dto: CommentDto) {
        if (!postRepository.existsByIdAndDeletedFalse(dto.postId)) {
            throw PostNotFoundException(dto.postId)
        }

        if (!userService.existsById(dto.userId)) {
            throw UserNotFoundException(dto.userId)
        }

        val comments =
            commentsRepository.findByIdAndPostIdAndUserIdAndDeletedFalse(commentId, dto.postId, dto.userId)
                ?: throw CommentNotFoundException(commentId)
        comments.text = dto.text
        commentsRepository.save(comments)
    }

    override fun getOne(id: Long):GetOneComment {
        return commentsRepository.findByIdAndDeletedFalse(id).let { GetOneComment.toDto(it!!) }
    }

    override fun getAll(pageable: Pageable): Page<GetOneComment> {
        return commentsRepository.findAllNotDeleted(pageable).map { GetOneComment.toDto(it) }
    }

    override fun delete(id: Long) {
        commentsRepository.trash(id)?:throw CommentNotFoundException(id)
    }

}