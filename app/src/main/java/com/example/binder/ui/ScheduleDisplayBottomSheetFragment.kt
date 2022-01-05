package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutScheduleDisplayBottomSheetFragmentBinding
import data.ScheduleDisplayBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ScheduleDisplayBottomSheetFragment(override val config: ScheduleDisplayBottomSheetConfig): BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<ScheduleDisplayBottomSheetViewModel>()

    private var binding: LayoutScheduleDisplayBottomSheetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutScheduleDisplayBottomSheetFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    fun Timestamp.toUtcDate(): Date {
        return this.toDate()
    }

    fun setUpUi() {
        binding?.let { binding ->
            binding.scheduleDisplayTitle.text = config.calendarEvent.name
            val formatter = SimpleDateFormat("MM dd yyyy   HH:mm:ss", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            val startDate = config.calendarEvent.startTime.toUtcDate()
            val endDate = config.calendarEvent.endTime.toUtcDate()
            binding.scheduleDisplayStartDate.text = formatter.format(startDate)
            binding.scheduleDisplayEndDate.text = formatter.format(endDate)
            binding.scheduleDisplayEta.text = requireContext()
                .getString(R.string.schedule_display_eta)
                .format(config.calendarEvent.minutesBefore.toString())
            binding.scheduleDisplayOccurrence.text = config.calendarEvent.recurringEvent
        }
    }

}