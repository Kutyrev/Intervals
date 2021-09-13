package com.github.kutyrev.intervals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.intervals.database.EventEntity
import com.github.kutyrev.intervals.database.ListEntity
import kotlinx.coroutines.launch
import java.util.*

class DetailViewModel : ViewModel() {

    val eventsLifeData : LiveData<List<EventEntity>> = AppDelegate.repository.getAllEvents().asLiveData()


    fun insertNewEvent(newEvent: EventEntity){
        viewModelScope.launch { AppDelegate.repository.insertAllEvents(newEvent) }
    }

    fun deleteEvent(event: EventEntity){
        viewModelScope.launch {AppDelegate.repository.deleteEvent(event)}
    }

    fun updateEvent(curEvent: EventEntity){
        viewModelScope.launch { AppDelegate.repository.updateEvent(curEvent)}
    }

    fun updateList(curList: ListEntity){
        viewModelScope.launch { AppDelegate.repository.updateList(curList) }
    }

    fun getAvgEventsDiffByYear(listId: Int): LiveData<Long> {

        val firstDate: Calendar = Calendar.getInstance() // this takes current date
        firstDate.set(Calendar.DAY_OF_YEAR, 1)

        val secondDate: Calendar = Calendar.getInstance() // this takes current date
        secondDate.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR))

        return AppDelegate.repository.getAvgDiffBetweenDates(listId, firstDate.timeInMillis, secondDate.timeInMillis).asLiveData()
    }

    fun getAvgEventsDiffByDay(listId: Int): LiveData<Long> {

        val firstDate: Calendar = Calendar.getInstance() // this takes current date
        firstDate.set(Calendar.HOUR_OF_DAY, 0)
        firstDate.set(Calendar.MINUTE, 0)
        firstDate.set(Calendar.SECOND, 0)
        firstDate.set(Calendar.MILLISECOND, 0)

        val secondDate: Calendar = Calendar.getInstance() // this takes current date
        secondDate.set(Calendar.HOUR_OF_DAY, 23)
        secondDate.set(Calendar.MINUTE, 59)
        secondDate.set(Calendar.SECOND, 59)
        secondDate.set(Calendar.MILLISECOND, 999)

        return AppDelegate.repository.getAvgDiffBetweenDates(listId, firstDate.timeInMillis, secondDate.timeInMillis).asLiveData()
    }

    fun getEventsDiffByMonth(listId: Int): LiveData<List<Long>> {

        val firstDate: Calendar = Calendar.getInstance() // this takes current date
        firstDate.set(Calendar.DAY_OF_MONTH, 1)

        val secondDate: Calendar = Calendar.getInstance() // this takes current date
        secondDate.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH))

        return AppDelegate.repository.getDiffBetweenDates(listId, firstDate.timeInMillis, secondDate.timeInMillis).asLiveData()
    }



}