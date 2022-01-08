package com.example.binder.ui

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.binder.ui.viewholder.BaseViewHolder
import com.example.binder.ui.viewholder.ViewHolderFactory

class ListAdapter(
    private val viewHolderFactory: ViewHolderFactory,
    private val actionListener: OnActionListener
) : RecyclerView.Adapter<BaseViewHolder<Item>>() {

    private val list = mutableListOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Item> {
        return viewHolderFactory.getViewHolder(parent, viewType, actionListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        holder.bindView(list[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    override fun getItemCount(): Int = list.size

    fun insertItem(item: Item, position: Int) {
        this.list.add(position, item)
        notifyItemRangeInserted(position, 1)
    }

    fun updateItems(items: List<Item>) {
        this.list.clear()
        this.list.addAll(items)
        this.notifyDataSetChanged()
    }

    fun insertItemEnd(item: Item) {
        this.list.add(item)
        this.notifyDataSetChanged()
    }

    fun deleteItemAt(index: Int){
        this.list.removeAt(index)
        this.notifyDataSetChanged()
    }
}

interface OnActionListener {
    fun onDeleteRequested(index: Int)
}

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class Item {
    abstract val type: Int
}
