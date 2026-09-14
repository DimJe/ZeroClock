package com.dimje.data.remote.model

import com.google.gson.annotations.SerializedName

data class WorryResponseRequest(
    @field:SerializedName("worry")
    val worry: String,
)
