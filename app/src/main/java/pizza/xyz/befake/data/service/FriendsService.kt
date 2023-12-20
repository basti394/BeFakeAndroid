package pizza.xyz.befake.data.service

import pizza.xyz.befake.model.dtos.feed.FeedResponseDTO
import pizza.xyz.befake.model.dtos.friendsOfFriends.FriendsOfFriendsResponseDTO
import pizza.xyz.befake.model.dtos.me.MeResponseDTO
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

interface FriendsService {

    suspend fun feed(): Result<FeedResponseDTO>

    suspend fun friendsOfFriends(): Result<FriendsOfFriendsResponseDTO>

    suspend fun me(): Result<MeResponseDTO>
}

@Singleton
class FriendsServiceImpl @Inject constructor(
    private val friendsAPI: FriendsAPI,
): FriendsService {

    override suspend fun feed(): Result<FeedResponseDTO> = runCatching {
        return@runCatching friendsAPI.feed()
    }

    override suspend fun friendsOfFriends(): Result<FriendsOfFriendsResponseDTO> = runCatching {
        return@runCatching friendsAPI.friendsOfFriends()
    }

    override suspend fun me(): Result<MeResponseDTO> = runCatching {
        val res = friendsAPI.me()
        println(res.message)
        return@runCatching res
    }

    interface FriendsAPI {

        @GET("/friends/feed")
        suspend fun feed(): FeedResponseDTO

        @GET("/friends/friends-of-friends")
        suspend fun friendsOfFriends(): FriendsOfFriendsResponseDTO

        @GET("/friends/me")
        suspend fun me(): MeResponseDTO
    }
}
