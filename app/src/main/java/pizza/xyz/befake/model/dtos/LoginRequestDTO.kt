package pizza.xyz.befake.model.dtos

import com.google.gson.annotations.SerializedName

data class LoginRequestDTO(
    @SerializedName("phone") val phone: String
)