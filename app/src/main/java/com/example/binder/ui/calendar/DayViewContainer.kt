package com.example.binder.ui.calendar

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import data.DayScheduleConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.CalendarFragmentViewModel
import viewmodel.DayViewContainerViewModel
import viewmodel.MainActivityViewModel

class DayViewContainer(view: View, listener: OnDayViewClickListener) : ViewContainer(view) {

    val binding: CalendarDayLayoutBinding by lazy {
        CalendarDayLayoutBinding.bind(view)
    }

    lateinit var day: CalendarDay
    init {
        view.setOnClickListener {
            listener.onDayViewClicked(day)
        }
    }
}

interface OnDayViewClickListener {
    fun onDayViewClicked(day: CalendarDay)
}
