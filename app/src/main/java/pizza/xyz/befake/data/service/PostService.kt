package pizza.xyz.befake.data.service

import pizza.xyz.befake.model.dtos.comment.CommentRequestDTO
import pizza.xyz.befake.model.dtos.comment.CommentResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface PostService {

    suspend fun comment(
        userId: String,
        postId: String,
        comment: String
    ): Result<CommentResponseDTO>
}

@Singleton
class PostServiceImpl @Inject constructor(
    private val postAPI: PostAPI
): PostService {

    override suspend fun comment(
        userId: String,
        postId: String,
        comment: String
    ) = runCatching {
        val commentRequestBody = CommentRequestDTO(
            userId = userId,
            postId = postId,
            comment = comment
        )
        val res = postAPI.comment(commentRequestBody)
        return@runCatching res
    }

    interface PostAPI {

        @POST("/post/comment")
        suspend fun comment(
            @Body commentRequestBody: CommentRequestDTO
        ): CommentResponseDTO
    }
}