package com.github.kutyrev.intervals

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.kutyrev.intervals.database.EventEntity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun computeDiffDates(date1: Date, date2: Date): Map<TimeUnit?, Long>? {
    val diffInMillies = date2.time - date1.time

    //create the list
    val units: List<TimeUnit?> = ArrayList<TimeUnit?>(EnumSet.allOf(TimeUnit::class.java))
    Collections.reverse(units)

    //create the result map of TimeUnit and difference
    val result: MutableMap<TimeUnit?, Long> = LinkedHashMap<TimeUnit?, Long>()
    var milliesRest = diffInMillies
    for (unit in units) {

        if (unit == TimeUnit.NANOSECONDS
            || unit == TimeUnit.MILLISECONDS
            || unit == TimeUnit.MICROSECONDS) continue

        //calculate difference in millisecond
        val diff: Long = unit?.convert(milliesRest, TimeUnit.MILLISECONDS) ?: 0
        val diffInMilliesForUnit: Long = unit?.toMillis(diff) ?: 0
        milliesRest = milliesRest - diffInMilliesForUnit

        //put the result in the map
        result[unit] = diff
    }
    return result
}

class IntervalsAdapter(val detailFragment: DetailFragment, val context: Context) : ListAdapter<EventEntity, EventEntityHolder>(
    EVENTENTITY_COMPARATOR
), SwipeToDeleteCallback.OnSwipeDeleteListener {

    internal lateinit var listener: EventActionsListener

    companion object {
        private val EVENTENTITY_COMPARATOR = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        try {
            listener = detailFragment as EventActionsListener
        }catch (e: ClassCastException){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventEntityHolder {


      /*  return EventEntityHolder.create(parent,  object : EventItemClickListener{
            override fun onEditEvent(position: Int) {
                println(getItem(position).dateStamp)

            }

        })*/

        return EventEntityHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: EventEntityHolder, position: Int) {
        val current = getItem(position)
        var stringDateRepr = ""
        if (position > 0){
            var diffDate : Map<TimeUnit?, Long>?
            val beforeCurrent = getItem(position - 1)
            diffDate = current.dateStamp?.let { beforeCurrent.dateStamp?.let { it1 -> computeDiffDates(it1.time, it.time) } }
            var intervalStringRepr = StringBuilder()

            if (diffDate != null) {
                for(curUnit in diffDate){
                    intervalStringRepr.append(curUnit.key?.name).append(": ").append(curUnit.value.toString()).append(" ")
                }
            }
            stringDateRepr = intervalStringRepr.toString()
        }
        holder.bind(current.comment, current.dateStamp, stringDateRepr, current)
    }

    override fun onSwipeDelete(position: Int) {
        val current = getItem(position)
        val editableList = currentList.toMutableList()
        editableList.remove(current)
        submitList(editableList)

        if (listener != null){
            listener.onDeleteEvent(current, this)
        }
    }

    interface EventActionsListener {
        fun onDeleteEvent(curEvent: EventEntity, adapter: IntervalsAdapter)
        fun onEditEvent(curEvent: EventEntity)
    }


}

class EventEntityHolder(itemView: View, private val editListener : IntervalsAdapter.EventActionsListener) : RecyclerView.ViewHolder(itemView){

    private val commentView: TextView = itemView.findViewById(R.id.comment)
    private val dateStampView: TextView = itemView.findViewById(R.id.datestamp)
    private val diffBetweenView : TextView = itemView.findViewById(R.id.diffbetween)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun bind(text: String?, dateStamp: Calendar?, diffDate: String, event: EventEntity) {
        commentView.text = text
        diffBetweenView.text = diffDate

        try {
            if (dateStamp != null) {
                dateStampView.text = dateFormat.format(dateStamp.time)
            }
        } catch (e: IllegalArgumentException) {

        }

        itemView.setOnClickListener{
            editListener.onEditEvent(event)
        }

    }


    companion object {
        fun create(parent: ViewGroup, editListener : IntervalsAdapter.EventActionsListener): EventEntityHolder {
            val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)

            return EventEntityHolder(view, editListener)

        }
    }


}
