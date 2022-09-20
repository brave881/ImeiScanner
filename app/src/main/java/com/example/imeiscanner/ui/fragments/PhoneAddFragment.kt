package com.example.imeiscanner.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import com.example.imeiscanner.utilits.QrScanner
import com.example.imeiscanner.utilits.showDatePicker
import com.example.imeiscanner.utilits.showToast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


class PhoneAddFragment : Fragment() {
    private lateinit var binding: FragmentPhoneAddBinding
    private lateinit var qrScanner: QrScanner
    private lateinit var options: ScanOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.btnDateUse.setOnClickListener { showDatePicker(requireContext(), binding) }
        qrScanner = QrScanner()
        initFunctions()

    }


    fun initFunctions(){
        scanOptions()
    }

    @SuppressLint("SetTextI18n")
    private fun qrScan(editText: EditText) {
        val DRAWABLE_RIGHT = 2
        editText.also {
            it.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= it.right - it.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                            // start Scanner
                            val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
                                registerForActivityResult(
                                    ScanContract()
                                ) { result ->
                                    if (result.contents == null) {
                                        showToast("Cancelled")
                                    } else {
                                        showToast("Scanned ${result.contents}")
                                    }
                                }

                            barcodeLauncher.launch(options)

                            // end scanner
                            return true
                        }
                    }
                    return false
                }
            })
        }
    }

    private fun scanOptions() {
        options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        options.setPrompt("Scan a barcode")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
    }

}