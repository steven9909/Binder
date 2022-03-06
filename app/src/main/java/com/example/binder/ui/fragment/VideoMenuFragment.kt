package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.api.HmsAuthTokenApi
import com.example.binder.ui.api.TokenRequestBody
import com.example.binder.ui.calendar.DaySchedule
import com.example.binder.ui.viewholder.ScheduledCallItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.VideoConfig
import data.VideoPlayerConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit
import timber.log.Timber
import viewmodel.MainActivityViewModel
import viewmodel.VideoMenuFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*

class VideoMenuFragment(override val config: VideoConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<VideoMenuFragmentViewModel>()

    private var binding: LayoutVideoMenuFragmentBinding? = null

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            Unit
        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            Unit
        }
    }

    private val viewHolderFactory: ViewHolderFactory by inject()
    private lateinit var genericListAdapter: GenericListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoMenuFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->

            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.scheduledCallList.layoutManager = LinearLayoutManager(context)
            binding.scheduledCallList.adapter = genericListAdapter


            val day = "1".toInt()
            val month = "3".toInt()
            val year = "2022".toInt()

            // convert passed date values into ms
            val startDateString = "$day-$month-$year | 00:00:00"
            val endDateString = "$day-$month-$year | 23:59:59"

            val formatter = SimpleDateFormat("d-M-yyyy | H:m:s", Locale.getDefault())
            val startDateInMillis = formatter.parse(startDateString).time
            val endDateInMillis = formatter.parse(endDateString).time

            (viewModel as? VideoMenuFragmentViewModel)?.updateScheduleForUser(
                uid = config.uid,
                startTime = startDateInMillis,
                endTime = endDateInMillis
            )

            (viewModel as? VideoMenuFragmentViewModel)?.getScheduleForUser()?.observe(viewLifecycleOwner){
                if (it.status == Status.SUCCESS && it.data != null) {
                    val eventStart = Calendar.getInstance()
                    val eventEnd = Calendar.getInstance()
                    genericListAdapter.submitList(it.data.mapIndexedNotNull { index, daySchedule ->

                            val eventStart = Calendar.getInstance()
                            val eventEnd = Calendar.getInstance()
                            eventStart.timeInMillis = it.data[index].startTime
                            eventEnd.timeInMillis = it.data[index].endTime
                            Timber.d("VideoMenuFragment: ${it.data[index]}")

                            ScheduledCallItem(
                                index.toLong(),
                                config.uid,
                                it.data[index].name,
                                eventStart.time,
                                eventEnd.time,
                            )

                    })

                }
            }

        }
    }
}
