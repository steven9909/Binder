package com.example.binder.ui.fragment

import android.app.Activity
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

import android.content.Intent
import android.net.Uri
import com.example.binder.ui.Item
import data.InputQuestionBottomSheetConfig
import com.example.binder.ui.viewholder.FileDetailItem
import com.example.binder.ui.viewholder.MessageSentByItem
import com.example.binder.ui.viewholder.QuestionDetailItem
import com.example.binder.ui.viewholder.TimeStampItem
import me.rosuh.filepicker.config.FilePickerManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import viewmodel.MainActivityViewModel
import java.io.File


class ChatFragment(override val config: ChatConfig) : BaseFragment() {

    companion object {
        private const val VERTICAL_SPACING = 10
    }

    override val viewModel: ViewModel by viewModel<ChatFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutChatFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var  genericListAdapter: GenericListAdapter

    private var folderId: String? = null

    private val listener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            (item as? FileDetailItem)?.urlEncoded?.let {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(browserIntent)
            }
        }
    }

    override val items: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutChatFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    @SuppressWarnings("LongMethod", "ComplexMethod")
    private fun setUpUi() {
        binding?.let { binding ->
            genericListAdapter = GenericListAdapter(viewHolderFactory, listener)

            binding.chatRecycler.layoutManager = LinearLayoutManager(context)
            binding.chatRecycler.adapter = genericListAdapter
            binding.chatRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            binding.sendFileButton.setOnClickListener {
                if (folderId != null) {
                    FilePickerManager
                        .from(this)
                        .enableSingleChoice()
                        .forResult(FilePickerManager.REQUEST_CODE)
                }
            }

            binding.nameText.text = SpannableStringBuilder().apply {
                val nameText = SpannableString(config.chatName)
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_white)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }

            lifecycleScope.launch {
                val job = (viewModel as ChatFragmentViewModel).initDrive()
                job.join()
                if (job.isCompleted) {
                    (viewModel as ChatFragmentViewModel).tryCreateFolder(config.guid)
                    (viewModel as ChatFragmentViewModel).getCreateFolderData()?.observe(viewLifecycleOwner) {
                        if(it.status == Status.SUCCESS) {
                            this@ChatFragment.folderId = it.data
                        }
                    }
                    (viewModel as? ChatFragmentViewModel)?.getUploadFileData()?.observe(viewLifecycleOwner) {
                        if(it.status == Status.SUCCESS && it.data != null) {
                            (viewModel as? ChatFragmentViewModel)?.messageSend(
                                Message(
                                    config.uid,
                                    binding.messageBox.text.toString(),
                                    timestampToMS(Timestamp.now()),
                                    it.data,
                                    null,
                                    config.name
                                ), config.guid
                            )
                        }
                    }
                }
            }

            lifecycleScope.launch {
                (viewModel as ChatFragmentViewModel).messageGetterFlow(config.guid).collect {
                    val sendingId = it.sendingId
                    val msg = it.msg
                    val timestamp = it.timestamp
                    if (it.fileLink != null) {
                        items.add(
                            FileDetailItem(
                                it.uid,
                                "",
                                it.fileLink,
                                sendingId == config.uid,
                                timestamp
                            )
                        )
                        items.add(
                            MessageSentByItem(
                                it.uid,
                                context?.getString(R.string.sent_by) + " " + it.sentByName,
                                sendingId == config.uid,
                                timestamp
                            )
                        )
                    } else if (it.question != null) {
                        val q = it.question
                        items.add(
                            QuestionDetailItem(
                                it.uid,
                                "",
                                q.question,
                                q.answers,
                                q.answerIndexes,
                                sendingId == config.uid,
                                timestamp
                            )
                        )
                        items.add(
                            MessageSentByItem(
                                it.uid,
                                context?.getString(R.string.sent_by) + " " + it.sentByName,
                                sendingId == config.uid,
                                timestamp
                            )
                        )
                    } else {
                        items.add(
                            MessageItem(
                                it.uid,
                                msg,
                                sendingId == config.uid,
                                timestamp
                            )
                        )
                        items.add(
                            MessageSentByItem(
                                it.uid,
                                context?.getString(R.string.sent_by) + " " + it.sentByName,
                                sendingId == config.uid,
                                timestamp
                            )
                        )
                    }
                    genericListAdapter.submitList(items) {
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
                            timestampToMS(Timestamp.now()),
                            null,
                            null,
                            config.name
                        ), config.guid
                    )
                    binding.messageBox.text.clear()
                }
            }

            binding.chatRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        (genericListAdapter.getItemAt(0) as? TimeStampItem)?.timestamp?.let {
                            (viewModel as ChatFragmentViewModel).getMoreMessages(
                                config.guid,
                                it
                            )
                        }
                    } else {
                        Unit
                    }
                }
            })

            (viewModel as ChatFragmentViewModel).getMoreMessagesData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    val list = mutableListOf<TimeStampItem>()
                    it.data?.forEach { message ->
                        if (message.fileLink != null) {
                            list.add(
                                FileDetailItem(
                                    message.uid,
                                    "",
                                    message.fileLink,
                                    message.sendingId == config.uid,
                                    message.timestamp
                                )
                            )
                            list.add(
                                MessageSentByItem(
                                    message.uid,
                                    context?.getString(R.string.sent_by) + " " + message.sentByName,
                                    message.sendingId == config.uid,
                                    message.timestamp
                                )
                            )
                        } else if (message.question != null) {
                            val q = message.question
                            list.add(
                                QuestionDetailItem(
                                    message.uid,
                                    "",
                                    q.question,
                                    q.answers,
                                    q.answerIndexes,
                                    message.sendingId == config.uid,
                                    message.timestamp
                                )
                            )
                            list.add(
                                MessageSentByItem(
                                    message.uid,
                                    context?.getString(R.string.sent_by) + " " + message.sentByName,
                                    message.sendingId == config.uid,
                                    message.timestamp
                                )
                            )
                        } else {
                            list.add(
                                MessageItem(
                                    message.uid,
                                    message.msg,
                                    message.sendingId == config.uid,
                                    message.timestamp
                                )
                            )
                            list.add(
                                MessageSentByItem(
                                    message.uid,
                                    context?.getString(R.string.sent_by) + " " + message.sentByName,
                                    message.sendingId == config.uid,
                                    message.timestamp
                                )
                            )
                        }
                    }
                    Timber.d("ChatFragment: Inserting Items")
                    items.addAll(0, list)
                    genericListAdapter.submitList(items)
                }
            }

            (viewModel as ChatFragmentViewModel).getMessageSendData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    Timber.d("ChatFragment: Send Success")
                    binding.chatRecycler.scrollToPosition(genericListAdapter.itemCount - 1)
                }
            }

            binding.sendQuestionButton.setOnClickListener {
                mainActivityViewModel.postNavigation(
                    InputQuestionBottomSheetConfig(
                        config.name,
                        config.uid,
                        config.guid,
                        config.chatName
                    )
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerManager.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val list = FilePickerManager.obtainData()
            if (list.size == 1) {
                folderId?.let { folderId ->
                    val file = File(list[0])
                    (viewModel as? ChatFragmentViewModel)?.setUploadFileParam(
                        folderId,
                        null,
                        file
                    )
                }
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun timestampToMS(timestamp: Timestamp): Long {
        return ((timestamp.seconds * 1000) + (timestamp.nanoseconds / 1000000))
    }
}
