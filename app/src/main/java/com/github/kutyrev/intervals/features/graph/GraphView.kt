package com.github.kutyrev.intervals.features.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

private const val STROKE_WIDTH = 7f
private const val STROKE_WIDTH_LARGE = 10f

class GraphView(context: Context,
                attributeSet: AttributeSet
): View(context, attributeSet) {

    private val dataSet = mutableListOf<DataPoint>()
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    private val dataPointPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val dataPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    private val dataPointLinePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = STROKE_WIDTH
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = STROKE_WIDTH_LARGE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dataSet.forEachIndexed { index, currentDataPoint ->
            val realX = currentDataPoint.xVal.toRealX()
            val realY = currentDataPoint.yVal.toRealY()

            if (index < dataSet.size - 1) {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX()
                val startY = currentDataPoint.yVal.toRealY()
                val endX = nextDataPoint.xVal.toRealX()
                val endY = nextDataPoint.yVal.toRealY()
                canvas.drawLine(startX, startY, endX, endY, dataPointLinePaint)
            }

            canvas.drawCircle(realX, realY, STROKE_WIDTH, dataPointFillPaint)
            canvas.drawCircle(realX, realY, STROKE_WIDTH, dataPointPaint)
        }

        canvas.drawLine(0f, 0f, 0f, height.toFloat(), axisLinePaint)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), axisLinePaint)
    }

    fun setData(newDataSet: List<DataPoint>) {
        xMin = newDataSet.minByOrNull{ it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxByOrNull{ it.xVal }?.xVal ?: 0
        yMin = newDataSet.minByOrNull{ it.yVal }?.yVal ?: 0
        yMax = newDataSet.maxByOrNull{ it.yVal }?.yVal ?: 0
        dataSet.clear()
        dataSet.addAll(newDataSet)
        invalidate()
    }

    private fun Int.toRealX(): Float {
        var res = toFloat() / xMax * width
        when(res){
            0f -> res += STROKE_WIDTH_LARGE
            width.toFloat() -> res-= STROKE_WIDTH_LARGE
        }
        return res
    }
    private fun Int.toRealY(): Float {
        var res = height - toFloat() / yMax * height
        when(res){
            0f -> res += STROKE_WIDTH_LARGE
            height.toFloat() -> res -= STROKE_WIDTH_LARGE
        }
        return res
    }
}

data class DataPoint(
        val xVal: Int,
        val yVal: Int
)
