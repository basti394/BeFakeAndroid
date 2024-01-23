package pizza.xyz.befake.data.service

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import pizza.xyz.befake.utils.Utils.TOKEN
import pizza.xyz.befake.utils.Utils.dataStore
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val context: Context,
    private val loginService: LoginService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (request.url.pathSegments.contains("login")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            context.dataStore.data.first()[TOKEN]
        }

        return if (token != null) {
            val response = chain.proceed(
                request.newBuilder()
                    .addHeader("token", token)
                    .build()
            )
            if (response.code == 401) {
                runBlocking {
                    val tokenResponse = loginService.refreshToken()
                    if (tokenResponse.isFailure) {
                        loginService.logOut()
                        return@runBlocking
                    }
                    chain.proceed(
                        request.newBuilder()
                            .addHeader("token", token)
                            .build()
                    )
                }
            }
            response
        } else {
            runBlocking {
                loginService.logOut()
            }
            chain.proceed(request)
        }
    }

}