package pizza.xyz.befake.model.dtos.login

import com.google.gson.annotations.SerializedName

data class LoginRequestDTO(
    @SerializedName("phone") val phone: String
)