package com.github.kutyrev.intervals

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kutyrev.intervals.database.EventEntity
import com.github.kutyrev.intervals.database.ListEntity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment(val list: ListEntity) : Fragment(R.layout.fragment_detail), DialogNewEditEvent.NewEventDialogListener,
        IntervalsAdapter.EventActionsListener, DialogNewEditList.NewEditListDialogListener {


    companion object {
        const val RECYCLER_MODE = 1
        const val GRAPH_MODE = 2
    }

    private val viewModel: DetailViewModel by viewModels()

    var curListMode: Int = 1
    private lateinit var recyclerView: RecyclerView
    lateinit var graphView: GraphView
    lateinit var labelview: TextView
    lateinit var avgByMonthView : TextView
    lateinit var avgByYearView : TextView
    lateinit var avgByDayView : TextView
    lateinit var fastAddBtn : FloatingActionButton
    lateinit var labelGraphView : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        graphView = view.findViewById<GraphView>(R.id.graph_view)
        avgByMonthView = view.findViewById<TextView>(R.id.textview_avg_by_month)
        avgByYearView = view.findViewById<TextView>(R.id.textview_avg_by_year)
        avgByDayView = view.findViewById<TextView>(R.id.textview_avg_by_day)
        fastAddBtn = view.findViewById<FloatingActionButton>(R.id.fab_fast_add)
        labelGraphView = view.findViewById<TextView>(R.id.textview_graph_title)

        /*    val bundle = arguments
            if (bundle != null) {
                val list = bundle.getParcelable<ListEntity>("list")
            }
    */

        labelview = view.findViewById(R.id.label)
        labelview.setText(list?.name)
        labelview.setOnClickListener { DialogNewEditList(list, false).show(childFragmentManager, "NewEditListDialog") }


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
            DialogNewEditEvent(list, null).show(childFragmentManager, "NewEditEventDialog")
        }

        view.findViewById<FloatingActionButton>(R.id.fab_fast_add).setOnClickListener {
            viewModel.insertNewEvent(EventEntity(list.id, Calendar.getInstance(),""))
        }

        getStatistic()
    }

    private fun getStatistic() {

  /*      val eventsForTheMonth: LiveData<List<EventEntity>> = viewModel.getEventsForTheMonth(list.id)

        eventsForTheMonth.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->

            var points : FloatArray = FloatArray(result.size-1)
            var diffs : LongArray = LongArray(result.size-1)
            val dataPoints : MutableList<DataPoint> = mutableListOf()

            if (result != null) {
                val iterator = result.iterator()
                var date1 : Long = 0
                var date2 : Long = 0
                var point : Int = 0


                for ((index, value) in iterator.withIndex()) {
                    if (index == 0) date1 = value.dateStamp?.timeInMillis ?: 0
                    else date2 = value.dateStamp?.timeInMillis  ?: 0

                    if(date1 > 0 && date2 > 0) {
                        points[point] = 1.0f / (date2 - date1)
                        diffs[point] = date2 - date1
                        point++
                        date1 = date2

                    }
                }

                val max: Float? = points.max()
                val min : Float? = points.min()

                if(max != null && min != null && max != min){
                    val step = (max - min) / 50
                    points.forEachIndexed { index, l -> dataPoints.add(DataPoint(index, (l/step).toInt()))  }
                }


            }*/

            val eventsDiffs : LiveData<List<Long>> = viewModel.getEventsDiffByMonth(list.id)
            eventsDiffs.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->

                var points: FloatArray = FloatArray(result.size)
                var diffs: LongArray = LongArray(result.size)
                val dataPoints: MutableList<DataPoint> = mutableListOf()

                if (result != null) {
                    val iterator = result.iterator()
                    var point: Int = 0

                    for (value in iterator) {
                        if (value != null) {
                            points[point] = 1.0f / (value)
                            diffs[point] = value
                            point++
                        }

                    }

                    val max: Float? = points.max()
                    val min: Float? = points.min()

                    if (max != null && min != null && max != min) {
                        val step = (max - min) / 50
                        points.forEachIndexed { index, l -> dataPoints.add(DataPoint(index, (l / step).toInt())) }
                    }

                    avgByMonthView.text = getString(R.string.avg_by_month, DateDiff(diffs.average().toLong()).toString())
                    graphView.setData(dataPoints)

                }
            })


        val avgByYear: LiveData<Long> = viewModel.getAvgEventsDiffByYear(list.id)
        avgByYear.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) avgByYearView.text = getString(R.string.avg_by_year, DateDiff(it).toString())
            else avgByYearView.text = getString(R.string.avg_by_year, "—")
        })

        val avgByDay: LiveData<Long> = viewModel.getAvgEventsDiffByDay(list.id)
        avgByDay.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) avgByDayView.text = getString(R.string.avg_by_day, DateDiff(it).toString())
            else avgByDayView.text = getString(R.string.avg_by_day, "—")
        })

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter: IntervalsAdapter = IntervalsAdapter(this, requireContext())

        recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter, requireContext()))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.eventsLifeData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->

            if (list != null) {

                var eventsForList: MutableList<EventEntity> = mutableListOf<EventEntity>()

                for (curEvent in result) {
                    if (curEvent.listId == list!!.id) {
                        eventsForList.add(curEvent)
                    }
                }
                adapter?.submitList(eventsForList)
                //notifyDataSetChanged()
            }

        })




    }

    override fun onAddNewEventDialogPositiveClickNewItem(newEvent: EventEntity) {
        viewModel.insertNewEvent(newEvent)
    }

    override fun onEditEventDialogPositiveClickNewItem(curEvent: EventEntity) {
        viewModel.updateEvent(curEvent)
    }

    override fun onEditEvent(curEvent: EventEntity) {
        list?.let { activity?.let { itActivity -> DialogNewEditEvent(it, curEvent).show(itActivity.supportFragmentManager, "MyCustomFragment") } }
    }

    override fun onDeleteEvent(curEvent: EventEntity, adapter: IntervalsAdapter) {

        val mainView: View? = getView()

        if (mainView != null) {

            val view: View = mainView.findViewById<CoordinatorLayout>(R.id.detail_main_layout)


            val snackbar: Snackbar = Snackbar.make(view, getString(R.string.delete_event_snackbar), Snackbar.LENGTH_LONG)
            snackbar.setAction(getString(R.string.snackbar_undo), View.OnClickListener { v -> undoDeleteEvent(curEvent, adapter) })

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogPositiveClickNewEditList(curList: ListEntity, isNew: Boolean) {
        labelview.setText(curList.name)
        viewModel.updateList(curList)
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val enableFastAddBtn = sharedPreferences.getBoolean("fast_add_button", false)
        if(enableFastAddBtn)fastAddBtn.visibility = View.VISIBLE
        else fastAddBtn.visibility = View.GONE

    }


}