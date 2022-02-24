package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.UserDataItem
import com.example.binder.ui.viewholder.VideoPlayerItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.HubConfig
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
import live.hms.video.utils.SharedEglContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.webrtc.RendererCommon
import timber.log.Timber
import viewmodel.MainActivityViewModel
import viewmodel.SharedVideoPlayerViewModel
import viewmodel.VideoPlayerFragmentViewModel
import java.lang.Exception

@SuppressWarnings("TooManyFunctions")
class VideoPlayerFragment(override val config: VideoPlayerConfig) : BaseFragment(), HMSUpdateListener {
    override val viewModel: ViewModel by viewModel<VideoPlayerFragmentViewModel>()

    private val sharedViewModel : SharedVideoPlayerViewModel by sharedViewModel()

    private var binding: LayoutVideoPlayerFragmentBinding? = null

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private lateinit var hmsSDK: HMSSDK

    override val items: MutableList<Item> = mutableListOf()

    private val people: MutableList<Item> = mutableListOf()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private var isMute = false;

    private var isPause = false;

    private var isPriority = true;

    private var local: VideoPlayerItem? = null

    private var first: VideoPlayerItem? = null

    val dominantSpeaker = MutableLiveData<VideoPlayerItem?>(null)

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

    private fun updateLists(){

        val updatedUsers = getCurrentParticipants().map{
            UserDataItem(it.peerID + (it.videoTrack?.trackId ?: ""), it.name)
        }

        val updatedItems = getCurrentParticipants().filterNot{it.peerID + (it.videoTrack?.trackId ?: "") == local?.uid}.map{
            VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
        }

        updatedItems.forEach{Timber.d("VideoPlayerFragment: items list : ${it.peer.name}")}

        people.clear()
        items.clear()

        people.addAll(updatedUsers)
        items.addAll(updatedItems)
    }

    @SuppressWarnings("MaxLineLength")
    private fun setUpUi() {
        binding?.let { binding ->

            val name = config.name
            val uuid = config.uid
            val token = config.token

            sharedViewModel.getSharedData().observe(viewLifecycleOwner) {
                Timber.d("VideoPlayerFragment: Observed:${it}")
            }

            val hmsConfig = HMSConfig(name, token)

            try {
                joinRoom(hmsConfig)
            } catch (e: Exception) {
                Timber.d("VideoPlayerFragment: $e")
            }

            binding.videoSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            binding.videoSurfaceView.setEnableHardwareScaler(true)
            binding.videoSurfaceView.init(SharedEglContext.context, null)

            binding.endCallButton.setOnClickListener {
                hmsSDK.leave()
//                Timber.d("VideoPlayerFragment : end call button triggered");
                mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid))
            }

            binding.muteButton.setOnClickListener {
                val myPeer = hmsSDK.getLocalPeer()
//                Timber.d("VideoPlayerFragment : mute call button triggered : ${myPeer?.name}");
                isMute = if ( !isMute ) {
                    myPeer?.audioTrack?.setMute(true)
                    binding.muteButton.setImageResource(R.drawable.ic_volume_off)
                    true
                } else {
                    myPeer?.audioTrack?.setMute(false)
                    binding.muteButton.setImageResource(R.drawable.ic_volume_up)
                    false
                }
            }

            binding.pauseVideoButton.setOnClickListener {
                val myPeer = hmsSDK.getLocalPeer()
//                Timber.d("VideoPlayerFragment : pause call button triggered : ${myPeer?.name}")
                isPause = if ( !isPause ) {
                    myPeer?.videoTrack?.setMute(true)
                    binding.pauseVideoButton.setImageResource(R.drawable.ic_videocam_off)
                    true
                } else {
                    myPeer?.videoTrack?.setMute(false)
                    binding.pauseVideoButton.setImageResource(R.drawable.ic_meetings)
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

        Timber.d("VideoPlayerFragment : Joined : ${config.name}, ${config.uid}");
//        Timber.d("VideoPlayerFragment : Join peerList : ${room.localPeer}")
//        room.peerList.forEach { Timber.d("${it.peerID}, ${it.videoTrack?.trackId}") }
        lifecycleScope.launch{
            local = room.localPeer?.let {
                VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
            }
            updateLists()


            val track = dominantSpeaker.value
            Timber.d("VideoPlayerFragment: dominant when join : ${track}")

        }
    }

    override fun onDestroyView() {
        binding?.videoSurfaceView?.release()
        super.onDestroyView()
    }

    override fun onMessageReceived(message: HMSMessage) {
        Unit
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {

        when (type) {
            HMSPeerUpdate.PEER_LEFT -> {
                Timber.d("VideoPlayerFragment : peerUpdate: ${peer.peerID + peer.videoTrack?.trackId}")

                updateLists();


                //            lifecycleScope.launch{
//                val index = items.indexOfFirst {
//                    it.uid == (peer.peerID + (peer.videoTrack?.trackId ?: ""))
//                }
//                if (index != -1) {
//                    items.removeAt(index)
//                    genericListAdapter.submitList(items)
//                }
//            }

            }

            HMSPeerUpdate.PEER_JOINED -> {
                Timber.d("VideoPlayerFragment : peerLeft: ${peer.peerID + peer.videoTrack?.trackId}")
                updateLists()
                lifecycleScope.launch {
                    val track = dominantSpeaker.value
                }
            }

            HMSPeerUpdate.BECAME_DOMINANT_SPEAKER -> {
                lifecycleScope.launch {
                    val track =
                        VideoPlayerItem(peer.peerID + (peer.videoTrack?.trackId ?: ""), peer)
                    dominantSpeaker.setValue(track)

                }

            }

            HMSPeerUpdate.NO_DOMINANT_SPEAKER -> {
//                dominantSpeaker.postValue(null)
            }

            HMSPeerUpdate.ROLE_CHANGED -> {

            }

            HMSPeerUpdate.METADATA_CHANGED -> {

            }
            HMSPeerUpdate.NAME_CHANGED -> {

            }

            else -> Unit
        }
        val track = dominantSpeaker.value
        Timber.d("VideoPlayerFragment : Dominant Speaker : {$track} ")
//        lifecycleScope.launch {
//            genericListAdapter.submitList(listOfNotNull(track))
//        }
//        if( items.size > 0 ) {
//            if (isPriority) {
//                Timber.d("VideoPlayerFragment: submit item lists on PEER : ${ (items[0] as VideoPlayerItem).peer.name}")
//                lifecycleScope.launch {
//                    genericListAdapter.submitList(listOfNotNull(items[0]))
//                }
//            } else {
//                lifecycleScope.launch {
//                    Timber.d("VideoPlayerFragment: submit item lists on PEER items : ${items}")
//                    genericListAdapter.submitList(items)
//                }
//            }
//        }
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

    private fun getCurrentParticipants(): List<HMSPeer> = hmsSDK.getPeers().mapNotNull { it }

}
