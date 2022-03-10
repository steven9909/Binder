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
import data.ChatConfig
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

    private var currentlyDisplayed: VideoPlayerItem? = null

    private var isMute = false;

    private var isPause = false;

    private var isPriority = true;

    private var local: VideoPlayerItem? = null

    private var focus: VideoPlayerItem? = null


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
            if (isPriority) {
                UserDataItem(it.peerID + (it.videoTrack?.trackId ?: ""), it.name, false)
            } else {
                if(it.peerID + (it.videoTrack?.trackId ?: "") == focus?.uid){
                    UserDataItem(it.peerID + (it.videoTrack?.trackId ?: ""), it.name, true)
                } else if (it.peerID == focus?.uid) {
                    UserDataItem(it.peerID + (it.videoTrack?.trackId ?: ""), it.name, true)
                } else {
                    UserDataItem(it.peerID + (it.videoTrack?.trackId ?: ""), it.name, false)
                }
            }

        }

        val updatedItems = getCurrentParticipants()
            .filterNot{
                it.peerID + (it.videoTrack?.trackId ?: "") == local?.uid
            }.map{
                VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
            }

        updatedItems.forEach{Timber.d("VideoPlayerFragment: items list : ${it.peer.name}")}

        people.clear()
        items.clear()

        people.addAll(updatedUsers)
        items.addAll(updatedItems)
    }

    @SuppressWarnings("MaxLineLength", "TooGenericExceptionCaught", "LongMethod", "ComplexMethod", "LongParameterList")
    private fun setUpUi() {
        binding?.let { binding ->

            sharedViewModel.getSharedData().observe(viewLifecycleOwner) {
                Timber.d("VideoPlayerFragment: Observed:${it}")
                val tempUid = it
                var tempUserData : HMSPeer? = null

                if (getCurrentParticipants().isNotEmpty()) {
                    tempUserData = getCurrentParticipants().find{it.peerID + (it.videoTrack?.trackId ?: "") == tempUid}
                    if (tempUserData == null) {
                        tempUserData = getCurrentParticipants().find{it.peerID == tempUid}
                    }
                }

                Timber.d("VideoPlayerFragment : ${getCurrentParticipants()}")
                focus = tempUserData?.let { it1 ->
                    VideoPlayerItem(tempUserData?.peerID + (tempUserData?.videoTrack?.trackId ?: ""),
                        it1
                    )
                }
                Timber.d("VideoPlayerFragment : ${focus?.peer?.name}")
                isPriority = currentlyDisplayed?.peer?.name == focus?.peer?.name
                Timber.d("VideoPlayerFragment: isPriority Vue =  : ${isPriority}")
                if(!isPriority) {
                    Timber.d("VideoPlayerFragment: Dislaying focused user : ${focus?.peer?.name}")
                    binding.videoSurfaceView.let {
                        currentlyDisplayed?.let { item ->
                            item.peer.videoTrack?.removeSink(it)
                        }
                        currentlyDisplayed = focus
                        focus?.peer?.videoTrack?.addSink(it)
                    }
                }
                updateLists()
            }

            val hmsConfig = HMSConfig(config.name, config.token)

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
                mainActivityViewModel.postNavigation(
                    ChatConfig(
                        config.name,
                        config.uid,
                        config.guid,
                        config.chatName,
                        config.owner,
                        config.members,
                        config.groupTypes)
                )
            }

            binding.muteButton.setOnClickListener {
                val myPeer = hmsSDK.getLocalPeer()
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
                try{
                    mainActivityViewModel.postNavigation(
                        ChatConfig(
                            config.name,
                            config.uid,
                            config.guid,
                            config.chatName,
                            config.owner,
                            config.members,
                            config.groupTypes)
                    )
                } catch (e: Exception){
                    Timber.d("VideoPlayerFragment: people button : $e")
                }
            }

            binding.peopleButton.setOnClickListener {
                try{
                    mainActivityViewModel.postNavigation(VideoUserBottomSheetConfig(people))
                } catch (e: Exception){
                    Timber.d("VideoPlayerFragment: people button : $e")
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
        lifecycleScope.launch{
            local = room.localPeer?.let {
                VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
            }
            updateLists()


            val track = dominantSpeaker.value
            Timber.d("VideoPlayerFragment: dominant when join : ${track?.peer?.name}")
            binding?.videoSurfaceView?.let {
                currentlyDisplayed?.let { item ->
                    item.peer.videoTrack?.removeSink(it)
                }
                currentlyDisplayed = local
                local?.peer?.videoTrack?.addSink(it)
            }

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
                Timber.d("VideoPlayerFragment : peerUpdate: ${peer.name}")
                updateLists();
            }

            HMSPeerUpdate.PEER_JOINED -> {
                Timber.d("VideoPlayerFragment : peerLeft: ${peer.name}")
                updateLists()
                lifecycleScope.launch {
                    val track = dominantSpeaker.value
                }
            }

            HMSPeerUpdate.BECAME_DOMINANT_SPEAKER -> {
                lifecycleScope.launch {
                    val track = VideoPlayerItem(peer.peerID + (peer.videoTrack?.trackId ?: ""), peer)
                    dominantSpeaker.setValue(track)
                    Timber.d("VideoPlayerFragment : Dominant Speaker : ${track?.peer?.name}, $isPriority ")

                    if (isPriority) {
                        binding?.videoSurfaceView?.let {
                            currentlyDisplayed?.let { item ->
                                item.peer.videoTrack?.removeSink(it)
                            }
                            currentlyDisplayed = track
                            track.peer.videoTrack?.addSink(it)
                        }
                    }

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
