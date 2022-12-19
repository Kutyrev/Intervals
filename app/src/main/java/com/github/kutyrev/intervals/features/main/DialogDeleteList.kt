package com.github.kutyrev.intervals.features.main

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.datasource.database.ListEntity

class DialogDeleteList(
    private val curList: ListEntity,
    private val position: Int,
    private val listener: DeleteListDialogListener
) : DialogFragment() {

    // Use this instance of the interface to deliver action events
    // internal lateinit var listener: DialogDeleteList.DeleteListDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.delete_list_dialog_confirm, curList.name))
                .setPositiveButton(
                    R.string.dialog_ok
                ) { dialog, id ->
                    listener.onDeleteListDialogPositiveClick(curList)
                }
                .setNegativeButton(
                    R.string.dialog_cancel
                ) { dialog, id ->
                    listener.onDeleteListDialogNegativeClick(curList, position)
                    dialog.cancel()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the DeleteListDialogListener
/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as DeleteListDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement DeleteListDialogListener"))
        }
    }*/

    interface DeleteListDialogListener {
        fun onDeleteListDialogPositiveClick(curList: ListEntity)
        fun onDeleteListDialogNegativeClick(curList: ListEntity, position: Int)
    }
}