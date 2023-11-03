package pizza.xyz.befake.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import pizza.xyz.befake.Utils
import pizza.xyz.befake.model.dtos.login.LoginRequestDTO
import pizza.xyz.befake.model.dtos.login.LoginResultDTO
import pizza.xyz.befake.model.dtos.refresh.RefreshTokenRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface LoginService {

    suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO>

    suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO>

    suspend fun refreshToken(): Result<VerifyOTPResponseDTO>
}

@Singleton
class LoginServiceImpl @Inject constructor(
    private val loginService: LoginAPI,
    private val dataStore: DataStore<Preferences>
): LoginService {

    override suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO> = runCatching {
        return@runCatching loginService.sendCode(body)
    }

    override suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO> = runCatching {
        val res = loginService.verifyCode(verifyOTPRequestDTO)
        dataStore.edit { pref ->
            res.data?.let {
                pref[Utils.TOKEN] = res.data.token
            }
        }
        return@runCatching res
    }

    override suspend fun refreshToken(): Result<VerifyOTPResponseDTO> = runCatching {
        val refreshTokenRequestDTO = dataStore.data.first()[Utils.TOKEN]?.let {
            RefreshTokenRequestDTO(
                token = it
            )
        } ?: RefreshTokenRequestDTO("")
        val res = loginService.refreshToken(refreshTokenRequestDTO)
        dataStore.edit { pref ->
            res.data?.let {
                pref[Utils.TOKEN] = res.data.token
            }
        }
        return@runCatching res
    }

    interface LoginAPI {

        @POST("/login/send-code")
        suspend fun sendCode(
            @Body body: LoginRequestDTO
        ): LoginResultDTO

        @POST("/login/verify")
        suspend fun verifyCode(
            @Body verifyOTPRequestDTO: VerifyOTPRequestDTO
        ): VerifyOTPResponseDTO

        @POST("/login/refresh")
        suspend fun refreshToken(
            @Body refreshTokenRequestDTO: RefreshTokenRequestDTO
        ): VerifyOTPResponseDTO
    }
}
