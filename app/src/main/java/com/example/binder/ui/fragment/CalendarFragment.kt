package com.example.binder.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.binder.R
import com.example.binder.databinding.LayoutCalendarFragmentBinding
import com.example.binder.ui.calendar.DayViewContainer
import com.example.binder.ui.calendar.MonthViewContainer
import com.example.binder.ui.calendar.OnDayViewClickListener
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import data.AddFriendConfig
import data.CalendarConfig
import data.CalendarEvent
import data.DayScheduleConfig
import data.InputScheduleBottomSheetConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.AbstractFileFilter
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.filetype.FileType
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import setVisibility
import timber.log.Timber
import transformer.ICSParser
import viewmodel.CalendarFragmentViewModel
import viewmodel.DayScheduleFragmentViewModel
import viewmodel.ChatFragmentViewModel
import viewmodel.MainActivityViewModel
import java.text.SimpleDateFormat
import java.io.File
import java.time.DayOfWeek
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment(override val config: CalendarConfig) : BaseFragment(), OnDayViewClickListener{

    private var binding: LayoutCalendarFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<CalendarFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    companion object {
        private const val MONTH_RANGE = 12L
    }

    private var currentYear: Int? = null
    private var currentMonth: Int? = null

    private val eventMap = mutableMapOf<Int, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutCalendarFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.convertIcsButton.setOnClickListener {
                FilePickerManager
                    .from(this)
                    .enableSingleChoice()
                    .forResult(FilePickerManager.REQUEST_CODE)
            }

            currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            currentYear = Calendar.getInstance().get(Calendar.YEAR)

            // set start and end of month dates to get relevant events from firebase
            val monthStart = Calendar.getInstance()
            monthStart.set(Calendar.DAY_OF_MONTH, 1)
            monthStart.set(Calendar.HOUR_OF_DAY, 0)
            monthStart.set(Calendar.MINUTE, 0)
            monthStart.set(Calendar.SECOND, 0)
            val monthEnd = Calendar.getInstance()
            monthEnd.set(currentYear!!,
                currentMonth!!, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)

            // use result data; by the event (start) time, sort the events by day_of_month
            (viewModel as? CalendarFragmentViewModel)?.getSchedule()?.observe(viewLifecycleOwner) { result ->
                when {
                    (result.status == Status.SUCCESS && result.data != null) -> {
                        eventMap.clear()
                        for (event in result.data) {
                            if (event.recurringEvent == null) {
                                val eventTime = Calendar.getInstance()
                                eventTime.timeInMillis = event.startTime
                                val dayOfMonth = eventTime.get(Calendar.DAY_OF_MONTH)
                                eventMap[dayOfMonth] = true
                            }
                            else {
                                //TODO: handle recurring event
                            }
                        }
                        binding.calendarView.notifyMonthChanged(YearMonth.of(currentYear!!, currentMonth!! + 1))
                    }
                }
            }

            binding.calendarView.monthScrollListener = {
                binding.calendarMonthYear.text = "%s %s".format(monthTitleFormatter.format(it.yearMonth), it.yearMonth.year.toString())
                eventMap.clear()
                binding.calendarView.notifyMonthChanged(YearMonth.of(currentYear!!, currentMonth!! + 1))
                currentYear = it.year
                currentMonth = it.month - 1

                monthStart.set(it.year, it.month - 1, 1, 0, 0, 0)
                monthEnd.set(it.year, it.month - 1, 1, 23, 59, 59)
                monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

                (viewModel as? CalendarFragmentViewModel)?.updateSchedule(
                    startTime = monthStart.timeInMillis,
                    endTime = monthEnd.timeInMillis
                )
            }

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            binding.calendarView.dayBinder = object: DayBinder<DayViewContainer> {
                override fun bind(container: DayViewContainer, day: CalendarDay) {

                    container.day = day
                    container.binding.calendarDayText.text = day.date.dayOfMonth.toString()
                    if (day.owner != DayOwner.THIS_MONTH) {
                        container.binding.calendarDayText.setTextColor(Color.GRAY)
                    } else if (day.date.dayOfWeek == DayOfWeek.SATURDAY || day.date.dayOfWeek == DayOfWeek.SUNDAY){
                        container.binding.calendarDayText.setTextColor(requireContext().getColor(R.color.app_yellow))
                    } else {
                        container.binding.calendarDayText.setTextColor(Color.WHITE)
                    }

                    if (day.owner == DayOwner.THIS_MONTH && eventMap[day.date.dayOfMonth] == true) {
                        container.binding.eventExists.setVisibility(View.VISIBLE)
                    } else if (day.owner == DayOwner.THIS_MONTH && eventMap[day.date.dayOfMonth] == false) {
                        container.binding.eventExists.setVisibility(View.GONE)
                    }
                }
                override fun create(view: View): DayViewContainer {
                    return DayViewContainer(view, this@CalendarFragment)
                }
            }
            binding.calendarView.scrollMode = ScrollMode.PAGED

            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(MONTH_RANGE)
            val lastMonth = currentMonth.plusMonths(MONTH_RANGE)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
            binding.calendarView.scrollToMonth(currentMonth)

            binding.addScheduleButton.setOnClickListener {
                mainActivityViewModel.postNavigation(InputScheduleBottomSheetConfig())
            }

            (viewModel as? CalendarFragmentViewModel)?.getBatchCalendarEvents()?.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    Unit
                }
            }
        }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerManager.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val list = FilePickerManager.obtainData()
            if (list.size == 1) {
                val file = File(list[0])
                lifecycleScope.launch(Dispatchers.IO) {
                    val parser = ICSParser
                    try {
                        val calendarEvents = parser.parse(file.inputStream())
                        (viewModel as? CalendarFragmentViewModel)?.setBatchCalendarEvents(calendarEvents)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }

        }
    }

    override fun onDayViewClicked(day: CalendarDay) {
        mainActivityViewModel.postNavigation(DayScheduleConfig(day.date.month.value, day.date.dayOfMonth, day.date.year))
    }

}
