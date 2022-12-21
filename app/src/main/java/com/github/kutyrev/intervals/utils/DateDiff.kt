package com.github.kutyrev.intervals.utils

import android.content.Context
import com.github.kutyrev.intervals.R
import java.util.*
import java.util.concurrent.TimeUnit

const val DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"

class DateDiff (diffInMillis : Long) {

    private val diffInUnits: MutableMap<TimeUnit?, Long> = LinkedHashMap<TimeUnit?, Long>()

    init {
        //create the list
        val units: List<TimeUnit?> = ArrayList<TimeUnit?>(EnumSet.allOf(TimeUnit::class.java)).reversed()
        //Collections.reverse(units)

        //create the result map of TimeUnit and difference
        var milliesRest = diffInMillis
        for (unit in units) {

            if (unit == TimeUnit.NANOSECONDS
                    || unit == TimeUnit.MILLISECONDS
                    || unit == TimeUnit.MICROSECONDS) continue

            //calculate difference in millisecond
            val diff: Long = unit?.convert(milliesRest, TimeUnit.MILLISECONDS) ?: 0
            val diffInMilliesForUnit: Long = unit?.toMillis(diff) ?: 0
            milliesRest -= diffInMilliesForUnit

            //put the result in the map
            diffInUnits[unit] = diff

        }
    }

    fun toString(context : Context?): String {

        val intervalStringRepr = StringBuilder()

        for(curUnit in diffInUnits){
            val timeUnitStr: String? = when(curUnit.key){
                TimeUnit.DAYS -> context?.getString(R.string.days)
                TimeUnit.HOURS -> context?.getString(R.string.hours)
                TimeUnit.MINUTES -> context?.getString(R.string.minutes)
                TimeUnit.SECONDS -> context?.getString(R.string.seconds)
                else -> curUnit.key?.name
            }
            intervalStringRepr.append(timeUnitStr).append(": ").append(curUnit.value.toString()).append(" ")
        }
        return intervalStringRepr.toString()
    }
}
