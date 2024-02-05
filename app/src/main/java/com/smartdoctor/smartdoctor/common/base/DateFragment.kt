package com.donationinstitutions.donationinstitutions.common.base

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import java.util.*

class DateFragment(private var listener: DatePickerDialog.OnDateSetListener) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.dialog?.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 40))
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }


        Calendar.getInstance().apply {
            DatePickerDialog(
                requireContext(),
                listener, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
            ).let { datePickerDialog ->
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() + 1000
                datePickerDialog.window?.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
                return datePickerDialog
            }
        }
    }

}



class TimeFragment(private var listener: TimePickerDialog.OnTimeSetListener) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.dialog?.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 40))
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }


        Calendar.getInstance().apply {
            TimePickerDialog(
                requireContext(),
                listener,
                get(Calendar.HOUR_OF_DAY),
                get(Calendar.MINUTE),
                false
            ).let { timePickerDialog ->
                timePickerDialog.window?.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
                return timePickerDialog
            }
        }
    }

}