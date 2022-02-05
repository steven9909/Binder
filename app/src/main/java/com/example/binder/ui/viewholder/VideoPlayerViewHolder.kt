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
import timber.log.Timber

class VideoPlayerViewHolder(
    parent: ViewGroup,
    listener: OnActionListener,
    private val getItem: (Int) -> Item
) : BaseViewHolder<Item> (
    listener,
    LayoutVideoPlayerViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.VIDEO_PLAYER_TYPE

    private var isInit: Boolean = false

    init {
        (binding as? LayoutVideoPlayerViewHolderBinding)?.let {
            binding.videoSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            binding.videoSurfaceView.setEnableHardwareScaler(true)
        }
    }

    override fun bindView(item: Item) {
        (binding as? LayoutVideoPlayerViewHolderBinding)?.let { binding ->
            if(!isInit){
                binding.videoSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                binding.videoSurfaceView.setEnableHardwareScaler(true)
                isInit = false
            }
        }
    }

    override fun onAttached() {
        super.onAttached()
        if (!isInit) {
            (binding as? LayoutVideoPlayerViewHolderBinding)?.let { binding ->
                binding.videoSurfaceView.init(SharedEglContext.context, null)
                (getItem(bindingAdapterPosition) as? VideoPlayerItem)
                    ?.peer
                    ?.videoTrack
                    ?.addSink(binding.videoSurfaceView)
                binding.peerName.text = context.getString(R.string.name).format((getItem(bindingAdapterPosition) as? VideoPlayerItem)?.peer?.name)
                isInit = true
            }
        }
    }

    override fun onDetached() {
        super.onDetached()
        (binding as? LayoutVideoPlayerViewHolderBinding)?.let { binding ->
            if (isInit && bindingAdapterPosition != -1) {
                (getItem(bindingAdapterPosition) as? VideoPlayerItem)?.peer?.videoTrack?.let {
                    it.removeSink(binding.videoSurfaceView)
                    binding.videoSurfaceView.release()
                    isInit = false
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class VideoPlayerItem(
    override val uid: String? = null,
    val peer: HMSPeer,
    override val type: Int = ViewHolderFactory.VIDEO_PLAYER_TYPE
): Item()
