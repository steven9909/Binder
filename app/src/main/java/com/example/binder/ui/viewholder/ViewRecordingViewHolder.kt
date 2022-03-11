package com.example.binder.ui.viewholder

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.example.binder.R
import com.example.binder.databinding.LayoutVideoUsersViewHolderBinding
import com.example.binder.databinding.LayoutViewRecordingViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import timber.log.Timber

class ViewRecordingViewHolder (
    parent: ViewGroup,
    listener: OnActionListener
    ) : BaseViewHolder<Item> (
    listener,
    LayoutViewRecordingViewHolderBinding.inflate(
    LayoutInflater.from(parent.context),
    parent, false
    )
    )  {

        override val type: Int
        get() = ViewHolderFactory.VIEW_RECORDING_TYPE

        override fun bindView(item: Item) {

            (item as? RecordingLinkItem)?.let { item ->
                (binding as? LayoutViewRecordingViewHolderBinding)?.let { binding ->
                    binding.hyperlinkText.text =
                        context.getString(R.string.name).format(item.hyperLink)
                    binding.hyperlinkText.setOnClickListener {
                        listener.onViewSelected(item)
                    }
                }
            }

        }

        override fun recycle() {
            //test
        }

}

data class RecordingLinkItem(
    val hyperLink: String?,
    override val uid: String?,
    override val type: Int = ViewHolderFactory.VIEW_RECORDING_TYPE,
): Item()



