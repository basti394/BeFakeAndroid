package pizza.xyz.befake.model.dtos.login

import com.google.gson.annotations.SerializedName

data class LoginResultDTO(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data?,
) {

    data class Data(
        @SerializedName("otpSesion") val otpSession: OtpSession,
    )

    data class OtpSession(
        @SerializedName("otpSesion") val otpSession: String,
    )
}