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
import com.example.binder.ui.viewholder.VideoPlayerItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.VideoConfig
import data.VideoPlayerConfig
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

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var genericListAdapter: GenericListAdapter

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
                    Timber.d("VideoPlayerFragment : Active Speakers are: ${speakers.map { s -> "${s.peer?.name} ${s.level}" }}}")
                    Timber.d("VideoPlayerFragment : ${HMSPeerUpdate.BECAME_DOMINANT_SPEAKER} ")
                    if (speakers.isNotEmpty()) {
                        items.clear()
                        items.addAll(speakers.mapNotNull { s ->
                            s.peer?.let{
                                VideoPlayerItem(
                                    s.peer?.peerID + (s.peer?.videoTrack?.trackId ?: ""), it
                                )
                            }

                        })
                        val others = getCurrentParticipants().filterNot { s -> (s?.peerID + (s?.videoTrack?.trackId ?: "")) in items.map { it.uid } }.map {
                            VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
                        }
                        items.addAll(others)
                        lifecycleScope.launch {
                            genericListAdapter.submitList(items)
                        }
                    }

                }
            })

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
        Unit
    }

    override fun onError(error: HMSException) {
        Unit
    }

    override fun onJoin(room: HMSRoom) {
        Unit
    }

    override fun onMessageReceived(message: HMSMessage) {
        Unit
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
        if (type == HMSPeerUpdate.PEER_JOINED) {
            lifecycleScope.launch{
                val others = getCurrentParticipants().filterNot { s -> (s?.peerID + (s?.videoTrack?.trackId ?: "")) in items.map { it.uid } }.map {
                    VideoPlayerItem(it.peerID + (it.videoTrack?.trackId ?: ""), it)
                }
                items.addAll(others)
                genericListAdapter.submitList(items)
            }
        } else if (type == HMSPeerUpdate.PEER_LEFT) {
            lifecycleScope.launch{
                val index = items.indexOfFirst {
                    it.uid == (peer?.peerID + (peer?.videoTrack?.trackId ?: ""))
                }
                if (index != -1) {
                    items.removeAt(index)
                    genericListAdapter.submitList(items)
                }
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

    private fun getCurrentParticipants(): List<HMSPeer> = hmsSDK.getPeers().mapNotNull { it }

}
