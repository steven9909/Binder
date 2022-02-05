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
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.UserDataItem
import com.example.binder.ui.viewholder.VideoPlayerItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.VideoConfig
import data.VideoPlayerConfig
import data.VideoUserBottomSheetConfig
import kotlinx.coroutines.launch
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.sdk.HMSAudioListener
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import live.hms.video.sdk.models.HMSMessage
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.sdk.models.HMSRoleChangeRequest
import live.hms.video.sdk.models.HMSRoom
import live.hms.video.sdk.models.HMSSpeaker
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

@SuppressWarnings("TooManyFunctions")
class VideoPlayerFragment(override val config: VideoPlayerConfig) : BaseFragment(), HMSUpdateListener {
    override val viewModel: ViewModel by viewModel<VideoPlayerFragmentViewModel>()

    private var binding: LayoutVideoPlayerFragmentBinding? = null

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val hmsSDK: HMSSDK by inject()

    override val items: MutableList<Item> = mutableListOf()

    private val people: MutableList<Item> = mutableListOf()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var genericListAdapter: GenericListAdapter

    private var isMute = false;

    private var isPause = false;

    private var isPriority = true;

    private var local: VideoPlayerItem? = null

    private var first: VideoPlayerItem? = null

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            Unit
        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            Unit
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoPlayerFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }


    private fun joinRoom(config: HMSConfig) {
        hmsSDK.join(config, this)
    }

    @SuppressWarnings("MaxLineLength")
    private fun setUpUi() {
        binding?.let { binding ->

            val name = config.name
            val uuid = config.uid
            val token = config.token


            val hmsConfig = HMSConfig(name, token)

            try {
                joinRoom(hmsConfig)
            } catch (e: Exception) {
                Timber.d("VideoPlayerFragment: $e")
            }

            hmsSDK.addAudioObserver(object : HMSAudioListener {

                override fun onAudioLevelUpdate(speakers: Array<HMSSpeaker>) {
                    if (speakers.isNotEmpty()) {
                        speakers.forEach { speaker ->

                        }
                    }
                }
            })

            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.videoPlayerRecycleView.layoutManager = LinearLayoutManager(context)
            binding.videoPlayerRecycleView.adapter = genericListAdapter

            binding.endCallButton.setOnClickListener {
                hmsSDK.leave()
//                Timber.d("VideoPlayerFragment : end call button triggered");
                mainActivityViewModel.postNavigation(VideoConfig(config.name, config.uid))
            }
            binding.muteButton.setOnClickListener {
                val myPeer = hmsSDK.getLocalPeer()
//                Timber.d("VideoPlayerFragment : mute call button triggered : ${myPeer?.name}");
                isMute = if ( !isMute ) {

                    myPeer?.audioTrack?.setMute(true)
                    true
                } else {
                    myPeer?.audioTrack?.setMute(false)
                    false
                }
            }
            binding.pauseVideoButton.setOnClickListener {
                val myPeer = hmsSDK.getLocalPeer()
//                Timber.d("VideoPlayerFragment : pause call button triggered : ${myPeer?.name}")
                isPause = if ( !isPause ) {
                    myPeer?.videoTrack?.setMute(true)
                    true
                } else {
                    myPeer?.videoTrack?.setMute(false)
                    false
                }
            }
            binding.voicePriorityButton.setOnClickListener {
                val myPeer = hmsSDK.getLocalPeer()
//                Timber.d("VideoPlayerFragment : priority call button triggered : ${myPeer?.name}")
                isPriority = !isPriority
            }
            binding.peopleButton.setOnClickListener {
//                Timber.d("VideoPlayerFragment : people call button triggered")
                try{
                    mainActivityViewModel.postNavigation(VideoUserBottomSheetConfig(people))
                } catch (e: Exception){
//                    Timber.d("VideoPlayerFragment: people button : $e")
                }
            }
        }
    }

    override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {
        Unit
    }

    override fun onError(error: HMSException) {
        Unit
    }

    override fun onJoin(room: HMSRoom) {
        Timber.d("VideoPlayerFragment: joined")
        room.localPeer?.let {
            val videoItem = VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
            val doesExist = items.firstOrNull {
                it.uid == videoItem.uid
            }
            if (doesExist == null) {
                items.add(videoItem)
            }
        }
        lifecycleScope.launch{
            //genericListAdapter.submitList(items)
        }
    }

    override fun onMessageReceived(message: HMSMessage) {
        Unit
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
        Timber.d("VideoPlayerFragment: ${type.name} for ${peer.name}")
        val peerId = (peer.peerID + (peer.videoTrack?.trackId ?: ""))
        if (type == HMSPeerUpdate.PEER_JOINED) {
            VideoPlayerItem(peerId, peer).takeIf {
                items.firstOrNull { item ->
                    item.uid == peerId
                } == null
            }?.let {
                items.add(it)
            }
            lifecycleScope.launch{
                genericListAdapter.submitList(items)
            }
        }
        else if (type == HMSPeerUpdate.PEER_LEFT) {
            items.removeIf {
                it.uid == peerId
            }
            lifecycleScope.launch{
                genericListAdapter.submitList(items)
            }
        }
    }

    override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {
        Unit
    }

    override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
        Unit
    }

    override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
        Unit
    }

    private fun getCurrentParticipants(exclude: List<Item>? = null): List<HMSPeer> {
        return hmsSDK.getPeers().filter{ peer ->
            exclude?.firstOrNull {
                it.uid == (peer.peerID + (peer.videoTrack?.trackId ?: ""))
            } != null
        }.map { it }
    }

}
