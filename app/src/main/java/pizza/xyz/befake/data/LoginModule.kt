package pizza.xyz.befake.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {

    @Binds
    abstract fun bindLoginService(
        loginServiceImpl: LoginServiceImpl
    ): LoginService

    @Module
    @InstallIn(SingletonComponent::class)
    class NonBindedModule {
        @Provides
        @Singleton
        fun provideLoginAPI(): LoginServiceImpl.LoginAPI {
            val gson: Gson = GsonBuilder().setLenient().create()

            val retrofit: Retrofit =
                Retrofit.Builder()
                    .baseUrl(LoginServiceImpl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(LoginServiceImpl.LoginAPI::class.java)
        }
    }
}