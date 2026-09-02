package com.dimje.data.local.datasource

import com.dimje.data.local.WorryDao
import com.dimje.data.local.WorryEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RoomWorryLocalDataSource @Inject constructor(
    private val dao: WorryDao,
) : WorryLocalDataSource {
    override fun observeAll(): Flow<List<WorryEntity>> = dao.observeAll()

    override suspend fun getByDate(localDate: String): WorryEntity? = dao.getByDate(localDate)

    override suspend fun save(entity: WorryEntity): WorryEntity = entity.copy(id = dao.insert(entity))
}
