package com.example.binder.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

abstract class BaseViewHolder<T: Item> (
    val listener: OnActionListener,
    val binding: ViewBinding
    ): RecyclerView.ViewHolder(binding.root) {
    abstract val type: Int

    abstract fun bindView(item: Item)
}
