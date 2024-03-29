package com.github.kutyrev.intervals.features.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.features.editing.DialogNewEditList
import com.github.kutyrev.intervals.features.main.model.MainListViewModel
import com.github.kutyrev.intervals.utils.SwipeToDeleteCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

private const val DELETE_DIALOG_TAG = "DialogDeleteList"
private const val NEW_LIST_DIALOG_TAG = "NewEditListDialog"

@AndroidEntryPoint
class MainListFragment : Fragment(R.layout.fragment_main_list),
    DialogNewEditList.NewEditListDialogListener, DialogDeleteList.DeleteListDialogListener {

    private val viewModel: MainListViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListEntityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.lists_recycler)

        view.findViewById<FloatingActionButton>(R.id.fab_main_list).setOnClickListener {
            DialogNewEditList(ListEntity("", false), true).show(
                childFragmentManager,
                NEW_LIST_DIALOG_TAG
            )
        }

        adapter = ListEntityAdapter({ curList -> onListElementClick(curList) },
            { curList, position -> onListSwipeDelete(curList, position) })
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter, requireContext()))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.listsLifeData.observe(viewLifecycleOwner) { result ->
            adapter.submitList(result)
        }
    }

    override fun onDialogPositiveClickNewEditList(curList: ListEntity, isNew: Boolean) {
        if (isNew) viewModel.insertNewList(curList)
    }

    private fun onListElementClick(curList: ListEntity) {
        (activity as MainActivity).goToDetailFragment(curList)
    }

    private fun onListSwipeDelete(curList: ListEntity, position: Int) {
        val newFragment = DialogDeleteList(curList, position, this)
        newFragment.show(childFragmentManager, DELETE_DIALOG_TAG)
    }

    override fun onDeleteListDialogPositiveClick(curList: ListEntity) {
        viewModel.deleteList(curList)
    }

    override fun onDeleteListDialogNegativeClick(curList: ListEntity, position: Int) {
        adapter.addToListView(curList, position)
    }
}
