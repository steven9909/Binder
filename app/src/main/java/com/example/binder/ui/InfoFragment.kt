package com.example.binder.ui

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
import com.example.binder.R
import com.example.binder.databinding.LayoutInfoFragmentBinding
import data.InfoConfig

class InfoFragment(override val config: InfoConfig) : BaseFragment() {

    private var binding: LayoutInfoFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutInfoFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let {
            it.welcomeText.text = SpannableStringBuilder().apply {
                this.append(context?.getString(R.string.welcome) + "\n")
                val nameText = SpannableString(config.name)
                nameText.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                nameText.setSpan(
                    ForegroundColorSpan(context?.getColor(R.color.app_yellow) ?: 0),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }
        }
    }

}