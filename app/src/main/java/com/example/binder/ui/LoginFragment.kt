package com.example.binder.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.binder.R
import com.example.binder.databinding.LayoutLoginFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginFragment: BaseFragment() {

    private var binding: LayoutLoginFragmentBinding? = null

    private var signInIntent: Intent? = null

    companion object {
        private const val RC_SIGN_IN = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutLoginFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        setUpSignIn()
        return binding!!.root
    }

    private fun setUpSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("12843939694-skboiqe0flibocshdibmf0e1nl9henn0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        signInIntent = GoogleSignIn.getClient(activity, gso).signInIntent
    }

    private fun setUpUi() {
        binding?.let {
            it.signInButton.setOnClickListener {
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            it.welcomeText.text = SpannableStringBuilder().apply {
                this.append(context?.getString(R.string.welcome_to) + "\n")
                val binder_text = SpannableString(context?.getString(R.string.app_name))
                binder_text.setSpan(StyleSpan(Typeface.BOLD), 0, binder_text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                this.append(binder_text)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LoginFragment", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginFragment", "Google sign in failed", e)
            }
        }
    }
}