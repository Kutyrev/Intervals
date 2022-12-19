package com.github.kutyrev.intervals.features.editing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.datasource.database.ListEntity

class DialogNewEditList(private val curList: ListEntity, private val isNew: Boolean) : DialogFragment() {

    // Use this instance of the interface to deliver action events
    internal lateinit var listener: NewEditListDialogListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.dialog_add_edit_list, container, false)

        val cancelButton: Button = view.findViewById(R.id.dialog_button_cancel)
        val okButton: Button = view.findViewById(R.id.dialog_button_ok)
        val labelEditText: EditText = view.findViewById(R.id.label_edit_text)
        val withoutSeconds : SwitchCompat = view.findViewById(R.id.checkbox_without_seconds)

        labelEditText.setText(curList.name)
        withoutSeconds.isChecked = curList.withoutSeconds

        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_round_corner)

        cancelButton.setOnClickListener{
            dialog?.cancel()
        }

        okButton.setOnClickListener {
                curList.name =  labelEditText.text.toString()
                curList.withoutSeconds = withoutSeconds.isChecked
                if (isNew) {
                    listener.onDialogPositiveClickNewEditList(curList, isNew)
                }else{
                    listener.onDialogPositiveClickNewEditList(curList, isNew)
                }
                dismiss()

        }

        return view
    }

    // Override the Fragment.onAttach() method to instantiate the NewEditListDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
           // listener = context as NewEditListDialogListener
            listener = this.parentFragment as NewEditListDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NewEditListDialogListener"))
        }
    }

    interface NewEditListDialogListener {
        fun onDialogPositiveClickNewEditList(curList: ListEntity, isNew: Boolean)
    }
}