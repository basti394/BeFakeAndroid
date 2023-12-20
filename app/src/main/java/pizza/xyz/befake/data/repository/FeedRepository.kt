package pizza.xyz.befake.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pizza.xyz.befake.data.daos.PostDAO
import pizza.xyz.befake.data.service.FriendsService
import pizza.xyz.befake.data.service.LoginService
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.PostData
import pizza.xyz.befake.model.entities.Post
import pizza.xyz.befake.utils.Utils.handle
import java.util.UUID
import javax.inject.Inject

interface FeedRepository {

    val feed: Flow<Post>

    suspend fun updateFeed()

    fun getPostByUsername(username: String): Flow<FriendsPosts?>
}

class FeedRepositoryImpl @Inject constructor(
    private val postDAO: PostDAO,
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : FeedRepository {

    override val feed: Flow<Post>
        get() = postDAO.getPostData().map { if (it != null) formatFeed(it.data, it.id) else it }

    override suspend fun updateFeed() {
        val id = postDAO.getPostData().map { if (it != null) it.id else UUID.randomUUID() }.first()

        suspend { friendsService.feed() }.handle(
            onSuccess = {
                it.data.data?.let { data ->
                    postDAO.insertPostData(Post(id, data))
                }
            },
            loginService = loginService
        )
    }

    override fun getPostByUsername(username: String): Flow<FriendsPosts?> {
        return feed.map { it.data.friendsPosts.find { it.user.username == username } }
    }

    private fun formatFeed(postData: PostData, id: UUID) : Post {
        val pData = postData.copy(
            friendsPosts = postData.friendsPosts.map { it.copy(posts = it.posts.sortedBy { post -> post.creationDate }) }.sortedBy { it.posts.last().creationDate }
        )

        return Post(id, pData)
    }
}