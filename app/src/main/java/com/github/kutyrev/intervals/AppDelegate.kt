package com.github.kutyrev.intervals

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.kutyrev.intervals.database.IntervalsDatabase


class AppDelegate: Application() {

    /*Основной конструктор не может содержать в себе исполняемого кода.
    Инициализирующий код может быть помещён в соответствующий блок (initializers blocks), который помечается словом init.
    */

    init {
        appInstance = this
    }

    companion object {
        lateinit var appInstance: AppDelegate
        private val db by lazy {Room.databaseBuilder(
                appInstance.applicationContext,
                IntervalsDatabase::class.java, "intervals_db"
        ).addMigrations(MIGRATION_1_2).build()}
        val repository by lazy {db.intervalsDao()}

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ListEntity "
                        + " ADD COLUMN withoutSeconds INTEGER DEFAULT 0 NOT NULL")
            }
        }

    }

    override fun onCreate() {

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }

        super.onCreate()
    }

}