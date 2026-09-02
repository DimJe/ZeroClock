package com.dimje.data.repository

import com.dimje.data.remote.datasource.ComfortResponseRemoteDataSource
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.repository.ComfortResponseRepository
import javax.inject.Inject

class ComfortResponseRepositoryImpl @Inject constructor(
    private val remoteDataSource: ComfortResponseRemoteDataSource,
) : ComfortResponseRepository {
    override suspend fun generate(worry: String): ComfortResponseResult = remoteDataSource.generate(worry)
}
