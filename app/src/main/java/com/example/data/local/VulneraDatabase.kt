package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Data Layer - Room Database]
 * Thread-safe RoomDatabase singleton providing persistent offline storage.
 * ============================================================================
 */
@Database(
  entities = [AuditReportEntity::class],
  version = 1,
  exportSchema = false
)
abstract class VulneraDatabase : RoomDatabase() {

  abstract fun auditReportDao(): AuditReportDao

  companion object {
    @Volatile
    private var INSTANCE: VulneraDatabase? = null

    fun getInstance(context: Context): VulneraDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          VulneraDatabase::class.java,
          "vulnera_security_lab.db"
        )
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
