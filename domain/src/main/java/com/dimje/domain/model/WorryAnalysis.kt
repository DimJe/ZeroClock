package com.dimje.domain.model

data class WorryAnalysis(
    val entryCount: Int,
    val mainConcern: String,
    val keywords: List<String>,
    val suggestion: String,
)
