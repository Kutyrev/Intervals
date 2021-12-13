package com.github.kutyrev.intervals

import android.content.Context
import java.util.*
import java.util.concurrent.TimeUnit

class DateDiff (diffInMillies : Long) {

    val diffInUnits: MutableMap<TimeUnit?, Long> = LinkedHashMap<TimeUnit?, Long>()


    init {
        //create the list
        val units: List<TimeUnit?> = ArrayList<TimeUnit?>(EnumSet.allOf(TimeUnit::class.java))
        Collections.reverse(units)

        //create the result map of TimeUnit and difference
        var milliesRest = diffInMillies
        for (unit in units) {

            if (unit == TimeUnit.NANOSECONDS
                    || unit == TimeUnit.MILLISECONDS
                    || unit == TimeUnit.MICROSECONDS) continue

            //calculate difference in millisecond
            val diff: Long = unit?.convert(milliesRest, TimeUnit.MILLISECONDS) ?: 0
            val diffInMilliesForUnit: Long = unit?.toMillis(diff) ?: 0
            milliesRest = milliesRest - diffInMilliesForUnit

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