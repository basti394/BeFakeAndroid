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

    fun getFeed(): Flow<Post>

    suspend fun updateFeed()

    fun getPostByUsername(username: String): Flow<FriendsPosts?>
}

class FeedRepositoryImpl @Inject constructor(
    private val postDAO: PostDAO,
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : FeedRepository {

    override fun getFeed(): Flow<Post> {
        return postDAO.getPostData()
    }

    override suspend fun updateFeed() {
        val existingPost = postDAO.getPostData().first()

        suspend { friendsService.feed() }.handle(
            onSuccess = {
                it.data.data?.let { data ->
                    if (existingPost != null) {
                        postDAO.updatePostData(formatFeed(data, existingPost.id))
                        return@handle
                    } else {
                        val id = UUID.randomUUID()
                        postDAO.insertPostData(Post(id, data))
                        return@handle
                    }
                }
            },
            loginService = loginService
        )
    }

    override fun getPostByUsername(username: String): Flow<FriendsPosts?> {
        return getFeed().map { it.data.friendsPosts.find { it.user.username == username } }
    }

    private fun formatFeed(postData: PostData, id: UUID) : Post {
        val pData = postData.copy(
            friendsPosts = postData.friendsPosts.map { it.copy(posts = it.posts.sortedBy { post -> post.creationDate }) }.sortedBy { it.posts.last().creationDate }.reversed()
        )
        return Post(id, pData)
    }
}