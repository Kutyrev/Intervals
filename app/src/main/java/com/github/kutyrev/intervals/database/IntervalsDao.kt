package com.github.kutyrev.intervals.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IntervalsDao {

    @Query("SELECT * FROM EventEntity ORDER BY dateStamp")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE listId IN (:listIds)ORDER BY dateStamp")
    fun getAllEventsByList(listIds: IntArray): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE listId = :listId AND datestamp BETWEEN :firstDate AND :secondDate ORDER BY dateStamp")
    fun getEventsBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<List<EventEntity>>

    @Query("SELECT AVG(EE2.dateStamp - EE.dateStamp) FROM(SELECT (SELECT COUNT(*) FROM EventEntity AS INEE WHERE INEE.listId = :listId AND INEE.datestamp BETWEEN :firstDate AND :secondDate AND EE.dateStamp >= INEE.dateStamp) AS NoID, dateStamp FROM EventEntity AS EE WHERE EE.listId = :listId AND EE.datestamp BETWEEN :firstDate AND :secondDate ORDER BY EE.dateStamp) AS EE LEFT JOIN (SELECT (SELECT COUNT(*) FROM EventEntity AS INEE2 WHERE INEE2.listId = :listId AND INEE2.datestamp BETWEEN :firstDate AND :secondDate AND EE2.dateStamp >= INEE2.dateStamp) AS NoID, dateStamp FROM EventEntity AS EE2 WHERE EE2.listId = :listId AND EE2.datestamp BETWEEN :firstDate AND :secondDate ORDER BY EE2.dateStamp) AS EE2 ON EE.NoID = (EE2.NoID - 1)")
    fun getAvgDiffBetweenDates(listId : Int, firstDate : Long, secondDate : Long) : Flow<Long>

    @Query("SELECT EE2.dateStamp - EE.dateStamp FROM(SELECT (SELECT COUNT(*) FROM EventEntity AS INEE WHERE INEE.listId = :listId AND INEE.datestamp BETWEEN :firstDate AND :secondDate AND EE.dateStamp >= INEE.dateStamp) AS NoID, dateStamp FROM EventEntity AS EE WHERE EE.listId = :listId AND EE.datestamp BETWEEN :firstDate AND :secondDate ORDER BY EE.dateStamp) AS EE LEFT JOIN (SELECT (SELECT COUNT(*) FROM EventEntity AS INEE2 WHERE INEE2.listId = :listId AND INEE2.datestamp BETWEEN :firstDate AND :secondDate AND EE2.dateStamp >= INEE2.dateStamp) AS NoID, dateStamp FROM EventEntity AS EE2 WHERE EE2.listId = :listId AND EE2.datestamp BETWEEN :firstDate AND :secondDate ORDER BY EE2.dateStamp) AS EE2 ON EE.NoID = (EE2.NoID - 1) WHERE (EE2.dateStamp - EE.dateStamp) NOT NULL")
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
