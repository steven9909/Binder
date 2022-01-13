package com.example.binder.ui.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

abstract class BaseViewHolder<T: Item> (
    val listener: OnActionListener,
    val binding: ViewBinding
    ): RecyclerView.ViewHolder(binding.root) {
    abstract val type: Int

    protected val context: Context by lazy {
        itemView.context
    }

    abstract fun bindView(item: Item, position: Int)

    abstract fun recycle()

    open fun onAttached() {}

    open fun onDetached() {}
}
