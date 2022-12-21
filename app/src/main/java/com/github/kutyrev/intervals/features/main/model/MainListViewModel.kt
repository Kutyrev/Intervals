package com.github.kutyrev.intervals.features.main.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.repository.database.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(private val dbRepository: DatabaseRepository) : ViewModel() {

    val listsLifeData : LiveData<List<ListEntity>> = dbRepository.getAllLists().asLiveData()

    fun insertNewList(newList: ListEntity){
        viewModelScope.launch { dbRepository.insertAllLists(newList) }
    }

    fun deleteList(list : ListEntity){
        viewModelScope.launch { dbRepository.deleteList(list) }
    }
}
