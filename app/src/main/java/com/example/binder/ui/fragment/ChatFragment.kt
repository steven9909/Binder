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
import kotlinx.coroutines.withContext
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.google.android.gms.common.api.ApiException
import android.widget.Toast
import com.example.binder.ui.Item
import data.InputQuestionBottomSheetConfig
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

    @SuppressWarnings("LongMethod")
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
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
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
                            Unit
                        }
                    }
                }
            }


            lifecycleScope.launch {
                (viewModel as ChatFragmentViewModel).messageGetterFlow(config.guid).collect {
                    val sendingId = it.sendingId
                    val msg = it.msg
                    val timestamp = it.timestamp
                    val read = it.read
                    items.add(MessageItem(
                        it.uid, msg,
                        sendingId == config.uid,
                        timestamp,
                        read
                    ))
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
                mainActivityViewModel.postNavigation(InputQuestionBottomSheetConfig())
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
