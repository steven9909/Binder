package com.example.binder.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import com.example.binder.ui.fragment.BaseFragment
import data.VideoConfig
import data.VideoPlayerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.media.tracks.HMSVideoTrack
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
import live.hms.video.utils.SharedEglContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber
import viewmodel.VideoPlayerFragmentViewModel
import java.lang.Exception

class VideoPlayerFragment(override val config: VideoPlayerConfig) : BaseFragment(), HMSUpdateListener {
    override val viewModel: ViewModel by viewModel<VideoPlayerFragmentViewModel>()

    private var binding: LayoutVideoPlayerFragmentBinding? = null

    private lateinit var hmsSDK: HMSSDK

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
            println("Name : $name , uuid : $uuid")
            val authToken =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2Nlc3Nfa2V5IjoiNjFkOGE0NzFiOGMzYzdiYzg2YTRkMzlhIiwicm9vbV9pZCI6IjYxZDkxNGNjMjc3OWJhMTZhNGU1YWUyOSIsInVzZXJfaWQiOiI2MWQ4YTQ3MWI4YzNjN2JjODZhNGQzOTYiLCJyb2xlIjoiaG9zdCIsImp0aSI6IjZlMTg4OTQxLWVkOWMtNDAwYS1hOTY3LWVhNmRjNjNlMTA1ZCIsInR5cGUiOiJhcHAiLCJ2ZXJzaW9uIjoyLCJleHAiOjE2NDIxMDg1OTZ9.SBX93Hz_MORu4VZqiYvE1iQjivPtOfWrxW2c9-5mzwY"

            val hmsConfig = HMSConfig(name, authToken)

            try {
                joinRoom(hmsConfig)
            } catch (e: Exception) {
                Timber.d("VideoPlayerFragment: ERROR JOINING ROOM $e")
            }

        }
    }

    override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {
    }

    override fun onError(error: HMSException) {
        Timber.d("VideoPlayerFragment: NOT POSSIBLE TO JOIN ROOM ! $error")
    }

    override fun onJoin(room: HMSRoom) {
        Timber.d("VideoPlayerFragment: JOINED SUCCESSFULLY")
    }

    override fun onMessageReceived(message: HMSMessage) {
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {

        Timber.d("VideoPlayerFragment: A NEW PEER JOINED SUCCESSFULLY")

        val hmsVideoTrack : HMSVideoTrack? = peer.videoTrack

        val surfaceView : SurfaceViewRenderer? = binding?.videoSurfaceView
        withContext(Dispatchers.IO){

        }
        surfaceView?.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        surfaceView?.init(SharedEglContext.context, null)

        if (surfaceView != null) {
            hmsVideoTrack?.addSink(surfaceView)
        }

    }

    override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {
    }

    override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
    }

    override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
    }

}
