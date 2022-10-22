package com.example.imeiscanner.ui.fragments.add_phone

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.*


class PhoneAddFragment : BaseFragment(R.layout.fragment_phone_add) {

    private lateinit var binding: FragmentPhoneAddBinding
    private lateinit var options: ScanOptions
    private lateinit var imei1: EditText
    private lateinit var imei2: EditText
    private lateinit var serialNumber: EditText
    private lateinit var price: EditText
    private lateinit var date: TextView
    private lateinit var batteryInfo: EditText
    private lateinit var memory: EditText
    private lateinit var name: EditText
    private var imei1Boolean: Boolean = false
    private var imei2Boolean: Boolean = false
    private var imei3Boolean: Boolean = false
    private var dateMap = hashMapOf<String, Any>()

    private var barcodeLauncher = registerForActivityResult(ScanContract()) { resultt ->
        if (resultt.contents == null) {
            showToast(getString(R.string.cancelled_from_barcode))
        } else {
            installResultForET(resultt)
        }
    }

    private fun checkImeiFill(dateMap: HashMap<String, Any>) {
        if (!imei1Boolean) dateMap[CHILD_IMEI1] = toStringEditText(imei1)
        if (!imei2Boolean) dateMap[CHILD_IMEI2] = toStringEditText(imei2)
        if (!imei3Boolean) dateMap[CHILD_SERIAL_NUMBER] = toStringEditText(serialNumber)
    }

    private fun qrScan() {
        binding.btnImei1.setOnClickListener {
            barcodeLauncher.launch(options)
            imei1Boolean = true
        }

        binding.btnImei2.setOnClickListener {
            barcodeLauncher.launch(options)
            imei2Boolean = true
        }

        binding.btnSerialNumber.setOnClickListener {
            barcodeLauncher.launch(options)
            imei3Boolean = true
        }

    }

    private fun installResultForET(resultt: ScanIntentResult) {
        val result = resultt.contents
        showToast(resultt.contents)
        if (imei1Boolean) {
            imei1Boolean = false
            imei1.setText(result)
        } else if (imei2Boolean) {
            imei2.setText(result)
            imei2Boolean = false
        } else {
            imei3Boolean = false
            serialNumber.setText(result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dateInstall()
        initFields()
        initFunctions()
        qrScan()
        binding.btnSave.setOnClickListener { saveDate() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initFields() {
        options = ScanOptions()
        imei1 = binding.phoneAddImei1
        imei2 = binding.phoneAddImei2
        serialNumber = binding.phoneAddSerialNumber
        memory = binding.phoneAddMemory
        price = binding.phoneAddPrice
        name = binding.phoneAddPhoneName
        batteryInfo = binding.phoneAddBatteryState
        val d = Calendar.getInstance()
        val currentDateTimeString =
            SimpleDateFormat(getString(R.string.dd_mm_yy_text)).format(d.time)
        date = binding.phoneAddDate
        date.text = currentDateTimeString
    }

    private fun saveDate() {
        if (imei1.text.toString().isNotEmpty()) {
            val id = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID).push().key!!
            dateMap = addDatabaseImei(
                id,
                dateMap,
                name,
                batteryInfo,
                memory,
                date.text.toString(),
                price,
                false
            )
            checkImeiFill(dateMap)
            setValuesToFireBase(dateMap, id, imei1.text.toString(),false)
        } else {
            Toast.makeText(requireContext(), R.string.please_input_imei_1, Toast.LENGTH_LONG).show()
        }
    }

    private fun initFunctions() {
        scanOptions(options)
    }

    private fun dateInstall() {
        binding.phoneAddDate.setOnClickListener {
            showDatePicker(requireContext(), date)
        }
    }
}