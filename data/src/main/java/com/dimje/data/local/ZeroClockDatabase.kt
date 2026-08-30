package com.dimje.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WorryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ZeroClockDatabase : RoomDatabase() {
    abstract fun worryDao(): WorryDao
}
