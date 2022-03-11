package com.example.binder.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutVideoUsersBottomSheetFragmentBinding
import com.example.binder.databinding.LayoutViewRecordingBottomSheetFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.RecordingLinkItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import com.example.binder.ui.viewholder.ViewRecordingViewHolder
import data.ViewRecordingBottomSheetConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.SharedVideoPlayerViewModel
import viewmodel.VideoUserBottomSheetViewModel
import viewmodel.ViewRecordingBottomSheetViewModel

class ViewRecordingBottomSheetFragment(override val config: ViewRecordingBottomSheetConfig) : BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<ViewRecordingBottomSheetViewModel>()

    private var binding: LayoutViewRecordingBottomSheetFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            (item as? RecordingLinkItem)?.let {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.hyperLink))
                startActivity(browserIntent)
            }
        }
    }


    private lateinit var genericListAdapter: GenericListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutViewRecordingBottomSheetFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    private fun setUpUi() {
        binding?.let { binding ->

            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.recordingList.layoutManager = LinearLayoutManager(context)
            binding.recordingList.adapter = genericListAdapter

            genericListAdapter.submitList(config.links)

        }
    }
}