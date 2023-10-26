package pizza.xyz.befake.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import pizza.xyz.befake.model.entities.User

@Dao
interface UserDAO {

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): User

    fun setPhoneById(id: Int, phone: String)

    fun setTokenById(id: Int, token: String)

    @Delete
    fun delete()
}