package com.example.todo.calendarview

import android.content.Context
import android.graphics.Canvas
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekView

class WeekView(context:Context) : WeekView(context){
    override fun onDrawSelected(
        canvas: Canvas?,
        calendar: Calendar?,
        x: Int,
        hasScheme: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDrawScheme(canvas: Canvas?, calendar: Calendar?, x: Int) {
        TODO("Not yet implemented")
    }

    override fun onDrawText(
        canvas: Canvas?,
        calendar: Calendar?,
        x: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        TODO("Not yet implemented")
    }
}