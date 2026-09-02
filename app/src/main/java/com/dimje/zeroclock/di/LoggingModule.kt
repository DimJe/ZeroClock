package com.dimje.zeroclock.di

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.time.DateProvider
import com.dimje.zeroclock.util.AndroidDataFlowLogger
import com.dimje.zeroclock.util.KoreaDateProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {
    @Binds
    @Singleton
    abstract fun bindDataFlowLogger(logger: AndroidDataFlowLogger): DataFlowLogger

    @Binds
    @Singleton
    abstract fun bindDateProvider(provider: KoreaDateProvider): DateProvider
}
