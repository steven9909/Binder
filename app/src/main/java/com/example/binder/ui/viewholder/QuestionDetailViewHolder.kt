package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.R
import com.example.binder.databinding.LayoutQuestionDetailViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class QuestionDetailViewHolder(parent: ViewGroup,
                               listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutQuestionDetailViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.QUESTION_DETAIL_TYPE

    private var isClicked1: Boolean = false
    private var isClicked2: Boolean = false
    private var isClicked3: Boolean = false
    private var isClicked4: Boolean = false

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    override fun bindView(item: Item) {
        (item as? QuestionDetailItem)?.let {
            (binding as? LayoutQuestionDetailViewHolderBinding)?.let { binding ->
                binding.questionText.text = it.question
                binding.answer1Text.text = it.answers[0]
                binding.answer2Text.text = it.answers[1]
                binding.answer3Text.text = it.answers[2]
                binding.answer4Text.text = it.answers[3]
                binding.questionText.setBackgroundColor(context.getColor(R.color.app_white))
                binding.answer1Text.setBackgroundColor(context.getColor(R.color.app_white))
                binding.answer2Text.setBackgroundColor(context.getColor(R.color.app_white))
                binding.answer3Text.setBackgroundColor(context.getColor(R.color.app_white))
                binding.answer4Text.setBackgroundColor(context.getColor(R.color.app_white))

                binding.questionText.setOnClickListener {
                    binding.questionText.setBackgroundColor(context.getColor(R.color.green))
                    if (item.correctIndexes[0] == 0) {
                        binding.answer1Text.setBackgroundColor(context.getColor(R.color.green))
                    }
                    if (item.correctIndexes[1] == 1) {
                        binding.answer2Text.setBackgroundColor(context.getColor(R.color.green))
                    }
                    if (item.correctIndexes[2] == 2) {
                        binding.answer3Text.setBackgroundColor(context.getColor(R.color.green))
                    }
                    if (item.correctIndexes[3] == 3) {
                        binding.answer4Text.setBackgroundColor(context.getColor(R.color.green))
                    }
                }

                binding.answer1Text.setOnClickListener {
                    isClicked1 = true
                    when (isClicked1) {
                        true -> {
                            if (item.correctIndexes[0] == 0) {
                                binding.answer1Text.setBackgroundColor(context.getColor(R.color.green))
                                binding.questionText.setBackgroundColor(context.getColor(R.color.green))
                            } else {
                                binding.answer1Text.setBackgroundColor(context.getColor(R.color.red))
                            }
                        }
                        false -> {
                            binding.answer1Text.setBackgroundColor(context.getColor(R.color.app_white))
                        }
                    }
                }

                binding.answer2Text.setOnClickListener {
                    isClicked2 = true
                    when (isClicked2) {
                        true -> {
                            if (item.correctIndexes[1] == 1) {
                                binding.answer2Text.setBackgroundColor(context.getColor(R.color.green))
                                binding.questionText.setBackgroundColor(context.getColor(R.color.green))
                            } else {
                                binding.answer2Text.setBackgroundColor(context.getColor(R.color.red))
                            }
                        }
                        false -> {
                            binding.answer2Text.setBackgroundColor(context.getColor(R.color.app_white))
                        }
                    }
                }

                binding.answer3Text.setOnClickListener {
                    isClicked3 = true
                    when (isClicked3) {
                        true -> {
                            if (item.correctIndexes[2] == 2) {
                                binding.answer3Text.setBackgroundColor(context.getColor(R.color.green))
                                binding.questionText.setBackgroundColor(context.getColor(R.color.green))
                            } else {
                                binding.answer3Text.setBackgroundColor(context.getColor(R.color.red))
                            }
                        }
                        false -> {
                            binding.answer3Text.setBackgroundColor(context.getColor(R.color.app_white))
                        }
                    }
                }

                binding.answer4Text.setOnClickListener {
                    isClicked4 = true
                    when (isClicked4) {
                        true -> {
                            if (item.correctIndexes[3] == 3) {
                                binding.answer4Text.setBackgroundColor(context.getColor(R.color.green))
                                binding.questionText.setBackgroundColor(context.getColor(R.color.green))
                            } else {
                                binding.answer4Text.setBackgroundColor(context.getColor(R.color.red))
                            }
                        }
                        false -> {
                            binding.answer4Text.setBackgroundColor(context.getColor(R.color.app_white))
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("MaxLineLength")
    override fun recycle() {
        isClicked1 = false
        isClicked2 = false
        isClicked3 = false
        isClicked4 = false
        (binding as? LayoutQuestionDetailViewHolderBinding)?.answer1Text?.setBackgroundColor(context.getColor(R.color.white))
        (binding as? LayoutQuestionDetailViewHolderBinding)?.answer2Text?.setBackgroundColor(context.getColor(R.color.white))
        (binding as? LayoutQuestionDetailViewHolderBinding)?.answer3Text?.setBackgroundColor(context.getColor(R.color.white))
        (binding as? LayoutQuestionDetailViewHolderBinding)?.answer4Text?.setBackgroundColor(context.getColor(R.color.white))
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
): TimeStampItem()
