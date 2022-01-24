package com.example.binder.ui.calendar

import android.content.Context
import java.util.Calendar
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity

data class DaySchedule(
    val id: Long,
    val uid: String,
    val title: String,
    val startTime: Calendar,
    val endTime: Calendar
)

interface LoadMoreHandler {
    fun loadMore(startTime: Calendar, endTime: Calendar)
}

class DayScheduleAdapter(
    private val loadMoreHandler: LoadMoreHandler,
    private val dayViewClickListener: DayViewClickListener
) : WeekView.PagingAdapter<DaySchedule>() {

    override fun onCreateEntity(
        item: DaySchedule
    ): WeekViewEntity {
        return WeekViewEntity.Event.Builder(item)
            .setId(item.id)
            .setTitle(item.title)
            .setStartTime(item.startTime)
            .setEndTime(item.endTime)
            .build()
    }

    override fun onLoadMore(startDate: Calendar, endDate: Calendar) {
        loadMoreHandler.loadMore(startDate, endDate)
    }

    override fun onEventClick(data: DaySchedule) {
        super.onEventClick(data)
        dayViewClickListener.onScheduleClick(data)
    }

}

interface DayViewClickListener {
    fun onScheduleClick(data: DaySchedule)
}

