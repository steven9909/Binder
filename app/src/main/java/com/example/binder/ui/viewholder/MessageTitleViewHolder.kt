package com.example.binder.ui.viewholder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutMessageTitleViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class MessageTitleViewHolder (parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutMessageTitleViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.MESSAGE_TITLE_TYPE
    override fun bindView(item: Item, position: Int) {
        (item as? MessageTitleItem)?.let { item ->
            (binding as? LayoutMessageTitleViewHolderBinding)?.let { binding ->
                binding.nameText.text = item.name
                if (item.isSelf) {
                    binding.nameText.gravity = Gravity.LEFT
                } else {
                    binding.nameText.gravity = Gravity.RIGHT
                }
            }
        }
    }
}

data class MessageTitleItem(
    val name: String,
    val isSelf: Boolean,
    override val type: Int = ViewHolderFactory.MESSAGE_TITLE_TYPE
): Item()
