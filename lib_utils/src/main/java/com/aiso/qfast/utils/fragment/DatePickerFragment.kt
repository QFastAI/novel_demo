package com.aiso.qfast.utils.fragment

import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.aiso.qfast.base.BaseBottomSheetDialogFragment
import com.aiso.qfast.base.ext.viewBinding
import com.aiso.qfast.utils.R
import com.aiso.qfast.utils.databinding.DatePickerLayoutBinding
import com.aiso.qfast.utils.setNoFastClickListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerFragment: BaseBottomSheetDialogFragment(R.layout.date_picker_layout){

    private val initDate  by lazy {
        requireArguments().getString("initDate").orEmpty()
    }

    var onDatePicked: ((String) -> Unit)? = null

    private val binding by viewBinding(DatePickerLayoutBinding::bind)

    private var selectYear:Int = 1970
    private var selectMonth:Int = 1
    private var selectDay:Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(initDate) // 转为Date对象

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }

        val year = calendar.get(Calendar.YEAR)      // 1996
        val month = calendar.get(Calendar.MONTH) + 1 // 4 (Calendar.MONTH从0开始)
        val day = calendar.get(Calendar.DAY_OF_MONTH) // 28
        binding.customDatePicker.init(year,month,day,object : DatePicker.OnDateChangedListener{
            override fun onDateChanged(
                view: DatePicker?,
                year: Int,
                monthOfYear: Int,
                dayOfMonth: Int
            ) {
                selectYear = year
                selectMonth = monthOfYear
                selectDay = dayOfMonth
            }
        })
        initViewClick()
    }

    private fun initViewClick() {
        binding.takeCancelBtn.setNoFastClickListener {
            dismiss()
        }
        binding.takeSureBtn.setNoFastClickListener {
            onDatePicked?.invoke("$selectYear-$selectMonth-$selectDay")
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            initDate: String
        ): DatePickerFragment {
            return DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putString("initDate", initDate)
                }
            }
        }
    }
}