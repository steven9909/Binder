package com.example.binder.ui.viewholder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.binder.databinding.LayoutMessageViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class MessageViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutMessageViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.MESSAGE_BODY_TYPE
    override fun bindView(item: Item) {
        (item as? MessageItem)?.let { item ->
            (binding as? LayoutMessageViewHolderBinding)?.let { binding ->
                binding.contentText.text = item.content
                if (item.isSelf) {
                    binding.contentText.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        horizontalBias = 0f
                    }
                } else {
                    binding.contentText.updateLayoutParams<ConstraintLayout.LayoutParams> {
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

data class MessageItem(
    override val uid: String? = null,
    val content: String,
    val isSelf: Boolean,
    override val timestamp: Long,
    override val type: Int = ViewHolderFactory.MESSAGE_BODY_TYPE
): TimeStampItem()
