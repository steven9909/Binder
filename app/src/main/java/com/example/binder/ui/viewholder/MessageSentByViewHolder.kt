package com.example.binder.ui.viewholder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.binder.databinding.LayoutMessageSentByViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class MessageSentByViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutMessageSentByViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
){
    override val type: Int
        get() = ViewHolderFactory.MESSAGE_SENT_BY_TYPE
    override fun bindView(item: Item) {
        (item as? MessageSentByItem)?.let { item ->
            (binding as? LayoutMessageSentByViewHolderBinding)?.let { binding ->
                binding.sentByText.text = item.content
                if (item.isSelf) {
                    binding.sentByText.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        horizontalBias = 0f
                    }
                } else {
                    binding.sentByText.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        horizontalBias = 1f
                    }
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class MessageSentByItem(
    override val uid: String? = null,
    val content: String,
    val isSelf: Boolean,
    override val timestamp: Long,
    override val type: Int = ViewHolderFactory.MESSAGE_SENT_BY_TYPE
): TimeStampItem()
