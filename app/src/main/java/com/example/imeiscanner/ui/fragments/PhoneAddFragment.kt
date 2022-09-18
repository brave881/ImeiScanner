package com.example.imeiscanner.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import java.util.*


class PhoneAddFragment : Fragment() {
    private lateinit var binding: FragmentPhoneAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.btnDateUse.setOnClickListener { showDatePicker() }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year1, month1, dayOfMonth ->
                binding.btnDateUse.text = "$dayOfMonth/${month1 + 1}/$year1"
            }, year, month, day
        )
        datePickerDialog.show()
    }


    private fun qrScan(editText: EditText) {
        val DRAWABLE_RIGHT = 2
        editText.also {
            it.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= it.right - it.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {

                            // your action here
                            return true
                        }
                    }
                    return false

                }
            })
        }
    }

}