package com.example.userpost

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(PostServiceException::class)
    fun handleException(exception: PostServiceException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource,exception.userId)
            )
            is CommentNotFoundException ->  ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource,exception.id))

            is LikedExistsUserIdException ->  ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource,exception.userId))

            is PostNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource,exception.id))
        }
    }
}

@RestController
class PostController(private val postService: PostService) {
    @PostMapping
    fun create(@RequestBody dto: PostDto) = postService.createPost(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: PostDto) = postService.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetPost = postService.getPost(id)

    @GetMapping
    fun getAll(pageable: Pageable): Page<GetPost> = postService.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = postService.delete(id)

    @PostMapping("/like/{id}")
    fun like(@PathVariable id: Long,dto: LikedDto) = postService.like(id,dto)

    @PostMapping("/view/{id}")
    fun viewedPost(@PathVariable id: Long,dto: ViewPostDto) = postService.viewedPost(id,dto)
}

@RestController
@RequestMapping("/comment")
class CommentController(private val commentsService: CommentsService) {
    @PostMapping
    fun create(@RequestBody dto: CommentDto) = commentsService.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: CommentDto) = commentsService.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneComment = commentsService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneComment> = commentsService.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = commentsService.delete(id)
}