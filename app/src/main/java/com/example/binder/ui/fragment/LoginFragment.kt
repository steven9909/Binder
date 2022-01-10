package com.example.binder.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import catchNonFatal
import com.example.binder.R
import com.example.binder.databinding.LayoutLoginFragmentBinding
import com.example.binder.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import data.InfoConfig
import data.LoginConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.LoginFragmentViewModel
import viewmodel.MainActivityViewModel

class LoginFragment(override val config: LoginConfig) : BaseFragment() {

    private var binding: LayoutLoginFragmentBinding? = null

    private var signInIntent: Intent? = null

    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 1
    }

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    override val viewModel: ViewModel by viewModel<LoginFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutLoginFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        setUpSignIn()

        auth = Firebase.auth

        return binding!!.root
    }

    private fun setUpSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireContext().getString(R.string.oauth_client_id))
            .requestEmail()
            .build()

        signInIntent = GoogleSignIn.getClient(activity, gso).signInIntent
    }

    private fun setUpUi() {
        binding?.let {
            it.signInButton.setOnClickListener {
                if (auth.currentUser == null) {
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }
            it.welcomeText.text = SpannableStringBuilder().apply {
                this.append(requireContext().getString(R.string.welcome_to) + "\n")
                val binderText = SpannableString(requireContext().getString(R.string.app_name))
                binderText.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    binderText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binderText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    binderText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(binderText)
            }
            it.debugButton.setOnClickListener {
                mainActivityViewModel.postNavigation(InfoConfig("Sample Name", "some_random_uid"))
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
        catchNonFatal {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnFailureListener {
            Timber.e(it)
            val toast = Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.login_failed),
                Toast.LENGTH_SHORT
            )
            toast.show()
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                auth.currentUser?.let { user ->
                    mainActivityViewModel.postNavigation(
                        InfoConfig(
                            (requireActivity() as? MainActivity)?.getNameFromGoogleSignIn() ?: "",
                            user.uid
                        )
                    )
                }
            } else {
                val toast = Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }
}
