package com.github.kutyrev.intervals.repository.database

import com.github.kutyrev.intervals.app.di.IoDispatcher
import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.IntervalsDao
import com.github.kutyrev.intervals.datasource.database.ListAndEvent
import com.github.kutyrev.intervals.datasource.database.ListEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultDatabaseRepository @Inject constructor(
    private val intervalsDao: IntervalsDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DatabaseRepository {

    override fun getAllEvents(): Flow<List<EventEntity>> = intervalsDao.getAllEvents().flowOn(dispatcher)

    override fun getAllEventsByList(listIds: IntArray): Flow<List<EventEntity>> =
        intervalsDao.getAllEventsByList(listIds).flowOn(dispatcher)

    override fun getEventsBetweenDates(
        listId: Int, firstDate: Long, secondDate: Long
    ): Flow<List<EventEntity>> = intervalsDao.getEventsBetweenDates(
        listId, firstDate, secondDate
    ).flowOn(dispatcher)

    override fun getAvgDiffBetweenDates(
        listId: Int, firstDate: Long, secondDate: Long
    ): Flow<Long> = intervalsDao.getAvgDiffBetweenDates(
        listId, firstDate, secondDate
    ).flowOn(dispatcher)

    override fun getDiffBetweenDates(
        listId: Int, firstDate: Long, secondDate: Long
    ): Flow<List<Long>> = intervalsDao.getDiffBetweenDates(
        listId, firstDate, secondDate
    ).flowOn(dispatcher)

    override suspend fun insertAllEvents(vararg events: EventEntity) = withContext(dispatcher) {
        intervalsDao.insertAllEvents(*events)
    }

    override suspend fun updateEvent(event: EventEntity) = withContext(dispatcher) {
        intervalsDao.updateEvent(event)
    }

    override suspend fun deleteEvent(event: EventEntity) = withContext(dispatcher) {
        intervalsDao.deleteEvent(event)
    }

    override fun getListsAndEvents(): List<ListAndEvent> = intervalsDao.getListsAndEvents()

    override fun getAllLists(): Flow<List<ListEntity>> = intervalsDao.getAllLists().flowOn(dispatcher)

    override suspend fun insertAllLists(vararg eventLists: ListEntity) = withContext(dispatcher) {
        intervalsDao.insertAllLists(*eventLists)
    }

    override suspend fun updateList(eventList: ListEntity) = withContext(dispatcher) {
        intervalsDao.updateList(eventList)
    }

    override suspend fun deleteList(eventList: ListEntity) = withContext(dispatcher) {
        intervalsDao.deleteList(eventList)
    }
}
