package com.example.binder.ui.viewholder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
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
                    binding.contentText.gravity = Gravity.RIGHT
                } else {
                    binding.contentText.gravity = Gravity.LEFT
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
    val timestamp: Long,
    val read: Boolean,
    override val type: Int = ViewHolderFactory.MESSAGE_BODY_TYPE
): Item()
