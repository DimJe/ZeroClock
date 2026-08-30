package com.dimje.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorryDao {
    @Query("SELECT * FROM worries ORDER BY localDate DESC")
    fun observeAll(): Flow<List<WorryEntity>>

    @Query("SELECT * FROM worries WHERE localDate = :localDate LIMIT 1")
    suspend fun getByDate(localDate: String): WorryEntity?

    @Insert
    suspend fun insert(entity: WorryEntity): Long
}
