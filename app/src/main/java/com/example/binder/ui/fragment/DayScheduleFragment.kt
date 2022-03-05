package com.example.binder.ui.fragment

import java.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.alamkanak.weekview.WeekViewEntity
import com.alamkanak.weekview.WeekViewEntity.Event
import com.alamkanak.weekview.WeekViewEvent
import com.example.binder.databinding.LayoutDayScheduleFragmentBinding
import com.example.binder.ui.calendar.DaySchedule
import com.example.binder.ui.calendar.DayScheduleAdapter
import com.example.binder.ui.calendar.DayViewClickListener
import com.example.binder.ui.calendar.LoadMoreHandler
import data.CalendarEvent
import data.DayScheduleConfig
import data.ScheduleDisplayBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.DayScheduleFragmentViewModel
import viewmodel.MainActivityViewModel
import java.util.*

@Suppress("LongMethod", "ComplexMethod")
class DayScheduleFragment(override val config: DayScheduleConfig) : BaseFragment(), DayViewClickListener {

    private var binding: LayoutDayScheduleFragmentBinding? = null
    private val adapter: DayScheduleAdapter by lazy {
        DayScheduleAdapter(loadMoreHandler = object: LoadMoreHandler {
            override fun loadMore(startTime: Calendar, endTime: Calendar) {
                //
            }
        }, this)
    }

    override val viewModel: ViewModel by viewModel<DayScheduleFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutDayScheduleFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.weekView.adapter = adapter

            val day = config.day
            val month = config.month
            val year = config.year

            // convert passed date values into ms
            val startDateString = "$day-$month-$year | 00:00:00"
            val endDateString = "$day-$month-$year | 23:59:59"

            val formatter = SimpleDateFormat("d-M-yyyy | H:m:s", Locale.getDefault())
            val startDateInMillis = formatter.parse(startDateString).time
            val endDateInMillis = formatter.parse(endDateString).time

            // convert ms dates into calendars
            val dayStartCalendar = Calendar.getInstance()
            val dayEndCalendar = Calendar.getInstance()

            dayStartCalendar.timeInMillis = startDateInMillis
            dayEndCalendar.timeInMillis = endDateInMillis

            // set dayview to selected day
            binding.weekView.minDate = dayStartCalendar
            binding.weekView.maxDate = dayEndCalendar
            binding.weekView.showFirstDayOfWeekFirst = false

            // pass the relevant time frame to viewmodel
            (viewModel as? DayScheduleFragmentViewModel)?.updateScheduleForUser(
                uid = config.uid,
                startTime = startDateInMillis,
                endTime = endDateInMillis
            )

            // get relevant CalendarEvents
            (viewModel as? DayScheduleFragmentViewModel)?.getScheduleForUser()?.observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        adapter.submitList(it.data.mapIndexedNotNull { index, daySchedule ->
                            daySchedule.uid?.let { uid ->
                                val eventStart = Calendar.getInstance()
                                val eventEnd = Calendar.getInstance()
                                eventStart.timeInMillis = daySchedule.startTime
                                eventEnd.timeInMillis = daySchedule.endTime

                                if (daySchedule.recurringEvent == "Monthly") {
                                    if (startDateInMillis >= daySchedule.startTime
                                        && startDateInMillis <= daySchedule.recurringEnd!!
                                        && eventStart.get(Calendar.DAY_OF_MONTH)
                                        == dayStartCalendar.get(Calendar.DAY_OF_MONTH)
                                    ) {
                                        eventStart.set(Calendar.DAY_OF_YEAR, dayStartCalendar.get(Calendar.DAY_OF_YEAR))
                                        eventEnd.set(Calendar.DAY_OF_YEAR, dayEndCalendar.get(Calendar.DAY_OF_YEAR))
                                    }

                                } else if (daySchedule.recurringEvent == "Weekly") {
                                    if (startDateInMillis >= daySchedule.startTime
                                        && startDateInMillis <= daySchedule.recurringEnd!!
                                        && eventStart.get(Calendar.DAY_OF_WEEK) == dayStartCalendar.get(
                                            Calendar.DAY_OF_WEEK)
                                    ) {
                                        eventStart.set(Calendar.DAY_OF_YEAR, dayStartCalendar.get(Calendar.DAY_OF_YEAR))
                                        eventEnd.set(Calendar.DAY_OF_YEAR, dayEndCalendar.get(Calendar.DAY_OF_YEAR))
                                    }

                                } else if (daySchedule.recurringEvent == "Daily") {
                                    if (startDateInMillis >= daySchedule.startTime
                                        && startDateInMillis <= daySchedule.recurringEnd!!
                                    ) {
                                        eventStart.set(Calendar.DAY_OF_YEAR, dayStartCalendar.get(Calendar.DAY_OF_YEAR))
                                        eventEnd.set(Calendar.DAY_OF_YEAR, dayEndCalendar.get(Calendar.DAY_OF_YEAR))
                                    }
                                }

                                DaySchedule(
                                    index.toLong(),
                                    uid,
                                    daySchedule.name,
                                    eventStart,
                                    eventEnd,
                                    recurring = daySchedule.recurringEvent.toString(),
                                    recurringEnd = daySchedule.recurringEnd,
                                    cid = daySchedule.uid
                                )
                            }
                        })
                    }
                }
            }
        }
    }

    override fun onScheduleClick(data: DaySchedule) {
        mainActivityViewModel.postNavigation(ScheduleDisplayBottomSheetConfig(config.name, config.uid, CalendarEvent(
            data.title,
            data.startTime.timeInMillis,
            data.endTime.timeInMillis,
            recurringEvent = data.recurring,
            recurringEnd = data.recurringEnd,
            uid = data.cid
            ),
        config.isGroupOwner
        ))
    }

}
