package pizza.xyz.befake.model.dtos.me

import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin pizza.xyz.befake.model.dtos.me.Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Data (

	@SerializedName("id") val id : String,
	@SerializedName("username") val username : String,
	@SerializedName("birthdate") val birthdate : String,
	@SerializedName("fullname") val fullname : String,
	@SerializedName("profilePicture") val profilePicture : ProfilePicture,
	@SerializedName("realmojis") val realmojis : List<Realmojis>,
	@SerializedName("devices") val devices : List<Devices>,
	@SerializedName("canDeletePost") val canDeletePost : Boolean,
	@SerializedName("canPost") val canPost : Boolean,
	@SerializedName("canUpdateRegion") val canUpdateRegion : Boolean,
	@SerializedName("phoneNumber") val phoneNumber : Int,
	@SerializedName("biography") val biography : String,
	@SerializedName("location") val location : String,
	@SerializedName("countryCode") val countryCode : String,
	@SerializedName("region") val region : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("isRealPeople") val isRealPeople : Boolean,
	@SerializedName("userFreshness") val userFreshness : String,
	@SerializedName("streakLength") val streakLength : Int
)