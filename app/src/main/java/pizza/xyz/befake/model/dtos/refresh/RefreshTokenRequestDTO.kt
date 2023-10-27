package pizza.xyz.befake.model.dtos.refresh

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequestDTO(
    @SerializedName("token") val token: String,
)