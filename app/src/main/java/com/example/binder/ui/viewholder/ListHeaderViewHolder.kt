package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutListHeaderViewHolderBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.ClickType
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory.Companion.LIST_HEADER_TYPE
import setVisibility

class ListHeaderViewHolder(parent: ViewGroup, listener: OnActionListener): BaseViewHolder<Item>(
    listener,
    LayoutListHeaderViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {

    override val type: Int = LIST_HEADER_TYPE

    override fun bindView(item: Item) {
        (item as? HeaderItem)?.let { item ->
            (binding as? LayoutListHeaderViewHolderBinding)?.let { binding ->
                binding.headerText.text = item.headerText
                binding.addButton.setVisibility(item.shouldShowAddButton)
                binding.messageButton.setVisibility(item.shouldShowMessageButton)
                binding.addButton.setOnClickListener {
                    listener.onViewSelected(bindingAdapterPosition, object: ClickInfo{
                        override fun getType() = ClickType.ADD
                        override fun getSource() = item.headerType
                    })
                }
                binding.messageButton.setOnClickListener {
                    listener.onViewSelected(bindingAdapterPosition, object: ClickInfo{
                        override fun getType() = ClickType.MESSAGE
                        override fun getSource() = item.headerType
                    })
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class HeaderItem(
    override val uid: String? = null,
    val headerText: String,
    val shouldShowMessageButton: Boolean,
    val shouldShowAddButton: Boolean,
    val headerType: String? = null,
    override val type: Int = LIST_HEADER_TYPE
): Item()
