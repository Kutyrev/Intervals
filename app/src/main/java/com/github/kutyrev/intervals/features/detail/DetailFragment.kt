package com.github.kutyrev.intervals.features.detail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.features.detail.model.DetailViewModel
import com.github.kutyrev.intervals.features.editing.DialogNewEditList
import com.github.kutyrev.intervals.features.graph.DataPoint
import com.github.kutyrev.intervals.features.graph.GraphView
import com.github.kutyrev.intervals.utils.DateDiff
import com.github.kutyrev.intervals.utils.LIST_KEY
import com.github.kutyrev.intervals.utils.SwipeToDeleteCallback
import com.github.kutyrev.intervals.utils.parcelable
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

private const val EDIT_EVENT_TAG = "NewEditEventDialog"
private const val EDIT_LIST_TAG = "NewEditListDialog"
private const val POINTS_STEP_SIZE = 50

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail),
    DialogNewEditEvent.NewEventDialogListener,
    IntervalsAdapter.EventActionsListener, DialogNewEditList.NewEditListDialogListener {

    companion object {
        const val RECYCLER_MODE = 1
        const val GRAPH_MODE = 2
    }

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var list: ListEntity

    private var curListMode: Int = RECYCLER_MODE
    private lateinit var recyclerView: RecyclerView
    private lateinit var graphView: GraphView
    private lateinit var labelview: TextView
    private lateinit var avgByMonthView: TextView
    private lateinit var avgByYearView: TextView
    private lateinit var avgByDayView: TextView
    private lateinit var fastAddBtn: FloatingActionButton
    private lateinit var labelGraphView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = arguments?.parcelable<ListEntity>(LIST_KEY) ?: ListEntity("", false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews(view)

        labelview.setText(list.name)
        labelview.setOnClickListener {
            DialogNewEditList(list, false).show(
                childFragmentManager,
                EDIT_LIST_TAG
            )
        }

        val toggleGroupMode: MaterialButtonToggleGroup = view.findViewById(R.id.toggleButtonMode)
        toggleGroupMode.check(R.id.button_recycler_mode)
        toggleGroupMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_recycler_mode -> {
                        curListMode = RECYCLER_MODE
                    }
                    R.id.button_stats_mode -> {
                        curListMode = GRAPH_MODE
                    }
                }

                when (curListMode) {
                    RECYCLER_MODE -> listMode()
                    GRAPH_MODE -> graphMode()
                }
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_detail).setOnClickListener {
            DialogNewEditEvent(list, null).show(childFragmentManager, EDIT_EVENT_TAG)
        }

        view.findViewById<FloatingActionButton>(R.id.fab_fast_add).setOnClickListener {
            if (list.withoutSeconds) {
                val today = Calendar.getInstance()
                today.set(
                    today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.HOUR_OF_DAY),
                    today.get(Calendar.MINUTE), 0
                )
                viewModel.insertNewEvent(EventEntity(list.id, today, ""))

            } else viewModel.insertNewEvent(EventEntity(list.id, Calendar.getInstance(), ""))
        }

        getStatistics()

        val adapter = IntervalsAdapter(this, requireContext())

        recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter, requireContext()))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.eventsLifeData.observe(viewLifecycleOwner) { result ->

            val eventsForList: MutableList<EventEntity> = mutableListOf<EventEntity>()

            for (curEvent in result) {
                if (curEvent.listId == list.id) {
                    eventsForList.add(curEvent)
                }
            }
            adapter.submitList(eventsForList)

            if (eventsForList.isNotEmpty()) recyclerView.smoothScrollToPosition(eventsForList.size - 1)
            //notifyDataSetChanged()
        }

        if (adapter.currentList.isNotEmpty()) recyclerView.smoothScrollToPosition(adapter.currentList.size - 1)

        viewModel.startCollectIsShowFastAddButton()
        viewModel.isShowFastAddButton.observe(viewLifecycleOwner) { enableFastAddBtn ->
            if (enableFastAddBtn) fastAddBtn.visibility = View.VISIBLE
            else fastAddBtn.visibility = View.GONE
        }
    }

    private fun setViews(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        graphView = view.findViewById<GraphView>(R.id.graph_view)
        avgByMonthView = view.findViewById<TextView>(R.id.textview_avg_by_month)
        avgByYearView = view.findViewById<TextView>(R.id.textview_avg_by_year)
        avgByDayView = view.findViewById<TextView>(R.id.textview_avg_by_day)
        fastAddBtn = view.findViewById<FloatingActionButton>(R.id.fab_fast_add)
        labelGraphView = view.findViewById<TextView>(R.id.textview_graph_title)
        labelview = view.findViewById(R.id.label)
    }

    private fun getStatistics() {
        lifecycle.coroutineScope.launch {
            viewModel.getEventsDiffByMonth(list.id).collect() { result ->
                if (result != null) {
                    val points = FloatArray(result.size)
                    val diffs = LongArray(result.size)
                    val dataPoints: MutableList<DataPoint> = mutableListOf()


                    val iterator = result.iterator()
                    var point = 0

                    for (value in iterator) {
                        points[point] = 1.0f / (value)
                        diffs[point] = value
                        point++

                    }

                    val max: Float? = points.maxOrNull()
                    val min: Float? = points.minOrNull()

                    if (max != null && min != null && max != min) {
                        val step = (max - min) / POINTS_STEP_SIZE
                        points.forEachIndexed { index, l ->
                            dataPoints.add(
                                DataPoint(
                                    index,
                                    (l / step).toInt()
                                )
                            )
                        }
                    }

                    avgByMonthView.text = getString(
                        R.string.avg_by_month,
                        DateDiff(diffs.average().toLong()).toString(context)
                    )
                    graphView.setData(dataPoints)
                }
            }
        }

        lifecycle.coroutineScope.launch {
            viewModel.getAvgEventsDiffByYear(list.id).collect() {
                if (it != null) avgByYearView.text =
                    getString(R.string.avg_by_year, DateDiff(it).toString(context))
                else avgByYearView.text = getString(R.string.avg_by_year, "—")
            }
        }

        lifecycle.coroutineScope.launch {
            viewModel.getAvgEventsDiffByDay(list.id).collect() {
                if (it != null) avgByDayView.text =
                    getString(R.string.avg_by_day, DateDiff(it).toString(context))
                else avgByDayView.text = getString(R.string.avg_by_day, "—")
            }
        }
    }

    private fun graphMode() {
        graphView.visibility = View.VISIBLE
        avgByMonthView.visibility = View.VISIBLE
        avgByYearView.visibility = View.VISIBLE
        avgByDayView.visibility = View.VISIBLE
        labelGraphView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun listMode() {
        graphView.visibility = View.GONE
        avgByMonthView.visibility = View.GONE
        avgByYearView.visibility = View.GONE
        avgByDayView.visibility = View.GONE
        labelGraphView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    override fun onAddNewEventDialogPositiveClickNewItem(newEvent: EventEntity) {
        viewModel.insertNewEvent(newEvent)
    }

    override fun onEditEventDialogPositiveClickNewItem(curEvent: EventEntity) {
        viewModel.updateEvent(curEvent)
    }

    override fun onEditEvent(curEvent: EventEntity) {
        list.let { DialogNewEditEvent(it, curEvent).show(childFragmentManager, EDIT_EVENT_TAG) }
    }

    override fun onDeleteEvent(curEvent: EventEntity, adapter: IntervalsAdapter) {
        val mainView: View? = getView()

        if (mainView != null) {

            val view: View = mainView.findViewById<CoordinatorLayout>(R.id.detail_main_layout)

            val snackbar: Snackbar =
                Snackbar.make(
                    view,
                    getString(R.string.delete_event_snackbar),
                    Snackbar.LENGTH_LONG
                )
            snackbar.setAction(
                getString(R.string.snackbar_undo)
            ) { undoDeleteEvent(curEvent, adapter) }

            snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

                override fun onDismissed(snackbar: Snackbar, event: Int) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        viewModel.deleteEvent(curEvent)
                    }
                }

            })
            snackbar.show()
        }
    }

    private fun undoDeleteEvent(curEvent: EventEntity, adapter: IntervalsAdapter) {
        val editableList = adapter.currentList.toMutableList()
        editableList.add(curEvent)
        editableList.sortBy { it.dateStamp }
        adapter.submitList(editableList)
    }

    override fun onDialogPositiveClickNewEditList(curList: ListEntity, isNew: Boolean) {
        labelview.setText(curList.name)
        viewModel.updateList(curList)
    }
}
