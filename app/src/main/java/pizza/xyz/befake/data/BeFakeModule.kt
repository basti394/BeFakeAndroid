package pizza.xyz.befake.data

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import pizza.xyz.befake.Utils.BASE_URL
import pizza.xyz.befake.Utils.dataStore
import retrofit2.HttpException
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

    @Binds
    abstract fun bindPostService(
        postServiceImpl: PostServiceImpl
    ): PostService

    @Binds
    abstract fun bindFriendsService(
        friendsServiceImpl: FriendsServiceImpl
    ): FriendsService



    @Module
    @InstallIn(SingletonComponent::class)
    class NonBindedModule {

        @Provides
        fun provideContext(application: Application): Context = application.applicationContext

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context) = context.dataStore

        @Provides
        @Singleton
        fun provideLoginAPI(okHttpClient: OkHttpClient): LoginServiceImpl.LoginAPI {
            val gson: Gson = GsonBuilder().setLenient().create()

            val retrofit: Retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(LoginServiceImpl.LoginAPI::class.java)
        }

        @Provides
        @Singleton
        fun provideFriendsAPI(okHttpClient: OkHttpClient): FriendsServiceImpl.FriendsAPI {
            val gson: Gson = GsonBuilder().setLenient().create()

            val retrofit: Retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(FriendsServiceImpl.FriendsAPI::class.java)
        }

        @Provides
        @Singleton
        fun getOkHttp(tokenInterceptor: TokenInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()
        }
    }
}