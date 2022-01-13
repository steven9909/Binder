package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendDetailViewHolderBinding
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import com.example.binder.databinding.LayoutVideoPlayerViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import live.hms.video.sdk.models.HMSPeer

class VideoPlayerViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item> (
    listener,
    LayoutVideoPlayerViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
)

{
    override val type: Int
        get() = ViewHolderFactory.VIDEO_PLAYER_TYPE

//    private var isClicked: Boolean = false

    override fun bindView(item: Item, position: Int) {
        (item as? VideoPlayerItem)?.let { item ->
            (binding as? LayoutVideoPlayerViewHolderBinding)?.let { binding ->
//                binding.videoSurfaceView = context.getString(R.string.name).format(item.name))
            }
        }
    }

    override fun recycle() {
//        isClicked = false
//        binding.root.changeBackgroundColor(context.getColor(R.color.white))
    }
}

data class VideoPlayerItem(
    val peer: HMSPeer?,
    override val type: Int = ViewHolderFactory.VIDEO_PLAYER_TYPE
): Item()
