package com.github.kutyrev.intervals.features.detail.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.repository.database.DatabaseRepository
import com.github.kutyrev.intervals.repository.preferences.PreferencesRepository
import com.github.kutyrev.intervals.utils.MainDispatcherRule
import com.github.kutyrev.intervals.utils.getOrAwaitValue
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val dbRepository: DatabaseRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()

    private val testEntity = EventEntity(TEST_EVENT_LIST_ID, Calendar.getInstance(), TEST_COMMENT)
    private val testListEntity = listOf(testEntity)
    private val listEntity = ListEntity(name = TEST_LIST_NAME, withoutSeconds = false)

    @Test
    fun getEventsLifeData() {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)

        assertEquals(
            testListEntity,
            detailViewModel.eventsLifeData.getOrAwaitValue()
        )

        verify {
            dbRepository.getAllEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isShowFastAddButton() = runTest {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        coEvery { preferencesRepository.getFastAddButtonProperty() } returns true

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)

        assertEquals(
            false,
            detailViewModel.isShowFastAddButton.getOrAwaitValue()
        )

        detailViewModel.startCollectIsShowFastAddButton()

        assertEquals(
            true,
            detailViewModel.isShowFastAddButton.getOrAwaitValue()
        )

        coVerify {
            preferencesRepository.getFastAddButtonProperty()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertNewEvent() = runTest {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        coEvery { dbRepository.insertAllEvents(any()) } just Runs

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)
        detailViewModel.insertNewEvent(testEntity)

        coVerify {
            dbRepository.insertAllEvents(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteEvent() = runTest {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        coEvery { dbRepository.deleteEvent(testEntity) } just Runs

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)
        detailViewModel.deleteEvent(testEntity)

        coVerify {
            dbRepository.deleteEvent(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateEvent() = runTest {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        coEvery { dbRepository.updateEvent(testEntity) } just Runs

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)
        detailViewModel.updateEvent(testEntity)

        coVerify {
            dbRepository.updateEvent(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateList() = runTest {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        coEvery { dbRepository.updateList(any()) } just Runs

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)
        detailViewModel.updateList(listEntity)

        coVerify {
            dbRepository.updateList(any())
        }
    }

    @Test
    fun getAvgEventsDiffByYear() {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        every { dbRepository.getAvgDiffBetweenDates(any(), any(), any()) } returns flowOf(AVG_DIFF)

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)
        assertEquals(
            AVG_DIFF,
            detailViewModel.getAvgEventsDiffByYear(TEST_EVENT_LIST_ID).getOrAwaitValue()
        )

        verify {
            dbRepository.getAvgDiffBetweenDates(any(), any(), any())
        }
    }

    @Test
    fun getAvgEventsDiffByDay() {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        every { dbRepository.getAvgDiffBetweenDates(any(), any(), any()) } returns flowOf(AVG_DIFF)

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)
        assertEquals(
            AVG_DIFF,
            detailViewModel.getAvgEventsDiffByDay(TEST_EVENT_LIST_ID).getOrAwaitValue()
        )

        verify {
            dbRepository.getAvgDiffBetweenDates(any(), any(), any())
        }
    }

    @Test
    fun getEventsDiffByMonth() {
        every { dbRepository.getAllEvents() } returns flowOf(testListEntity)
        every { dbRepository.getDiffBetweenDates(any(), any(), any()) } returns flowOf(
            listOf(
                AVG_DIFF
            )
        )

        val detailViewModel = DetailViewModel(preferencesRepository, dbRepository)

        assertEquals(
            listOf(AVG_DIFF),
            detailViewModel.getEventsDiffByMonth(TEST_EVENT_LIST_ID).getOrAwaitValue()
        )

        verify {
            dbRepository.getDiffBetweenDates(any(), any(), any())
        }
    }
}