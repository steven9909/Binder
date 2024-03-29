package com.example.binder.ui.viewholder

import android.graphics.Color
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
    listener: OnActionListener
) : BaseViewHolder<Item> (
    listener,
    LayoutVideoUsersViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
)  {

    override val type: Int
        get() = ViewHolderFactory.VIDEO_USER_TYPE


    init {
        (binding as? LayoutVideoUsersViewHolderBinding)?.let {
            binding.userName.text = "Use Voice Priority"
        }

    }

    override fun bindView(item: Item) {

        (item as? UserDataItem)?.let { item ->
            (binding as? LayoutVideoUsersViewHolderBinding)?.let { binding ->
                binding.userName.text = context.getString(R.string.name).format(item.name)
                Timber.d("VideoPlayerFragment: focuus get ${item.name}, ${item.focus}")
                binding.userName.setOnClickListener {
                    listener.onViewSelected(item)
                }
                if (item.focus) {
                    Timber.d("VideoPlayerFragment: focuus get ${item.name}")
                    binding.userLayout.setBackgroundColor(Color.YELLOW)
                    binding.userName.setTextColor(Color.BLACK)
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
    val focus:Boolean,
    override val type: Int = ViewHolderFactory.VIDEO_USER_TYPE
): Item()


