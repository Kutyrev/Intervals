package com.github.kutyrev.intervals

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.kutyrev.intervals.database.ListEntity

class DialogDeleteList(val curList : ListEntity, val position : Int, val listener : DeleteListDialogListener):DialogFragment() {

    // Use this instance of the interface to deliver action events
   // internal lateinit var listener: DialogDeleteList.DeleteListDialogListener


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Delete list " + curList.name + "?")
                    .setPositiveButton(R.string.dialog_ok,
                            DialogInterface.OnClickListener { dialog, id ->
                            listener.onDeleteListDialogPositiveClick(curList)
                            })
                    .setNegativeButton(R.string.dialog_cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                listener.onDeleteListDialogNegativeClick(curList, position)
                                dialog.cancel()
                            })
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
        fun onDeleteListDialogNegativeClick(curList: ListEntity, position : Int)
    }


}