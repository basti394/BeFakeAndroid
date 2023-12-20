package pizza.xyz.befake.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.PostData
import pizza.xyz.befake.model.dtos.feed.UserPosts
import java.util.UUID

@Entity
data class Post(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @SerializedName("data") val data: PostData
)

class PostConverter {

    @TypeConverter
    fun toPost(post: String): PostData {
        val type = object : TypeToken<PostData>() { }.type
        return Gson().fromJson(post, type)
    }

    @TypeConverter
    fun fromPost(post: PostData): String {
        return Gson().toJson(post)
    }

    @TypeConverter
    fun toUserPost(value: String?): UserPosts? {
        val listType = object : TypeToken<UserPosts?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromUserPosts(userPosts: UserPosts?): String? {
        val gson = Gson()
        return gson.toJson(userPosts)
    }

    @TypeConverter
    fun toFriendsPost(value: String?): List<FriendsPosts?>? {
        val listType = object : TypeToken<List<FriendsPosts?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromFriendsPostsList(friendsPosts: List<FriendsPosts?>?): String? {
        val gson = Gson()
        return gson.toJson(friendsPosts)
    }
}