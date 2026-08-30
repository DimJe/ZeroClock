package com.dimje.zeroclock.util

import java.time.LocalDate
import java.time.ZoneId

object KoreaDate {
    private val zoneId = ZoneId.of("Asia/Seoul")

    fun today(): LocalDate = LocalDate.now(zoneId)
}
