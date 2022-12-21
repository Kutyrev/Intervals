package com.github.kutyrev.intervals.features.detail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.datasource.database.EventEntity
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.utils.DATE_PATTERN
import java.text.SimpleDateFormat
import java.util.*

private const val MIN_SECONDS_VALUE = 0
private const val MAX_SECONDS_VALUE = 59
private const val EVENT_DIAL_EXCEPTION_TEXT = " must implement NewEventDialogListener"

class DialogNewEditEvent(
    private val curList: ListEntity,
    private val curEvent: EventEntity? = null
) : DialogFragment() {

    var today: Calendar = Calendar.getInstance()
    internal lateinit var listener: NewEventDialogListener
    private val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_round_corner)

        val view: View = inflater.inflate(R.layout.dialog_new_event, container, false)

        val cancelButton: Button = view.findViewById(R.id.dialog_button_cancel)
        val okButton: Button = view.findViewById(R.id.dialog_button_ok)
        val newDateTextView: TextView = view.findViewById(R.id.new_date_textview)
        val calendarButton: Button = view.findViewById(R.id.cal_button)
        val timeButton: Button = view.findViewById(R.id.time_button)
        val commentEditText: EditText = view.findViewById(R.id.comment_edit_text)
        val secondsButton: Button = view.findViewById(R.id.seconds_button)

        if (curList.withoutSeconds) secondsButton.isEnabled = false

        var year = today.get(Calendar.YEAR)
        var month = today.get(Calendar.MONTH)
        var day = today.get(Calendar.DAY_OF_MONTH)
        var hourOfDay = today.get(Calendar.HOUR_OF_DAY)
        var minute = today.get(Calendar.MINUTE)
        var second = today.get(Calendar.SECOND)

        if (curList.withoutSeconds) {
            second = MIN_SECONDS_VALUE
            today.set(year, month, day, hourOfDay, minute, second)
        }

        if (curEvent?.dateStamp != null) {
            year = curEvent.dateStamp!!.get(Calendar.YEAR)
            month = curEvent.dateStamp!!.get(Calendar.MONTH)
            day = curEvent.dateStamp!!.get(Calendar.DAY_OF_MONTH)
            hourOfDay = curEvent.dateStamp!!.get(Calendar.HOUR_OF_DAY)
            minute = curEvent.dateStamp!!.get(Calendar.MINUTE)
            second = curEvent.dateStamp!!.get(Calendar.SECOND)
            if (curList.withoutSeconds) second = MIN_SECONDS_VALUE
            commentEditText.setText(curEvent.comment)
            today.set(year, month, day, hourOfDay, minute, second)
        }

        newDateTextView.setText(dateFormat.format(today.time))

        setupBottomButtons(okButton, commentEditText, curEvent, cancelButton)

        setupCalendarButton(calendarButton, newDateTextView, year, month, day)

        setupTimeButton(timeButton, newDateTextView, hourOfDay, minute)

        setupSecondsButton(secondsButton, second, newDateTextView)

        return view
    }

    private fun setupSecondsButton(
        secondsButton: Button,
        second: Int,
        newDateTextView: TextView
    ) {
        secondsButton.setOnClickListener {
            val picker = NumberPicker(this.requireContext())
            picker.minValue = MIN_SECONDS_VALUE
            picker.maxValue = MAX_SECONDS_VALUE
            picker.value = second

            val layout = FrameLayout(this.requireContext())
            layout.addView(
                picker, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
            )
            val secondsDialog = AlertDialog.Builder(this.requireContext())
                .setView(layout)
                .setPositiveButton(
                    R.string.dialog_ok,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        today.set(
                            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                            today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.HOUR_OF_DAY),
                            today.get(Calendar.MINUTE), picker.value
                        )
                        val dateTime: Date = today.getTime()
                        newDateTextView.setText(dateFormat.format(dateTime))

                    })
                .setNegativeButton(R.string.dialog_cancel, null)
            secondsDialog.show()
        }
    }

    private fun setupTimeButton(
        timeButton: Button,
        newDateTextView: TextView,
        hourOfDay: Int,
        minute: Int
    ) {
        timeButton.setOnClickListener {
            val datePickerDialog = TimePickerDialog(
                this.requireContext(),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    today.set(
                        today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH), hourOfDay, minute
                    )
                    val dateTime: Date = today.getTime()
                    newDateTextView.setText(dateFormat.format(dateTime))
                }, hourOfDay, minute, true
            )
            datePickerDialog.show()
        }
    }

    private fun setupCalendarButton(
        calendarButton: Button,
        newDateTextView: TextView,
        year: Int,
        month: Int,
        day: Int
    ) {
        calendarButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    today.set(
                        year,
                        month,
                        day,
                        today.get(Calendar.HOUR_OF_DAY),
                        today.get(Calendar.MINUTE)
                    )
                    // Display Selected date in textbox
                    val dateTime: Date = today.getTime()
                    newDateTextView.setText(dateFormat.format(dateTime))

                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun setupBottomButtons(
        okButton: Button,
        commentEditText: EditText,
        curEvent: EventEntity?,
        cancelButton: Button
    ) {
        okButton.setOnClickListener {

            if (curEvent == null) {
                listener.onAddNewEventDialogPositiveClickNewItem(
                    EventEntity(
                        curList.id,
                        today,
                        commentEditText.text.toString()
                    )
                )
            } else {
                curEvent.dateStamp = today
                curEvent.comment = commentEditText.text.toString()
                listener.onEditEventDialogPositiveClickNewItem(curEvent)
            }
            this.dismiss()
        }

        cancelButton.setOnClickListener {
            this.dismiss()
        }
    }

    // Override the Fragment.onAttach() method to instantiate the NewEventDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            //listener = context as NewEventDialogListener
            listener = this.parentFragment as NewEventDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() + EVENT_DIAL_EXCEPTION_TEXT)
            )
        }
    }

    interface NewEventDialogListener {
        fun onAddNewEventDialogPositiveClickNewItem(newEvent: EventEntity)
        fun onEditEventDialogPositiveClickNewItem(newEvent: EventEntity)
    }
}
