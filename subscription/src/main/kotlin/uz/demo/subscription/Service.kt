package uz.demo.subscription

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

interface SubscriptionService {
    fun followToUser(userId:Long, dto: FollowDto)
    fun createSubscription(subscriptionDto: SubscriptionDto): Boolean
    fun myFollowers(id: Long,pageable: Pageable):Page<FollowersDto>
    fun myFollowing(id: Long, pageable: Pageable): Page<FollowersDto>
}

@Service
class SubscriptionServiceImpl(
    private val subscriptionRepository: SubscriptionRepository,
    private val userFollowersRepository: UserFollowersRepository,
    private val userFollowingRepository: UserFollowingRepository,
) : SubscriptionService {
    @Transactional
    override fun followToUser(userId: Long,dto: FollowDto) {

        val subscriptionFollowing = subscriptionRepository.findByUserId(userId)
            ?: throw SubscriptionNotFoundException(userId)

        val subscriptionFollowers = subscriptionRepository.findByUserId(dto.targetUserId)
            ?: throw SubscriptionNotFoundException(dto.targetUserId)

        val following = userFollowingRepository.findBySubscriptionId(subscriptionFollowing.id)
        following.userId = dto.targetUserId
        userFollowingRepository.save(following)

        val follower = userFollowersRepository.findBySubscriptionId(subscriptionFollowers.id)
        follower.userId = userId
        userFollowersRepository.save(follower)
    }

    override fun createSubscription(subscriptionDto: SubscriptionDto): Boolean {
       subscriptionDto.run {
           val subscription = Subscription(userId)
           subscriptionRepository.save(subscription)
           return true
       }
    }

    override fun myFollowers(id: Long, pageable: Pageable): Page<FollowersDto> {
        val subscription = subscriptionRepository.findByUserId(id)?:throw SubscriptionNotFoundException(id)
        return userFollowersRepository.findAllBySubscriptionId(subscription.id,pageable).map { FollowersDto.toDto(it) }
    }

    override fun myFollowing(id: Long, pageable: Pageable): Page<FollowersDto> {
        val subscription = subscriptionRepository.findByUserId(id)?:throw SubscriptionNotFoundException(id)
        return userFollowingRepository.findAllBySubscriptionId(subscription.id,pageable).map { FollowingDto.toDto(it) }
    }


}