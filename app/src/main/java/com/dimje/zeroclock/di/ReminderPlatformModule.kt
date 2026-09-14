package com.dimje.zeroclock.di

import android.content.ComponentName
import android.content.Context
import com.dimje.zeroclock.reminder.ReminderReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ReminderPlatformModule {
    @Provides
    @Named("reminderReceiver")
    fun provideReminderReceiverComponent(@ApplicationContext context: Context): ComponentName =
        ComponentName(context, ReminderReceiver::class.java)
}
