package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutChatMoreOptionsBottomSheetFragmentBinding
import com.example.binder.databinding.LayoutVideoUsersBottomSheetFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.MainActivity
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.CalendarConfig
import data.ChatMoreOptionsBottomSheetConfig
import data.InputScheduleBottomSheetConfig
import data.ScheduleDisplayBottomSheetConfig
import data.VideoUserBottomSheetConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import viewmodel.ChatMoreOptionsBottomSheetViewModel
import viewmodel.MainActivityViewModel
import viewmodel.SharedVideoPlayerViewModel
import viewmodel.VideoUserBottomSheetViewModel

class ChatMoreOptionBottomSheetFragment(override val config: ChatMoreOptionsBottomSheetConfig) : BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<ChatMoreOptionsBottomSheetViewModel>()

    private var binding: LayoutChatMoreOptionsBottomSheetFragmentBinding? = null

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {

        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            Unit
        }
    }


    private lateinit var genericListAdapter: GenericListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutChatMoreOptionsBottomSheetFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
    private fun setUpUi() {
        binding?.let { binding ->
            binding.ScheduleCallButton.setOnClickListener{
                mainActivityViewModel.postNavigation(InputScheduleBottomSheetConfig(config.guid))
            }
            binding.GroupCalendarButton.setOnClickListener{
                mainActivityViewModel.postNavigation(CalendarConfig(
                    config.name,
                    config.guid,
                    shouldOpenInStaticSheet = true
                )
                )
            }
        }
    }
}