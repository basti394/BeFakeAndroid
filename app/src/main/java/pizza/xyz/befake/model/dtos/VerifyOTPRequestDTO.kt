package pizza.xyz.befake.model.dtos

import com.google.gson.annotations.SerializedName

data class VerifyOTPRequestDTO(
    @SerializedName("otpSesion") val otpSession: String,
    @SerializedName("code") val code: String
)