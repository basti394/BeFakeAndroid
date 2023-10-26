package pizza.xyz.befake.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pizza.xyz.befake.Utils.BASE_URL
import pizza.xyz.befake.Utils.dataStore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BeFakeModule {

    @Binds
    abstract fun bindLoginService(
        loginServiceImpl: LoginServiceImpl
    ): LoginService

    @Module
    @InstallIn(SingletonComponent::class)
    class NonBindedModule {

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context) = context.dataStore

        @Provides
        @Singleton
        fun provideLoginAPI(): LoginServiceImpl.LoginAPI {
            val gson: Gson = GsonBuilder().setLenient().create()

            val retrofit: Retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(LoginServiceImpl.LoginAPI::class.java)
        }
    }
}