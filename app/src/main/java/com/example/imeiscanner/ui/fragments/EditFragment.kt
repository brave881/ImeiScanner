package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentEditBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class EditFragment : BaseFragment(R.layout.fragment_edit) {
    private lateinit var binding: FragmentEditBinding
    private lateinit var options: ScanOptions
    private lateinit var imei1: EditText
    private lateinit var imei2: EditText
    private lateinit var serialNumber: EditText
    private lateinit var price: EditText
    private lateinit var dateView: TextView
    private lateinit var battery: EditText
    private lateinit var memory: EditText
    private lateinit var name: EditText
    private lateinit var phoneId: String
    private var favouriteState: Boolean = false
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

    private fun installItemsToEditTexts() {
        parentFragmentManager.setFragmentResultListener(
            DATA_FROM_PHONE_INFO_FRAGMENT,
            this
        ) { _, result ->
            val item = result.getSerializable(POSITION_ITEM) as PhoneDataModel
            dateView.text = item.phone_added_date
            imei1.setText(item.phone_imei1)
            imei2.setText(item.phone_imei2)
            serialNumber.setText(item.phone_serial_number)
            battery.setText(item.phone_battery_info)
            price.setText(item.phone_price)
            memory.setText(item.phone_memory)
            name.setText(item.phone_name)
            phoneId = item.id
            favouriteState = item.favourite_state
        }
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title=getString(R.string.edit_phone_frg)
        dateInstall()
        initFields()
        installItemsToEditTexts()
        initFunctions()
        qrScan()
        saveDate()
    }


    private fun initFields() {
        options = ScanOptions()
        imei1 = binding.phoneAddImei1
        imei2 = binding.phoneAddImei2
        serialNumber = binding.phoneAddSerialNumber
        battery = binding.phoneAddBatteryState
        price = binding.phoneAddPrice
        memory = binding.phoneAddMemory
        name = binding.phoneAddPhoneName
        dateView = binding.phoneAddDate


    }

    private fun saveDate() {
        binding.btnSave.setOnClickListener {
            if (imei1.text.toString().isNotEmpty()
            ) {
                dateMap = addDatabaseImei(
                    phoneId,
                    dateMap,
                    name,
                    battery,
                    memory,
                    dateView.text.toString(),
                    price,
                    favouriteState
                )
                checkImeiFill(dateMap)
                setValuesToFireBase(dateMap, phoneId, imei1.text.toString(),true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_input_imei_1),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun initFunctions() {
        scanOptions(options)
    }


    private fun dateInstall() {
        binding.buttonPanel.setOnClickListener {
            showDatePicker(requireContext(), dateView)
        }
    }

    /// scanner block
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

    // end scanner block
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

}