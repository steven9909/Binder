package com.example.binder.ui.calendar

import android.view.View
import com.example.binder.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val binding: CalendarDayLayoutBinding by lazy {
        CalendarDayLayoutBinding.bind(view)
    }
}