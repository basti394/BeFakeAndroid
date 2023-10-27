package pizza.xyz.befake.data

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import pizza.xyz.befake.Utils.TOKEN
import pizza.xyz.befake.Utils.dataStore
import pizza.xyz.befake.ui.viewmodel.LoginScreenViewModel
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val context: Context,
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
            chain.proceed(
                request.newBuilder()
                    .addHeader("token", token)
                    .build()
            )
        } else {
            //loginScreenViewModel.onBackToPhoneNumberClicked()
            chain.proceed(request)
        }
    }

}