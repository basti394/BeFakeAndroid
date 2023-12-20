package pizza.xyz.befake

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pizza.xyz.befake.data.daos.PostDAO
import pizza.xyz.befake.model.entities.Post
import pizza.xyz.befake.model.entities.PostConverter

@Database(entities = [Post::class], version = 1)
@TypeConverters(PostConverter::class)
abstract class BeFakeDatabase: RoomDatabase() {
    abstract fun postDao(): PostDAO
}