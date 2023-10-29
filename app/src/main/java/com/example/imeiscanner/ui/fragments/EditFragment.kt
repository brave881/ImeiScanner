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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditFragment : BaseFragment(R.layout.fragment_edit) {

    private var _binding: FragmentEditBinding? = null
    private val binding: FragmentEditBinding get() = _binding!!
    private var _options: ScanOptions? = null
    private val options: ScanOptions get() = _options!!
    private var _imei1: EditText? = null
    private val imei1: EditText get() = _imei1!!
    private var imei2: EditText? = null
    private var _serialNumber: EditText? = null
    private val serialNumber: EditText get() = _serialNumber!!
    private var _price: EditText? = null
    private val price: EditText get() = _price!!
    private var _dateView: TextView? = null
    private val dateView: TextView get() = _dateView!!
    private var _battery: EditText? = null
    private val battery: EditText get() = _battery!!
    private var _memory: EditText? = null
    private val memory: EditText get() = _memory!!
    private var _name: EditText? = null
    private val name: EditText get() = _name!!
    private var _phoneId: String? = null
    private val phoneId: String get() = _phoneId!!
    private var favouriteState: Boolean = false
    private var imei1Boolean: Boolean = false
    private var imei2Boolean: Boolean = false
    private var imei3Boolean: Boolean = false
    private var dateMap = hashMapOf<String, Any>()
    private var coroutineScope: CoroutineScope? = null


    private var barcodeLauncher = registerForActivityResult(ScanContract()) { resultt ->
        if (resultt.contents == null) {
            showToast(getString(R.string.cancelled_from_barcode))
        } else {
            installResultForET(resultt)
        }
    }

    private fun checkImeiFill(dateMap: HashMap<String, Any>) {
        if (!imei1Boolean) dateMap[CHILD_IMEI1] = toStringEditText(imei1)
        if (!imei2Boolean) dateMap[CHILD_IMEI2] = toStringEditText(imei2!!)
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
            imei2?.setText(item.phone_imei2)
            serialNumber.setText(item.phone_serial_number)
            battery.setText(item.phone_battery_info)
            price.setText(item.phone_price)
            memory.setText(item.phone_memory)
            name.setText(item.phone_name)
            _phoneId = item.id
            favouriteState = item.favourite_state
        }
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.edit_phone_frg)
        dateInstall()
        initFields()
        installItemsToEditTexts()
        initFunctions()
        qrScan()
        saveDate()
    }


    private fun initFields() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        _options = ScanOptions()
        _imei1 = binding.phoneAddImei1
        imei2 = binding.phoneAddImei2
        _serialNumber = binding.phoneAddSerialNumber
        _battery = binding.phoneAddBatteryState
        _price = binding.phoneAddPrice
        _memory = binding.phoneAddMemory
        _name = binding.phoneAddPhoneName
        _dateView = binding.phoneAddDate


    }

    private fun saveDate() {
        binding.btnSave.setOnClickListener {
            if (imei1.text.toString().isNotEmpty()) {
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
                coroutineScope?.launch {
                    addData(
                        dateMap,
                        id = phoneId,
                        imei1 = imei1.text.toString(),
                        isEditing = true,
                        isBeforePicturesHave = false,
                        onProgress = {}
                    )
                }
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
            imei2?.setText(result)
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
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _options = null
        _imei1 = null
        imei2 = null
        _serialNumber = null
        _price = null
        _dateView = null
        _memory = null
        _phoneId = null
        _name = null
        _battery = null
        coroutineScope = null
    }
}