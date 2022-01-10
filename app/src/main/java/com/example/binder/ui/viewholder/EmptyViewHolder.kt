package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutEmptyViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class EmptyViewHolder (parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutEmptyViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.MESSAGE_TITLE_TYPE
    override fun bindView(item: Item, position: Int) {
        Unit
    }

    override fun recycle() {
        TODO("Not yet implemented")
    }
}
