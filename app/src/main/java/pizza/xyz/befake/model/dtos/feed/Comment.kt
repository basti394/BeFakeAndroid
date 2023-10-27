package pizza.xyz.befake.model.dtos.feed

import com.google.gson.annotations.SerializedName

data class Comment (

    @SerializedName("id") val id : String,
    @SerializedName("user") val user : User,
    @SerializedName("content") val content : String,
    @SerializedName("postedAt") val postedAt : String
)