package com.ravinada.mvvmarchitecture.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {

    fun showToast(context: Context?, message: String, showShort: Boolean = false) {
        context?.let {
            Toast.makeText(it, message, if (showShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
        }
    }

    fun showDatePicker(textView: WeakReference<AppCompatEditText>, act: WeakReference<FragmentActivity?>) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DAY_OF_MONTH)
        act.get()?.let { activity ->
            val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, date: Int ->
                textView.get()?.let { tv ->
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, date)
                    val sdf = SimpleDateFormat(AppConstant.DateUtilKey.FORMAT_IN_SIMPLE, Locale.getDefault())
                    tv.setText(sdf.format(cal.time))
                }
            }, year, month, date)

            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }
}