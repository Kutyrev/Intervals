package com.github.kutyrev.intervals.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EventEntity::class, ListEntity::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class IntervalsDatabase : RoomDatabase() {
    abstract fun intervalsDao() : IntervalsDao
}
