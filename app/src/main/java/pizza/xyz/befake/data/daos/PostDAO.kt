package pizza.xyz.befake.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pizza.xyz.befake.model.entities.Post

@Dao
interface PostDAO {

    @Query("SELECT * FROM post")
    fun getPostData(): Flow<Post>


    @Update
    fun insertPostData(post: Post)
}