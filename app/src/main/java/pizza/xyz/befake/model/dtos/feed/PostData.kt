package pizza.xyz.befake.model.dtos.feed

import com.google.gson.annotations.SerializedName

data class PostData(
    @SerializedName("userPosts") val userPosts: UserPosts,
    @SerializedName("friendsPosts") val friendsPosts: List<FriendsPosts>,
    @SerializedName("remainingPosts") val remainingPosts: Int,
    @SerializedName("maxPostsPerMoment") val maxPostsPerMoment: Int,
)
