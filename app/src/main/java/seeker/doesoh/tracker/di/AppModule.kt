package seeker.doesoh.tracker.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import seeker.doesoh.tracker.notification.GpsEventNotification
import seeker.doesoh.tracker.util.Constant
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideClient(): OkHttpClient {
        val cookiesManager = CookieManager()
        cookiesManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        return OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .cookieJar(JavaNetCookieJar(cookiesManager))
            .build()
    }

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("doesoh_preferences", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGpsEventNotification(@ApplicationContext context: Context,sharedPreferences: SharedPreferences): GpsEventNotification {
        return GpsEventNotification(sharedPreferences,context)
    }


}