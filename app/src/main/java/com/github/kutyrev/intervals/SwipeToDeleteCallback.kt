package com.github.kutyrev.intervals

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeToDeleteCallback(private val listener: OnSwipeDeleteListener, context : Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val background = ColorDrawable(0xffff6666.toInt())
    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position: Int = viewHolder.adapterPosition
        listener.onSwipeDelete(position)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset: Int = 20
        var iconMargin: Int = 0
        var iconTop: Int = 0
        var iconBottom: Int = 0

        if (deleteIcon != null) {
            iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
            iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
            iconBottom = iconTop + deleteIcon.intrinsicHeight
        }

        when {
            dX > 0 -> { // Swiping to the right
                if (deleteIcon != null) {
                    val iconRight: Int = itemView.left + iconMargin + deleteIcon.intrinsicWidth
                    val iconLeft: Int = itemView.left + iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                background.setBounds(itemView.left, itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset,
                        itemView.bottom)

            }
            dX < 0 -> { // Swiping to the left

                if (deleteIcon != null) {
                    val iconLeft: Int = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom)
            }
            else -> { // view is unSwiped
                background.setBounds(0, 0, 0, 0)
            }
        }
        background.draw(c)
        deleteIcon?.draw(c)
    }

    interface OnSwipeDeleteListener {
        fun onSwipeDelete(position : Int)
    }

}