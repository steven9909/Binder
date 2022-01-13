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
import live.hms.video.utils.SharedEglContext
import org.webrtc.RendererCommon

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

    private var isInit: Boolean = false

    override fun bindView(item: Item, position: Int) {
        (item as? VideoPlayerItem)?.let { item ->
            (binding as? LayoutVideoPlayerViewHolderBinding)?.let { binding ->
                if(!isInit){
                    binding.videoSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                    binding.videoSurfaceView.init(SharedEglContext.context, null)
                    isInit = true
                }
                item.peer.videoTrack?.addSink(binding.videoSurfaceView)
            }
        }
    }

    override fun recycle() {
    }
}

data class VideoPlayerItem(
    val peer: HMSPeer,
    override val type: Int = ViewHolderFactory.VIDEO_PLAYER_TYPE
): Item()
