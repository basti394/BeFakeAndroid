package pizza.xyz.befake.model

import com.google.gson.annotations.SerializedName

data class LoginResultDTO(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data?,
)

data class Data(
    @SerializedName("otpSession") val otpSession: OTPSession,
)

data class OTPSession(
    @SerializedName("otpSession") val otpSession: String,
)