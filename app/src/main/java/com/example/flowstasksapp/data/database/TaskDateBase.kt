package com.example.flowstasksapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flowstasksapp.data.database.daos.NotificationDao
import com.example.flowstasksapp.data.database.daos.TaskDao
import com.example.flowstasksapp.data.database.entities.NotificationsEntity
import com.example.flowstasksapp.data.database.entities.TaskEntity

@Database(
    entities = [TaskEntity::class, NotificationsEntity::class],
    version = 2
)
abstract class TaskDateBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Создаем таблицу notifications
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY NOT NULL,
                        taskId INTEGER NOT NULL,
                        hour INTEGER NOT NULL,
                        minutes INTEGER NOT NULL
                    )
                """
                )
            }
        }

        @Volatile
        private var INSTANCE: TaskDateBase? = null

        fun getInstance(context: Context): TaskDateBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDateBase::class.java,
                    "data_base.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance

            }
        }
    }
}