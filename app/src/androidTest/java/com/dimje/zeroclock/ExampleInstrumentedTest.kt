package com.dimje.zeroclock

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/** 실제 Android 기기에서 애플리케이션 패키지 구성을 검증합니다. */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun 앱_컨텍스트의_패키지명이_설정과_일치한다() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dimje.zeroclock", appContext.packageName)
    }
}
