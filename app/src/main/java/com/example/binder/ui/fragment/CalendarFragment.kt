package com.example.binder.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
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
import data.DayScheduleConfig
import data.InputScheduleBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.CalendarFragmentViewModel
import viewmodel.MainActivityViewModel
import java.time.DayOfWeek
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment(override val config: CalendarConfig) : BaseFragment(), OnDayViewClickListener{

    private var binding: LayoutCalendarFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<CalendarFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    companion object {
        private const val MONTH_RANGE = 12L
    }

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
            binding.calendarView.monthScrollListener = {
                binding.calendarMonthYear.text = "%s %s".format(monthTitleFormatter.format(it.yearMonth), it.yearMonth.year.toString())
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

        }


    }

    override fun onDayViewClicked(day: CalendarDay) {
        mainActivityViewModel.postNavigation(DayScheduleConfig(day.date.month.value, day.date.dayOfMonth, day.date.year))
    }

}
