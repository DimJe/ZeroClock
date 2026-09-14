package com.dimje.zeroclock

import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseInitializationTest {
    @Test
    fun Firebase가_초기화되고_디버그에서는_오류를_수집하지_않는다() {
        assertTrue(FirebaseApp.getInstance().options.applicationId.isNotBlank())
        assertFalse(FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled)
    }
}
