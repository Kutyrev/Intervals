package com.github.kutyrev.intervals.datasource.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? =
        if (value == null) null else Calendar.getInstance().apply { timeInMillis = value }

    @TypeConverter
    fun dateToTimestamp(date: Calendar?): Long? {
        return date?.time?.time
    }
}