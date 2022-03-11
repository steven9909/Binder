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
import android.view.Gravity.CENTER
import android.widget.ArrayAdapter

import android.widget.DatePicker
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import viewmodel.ScheduleDisplayBottomSheetViewModel
import java.sql.Time

@Suppress("MagicNumber", "LongMethod", "ComplexMethod", "NestedBlockDepth")
class InputScheduleBottomSheetFragment(
    override val config: InputScheduleBottomSheetConfig) : BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<InputScheduleBottomSheetViewModel>()

    // private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutInputScheduleBottomSheetFragmentBinding? = null

    private val recurringChoices: List<String> = listOf("Never", "Daily", "Weekly", "Monthly")

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
            val recurringEndTime = Calendar.getInstance()
            val formatter = SimpleDateFormat("MM/dd/yyyy|HH:mm", Locale.getDefault())
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            val adapter = ArrayAdapter<String>(binding.recurringEdit.context,
                R.layout.layout_recurring_event_dropdown, recurringChoices)
            binding.recurringEdit.adapter = adapter
            binding.recurringEdit.setSelection(0)

            // edit calendar pre-fill
            config.calendarEvent?.let {
                binding.titleEdit.setText(config.calendarEvent.name)
                val eventStart = Calendar.getInstance()
                eventStart.timeInMillis = config.calendarEvent.startTime
                val eventEnd = Calendar.getInstance()
                eventEnd.timeInMillis = config.calendarEvent.endTime

                binding.dateEdit.setText(dateFormatter.format(eventStart.time))
                binding.timeStartEdit.setText(timeFormatter.format(eventStart.time))
                binding.timeEndEdit.setText(timeFormatter.format(eventEnd.time))
                binding.allDayCheckbox.isChecked = config.calendarEvent.allDay

                if (config.calendarEvent.recurringEvent != ""
                    && config.calendarEvent.recurringEvent != null
                    && config.calendarEvent.recurringEvent != "null") {
                    val recurringEnd = Calendar.getInstance()
                    recurringEnd.timeInMillis = config.calendarEvent.recurringEnd!!
                    binding.recurringEndEdit.setText(dateFormatter.format(recurringEnd.time))
                    if (config.calendarEvent.recurringEvent == "Monthly") {
                        binding.recurringEdit.setSelection(3)
                    }
                    if (config.calendarEvent.recurringEvent == "Weekly") {
                        binding.recurringEdit.setSelection(2)
                    }
                    if (config.calendarEvent.recurringEvent == "Daily") {
                        binding.recurringEdit.setSelection(1)
                    }
                }
            }

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

                        if (startTime.time < endTime.time) {
                            if (binding.recurringEndEdit.text.toString() != "") {
                                recurringEndTime.time = formatter.parse(
                                    binding.recurringEndEdit.text.toString() + "|23:59"
                                )
                            }

                            if (binding.recurringEdit.selectedItem.toString() != "Never") {
                                if (binding.recurringEndEdit.text.toString() != "") {
                                    val calendarEvent = CalendarEvent(
                                        titleExitText.toString(),
                                        startTime.timeInMillis,
                                        endTime.timeInMillis,
                                        binding.allDayCheckbox.isChecked,
                                        binding.recurringEdit.selectedItem.toString(),
                                        recurringEndTime.timeInMillis
                                        // minutes before ---

                                    )
                                    (viewModel as? InputScheduleBottomSheetViewModel)?.updateSchedule(
                                        config.uid!!, calendarEvent
                                    )

                                    config.calendarEvent?.let {
                                        config.calendarEvent.uid?.let {
                                            config.uid?.let {
                                                (viewModel as InputScheduleBottomSheetViewModel).deleteEvent(
                                                    config.uid,
                                                    config.calendarEvent.uid
                                                )
                                            }
                                        }
                                    }

                                }
                            } else {
                                val calendarEvent = CalendarEvent(
                                    titleExitText.toString(),
                                    startTime.timeInMillis,
                                    endTime.timeInMillis,
                                    binding.allDayCheckbox.isChecked,
                                    // recurring: null
                                    // recurring end: none
                                    // minutes before ---
                                )
                                (viewModel as? InputScheduleBottomSheetViewModel)?.updateSchedule(
                                    config.uid!!, calendarEvent
                                )

                                config.calendarEvent?.let {
                                    config.calendarEvent.uid?.let {
                                        config.uid?.let {
                                            (viewModel as InputScheduleBottomSheetViewModel).deleteEvent(
                                                config.uid,
                                                config.calendarEvent.uid
                                            )
                                        }
                                    }
                                }

                            }
                        }
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

            (viewModel as? InputScheduleBottomSheetViewModel)?.getScheduleToDelete()?.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    Unit
                }
            }

            binding.dateEdit.transformIntoDatePicker(requireContext(), "MM/dd/yyyy")
            binding.recurringEndEdit.transformIntoDatePicker(requireContext(), "MM/dd/yyyy")
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
