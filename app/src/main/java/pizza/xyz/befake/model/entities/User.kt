package pizza.xyz.befake.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "token") val token: String,
)