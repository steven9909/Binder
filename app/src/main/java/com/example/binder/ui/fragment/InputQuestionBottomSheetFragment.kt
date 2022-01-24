package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutInputQuestionBottomSheetFragmentBinding
import com.example.binder.databinding.LayoutInputScheduleBottomSheetFragmentBinding
import data.InputQuestionBottomSheetConfig
import data.Question
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.InputQuestionBottomSheetViewModel
import viewmodel.MainActivityViewModel

class InputQuestionBottomSheetFragment(
    override val config: InputQuestionBottomSheetConfig) : BaseBottomSheetFragment() {
    override val viewModel: ViewModel by viewModel<InputQuestionBottomSheetViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutInputQuestionBottomSheetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutInputQuestionBottomSheetFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    fun setUpUi() {
        binding?.let { binding ->

            binding.submitQuestionButton.setOnClickListener {
                if (binding.questionText.text.isNotBlank() && binding.answerText1.text.isNotBlank() &&
                    binding.answerText2.text.isNotBlank() && binding.answerText3.text.isNotBlank() &&
                    binding.answerText4.text.isNotBlank()) {
                    (viewModel as InputQuestionBottomSheetViewModel).addQuestionToDatabase(
                        Question(
                            binding.questionText.text.toString(),
                            listOf(
                                binding.answerText1.text.toString(),
                                binding.answerText2.text.toString(),
                                binding.answerText3.text.toString(),
                                binding.answerText4.text.toString()
                            ),
                            listOf(
                                if (binding.answerText1Checkbox.isChecked) 0 else -1,
                                if (binding.answerText2Checkbox.isChecked) 1 else -1,
                                if (binding.answerText3Checkbox.isChecked) 2 else -1,
                                if (binding.answerText4Checkbox.isChecked) 3 else -1)
                        )
                    )
                    binding.questionText.text.clear()
                    binding.answerText1.text.clear()
                    binding.answerText2.text.clear()
                    binding.answerText3.text.clear()
                    binding.answerText4.text.clear()
                    binding.answerText1Checkbox.isChecked = false
                    binding.answerText2Checkbox.isChecked = false
                    binding.answerText3Checkbox.isChecked = false
                    binding.answerText4Checkbox.isChecked = false
                }
            }

            (viewModel as InputQuestionBottomSheetViewModel).getAddQuestionToDBData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    //send msg encoded here and go back to chatFragment
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}