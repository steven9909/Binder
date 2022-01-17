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
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.GoogleAccountProvider
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
import com.google.android.gms.common.Scopes

import com.example.binder.ui.MainActivity

import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers


class ChatFragment(override val config: ChatConfig) : BaseFragment() {

    companion object {
        private const val VERTICAL_SPACING = 10
    }

    override val viewModel: ViewModel by viewModel<ChatFragmentViewModel>()

    private var binding: LayoutChatFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var  genericListAdapter: GenericListAdapter

    private val listener = object: OnActionListener {
        override fun onDeleteRequested(index: Int) {
            genericListAdapter.deleteItemAt(index)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutChatFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        driveSetUp()
        return binding!!.root
    }

    @SuppressWarnings("LongMethod")
    private fun setUpUi() {
        binding?.let { binding ->
            genericListAdapter = GenericListAdapter(viewHolderFactory, listener)

            binding.chatRecycler.layoutManager = LinearLayoutManager(context)
            binding.chatRecycler.adapter = genericListAdapter
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
                    val sendingId = it.sendingId
                    val msg = it.msg
                    val timestamp = it.timestamp
                    val read = it.read
                    genericListAdapter.insertItemEnd(
                        MessageItem(
                            it.uid, msg,
                            sendingId == config.uid,
                            timestamp,
                            read
                        )
                    ) {
                        binding.chatRecycler.scrollToPosition(genericListAdapter.itemCount - 1)
                    }
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
                            (genericListAdapter.getItemAt(0) as MessageItem).timestamp
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
                            message.uid,
                            message.msg,
                            message.sendingId == config.uid,
                            message.timestamp,
                            message.read))
                    }
                    Timber.d("ChatFragment: Inserting Items")
                    genericListAdapter.insertItemsAt(list, 0)
                }
            }

            (viewModel as ChatFragmentViewModel).getMessageSendData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    Timber.d("ChatFragment: Send Success")
                    binding.chatRecycler.scrollToPosition(genericListAdapter.itemCount - 1)
                }
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun timestampToMS(timestamp: Timestamp): Long {
        return ((timestamp.seconds * 1000) + (timestamp.nanoseconds / 1000000))
    }

    private fun driveSetUp() {

    }
}
