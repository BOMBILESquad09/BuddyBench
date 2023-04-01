package it.polito.mad.buddybench.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MyDateListener(private var birthday: MutableLiveData<LocalDate>) : DatePickerDialog.OnDateSetListener {

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        birthday.value = LocalDate.of(year,month,day)
    }

}