package com.dimje.data.remote.datasource

class WorryResponseApiException(
    message: String,
    cause: Throwable? = null,
) : IllegalStateException(message, cause)
