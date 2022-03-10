package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.work.ListenableWorker
import androidx.work.Operation
import com.example.binder.R
import com.example.binder.databinding.LayoutScheduleDisplayBottomSheetFragmentBinding
import data.ScheduleDisplayBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import data.InputScheduleBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import viewmodel.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

class ScheduleDisplayBottomSheetFragment(override val config: ScheduleDisplayBottomSheetConfig):
    BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<ScheduleDisplayBottomSheetViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutScheduleDisplayBottomSheetFragmentBinding? = null

    private val isCurrentUser = (config.uid == FirebaseAuth.getInstance().uid)

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
            val formatter = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            val startDate = config.calendarEvent.startTime
            val endDate = config.calendarEvent.endTime

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = startDate
            binding.scheduleDisplayTitle.text = config.calendarEvent.name
            binding.scheduleDisplayStartDate.text = requireContext()
                .getString(R.string.schedule_display_begin)
                .format(formatter.format(calendar.time))

            calendar.timeInMillis = endDate
            binding.scheduleDisplayEndDate.text = requireContext()
                .getString(R.string.schedule_display_end)
                .format(formatter.format(calendar.time))

            binding.scheduleDisplayEta.text = requireContext()
                .getString(R.string.schedule_display_eta)
                .format(config.calendarEvent.minutesBefore.toString())

            if (config.calendarEvent.recurringEvent != "null") {
                binding.scheduleDisplayOccurrence.text = config.calendarEvent.recurringEvent
            }

            if (isCurrentUser || config.isGroupOwner) {

                binding.scheduleDisplayEdit.setOnClickListener {
                    mainActivityViewModel.postNavigation(InputScheduleBottomSheetConfig(config.uid,
                        config.calendarEvent))
                }

                binding.scheduleDisplayDelete.setOnClickListener {
                    mainActivityViewModel.postLoadingScreenState(true)
                    config.calendarEvent.uid?.let { it1 ->
                        (viewModel as ScheduleDisplayBottomSheetViewModel)
                            .deleteEvent(config.uid, it1)
                    }
                }

                (viewModel as? ScheduleDisplayBottomSheetViewModel)?.getScheduleToDelete()?.observe(
                    viewLifecycleOwner) {
                    if (it.status == Status.SUCCESS) {
                        dismiss()
                    }
                }

            } else {
                binding.scheduleDisplayDelete.isEnabled = false
                binding.scheduleDisplayDelete.visibility = View.GONE
                binding.scheduleDisplayEdit.isEnabled= false
                binding.scheduleDisplayEdit.visibility = View.GONE
            }
        }
    }

}
