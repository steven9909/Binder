package com.example.binder.ui.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binder.R
import com.example.binder.databinding.LayoutChatFragmentBinding
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.MessageItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import com.google.firebase.Timestamp
import data.ChatConfig
import data.Message
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.ChatFragmentViewModel

class ChatFragment(override val config: ChatConfig) : BaseFragment() {

    companion object {
        private const val VERTICAL_SPACING = 10
    }

    override val viewModel: ViewModel by viewModel<ChatFragmentViewModel>()

    private var binding: LayoutChatFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val listener = object: OnActionListener {
        override fun onDeleteRequested(index: Int) {
            listAdapter.deleteItemAt(index)
        }
    }

    private val listAdapter: ListAdapter = ListAdapter(viewHolderFactory, listener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutChatFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    @SuppressWarnings("LongMethod", "ComplexCondition")
    private fun setUpUi() {
        binding?.let { binding ->
            binding.chatRecycler.layoutManager = LinearLayoutManager(context)
            binding.chatRecycler.adapter = listAdapter
            binding.chatRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            binding.nameText.text = SpannableStringBuilder().apply {
                val nameText = SpannableString(config.chatName)
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }

            lifecycleScope.launch {
                (viewModel as ChatFragmentViewModel).messageGetterFlow(config.guid).collect {
                    val sendingId = it[0]?.first as? String
                    val msg = it[0]?.second as? String
                    val timestamp = it[1]?.first as? Long
                    val read = it[1]?.second as? Boolean
                    if (sendingId != null && msg != null && timestamp != null && read != null) {
                        listAdapter.insertItemEnd(MessageItem(msg, sendingId == config.uid, timestamp, read))
                    }
                    binding.chatRecycler.scrollToPosition(listAdapter.itemCount - 1)
                }
            }

            binding.sendButton.setOnClickListener {
                if (binding.messageBox.text.isNotBlank()) {
                    (viewModel as ChatFragmentViewModel).messageSend(
                        Message(
                            config.uid,
                            binding.messageBox.text.toString(),
                            timestampToMS(Timestamp.now())
                        ),
                        config.guid)
                    binding.messageBox.text.clear()
                }
            }

            binding.chatRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        Timber.d("ChatFragment: Getting More Messages")
                        (viewModel as ChatFragmentViewModel).getMoreMessages(
                            config.guid,
                            (listAdapter.getItem(0) as MessageItem).timestamp
                        )
                    } else {
                        Unit
                    }
                }
            })

            (viewModel as ChatFragmentViewModel).getMoreMessagesData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    val list = mutableListOf<MessageItem>()
                    it.data?.forEach { message ->
                        list.add(MessageItem(
                            message.msg,
                            message.sendingId == config.uid,
                            message.timestamp,
                            message.read))
                    }
                    Timber.d("ChatFragment: Inserting Items")
                    listAdapter.insertItems(list, 0)
                }
            }

            (viewModel as ChatFragmentViewModel).getMessageSendData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    Timber.d("ChatFragment: Send Success")
                    binding.chatRecycler.scrollToPosition(listAdapter.itemCount - 1)
                }
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun timestampToMS(timestamp: Timestamp): Long {
        return ((timestamp.seconds * 1000) + (timestamp.nanoseconds / 1000000))
    }
}
