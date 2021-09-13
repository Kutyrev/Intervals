package com.github.kutyrev.intervals.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IntervalsDao {

    @Query("SELECT * FROM EventEntity")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE listId IN (:listIds)")
    fun getAllEventsByList(listIds: IntArray): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE listId = :listId AND datestamp BETWEEN :firstDate AND :secondDate")
    fun getEventsBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<List<EventEntity>>

    @Query("SELECT avg(EE2.dateStamp - EE.dateStamp) FROM EventEntity AS EE LEFT JOIN EventEntity AS EE2 ON EE.id = (EE2.id - 1)  WHERE EE.listId = :listId AND EE.datestamp BETWEEN :firstDate AND :secondDate")
    fun getAvgDiffBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<Long>

    @Query("SELECT EE2.dateStamp - EE.dateStamp FROM EventEntity AS EE LEFT JOIN EventEntity AS EE2 ON EE.id = (EE2.id - 1)  WHERE EE.listId = :listId AND EE.datestamp BETWEEN :firstDate AND :secondDate AND (EE2.dateStamp - EE.dateStamp) NOT NULL")
    fun getDiffBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<List<Long>>



    @Insert
    suspend fun insertAllEvents(vararg events: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Transaction
    @Query("SELECT * FROM ListEntity")
    fun getListsAndEvents(): List<ListAndEvent>

    @Query("SELECT * FROM ListEntity")
    fun getAllLists(): Flow<List<ListEntity>>

    @Insert
    suspend fun insertAllLists(vararg eventLists: ListEntity)

    @Update
    suspend fun updateList(eventList: ListEntity)

    @Delete
    suspend fun deleteList(eventList: ListEntity)


}
