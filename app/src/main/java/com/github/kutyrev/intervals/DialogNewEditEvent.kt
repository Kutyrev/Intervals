package com.github.kutyrev.intervals

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.github.kutyrev.intervals.database.EventEntity
import com.github.kutyrev.intervals.database.ListEntity
import java.text.SimpleDateFormat
import java.util.*


class DialogNewEditEvent(val curList: ListEntity, val curEvent : EventEntity? = null ) : DialogFragment()  {

    var today = Calendar.getInstance()
    internal lateinit var listener: NewEventDialogListener
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_round_corner);

        val view: View = inflater.inflate(R.layout.dialog_new_event, container, false)

        val cancelButton: Button = view.findViewById(R.id.dialog_button_cancel)
        val okButton: Button = view.findViewById(R.id.dialog_button_ok)
        val newDateTextView : TextView = view.findViewById(R.id.new_date_textview)
        val calendarButton: Button = view.findViewById(R.id.show_cal_button)
        val timeButton: Button = view.findViewById(R.id.show_time_button)
        val commentEditText : EditText = view.findViewById(R.id.comment_edit_text)


        var year = today.get(Calendar.YEAR)
        var month = today.get(Calendar.MONTH)
        var day = today.get(Calendar.DAY_OF_MONTH)
        var hourOfDay = today.get(Calendar.HOUR_OF_DAY)
        var minute = today.get(Calendar.MINUTE)

        if(curEvent?.dateStamp != null){
            year = curEvent.dateStamp!!.get(Calendar.YEAR)
            month = curEvent.dateStamp!!.get(Calendar.MONTH)
            day = curEvent.dateStamp!!.get(Calendar.DAY_OF_MONTH)
            hourOfDay = curEvent.dateStamp!!.get(Calendar.HOUR_OF_DAY)
            minute = curEvent.dateStamp!!.get(Calendar.MINUTE)
            commentEditText.setText(curEvent.comment)
        }

        val dateTime: Date = today.getTime()
        newDateTextView.setText(dateFormat.format(dateTime))

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
            val datePickerDialog : DatePickerDialog  = DatePickerDialog(this.requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, month, day ->
                        today.set(year, month, day, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE))
                        // Display Selected date in textbox
                        val dateTime: Date = today.getTime()
                        newDateTextView.setText(dateFormat.format(dateTime))

                    }, year, month, day)
            datePickerDialog.show();
        }

        timeButton.setOnClickListener{
            val datePickerDialog : TimePickerDialog  = TimePickerDialog(this.requireContext(),
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                                today.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
                        val dateTime: Date = today.getTime()
                        newDateTextView.setText(dateFormat.format(dateTime))
                    }, hourOfDay, minute, true)
            datePickerDialog.show();
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