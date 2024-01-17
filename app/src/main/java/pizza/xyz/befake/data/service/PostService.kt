package pizza.xyz.befake.data.service

import pizza.xyz.befake.model.dtos.comment.CommentRequestDTO
import pizza.xyz.befake.model.dtos.comment.CommentResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface PostService {

    suspend fun comment(
        postId: String,
        comment: String
    ): Result<CommentResponseDTO>
}

@Singleton
class PostServiceImpl @Inject constructor(
    private val postAPI: PostAPI
): PostService {

    override suspend fun comment(
        postId: String,
        comment: String
    ) = runCatching {
        val commentRequestBody = CommentRequestDTO(
            postId = postId,
            comment = comment
        )
        return@runCatching postAPI.comment(commentRequestBody)
    }

    interface PostAPI {

        @POST("/post/comment")
        suspend fun comment(
            @Body commentRequestBody: CommentRequestDTO
        ): CommentResponseDTO
    }
}