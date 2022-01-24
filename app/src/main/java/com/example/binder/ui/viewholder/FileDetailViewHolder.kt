package com.example.binder.ui.viewholder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutFileDetailViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class FileDetailViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutFileDetailViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.FILE_DETAIL_TYPE

    override fun bindView(item: Item) {
        (item as? FileDetailItem)?.let {
            (binding as? LayoutFileDetailViewHolderBinding)?.let { binding ->
                binding.fileNameText.text = it.urlEncoded
                binding.fileNameText.setOnClickListener {
                    listener.onViewSelected(item)
                }
                binding.iconLink.setOnClickListener {
                    listener.onViewSelected(item)
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

abstract class TimeStampItem: Item() {
    abstract val timestamp: Long
}

data class FileDetailItem(
    override val uid: String?,
    val content: String,
    val urlEncoded: String,
    val isSelf: Boolean,
    override val timestamp: Long,
    override val type: Int = ViewHolderFactory.FILE_DETAIL_TYPE
): TimeStampItem()
