package com.dimje.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worries",
    indices = [Index(value = ["localDate"], unique = true)],
)
data class WorryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val worry: String,
    val response: String,
    val localDate: String,
    val createdAt: Long,
)
