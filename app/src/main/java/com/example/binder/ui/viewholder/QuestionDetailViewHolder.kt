package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutQuestionDetailViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class QuestionDetailViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutQuestionDetailViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.QUESTION_DETAIL_TYPE

    override fun bindView(item: Item) {
        (item as? QuestionDetailItem)?.let {
            (binding as? LayoutQuestionDetailViewHolderBinding)?.let { binding ->
                binding.questionText.text = it.question
                binding.answer1Text.text = it.answers[0]
                binding.answer2Text.text = it.answers[1]
                binding.answer3Text.text = it.answers[2]
                binding.answer4Text.text = it.answers[3]
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class QuestionDetailItem(
    override val uid: String?,
    val content: String,
    val question: String,
    val answers: List<String>,
    val correctIndexes: List<Int>,
    val isSelf: Boolean,
    override val timestamp: Long,
    override val type: Int = ViewHolderFactory.QUESTION_DETAIL_TYPE
): Item()