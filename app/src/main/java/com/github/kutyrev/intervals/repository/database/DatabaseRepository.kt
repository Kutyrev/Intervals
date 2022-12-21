package com.github.kutyrev.intervals.repository.database

import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.ListAndEvent
import com.github.kutyrev.intervals.datasource.database.ListEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getAllEvents(): Flow<List<EventEntity>>
    fun getAllEventsByList(listIds: IntArray): Flow<List<EventEntity>>
    fun getEventsBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<List<EventEntity>>
    fun getAvgDiffBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<Long>
    fun getDiffBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<List<Long>>
    suspend fun insertAllEvents(vararg events: EventEntity)
    suspend fun updateEvent(event: EventEntity)
    suspend fun deleteEvent(event: EventEntity)
    fun getListsAndEvents(): List<ListAndEvent>
    fun getAllLists(): Flow<List<ListEntity>>
    suspend fun insertAllLists(vararg eventLists: ListEntity)
    suspend fun updateList(eventList: ListEntity)
    suspend fun deleteList(eventList: ListEntity)
}
