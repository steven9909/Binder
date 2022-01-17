package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import com.example.binder.ui.api.HmsAuthTokenApi
import com.example.binder.ui.api.TokenRequestBody
import data.VideoConfig
import data.VideoPlayerConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit
import timber.log.Timber
import viewmodel.MainActivityViewModel
import viewmodel.VideoMenuFragmentViewModel
import java.util.*

class VideoMenuFragment(override val config: VideoConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<VideoMenuFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutVideoMenuFragmentBinding? = null

    private val retrofitClient: Retrofit by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoMenuFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.scheduleButton.setOnClickListener {

                val room_id = "61d914cc2779ba16a4e5ae29"
                val name = config.name
                val uuid = config.uid

                lifecycleScope.launchWhenCreated{
                    try{
                        var token = getToken(uuid, room_id)
                        mainActivityViewModel.postNavigation(VideoPlayerConfig(config.name, config.uid, token))
                    } catch (e: Exception){
                        Timber.d("VideoMenuFragment: Error in GetToken $e")
                    }

                }



            }
        }
    }

    private suspend fun getToken(uuid : String, roomId: String): String {
        val tokenReq = TokenRequestBody(roomId = roomId,
                                        uuid   = uuid)
        val token = retrofitClient
            .create(HmsAuthTokenApi::class.java)
            .getAuthToken(tokenReq)
        return token.token
    }
}
