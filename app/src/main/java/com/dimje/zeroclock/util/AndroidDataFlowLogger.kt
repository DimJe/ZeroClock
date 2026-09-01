package com.dimje.zeroclock.util

import android.util.Log
import com.dimje.domain.logging.DataFlowLogger
import javax.inject.Inject

class AndroidDataFlowLogger @Inject constructor() : DataFlowLogger {
    override fun log(module: String, event: String, details: String) {
        Log.d(LOG_TAG, "[$module][$event] $details")
    }

    private companion object {
        const val LOG_TAG = "ZeroClockFlow"
    }
}
