package com.company.user

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource,
) {
    @ExceptionHandler(UserServiceException::class)
    fun handleException(exception: UserServiceException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.userId)
            )

            is SubscriptionNotCreated -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.userId)
            )

            is UserEmailExistsException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.email))

            is UsernameExistsException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.username))

            is UserEmailNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.email, exception.emailCode))

            is EmailValidException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.email))
        }
    }
}


@RestController
class UserController(private val service: UserService) {
    @PostMapping
    fun create(@RequestBody dto: UserDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UserDto) = service.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): UserDto = service.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable): Page<UserDto> = service.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("/auth/verifyEmail")
    fun confirmUserAccount(@RequestParam emailCode: String, @RequestParam email: String) =
        service.confirmUserAccount(emailCode, email)

}

@RestController
@RequestMapping("internal")
class UserInternalController(private val service: UserService) {
    @GetMapping("exists/{id}")
    fun existById(@PathVariable id: Long) = service.existById(id)
}





