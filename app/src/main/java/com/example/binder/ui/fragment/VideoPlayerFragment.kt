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

    private lateinit var hmsSDK: HMSSDK

    override val items: MutableList<Item> = mutableListOf()

    private val people: MutableList<Item> = mutableListOf()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var genericListAdapter: GenericListAdapter

    private var isMute = false;

    private var isPause = false;

    private var isPriority = true;

    private var local: VideoPlayerItem? = null

    private var first: VideoPlayerItem? = null

    val dominantSpeaker = MutableLiveData<VideoPlayerItem?>(null)

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

//        updatedItems.forEach{Timber.d("VideoPlayerFragment: items list : ${it.peer.name}")}

        people.clear()
        items.clear()

        people.addAll(updatedUsers)
//        people.addAll(updatedItems)
        items.addAll(updatedItems)

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
//                    Timber.d("VideoPlayerFragment : Active Speakers are: ${speakers.map { s -> "${s.peer?.name} ${s.level}" }}}")

                    if (speakers.isNotEmpty()) {
                        first = if (speakers[0].peer?.peerID + (speakers[0].peer?.videoTrack?.trackId ?: "") != local?.uid){
//                                        Timber.d("VideoPlayerFragment : in if for FIRST")
                                        speakers[0].let { s ->
                                            s.peer?.let{
//                                                Timber.d("VideoPlayerFragment : inside let first")
                                                VideoPlayerItem(
                                                    s.peer?.peerID + (s.peer?.videoTrack?.trackId ?: ""), it
                                                )
                                            }
                                        }
                                    } else {
                                        if (speakers.size > 1){
//                                            Timber.d("VideoPlayerFragment : in if for second")
                                            speakers[1].let { s ->
                                                s.peer?.let{
//                                                    Timber.d("VideoPlayerFragment : else : ${it.name}")
                                                    VideoPlayerItem(
                                                        s.peer?.peerID + (s.peer?.videoTrack?.trackId ?: ""), it
                                                    )
                                                }
                                            }
                                        } else {
                                            first
                                        }
                                    }

                        val others = items.filterNot{(it as VideoPlayerItem).uid == first?.uid}
                        if (first != null) {
                            items.clear()
                            items.add(first!!)
                            items.addAll(others)
                        }
                        items.forEach{Timber.d("VideoPlayerFragment: items list NEW : ${ (it as VideoPlayerItem).peer.name}")}

                    }
                }
            })

            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.videoPlayerRecycleView.layoutManager = LinearLayoutManager(context)
            binding.videoPlayerRecycleView.adapter = genericListAdapter

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

//        Timber.d("VideoPlayerFragment : Joined : ${config.name}, ${config.uid}");
//        Timber.d("VideoPlayerFragment : Join peerList : ${room.localPeer}")
//        room.peerList.forEach { Timber.d("${it.peerID}, ${it.videoTrack?.trackId}") }
        lifecycleScope.launch{
            local = room.localPeer?.let {
                VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
            }
            updateLists()
            if( items.size > 0 ) {
                Timber.d("VideoPlayerFragment: submit item lists on PEER Join : ${items[0]}")
                genericListAdapter.submitList(listOfNotNull(items[0]))
            }
        }
    }

    override fun onMessageReceived(message: HMSMessage) {
        Unit
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {

        if( items.size > 0 ) {
            if (isPriority) {
                Timber.d("VideoPlayerFragment: submit item lists on PEER : ${ (items[0] as VideoPlayerItem).peer.name}")
                lifecycleScope.launch {
                    genericListAdapter.submitList(listOfNotNull(items[0]))
                }
            } else {
                lifecycleScope.launch {
                    Timber.d("VideoPlayerFragment: submit item lists on PEER items : ${items}")
                    genericListAdapter.submitList(items)
                }
            }
        }

        if (type == HMSPeerUpdate.PEER_JOINED) {

            Timber.d("VideoPlayerFragment : peerUpdate: ${peer.peerID + peer.videoTrack?.trackId}")

            updateLists()

        }
        if (type == HMSPeerUpdate.PEER_LEFT) {


            Timber.d("VideoPlayerFragment : peerUpdate: ${peer.peerID + peer.videoTrack?.trackId}")

            updateLists();

            if( items.size > 0 ) {
                lifecycleScope.launch {
                    Timber.d("VideoPlayerFragment: submit item lists on PEER left : ${items[0]}")
                    genericListAdapter.submitList(listOfNotNull(items[0]))
                }
            }


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
