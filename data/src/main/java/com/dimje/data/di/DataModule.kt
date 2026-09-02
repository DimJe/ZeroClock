package com.dimje.data.di

import android.content.Context
import androidx.room.Room
import com.dimje.data.BuildConfig
import com.dimje.data.ai.SupabaseComfortResponseGenerator
import com.dimje.data.local.WorryDao
import com.dimje.data.local.ZeroClockDatabase
import com.dimje.data.remote.SupabaseWorryResponseApi
import com.dimje.data.remote.SupabaseWorryResponseService
import com.dimje.data.repository.RoomWorryRepository
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import dagger.Binds
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
abstract class DataBindingModule {
    @Binds
    @Singleton
    abstract fun bindWorryRepository(repository: RoomWorryRepository): WorryRepository

    @Binds
    @Singleton
    abstract fun bindComfortResponseGenerator(
        generator: SupabaseComfortResponseGenerator,
    ): ComfortResponseGenerator
}

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
    fun provideWorryResponseApi(
        service: SupabaseWorryResponseService,
        flowLogger: DataFlowLogger,
    ): SupabaseWorryResponseApi = SupabaseWorryResponseApi(service, flowLogger)

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
