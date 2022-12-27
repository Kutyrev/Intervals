package com.github.kutyrev.intervals.features.main.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.repository.database.DatabaseRepository
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

private const val TEST_ENTITY_NAME = "Test entity"

class MainListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val dbRepository: DatabaseRepository = mockk()
    private val testEntity = ListEntity(name = TEST_ENTITY_NAME, withoutSeconds = true)

    @Test
    fun getListsLifeData() {
        every { dbRepository.getAllLists() } returns flowOf(List<ListEntity>(1) { testEntity })

        val mainViewModel = MainListViewModel(dbRepository)

        assertEquals(List<ListEntity>(1) { testEntity }, mainViewModel.listsLifeData.getOrAwaitValue())

        verify {
            dbRepository.getAllLists()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertNewList() = runTest {
        coEvery { dbRepository.insertAllLists(any()) } just Runs
        every { dbRepository.getAllLists() } returns flowOf(List<ListEntity>(1) { testEntity })

        val mainViewModel = MainListViewModel(dbRepository)

        mainViewModel.insertNewList(testEntity)

        coVerify {
            dbRepository.insertAllLists(testEntity)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteList() = runTest {
        coEvery { dbRepository.deleteList(any()) } just Runs
        every { dbRepository.getAllLists() } returns flowOf(List<ListEntity>(1) { testEntity })

        val mainViewModel = MainListViewModel(dbRepository)

        mainViewModel.deleteList(testEntity)

        coVerify {
            dbRepository.deleteList(testEntity)
        }
    }
}
