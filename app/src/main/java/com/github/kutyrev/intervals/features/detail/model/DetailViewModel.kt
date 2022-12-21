package com.github.kutyrev.intervals.features.detail.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.repository.database.DatabaseRepository
import com.github.kutyrev.intervals.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val LAST_DAY_HOUR = 23
private const val LAST_MINUTE_SECOND = 59
private const val LAST_MILLISECOND = 999
private const val ZERO_DATE_VALUE = 0

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    val eventsLifeData: LiveData<List<EventEntity>> =
        dbRepository.getAllEvents().asLiveData()

    private val _isShowFastAddButton: MutableLiveData<Boolean> = MutableLiveData(false)
    val isShowFastAddButton: LiveData<Boolean>
        get() = _isShowFastAddButton

    fun insertNewEvent(newEvent: EventEntity) {
        viewModelScope.launch { dbRepository.insertAllEvents(newEvent) }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { dbRepository.deleteEvent(event) }
    }

    fun updateEvent(curEvent: EventEntity) {
        viewModelScope.launch { dbRepository.updateEvent(curEvent) }
    }

    fun updateList(curList: ListEntity) {
        viewModelScope.launch { dbRepository.updateList(curList) }
    }

    fun getAvgEventsDiffByYear(listId: Int): LiveData<Long> {

        val firstDate: Calendar = Calendar.getInstance() // this takes current date
        firstDate.set(Calendar.DAY_OF_YEAR, 1)

        val secondDate: Calendar = Calendar.getInstance()
        secondDate.set(
            Calendar.DAY_OF_YEAR,
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)
        )

        return dbRepository.getAvgDiffBetweenDates(
            listId, firstDate.timeInMillis,
            secondDate.timeInMillis
        ).asLiveData()
    }

    fun getAvgEventsDiffByDay(listId: Int): LiveData<Long> {
        val firstDate: Calendar = Calendar.getInstance()
        firstDate.set(Calendar.HOUR_OF_DAY, ZERO_DATE_VALUE)
        firstDate.set(Calendar.MINUTE, ZERO_DATE_VALUE)
        firstDate.set(Calendar.SECOND, ZERO_DATE_VALUE)
        firstDate.set(Calendar.MILLISECOND, ZERO_DATE_VALUE)

        val secondDate: Calendar = Calendar.getInstance()
        secondDate.set(Calendar.HOUR_OF_DAY, LAST_DAY_HOUR)
        secondDate.set(Calendar.MINUTE, LAST_MINUTE_SECOND)
        secondDate.set(Calendar.SECOND, LAST_MINUTE_SECOND)
        secondDate.set(Calendar.MILLISECOND, LAST_MILLISECOND)

        return dbRepository.getAvgDiffBetweenDates(
            listId, firstDate.timeInMillis,
            secondDate.timeInMillis
        ).asLiveData()
    }

    fun getEventsDiffByMonth(listId: Int): LiveData<List<Long>> {
        val firstDate: Calendar = Calendar.getInstance()
        firstDate.set(Calendar.DAY_OF_MONTH, 1)

        val secondDate: Calendar = Calendar.getInstance()
        secondDate.set(
            Calendar.DAY_OF_MONTH,
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        )

        return dbRepository.getDiffBetweenDates(
            listId, firstDate.timeInMillis,
            secondDate.timeInMillis
        ).asLiveData()
    }

    fun isShowFastAddButton() {
        viewModelScope.launch {
            _isShowFastAddButton.postValue(preferencesRepository.getFastAddButtonProperty())
        }
    }
}
