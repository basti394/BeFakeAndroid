package pizza.xyz.befake.data.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.login.LoginRequestDTO
import pizza.xyz.befake.model.dtos.login.LoginResultDTO
import pizza.xyz.befake.model.dtos.refresh.RefreshTokenRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPResponseDTO
import pizza.xyz.befake.ui.viewmodel.LoginState
import pizza.xyz.befake.utils.Utils
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface LoginService {

    val loginState: StateFlow<LoginState>

    suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO>

    suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO>

    suspend fun refreshToken(): Result<VerifyOTPResponseDTO>

    suspend fun logOut(): Result<Boolean>

    fun setLoginState(loginState: LoginState)

    suspend fun checkIfLoggedIn()
}

@Singleton
class LoginServiceImpl @Inject constructor(
    private val loginService: LoginAPI,
    private val dataStore: DataStore<Preferences>,
): LoginService {

    private var _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.PhoneNumber)
    override val loginState = _loginState.asStateFlow()

    override suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO> = runCatching {
        if (
            !(_loginState.value is LoginState.PhoneNumber
            || (_loginState.value is LoginState.Error && (_loginState.value as LoginState.Error).previousState == LoginState.PhoneNumber))
        ) throw Exception("Invalid state")

        _loginState.value = LoginState.Loading(LoginState.PhoneNumber)
        if (!body.phone.startsWith("+")) {
            _loginState.value = LoginState.Error(LoginState.PhoneNumber, R.string.phone_numer_start_with_plus)
            throw Exception("Invalid phone number")
        }
        return@runCatching loginService.sendCode(body).also { _loginState.value = LoginState.OTPCode }
    }

    override suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO> = runCatching {
        if (
            !(_loginState.value is LoginState.OTPCode
            || (_loginState.value is LoginState.Error && (_loginState.value as LoginState.Error).previousState == LoginState.OTPCode))
        ) throw Exception("Invalid state")

        _loginState.value = LoginState.Loading(LoginState.OTPCode)
        val res = loginService.verifyCode(verifyOTPRequestDTO)
        dataStore.edit { pref ->
            res.data?.let {
                pref[Utils.TOKEN] = res.data.token
            }
        }
        return@runCatching res.also { _loginState.value = LoginState.LoggedIn }
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

    override suspend fun logOut(): Result<Boolean> {
        dataStore.edit { pref ->
            pref[Utils.TOKEN] = ""
        }
        _loginState.value = LoginState.Error(LoginState.PhoneNumber, R.string.log_out_text)
        return Result.success(true)
    }

    override fun setLoginState(loginState: LoginState) {
        _loginState.value = loginState
    }

    override suspend fun checkIfLoggedIn() {
        val token = dataStore.data.first()[Utils.TOKEN]
        if (token?.isNotEmpty() == true) {
            _loginState.value = LoginState.LoggedIn
        }
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
