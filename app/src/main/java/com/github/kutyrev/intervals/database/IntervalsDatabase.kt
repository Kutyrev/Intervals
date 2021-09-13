package com.github.kutyrev.intervals.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EventEntity::class, ListEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class IntervalsDatabase : RoomDatabase() {
    abstract fun intervalsDao() : IntervalsDao
}