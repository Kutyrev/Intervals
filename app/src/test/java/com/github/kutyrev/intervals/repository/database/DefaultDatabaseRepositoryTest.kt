package com.github.kutyrev.intervals.repository.database

import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.IntervalsDao
import com.github.kutyrev.intervals.datasource.database.ListAndEvent
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.utils.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule

import org.junit.Test
import java.util.*

private const val TEST_COMMENT = "Test comment"
private const val TEST_LIST_NAME = "Test list entity"
private const val TEST_EVENT_LIST_ID = 1
private const val AVG_DIFF = 100L

class DefaultDatabaseRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val intervalsDao: IntervalsDao = mockk()

    private val testEntity = EventEntity(TEST_EVENT_LIST_ID, Calendar.getInstance(), TEST_COMMENT)
    private val testListOfEntities = listOf(testEntity)
    private val testDate = Calendar.getInstance().timeInMillis
    private val listEntity = ListEntity(name = TEST_LIST_NAME, withoutSeconds = false)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllEvents() = runTest {
        every { intervalsDao.getAllEvents() } returns flowOf(testListOfEntities)

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(testListOfEntities, defaultDatabaseRepository.getAllEvents().first())

        verify {
            intervalsDao.getAllEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllEventsByList() = runTest {
        every { intervalsDao.getAllEventsByList(any()) } returns flowOf(testListOfEntities)

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(
            testListOfEntities,
            defaultDatabaseRepository.getAllEventsByList(intArrayOf(TEST_EVENT_LIST_ID)).first()
        )

        verify {
            intervalsDao.getAllEventsByList(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getEventsBetweenDates() = runTest {
        every { intervalsDao.getEventsBetweenDates(any(), any(), any()) } returns flowOf(
            testListOfEntities
        )

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(
            testListOfEntities,
            defaultDatabaseRepository.getEventsBetweenDates(TEST_EVENT_LIST_ID, testDate, testDate)
                .first()
        )

        verify {
            intervalsDao.getEventsBetweenDates(any(), any(), any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAvgDiffBetweenDates() = runTest {
        every { intervalsDao.getAvgDiffBetweenDates(any(), any(), any()) } returns flowOf(
            AVG_DIFF
        )

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(
            AVG_DIFF,
            defaultDatabaseRepository.getAvgDiffBetweenDates(TEST_EVENT_LIST_ID, testDate, testDate)
                .first()
        )

        verify {
            intervalsDao.getAvgDiffBetweenDates(any(), any(), any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getDiffBetweenDates() = runTest {

        val listDiff = listOf(AVG_DIFF)

        every { intervalsDao.getDiffBetweenDates(any(), any(), any()) } returns flowOf(
            listDiff
        )

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(
            listDiff,
            defaultDatabaseRepository.getDiffBetweenDates(TEST_EVENT_LIST_ID, testDate, testDate)
                .first()
        )

        verify {
            intervalsDao.getDiffBetweenDates(any(), any(), any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAllEvents() = runTest {
        coEvery { intervalsDao.insertAllEvents(any()) } just Runs

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        defaultDatabaseRepository.insertAllEvents(testEntity)

        coVerify {
            intervalsDao.insertAllEvents(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateEvent() = runTest {
        coEvery { intervalsDao.updateEvent(any()) } just Runs

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        defaultDatabaseRepository.updateEvent(testEntity)

        coVerify {
            intervalsDao.updateEvent(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteEvent() = runTest {
        coEvery { intervalsDao.deleteEvent(any()) } just Runs

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        defaultDatabaseRepository.deleteEvent(testEntity)

        coVerify {
            intervalsDao.deleteEvent(any())
        }
    }

    @Test
    fun getListsAndEvents() {
        val testListAndEvent = listOf(
            ListAndEvent(
                listEntity,
                testEntity
            )
        )

        every { intervalsDao.getListsAndEvents() } returns testListAndEvent

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(testListAndEvent, defaultDatabaseRepository.getListsAndEvents())

        verify {
            intervalsDao.getListsAndEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllLists() = runTest {

        val listOfLists = listOf(listEntity)

        every { intervalsDao.getAllLists() } returns flowOf(listOfLists)

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        assertEquals(listOfLists, defaultDatabaseRepository.getAllLists().first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAllLists() = runTest {
        coEvery { intervalsDao.insertAllLists(any()) } just Runs

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        defaultDatabaseRepository.insertAllLists(listEntity)

        coVerify {
            intervalsDao.insertAllLists(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateList() = runTest {
        coEvery { intervalsDao.updateList(any()) } just Runs

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        defaultDatabaseRepository.updateList(listEntity)

        coVerify {
            intervalsDao.updateList(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteList() = runTest {
        coEvery { intervalsDao.deleteList(any()) } just Runs

        val defaultDatabaseRepository = DefaultDatabaseRepository(intervalsDao)

        defaultDatabaseRepository.deleteList(listEntity)

        coVerify {
            intervalsDao.deleteList(any())        }
    }
}
