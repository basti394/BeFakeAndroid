package pizza.xyz.befake.model

import com.google.gson.annotations.SerializedName

data class LoginRequestDTO(
    @SerializedName("phone") val phone: String
)