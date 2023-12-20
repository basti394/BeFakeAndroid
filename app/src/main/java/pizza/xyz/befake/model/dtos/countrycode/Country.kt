package pizza.xyz.befake.model.dtos.countrycode

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name") val name: String,
    @SerializedName("dial_code") val dialCode: String,
    @SerializedName("code") val code: String
)