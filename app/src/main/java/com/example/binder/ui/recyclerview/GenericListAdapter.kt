package com.example.binder.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.binder.ui.viewholder.BaseViewHolder
import com.example.binder.ui.viewholder.ViewHolderFactory
import kotlinx.coroutines.launch

class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        if (oldItem::class == newItem::class) {
            if (oldItem.uid != null && newItem.uid != null) {
                return oldItem.uid == newItem.uid
            }
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem.equals(newItem)

}

@SuppressWarnings("TooManyFunctions")
class GenericListAdapter(
    private val viewHolderFactory: ViewHolderFactory,
    private val actionListener: OnActionListener
) : ListAdapter<Item, BaseViewHolder<Item>>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Item> {
        return viewHolderFactory.getViewHolder(parent, viewType, actionListener, ::getItem)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onViewRecycled(holder: BaseViewHolder<Item>) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<Item>) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<Item>) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }

    override fun submitList(list: List<Item>?) {
        super.submitList(list?.toMutableList())
    }

    override fun submitList(list: MutableList<Item>?, commitCallback: Runnable?) {
        super.submitList(list?.toMutableList(), commitCallback)
    }

    fun getItemAt(position: Int): Item? = currentList.getOrNull(position)
}

interface OnActionListener {
    fun onDeleteRequested(index: Int) {

    }
    fun onViewSelected(index: Int, clickInfo: ClickInfo? = null) {

    }
    fun onViewUnSelected(index: Int, clickInfo: ClickInfo? = null) {

    }
    fun onViewSelected(item: Item) {

    }
    fun onViewUnSelected(item: Item) {

    }
}

enum class ClickType {
    ADD, MESSAGE
}

interface ClickInfo {
    fun getSource(): String?
    fun getType(): ClickType?
}


@SuppressWarnings("UnnecessaryAbstractClass")
abstract class Item {
    abstract val uid: String?
    abstract val type: Int
}
