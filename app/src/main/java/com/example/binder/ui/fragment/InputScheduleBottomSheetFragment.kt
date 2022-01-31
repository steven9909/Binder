package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutInputScheduleBottomSheetFragmentBinding
import com.google.android.material.snackbar.Snackbar
import data.CalendarEvent
import data.InputScheduleBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.InputScheduleBottomSheetViewModel
import viewmodel.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.binder.ui.MainActivity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context

import android.widget.DatePicker
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.sql.Time


class InputScheduleBottomSheetFragment(
    override val config: InputScheduleBottomSheetConfig) : BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<InputScheduleBottomSheetViewModel>()

    private var binding: LayoutInputScheduleBottomSheetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutInputScheduleBottomSheetFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->

            binding.exitButton.setOnClickListener {
                dismiss()
            }

            val startTime = Calendar.getInstance()
            val endTime = Calendar.getInstance()
            val formatter = SimpleDateFormat("MM/dd/yyyy|HH:mm", Locale.getDefault())

            binding.submitButton.setOnClickListener {
                binding.titleEdit.text?.let { titleExitText ->
                    if (binding.dateEdit.text.toString() != ""
                        && binding.timeStartEdit.text.toString() != ""
                        && binding.timeEndEdit.text.toString() != ""
                    ) {
                        startTime.time = formatter.parse(
                            binding.dateEdit.text.toString()+"|"+binding.timeStartEdit.text.toString()
                        )
                        endTime.time = formatter.parse(
                            binding.dateEdit.text.toString()+"|"+binding.timeEndEdit.text.toString()
                        )
                        val calendarEvent = CalendarEvent(
                            titleExitText.toString(),
                            startTime.timeInMillis,
                            endTime.timeInMillis,
                            binding.allDayCheckbox.isChecked
                            // recurring
                            // minutes before
                        )
                        (viewModel as? InputScheduleBottomSheetViewModel)?.updateSchedule(calendarEvent)
                    }
                }
            }

            (viewModel as? InputScheduleBottomSheetViewModel)?.getSchedule()?.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    dismiss()
                    view?.let { view ->
                        val popUp = Snackbar.make(view, "Added Schedule", Snackbar.LENGTH_SHORT)
                        popUp.show()
                    }
                }
            }

            binding.dateEdit.transformIntoDatePicker(requireContext(), "MM/dd/yyyy")
            binding.timeStartEdit.transformIntoTimePicker(requireContext(), "HH:mm")
            binding.timeEndEdit.transformIntoTimePicker(requireContext(), "HH:mm")

        }
    }


    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }

    fun EditText.transformIntoTimePicker(context: Context, format: String) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val timePickerOnDataSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hour)
                myCalendar.set(Calendar.MINUTE, minute)
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                setText(sdf.format(myCalendar.time))
            }
        setOnClickListener {
            TimePickerDialog(
                context,
                timePickerOnDataSetListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                false
            ).run {
                show()
            }
        }
    }
}
