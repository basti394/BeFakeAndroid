package pizza.xyz.befake

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import pizza.xyz.befake.data.daos.PostDAO
import pizza.xyz.befake.data.repository.FeedRepository
import pizza.xyz.befake.data.repository.FeedRepositoryImpl
import pizza.xyz.befake.data.service.FriendsService
import pizza.xyz.befake.data.service.FriendsServiceImpl
import pizza.xyz.befake.data.service.LoginService
import pizza.xyz.befake.data.service.LoginServiceImpl
import pizza.xyz.befake.data.service.PostService
import pizza.xyz.befake.data.service.PostServiceImpl
import pizza.xyz.befake.data.service.TokenInterceptor
import pizza.xyz.befake.utils.Utils.BASE_URL
import pizza.xyz.befake.utils.Utils.dataStore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BeFakeModule {

    @Singleton
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

    @Binds
    abstract fun bindFeedRepository(
        feedRepositoryImpl: FeedRepositoryImpl
    ): FeedRepository


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
        fun providePostAPI(okHttpClient: OkHttpClient): PostServiceImpl.PostAPI {
            val gson: Gson = GsonBuilder().setLenient().create()

            val retrofit: Retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(PostServiceImpl.PostAPI::class.java)
        }

        @Provides
        @Singleton
        fun provideBeFakeDatabase(@ApplicationContext context: Context): BeFakeDatabase {
            return Room.databaseBuilder(
                context,
                BeFakeDatabase::class.java,
                "befake_database"
            ).fallbackToDestructiveMigration() .build()
        }

        @Provides
        @Singleton
        fun providePostDAO(beFakeDatabase: BeFakeDatabase): PostDAO = beFakeDatabase.postDao()

        @Provides
        @Singleton
        fun getOkHttp(tokenInterceptor: TokenInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()
        }
    }
}