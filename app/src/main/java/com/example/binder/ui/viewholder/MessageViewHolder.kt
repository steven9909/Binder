package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutMessageViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class MessageViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(parent, listener, LayoutMessageViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {
    override val type: Int
        get() = ViewHolderFactory.MESSAGE_BODY_TYPE
    override fun bindView(item: Item) {

    }
}

data class MessageItem(override val type: Int = ViewHolderFactory.MESSAGE_BODY_TYPE): Item()