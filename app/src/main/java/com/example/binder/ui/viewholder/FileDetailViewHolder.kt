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
                if (item.isSelf) {
                    binding.fileNameText.gravity = Gravity.RIGHT
                } else {
                    binding.fileNameText.gravity = Gravity.LEFT
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class FileDetailItem(
    override val uid: String?,
    val urlEncoded: String,
    val name: String,
    val shouldShowFileName: Boolean,
    val isSelf: Boolean,
    val timestamp: Long,
    override val type: Int = ViewHolderFactory.LIST_HEADER_TYPE
): Item()
