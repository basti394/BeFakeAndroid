package pizza.xyz.befake.data

import pizza.xyz.befake.model.dtos.LoginRequestDTO
import pizza.xyz.befake.model.dtos.LoginResultDTO
import pizza.xyz.befake.model.dtos.VerifyOTPRequestDTO
import pizza.xyz.befake.model.dtos.VerifyOTPResponseDTO
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
}

@Singleton
class LoginServiceImpl @Inject constructor(
    private val loginService: LoginAPI
): LoginService {

    override suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO> = runCatching {
        val test = loginService.sendCode(body)
        println("test: $test")
        return@runCatching test
    }

    override suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO> = runCatching {
        return@runCatching loginService.verifyCode(verifyOTPRequestDTO)
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
    }
}
