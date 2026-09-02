package com.dimje.data.di

import android.content.Context
import androidx.room.Room
import com.dimje.data.BuildConfig
import com.dimje.data.local.WorryDao
import com.dimje.data.local.ZeroClockDatabase
import com.dimje.data.remote.SupabaseWorryResponseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataProviderModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SUPABASE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideWorryResponseService(retrofit: Retrofit): SupabaseWorryResponseService =
        retrofit.create(SupabaseWorryResponseService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZeroClockDatabase =
        Room.databaseBuilder(
            context,
            ZeroClockDatabase::class.java,
            "zero-clock.db",
        )
            .addMigrations(ZeroClockDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideWorryDao(database: ZeroClockDatabase): WorryDao = database.worryDao()
}
