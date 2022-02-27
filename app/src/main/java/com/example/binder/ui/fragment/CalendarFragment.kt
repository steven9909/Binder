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
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import data.AddFriendConfig
import data.CalendarConfig
import data.CalendarEvent
import data.CalendarSelectConfig
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
import repository.FirebaseRepository
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

@Suppress("MagicNumber", "LongMethod", "ComplexMethod")
class CalendarFragment(override val config: CalendarConfig) : BaseFragment(), OnDayViewClickListener{

    private var binding: LayoutCalendarFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<CalendarFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    private val isCurrentUser = (config.uid == FirebaseAuth.getInstance().uid)

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
            (viewModel as? CalendarFragmentViewModel)?.getScheduleForUser()?.observe(viewLifecycleOwner) { result ->
                when {
                    (result.status == Status.SUCCESS && result.data != null) -> {
                        Timber.d("CalendarFragment: current month: ${currentMonth}, current year: ${currentYear}")
                        eventMap.clear()
                        for (event in result.data) {
                            val eventTime = Calendar.getInstance()
                            eventTime.timeInMillis = event.startTime
                            if (event.recurringEvent == null) {
                                val dayOfMonth = eventTime.get(Calendar.DAY_OF_MONTH)
                                eventMap[dayOfMonth] = true
                            }
                            else if (event.recurringEvent == "Daily") {
                                // not really implemented fully,
                                    // maybe could use a bar on the calendar for a series of days?
                                val dayOfMonth = eventTime.get(Calendar.DAY_OF_MONTH)
                                eventMap[dayOfMonth] = true
                            }
                            else if (event.recurringEvent == "Weekly") {
                                val week = eventTime.get(Calendar.DAY_OF_WEEK)
                                val temp = Calendar.getInstance()
                                temp.set(Calendar.MONTH, currentMonth!!)
                                temp.set(Calendar.DAY_OF_MONTH, 1)
                                var day = 1
                                for (i in 1..temp.getActualMaximum(Calendar.DAY_OF_MONTH)+1) {
                                    val dayOfWeek = temp.get(Calendar.DAY_OF_WEEK)
                                    if (dayOfWeek == week) {
                                        day = temp.get(Calendar.DAY_OF_MONTH)
                                        break
                                    }
                                    temp.add(Calendar.DAY_OF_MONTH, 1)
                                }
                                eventTime.set(Calendar.HOUR_OF_DAY, 0)
                                eventTime.set(Calendar.MINUTE, 0)
                                eventTime.set(Calendar.SECOND, 0)
                                for (i in day..temp.getActualMaximum(Calendar.DAY_OF_MONTH)+1 step 7) {
                                    temp.set(Calendar.DAY_OF_MONTH, i)
                                    if (temp.timeInMillis >= eventTime.timeInMillis
                                        && temp.timeInMillis <= event.recurringEnd!!) {
                                        eventMap[i] = true
                                    }
                                }
                            }
                            else if (event.recurringEvent == "Monthly") {
                                eventTime.set(Calendar.MONTH, currentMonth!!)
                                if (eventTime.timeInMillis >= event.startTime
                                    && eventTime.timeInMillis <= event.recurringEnd!!) {
                                    eventMap[eventTime.get(Calendar.DAY_OF_MONTH)] = true
                                }
                            }
                        }
                        binding.calendarView.notifyMonthChanged(YearMonth.of(currentYear!!, currentMonth!! + 1))
                    }
                }
            }

            binding.calendarView.monthScrollListener = {
                binding.calendarMonthYear.text = "%s %s".format(
                    monthTitleFormatter.format(it.yearMonth),
                    it.yearMonth.year.toString()
                )
                currentYear = it.year
                currentMonth = it.month - 1
                Timber.d("CalendarFragment: current month: ${currentMonth}, current year: ${currentYear}")

                monthStart.set(it.year, it.month - 1, 1, 0, 0, 0)
                monthEnd.set(it.year, it.month - 1, 1, 23, 59, 59)
                monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

                (viewModel as? CalendarFragmentViewModel)?.updateScheduleForUser(
                    uid = config.uid,
                    startTime = monthStart.timeInMillis,
                    endTime = monthEnd.timeInMillis
                )
            }
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
                        container.binding.eventExists.visibility = View.VISIBLE
                    } else {
                        container.binding.eventExists.visibility = View.GONE
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

            binding.userOfCalendar.text = "${config.name}'s Calendar"

            if (isCurrentUser || config.isGroupOwner) {
                binding.addScheduleButton.setOnClickListener {
                    mainActivityViewModel.postNavigation(InputScheduleBottomSheetConfig())
                }

                binding.convertIcsButton.setOnClickListener {
                    FilePickerManager
                        .from(this)
                        .enableSingleChoice()
                        .forResult(FilePickerManager.REQUEST_CODE)
                }

                (viewModel as? CalendarFragmentViewModel)?.getBatchCalendarEvents()?.observe(viewLifecycleOwner) {
                    if (it.status == Status.SUCCESS) {
                        Unit
                    }
                }
            } else {
                binding.addScheduleButton.isEnabled = false
                binding.addScheduleButton.visibility = View.GONE
                binding.convertIcsButton.isEnabled = false
                binding.convertIcsButton.visibility = View.GONE
            }

            if (isCurrentUser) {
                binding.changeCalendarButton.setOnClickListener {
                    mainActivityViewModel.postNavigation(
                        CalendarSelectConfig(
                            config.name,
                            config.uid,
                            shouldOpenInStaticSheet = true
                        )
                    )
                }
            } else {
                binding.changeCalendarButton.isEnabled = false
                binding.changeCalendarButton.visibility = View.GONE
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
        mainActivityViewModel.postNavigation(DayScheduleConfig(
            config.name,
            config.uid,
            day.date.month.value,
            day.date.dayOfMonth,
            day.date.year,
            config.isGroupOwner)
        )
    }

}
