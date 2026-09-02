package com.dimje.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZeroClockDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ZeroClockDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun 버전1에서_2로_이전하면_기존_기록을_보존한다() {
        helper.createDatabase(TEST_DATABASE, 1).apply {
            execSQL(
                """INSERT INTO worries (id, worry, response, localDate, createdAt)
                    VALUES (1, '기존 고민', '기존 답변', '2026-09-01', 1)
                """.trimIndent(),
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DATABASE,
            2,
            true,
            ZeroClockDatabase.MIGRATION_1_2,
        )

        migrated.query("SELECT worry, response, riskLevel FROM worries WHERE id = 1").use { cursor ->
            cursor.moveToFirst()
            assertEquals("기존 고민", cursor.getString(cursor.getColumnIndexOrThrow("worry")))
            assertEquals("기존 답변", cursor.getString(cursor.getColumnIndexOrThrow("response")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("riskLevel")))
        }
        migrated.close()
    }

    private companion object {
        const val TEST_DATABASE = "migration-test"
    }
}
