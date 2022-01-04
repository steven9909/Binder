package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutChatFragmentBinding
import com.example.binder.ui.viewholder.MessageTitleItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.Config
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.ChatFragmentViewModel

class ChatFragment(override val config: Config) : BaseFragment() {
    private var binding: LayoutChatFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<ChatFragmentViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val listAdapter: ListAdapter = ListAdapter(viewHolderFactory, object: OnActionListener {

    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutChatFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.chatRecycler.layoutManager = LinearLayoutManager(context)
            binding.chatRecycler.adapter = listAdapter
        }
    }
}