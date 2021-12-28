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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment: BaseFragment() {

    private var binding: LayoutLoginFragmentBinding? = null

    private var signInIntent: Intent? = null

    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 1
        private const val TAG = "LoginFragment"
    }

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
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Sign in successful")
            } else {
                Log.d(TAG, "Sign in failed")
            }
        }
    }
}