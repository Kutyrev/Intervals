package com.github.kutyrev.intervals.repository.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.kutyrev.intervals.datasource.database.IntervalsDao
import com.github.kutyrev.intervals.datasource.database.IntervalsDatabase
import com.github.kutyrev.intervals.repository.database.DatabaseRepository
import com.github.kutyrev.intervals.repository.database.DefaultDatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

private const val DB_NAME = "intervals_db"

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideIntervalsDao(intervalsDatabase: IntervalsDatabase): IntervalsDao {
        return intervalsDatabase.intervalsDao()
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): IntervalsDatabase {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE ListEntity "
                            + " ADD COLUMN withoutSeconds INTEGER DEFAULT 0 NOT NULL"
                )
            }
        }

        return Room.databaseBuilder(
            appContext,
            IntervalsDatabase::class.java,
            DB_NAME
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    fun provideDatabaseRepository(intervalsDao: IntervalsDao): DatabaseRepository {
        return DefaultDatabaseRepository(intervalsDao)
    }
}