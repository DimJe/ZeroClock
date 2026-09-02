package com.dimje.data.local.datasource

import com.dimje.data.local.WorryEntity
import kotlinx.coroutines.flow.Flow

interface WorryLocalDataSource {
    fun observeAll(): Flow<List<WorryEntity>>

    suspend fun getByDate(localDate: String): WorryEntity?

    suspend fun save(entity: WorryEntity): WorryEntity
}
