package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.VideoPlayerItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.VideoConfig
import data.VideoPlayerConfig
import kotlinx.coroutines.launch
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import live.hms.video.sdk.models.HMSMessage
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.sdk.models.HMSRoleChangeRequest
import live.hms.video.sdk.models.HMSRoom
import live.hms.video.sdk.models.enums.HMSPeerUpdate
import live.hms.video.sdk.models.enums.HMSRoomUpdate
import live.hms.video.sdk.models.enums.HMSTrackUpdate
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.MainActivityViewModel
import viewmodel.VideoPlayerFragmentViewModel
import java.lang.Exception

class VideoPlayerFragment(override val config: VideoPlayerConfig) : BaseFragment(), HMSUpdateListener {
    override val viewModel: ViewModel by viewModel<VideoPlayerFragmentViewModel>()

    private var binding: LayoutVideoPlayerFragmentBinding? = null

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private lateinit var hmsSDK: HMSSDK

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var genericListAdapter: GenericListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {

        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoPlayerFragmentBinding.inflate(inflater, container, false)
        hmsSDK = HMSSDK.Builder(requireActivity().application).build()
        setUpUi()
        return binding!!.root
    }


    private fun joinRoom(config: HMSConfig) {
        hmsSDK.join(config, this)
    }

    private fun setUpUi() {
        binding?.let { binding ->

            val room_id = "61d914cc2779ba16a4e5ae29"
            val name = config.name
            val uuid = config.uid
            val token = config.token

            val hmsConfig = HMSConfig(name, token)

            try {
                joinRoom(hmsConfig)
            } catch (e: Exception) {
                Timber.d("VideoPlayerFragment: $e")
            }
            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.videoPlayerRecycleView.layoutManager = LinearLayoutManager(context)
            binding.videoPlayerRecycleView.adapter = genericListAdapter

            binding.endCallButton.setOnClickListener {
                hmsSDK.leave()
                mainActivityViewModel.postNavigation(VideoConfig(config.name, config.uid))
            }
        }
    }

    override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {
    }

    override fun onError(error: HMSException) {
    }

    override fun onJoin(room: HMSRoom) {
    }

    override fun onMessageReceived(message: HMSMessage) {
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
        lifecycleScope.launch{
            genericListAdapter.submitList(
                getCurrentParticipants().map {
                    VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
                }
            )
        }
    }

    override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {
    }

    override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
    }

    override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
    }

    private fun getCurrentParticipants(): List<HMSPeer> = hmsSDK.getPeers().mapNotNull { it }

}
