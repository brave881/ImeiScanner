package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.nio.channels.FileLock
import java.text.SimpleDateFormat
import java.util.*


class PhoneAddFragment : BaseFragment(R.layout.fragment_phone_add) {
    private lateinit var binding: FragmentPhoneAddBinding
    private lateinit var options: ScanOptions
    private lateinit var imei1: EditText
    private lateinit var imei2: EditText
    private lateinit var serialNumber: EditText
    private lateinit var price: EditText
    private var date: String = ""
    private lateinit var batteryInfo: EditText
    private lateinit var memory: EditText
    private lateinit var name: EditText
    private var imei1Boolean: Boolean = false
    private var imei2Boolean: Boolean = false
    private var imei3Boolean: Boolean = false


    private var barcodeLauncher = registerForActivityResult(ScanContract()) { resultt ->
        if (resultt.contents == null) {
            showToast("Cancelled")
        } else {
            installResultForET(resultt)
        }
    }
    private fun checkImeiFill(dateMap: HashMap<String, Any>) {
        if (!imei1Boolean) dateMap[CHILD_IMEI1] = toStringEditText(imei1)
        if (!imei2Boolean) dateMap[CHILD_IMEI2] = toStringEditText(imei2)
        if (!imei3Boolean) dateMap[CHILD_SERIAL_NUMBER] = toStringEditText(serialNumber)
        val a :Float =
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

    private fun addDatabaseImei() {
        val dateMap = hashMapOf<String, Any>()
        dateMap[CHILD_PHONE_NAME] = toStringEditText(name)
        dateMap[CHILD_BATTERY_INFO] = toStringEditText(batteryInfo)
        dateMap[CHILD_PHONE_MEMORY] = toStringEditText(memory)
        dateMap[CHILD_PHONE_ADDED_DATE] = date
        dateMap[CHILD_PHONE_PRICE] = toStringEditText(price)
        checkImeiFill(dateMap)
        setValuesToFireBase(dateMap)
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        saveDate()
    }

    private fun initFields() {
        options = ScanOptions()
        imei1 = binding.phoneAddPhoneImei1
        imei2 = binding.phoneAddPhoneImei2
        serialNumber = binding.phoneAddPhoneSerialNumber
        memory = binding.phoneAddPhoneMemory
        price = binding.phoneAddPhonePrice
        name = binding.phoneAddPhoneName
        batteryInfo = binding.phoneAddPhoneBattery

    }

    private fun saveDate() {
        binding.btnSave.setOnClickListener {
            if (imei1.text.toString().isNotEmpty() or imei2.text.toString()
                    .isNotEmpty() or serialNumber.text.toString().isNotEmpty()
            ) {
                addDatabaseImei()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter SerialNumber or Imei1 or Imei2",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initFunctions() {
        scanOptions(options)
    }


    private fun dateInstall() {
        binding.btnDate.setOnClickListener {
            date = showDatePicker(binding, requireContext())
        }

        if (date.isEmpty()) {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale(""))
            date = sdf.format(calendar.time)
        }
    }


}