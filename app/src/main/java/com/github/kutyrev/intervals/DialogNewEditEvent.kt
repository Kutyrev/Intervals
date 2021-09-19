package com.github.kutyrev.intervals

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
import com.github.kutyrev.intervals.database.EventEntity
import com.github.kutyrev.intervals.database.ListEntity
import java.text.SimpleDateFormat
import java.util.*


class DialogNewEditEvent(val curList: ListEntity, val curEvent : EventEntity? = null ) : DialogFragment()  {

    var today = Calendar.getInstance()
    internal lateinit var listener: NewEventDialogListener
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_round_corner)

        val view: View = inflater.inflate(R.layout.dialog_new_event, container, false)

        val cancelButton: Button = view.findViewById(R.id.dialog_button_cancel)
        val okButton: Button = view.findViewById(R.id.dialog_button_ok)
        val newDateTextView : TextView = view.findViewById(R.id.new_date_textview)
        val calendarButton: Button = view.findViewById(R.id.cal_button)
        val timeButton: Button = view.findViewById(R.id.time_button)
        val commentEditText : EditText = view.findViewById(R.id.comment_edit_text)
        val secondsButton : Button = view.findViewById(R.id.seconds_button)


        var year = today.get(Calendar.YEAR)
        var month = today.get(Calendar.MONTH)
        var day = today.get(Calendar.DAY_OF_MONTH)
        var hourOfDay = today.get(Calendar.HOUR_OF_DAY)
        var minute = today.get(Calendar.MINUTE)
        var second = today.get(Calendar.SECOND)


        if(curEvent?.dateStamp != null){
            year = curEvent.dateStamp!!.get(Calendar.YEAR)
            month = curEvent.dateStamp!!.get(Calendar.MONTH)
            day = curEvent.dateStamp!!.get(Calendar.DAY_OF_MONTH)
            hourOfDay = curEvent.dateStamp!!.get(Calendar.HOUR_OF_DAY)
            minute = curEvent.dateStamp!!.get(Calendar.MINUTE)
            second = curEvent.dateStamp!!.get(Calendar.SECOND)
            commentEditText.setText(curEvent.comment)
            today.set(year, month, day, hourOfDay, minute, second)
        }

        newDateTextView.setText(dateFormat.format(today.time))

        okButton.setOnClickListener {

            if (curEvent == null) {
                listener.onAddNewEventDialogPositiveClickNewItem(EventEntity(curList.id, today, commentEditText.text.toString()))
            }else{
                curEvent.dateStamp = today
                curEvent.comment = commentEditText.text.toString()
                listener.onEditEventDialogPositiveClickNewItem(curEvent)
            }
            this.dismiss()
        }

        cancelButton.setOnClickListener{
            this.dismiss()
        }

        calendarButton.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this.requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, month, day ->
                        today.set(year, month, day, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE))
                        // Display Selected date in textbox
                        val dateTime: Date = today.getTime()
                        newDateTextView.setText(dateFormat.format(dateTime))

                    }, year, month, day)
            datePickerDialog.show()
        }

        timeButton.setOnClickListener{
            val datePickerDialog = TimePickerDialog(this.requireContext(),
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                                today.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
                        val dateTime: Date = today.getTime()
                        newDateTextView.setText(dateFormat.format(dateTime))
                    }, hourOfDay, minute, true)
            datePickerDialog.show()
        }

        secondsButton.setOnClickListener {
            val picker = NumberPicker(this.requireContext())
            picker.minValue = 0
            picker.maxValue = 59
            picker.value = second

            val layout = FrameLayout(this.requireContext())
            layout.addView(picker, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ))
            val secondsDialog = AlertDialog.Builder(this.requireContext())
                .setView(layout)
                .setPositiveButton(R.string.dialog_ok, DialogInterface.OnClickListener {
                        dialogInterface, i ->
                    today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.HOUR_OF_DAY),
                        today.get(Calendar.MINUTE), picker.value)
                    val dateTime: Date = today.getTime()
                    newDateTextView.setText(dateFormat.format(dateTime))

                })
                .setNegativeButton(R.string.dialog_cancel, null)
            secondsDialog.show()
        }


        return view
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
            throw ClassCastException((context.toString() +
                    " must implement NewEventDialogListener"))
        }
    }

    interface NewEventDialogListener {
        fun onAddNewEventDialogPositiveClickNewItem(newEvent: EventEntity)
        fun onEditEventDialogPositiveClickNewItem(newEvent: EventEntity)
    }
}