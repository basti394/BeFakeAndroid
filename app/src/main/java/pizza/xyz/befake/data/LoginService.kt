package pizza.xyz.befake.data

import dagger.Provides
import pizza.xyz.befake.model.LoginRequestDTO
import pizza.xyz.befake.model.LoginResultDTO
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface LoginService {

    suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO>
}

@Singleton
class LoginServiceImpl @Inject constructor(
    private val loginService: LoginAPI
): LoginService {

    companion object {
        const val BASE_URL = "https://berealapi.fly.dev/"
    }

    override suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO> = kotlin.runCatching {
        return@runCatching loginService.sendCode(body)
    }

    interface LoginAPI {

        @POST("/login/send-code")
        suspend fun sendCode(
            @Body body: LoginRequestDTO
        ): LoginResultDTO
    }
}
