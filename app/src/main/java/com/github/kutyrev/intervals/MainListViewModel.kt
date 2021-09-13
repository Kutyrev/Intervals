package com.github.kutyrev.intervals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.intervals.database.ListEntity
import kotlinx.coroutines.launch

class MainListViewModel : ViewModel() {

    val listsLifeData : LiveData<List<ListEntity>> = AppDelegate.repository.getAllLists().asLiveData()

    fun insertNewList(newList: ListEntity){
        viewModelScope.launch { AppDelegate.repository.insertAllLists(newList) }
    }

    fun deleteList(list : ListEntity){
        viewModelScope.launch { AppDelegate.repository.deleteList(list) }
    }

}