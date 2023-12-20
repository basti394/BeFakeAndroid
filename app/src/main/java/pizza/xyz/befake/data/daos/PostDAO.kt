package pizza.xyz.befake.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pizza.xyz.befake.model.entities.Post

@Dao
interface PostDAO {

    @Query("SELECT * FROM post")
    fun getPostData(): Flow<Post>


    @Insert
    fun insertPostData(post: Post)

    @Update
    fun updatePostData(post: Post)
}