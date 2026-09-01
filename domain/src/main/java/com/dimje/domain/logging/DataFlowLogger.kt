package com.dimje.domain.logging

interface DataFlowLogger {
    fun log(module: String, event: String, details: String)

    companion object {
        val NONE: DataFlowLogger = object : DataFlowLogger {
            override fun log(module: String, event: String, details: String) = Unit
        }
    }
}
