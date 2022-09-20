package com.example.imeiscanner.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import com.example.imeiscanner.utilits.QrScanner
import com.example.imeiscanner.utilits.showToast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*


class PhoneAddFragment : Fragment() {
    private lateinit var binding: FragmentPhoneAddBinding
    private lateinit var qrScanner: QrScanner
    private lateinit var options: ScanOptions
    private var imei1: Int = 1
    private var imei2: Int = 2
    private var serialNum: Int = 3
    private lateinit var result: String
    private  var barcodeLauncher = registerForActivityResult(ScanContract()) { resultt ->
        if (resultt.contents == null) {
            showToast("Cancelled")
        } else {
              result = resultt.contents
              resultt.originalIntent

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.btnDate.setOnClickListener { showDatePicker() }
        initFunctions()
        var a = barcodeLauncher.launch(options)
        qrScan(binding.phoneAddPhoneImei1).setText(result)
        qrScan(binding.phoneAddPhoneImei2).setText(result)
        qrScan(binding.phoneAddPhoneSerialNumber).setText(result)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @SuppressLint("SetTextI18n")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year1, month1, dayOfMonth ->
                binding.btnDate.text = "$dayOfMonth/${month1 + 1}/$year1"
            }, year, month, day
        )

        datePickerDialog.show()
    }


    fun initFunctions() {
        scanOptions()
    }

    @SuppressLint("SetTextI18n")
    private fun qrScan(editText: EditText): EditText {
        val DRAWABLE_RIGHT = 2
        editText.also {
            it.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= it.right - it.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                            // start Scanner
                            barcodeLauncher.launch(options)

                            // end scanner
                            return true
                        }
                    }
                    return false
                }
            })
        }
        return editText
    }

    private fun scanOptions() {
        options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        options.setPrompt("Scan a barcode")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
    }

}