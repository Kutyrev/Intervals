package com.github.kutyrev.intervals.features.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.utils.SwipeToDeleteCallback
import com.github.kutyrev.intervals.datasource.database.ListEntity

class ListEntityAdapter(
    private val onClick: (ListEntity) -> Unit,
    private val onSwipeDeleteList: (ListEntity, Int) -> Unit
) : ListAdapter<ListEntity, ListEntityHolder>(
    LISTENTITY_COMPARATOR
), SwipeToDeleteCallback.OnSwipeDeleteListener {

    companion object {
        private val LISTENTITY_COMPARATOR = object : DiffUtil.ItemCallback<ListEntity>() {
            override fun areItemsTheSame(oldItem: ListEntity, newItem: ListEntity): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ListEntity, newItem: ListEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEntityHolder {
        return ListEntityHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: ListEntityHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    override fun onSwipeDelete(position: Int) {
        val current = getItem(position)
        val editableList = currentList.toMutableList()
        editableList.remove(current)
        submitList(editableList)
        onSwipeDeleteList(getItem(position), position)
    }

    fun addToListView(curList: ListEntity, position: Int) {
        val editableList = currentList.toMutableList()
        editableList.add(position, curList)
        submitList(editableList)
    }

}

class ListEntityHolder(itemView: View, val onClick: (ListEntity) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private val label: TextView = itemView.findViewById(R.id.item_label_textview)
    private var curList: ListEntity? = null

    companion object {
        fun create(parent: ViewGroup, onClick: (ListEntity) -> Unit): ListEntityHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_recyclerview_item, parent, false)

            return ListEntityHolder(view, onClick)

        }
    }

    init {
        itemView.setOnClickListener {
            curList?.let { onClick(it) }
        }
    }

    fun bind(list: ListEntity) {
        label.text = list.name
        curList = list
    }
}
