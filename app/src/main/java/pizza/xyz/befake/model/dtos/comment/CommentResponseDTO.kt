package pizza.xyz.befake.model.dtos.comment

import com.google.gson.annotations.SerializedName

data class CommentResponseDTO(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Any?
)