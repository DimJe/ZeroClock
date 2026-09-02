package com.dimje.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WorryEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class ZeroClockDatabase : RoomDatabase() {
    abstract fun worryDao(): WorryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE worries ADD COLUMN riskLevel TEXT")
            }
        }
    }
}
