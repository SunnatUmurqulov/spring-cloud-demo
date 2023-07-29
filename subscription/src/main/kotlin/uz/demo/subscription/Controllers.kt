package uz.demo.subscription

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(SubscriptionServiceException::class)
    fun handleException(exception: SubscriptionServiceException): ResponseEntity<*> {
        return when (exception) {
            is SubscriptionNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource,exception.userId)
            )
        }
    }
}


@RestController
@RequestMapping("/internal")
class SubscriptionInternalController(private val subscriptionService: SubscriptionService) {
    @PostMapping("/create")
    fun createSubscription(@RequestBody subscriptionDto: SubscriptionDto) = subscriptionService.createSubscription(subscriptionDto)
}


@RestController
class SubscriptionController(private val subscriptionService: SubscriptionService) {
    @PostMapping("/{id}")
    fun followToUser(@PathVariable id: Long,@RequestBody dto: FollowDto) = subscriptionService.followToUser(id,dto)


    @GetMapping("{id}")
    fun myFollowers(@PathVariable id: Long,pageable: Pageable): Page<FollowersDto> = subscriptionService.myFollowers(id,pageable)


    @GetMapping("following/{id}")
    fun myFollowing(@PathVariable id: Long,pageable: Pageable): Page<FollowersDto> = subscriptionService.myFollowing(id,pageable)
}

