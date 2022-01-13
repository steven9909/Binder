package com.example.binder.ui.fragment

import java.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutDayScheduleFragmentBinding
import com.example.binder.ui.calendar.DayScheduleAdapter
import com.example.binder.ui.calendar.LoadMoreHandler
import data.DayScheduleConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.DayScheduleFragmentViewModel
import java.util.*

class DayScheduleFragment(override val config: DayScheduleConfig) : BaseFragment() {

    private var binding: LayoutDayScheduleFragmentBinding? = null
    private val adapter: DayScheduleAdapter by lazy {
        DayScheduleAdapter(loadMoreHandler = object: LoadMoreHandler {
            override fun loadMore(startTime: Calendar, endTime: Calendar) {
                Unit
            }
        })
    }

    override val viewModel: ViewModel by viewModel<DayScheduleFragmentViewModel>()

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

            val formatter = SimpleDateFormat("dd-MM-yyyy | HH:mm:ss", Locale.getDefault())
            val day = config.day
            val month = config.month
            val year = config.year
            val startDate = "$day-$month-$year | 00:00:00"
            val endDate = ""

            (viewmodel as? DayScheduleFragmentViewModel)?.updateSchedule(startTime = c)
            (viewModel as? DayScheduleFragmentViewModel)?.getSchedule()?.observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        adapter.submitList(it.data.mapNotNull {

                        })
                    }
                }
            }
        }
    }

}
