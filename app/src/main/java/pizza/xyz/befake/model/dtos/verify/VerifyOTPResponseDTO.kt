package pizza.xyz.befake.model.dtos.verify

import com.google.gson.annotations.SerializedName

data class VerifyOTPResponseDTO(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data?,
) {
    data class Data(
        @SerializedName("token") val token: String,
    )
}