package pizza.xyz.befake.model.dtos.comment

import com.google.gson.annotations.SerializedName

data class CommentRequestDTO(
    @SerializedName("userId") val userId: String,
    @SerializedName("postId") val postId: String,
    @SerializedName("comment") val comment: String,
)