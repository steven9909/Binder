package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutInputQuestionBottomSheetFragmentBinding
import com.google.firebase.Timestamp
import data.ChatConfig
import data.InputQuestionBottomSheetConfig
import data.Message
import data.Question
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
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

    @SuppressWarnings("ComplexCondition", "MagicNumber", "LongMethod", "ComplexMethod")
    fun setUpUi() {
        binding?.let { binding ->

            (viewModel as InputQuestionBottomSheetViewModel).getGroupTypes(config.guid)

            (viewModel as InputQuestionBottomSheetViewModel).getGroupTypesData().observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS && it.data != null) {
                    binding.typeDropdown.isEnabled = true
                    val arr : MutableList<String> = mutableListOf()
                    it.data.forEach { item ->
                        arr.add(item)
                    }
                    val spinner : Spinner = binding.typeDropdown
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arr)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                } else {
                    binding.typeDropdown.isEnabled = false
                }
            }

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
                                if (binding.answerText4Checkbox.isChecked) 3 else -1
                            ),
                            binding.typeDropdown.selectedItem as String?
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
                if (it.status == Status.SUCCESS && it.data != null) {
                    (viewModel as InputQuestionBottomSheetViewModel).messageSend(
                        Message(
                            config.uid,
                            "",
                            timestampToMS(Timestamp.now()),
                            null,
                            it.data,
                            config.name
                        ),
                        config.guid
                    )
                }
            }

            (viewModel as InputQuestionBottomSheetViewModel).getMessageSendData().observe(viewLifecycleOwner) {
                mainActivityViewModel.postLoadingScreenState(true)
                if (it.status == Status.SUCCESS) {
                    mainActivityViewModel.postNavigation(
                        ChatConfig(
                            config.name,
                            config.uid,
                            config.guid,
                            config.chatName)
                    )
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    @SuppressWarnings("MagicNumber")
    private fun timestampToMS(timestamp: Timestamp): Long {
        return ((timestamp.seconds * 1000) + (timestamp.nanoseconds / 1000000))
    }
}
