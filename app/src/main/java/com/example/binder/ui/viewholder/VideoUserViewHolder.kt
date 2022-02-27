package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendDetailViewHolderBinding
import com.example.binder.databinding.LayoutVideoPlayerViewHolderBinding
import com.example.binder.databinding.LayoutVideoUsersViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import org.koin.android.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.webrtc.RendererCommon
import timber.log.Timber
import viewmodel.SharedVideoPlayerViewModel

class VideoUserViewHolder(
    parent: ViewGroup,
    listener: OnActionListener,
    private val getItem: (Int) -> Item
) : BaseViewHolder<Item> (
    listener,
    LayoutVideoUsersViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
)  {

    override val type: Int
        get() = ViewHolderFactory.VIDEO_USER_TYPE
    private var isInit: Boolean = false


    init {
        (binding as? LayoutVideoUsersViewHolderBinding)?.let {
            binding.userName.text = "Use Voice Priority"
        }

    }

    override fun bindView(item: Item) {

        (item as? UserDataItem)?.let { item ->
            (binding as? LayoutVideoUsersViewHolderBinding)?.let { binding ->
                binding.userName.text = context.getString(R.string.name).format(item.name)
                binding.userName.setOnClickListener {
                    listener.onViewSelected(item)
                }
                binding.kickButton.setOnClickListener{
                    listener.onViewSelected(item)
                }
            }
        }

    }

    override fun recycle() {
        //test
    }

}

data class UserDataItem(
    override val uid: String?,
    val name:String,
    override val type: Int = ViewHolderFactory.VIDEO_USER_TYPE
): Item()


