package uz.demo.subscription

data class BaseMessage(val code: Int, val message: String?)
data class SubscriptionDto(
    val userId:Long
)

data class FollowDto(
    val userId:Long,
    val targetUserId:Long
)

data class FollowersDto(
    val userId: Long,
    val subscription :Subscription
){
    companion object{
        fun toDto(userFollowers: UserFollowers):FollowersDto{
            userFollowers.run {
                return FollowersDto(userId, subscription )
            }
        }
    }
}


data class FollowingDto(
    val userId: Long,
    val subscription :Subscription
){
    companion object{
        fun toDto(userFollowers: UserFollowing):FollowersDto{
            userFollowers.run {
                return FollowersDto(userId, subscription )
            }
        }
    }
}