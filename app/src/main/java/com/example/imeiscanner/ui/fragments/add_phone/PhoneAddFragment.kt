package com.example.imeiscanner.ui.fragments.add_phone

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PhoneAddFragment : BaseFragment(R.layout.fragment_phone_add) {

    private var phoneData: PhoneDataModel? = null
    private var _binding: FragmentPhoneAddBinding? = null
    private val binding: FragmentPhoneAddBinding get() = _binding!!
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
    private var isInitialized: Boolean = false
    private var imei2Boolean: Boolean = false
    private var imei3Boolean: Boolean = false
    private var dateMap = hashMapOf<String, Any>()
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!
    private var adapter: PhoneAddAdapter? = null
    private var intent: Intent? = null
    private var coroutineScope: CoroutineScope? = null
    private var itemList: ArrayList<Uri> = arrayListOf(Uri.EMPTY)
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var progressBar: ProgressBar? = null
    private var percentTextView: TextView? = null
    private val dialog = Dialog(MAIN_ACTIVITY)

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
        _binding = FragmentPhoneAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initResultLauncher()
    }

    override fun onResume() {
        super.onResume()
        installData()
        MAIN_ACTIVITY.title =
            if (phoneData == null) getString(R.string.add_phone_information) else getString(R.string.phone_information)
        dateInstall()
        initFields()
        initFunctions()
        qrScan()
        binding.btnSave.setOnClickListener { saveData() }
    }

    private fun installData() {
        parentFragmentManager.setFragmentResultListener(
            DATA_FROM_PHONE_INFO_FRAGMENT,
            this
        ) { _, result ->
            phoneData = result.getSerializable(POSITION_ITEM) as PhoneDataModel
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.data?.clipData != null) { //this is for to get multiple images
                    val listSize = result.data?.clipData!!.itemCount
                    for (i in 0 until listSize) {
                        if (itemList.size <= 8) {
                            itemList.add(result.data?.clipData!!.getItemAt(i).uri)
                        } else {
                            showToast(MAIN_ACTIVITY.getString(R.string.limit_pick_picture_text))
                        }
                    }
                    adapter?.addItems(itemList)
                    adapter?.notifyDataSetChanged()
                } else if (result.data?.data != null) { // this is for to get single image
                    if (itemList.size <= 8) {
                        itemList.add(result.data?.data!!)
                    } else {
                        showToast(MAIN_ACTIVITY.getString(R.string.limit_pick_picture_text))
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) cameraOrGalleryDialog()
        }


    @SuppressLint("SimpleDateFormat")
    private fun initFields() {
        dialog.setContentView(R.layout.item_progress_loading)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(com.google.zxing.client.android.R.color.zxing_transparent)
        dialog.setCancelable(false)
        progressBar = dialog.findViewById(R.id.progress_bar_deleting)
        percentTextView = dialog.findViewById(R.id.percent_text)

        coroutineScope = CoroutineScope(Dispatchers.IO)
        options = ScanOptions()
        intent = Intent(Intent.ACTION_GET_CONTENT)
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
        initFieldsIfEditing(currentDateTimeString)
        adapter = PhoneAddAdapter()
        adapter!!.addItems(itemList)
        _recyclerView = binding.rvImages
        recyclerView.adapter = adapter
        (adapter as PhoneAddAdapter).addPhotoOnClickListener { onAddPhotoClick() }
        (adapter as PhoneAddAdapter).openPhotoOnClickListener { openPhoto(it) }
        (adapter as PhoneAddAdapter).deletePhotoOnClickListener { deletePhoto(it) }
    }

    private fun initFieldsIfEditing(currentDateTimeString: String) {
        if (!isInitialized) {
            if (phoneData != null) {
                if (phoneData!!.photoList.isNotEmpty()) {
                    phoneData!!.photoList.forEach { itemList.add(it.toUri()) }
                }
                imei1.setText(phoneData!!.phone_imei1)
                imei2.setText(phoneData!!.phone_imei2)
                serialNumber.setText(phoneData!!.phone_serial_number)
                memory.setText(phoneData!!.phone_memory)
                price.setText(phoneData!!.phone_price)
                name.setText(phoneData!!.phone_name)
                batteryInfo.setText(phoneData!!.phone_battery_info)
                date.text = phoneData!!.phone_added_date
            } else {
                date.text = currentDateTimeString
            }
            isInitialized = true
        }
    }

    private fun openPhoto(index: Int) {
        val dialog = Dialog(MAIN_ACTIVITY)
        dialog.setContentView(R.layout.dialog_zoom_image)
        val imageView: ImageView = dialog.findViewById(R.id.dialog_zoom_image)
        val layoutParams = imageView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        imageView.layoutParams = layoutParams
        Glide.with(MAIN_ACTIVITY).load(itemList[index]).into(imageView)
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deletePhoto(index: Int) {
        itemList.removeAt(index)
        adapter!!.submitItems(itemList)
        adapter!!.notifyDataSetChanged()
    }

    private fun onAddPhotoClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    MAIN_ACTIVITY,
                    READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            } else {
                cameraOrGalleryDialog()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    MAIN_ACTIVITY,
                    READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
            } else {
                cameraOrGalleryDialog()
            }
        }
    }

    private fun cameraOrGalleryDialog() {
        val dialog = Dialog(MAIN_ACTIVITY)
        dialog.setContentView(R.layout.dialog_camera_or_gallery)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val cameraContainer: ConstraintLayout = dialog.findViewById(R.id.dialog_camera_container)
        val galleryContainer: ConstraintLayout = dialog.findViewById(R.id.dialog_gallery_container)
        val cancelButton: TextView = dialog.findViewById(R.id.dialog_cancel_btn)

        cameraContainer.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }
        galleryContainer.setOnClickListener {
            pickPictures()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                MAIN_ACTIVITY,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cropImage.launch(
                options {
                    setImageSource(false, includeCamera = true)
//                    setRequestedSize(600, 600)
//                    setAspectRatio(1, 1)
                }
            )
        } else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    @SuppressLint("NotifyDataSetChanged")
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        val uri = result.uriContent       //this gives cropped image
        if (uri != null) {
            if (itemList.size <= 8) {
                itemList.add(uri)
            } else {
                showToast(MAIN_ACTIVITY.getString(R.string.limit_pick_picture_text))
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun pickPictures() {
        intent!!.type = "image/*"
        intent!!.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent!!.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private fun saveData() {
        var totalPercent = 0.0
        if (imei1.text.toString().isNotEmpty()) {
            val id = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID).push().key!!
            dateMap = addDatabaseImei(
                if (phoneData != null) phoneData!!.id else id,
                dateMap,
                name,
                batteryInfo,
                memory,
                date.text.toString(),
                price,
                if (phoneData != null) phoneData!!.favourite_state else false
            )
            coroutineScope?.launch {
                checkImeiFill(dateMap)

                addData(
                    dateMap,
                    imageList = if (itemList.size != 1) {
                        itemList.removeFirst()
                        itemList
                    } else null,
                    id = if (phoneData != null) phoneData!!.id else id,
                    imei1.text.toString(),
                    isEditing = phoneData != null,
                    isBeforePicturesHave = phoneData != null && phoneData!!.photoList.isNotEmpty(),
                    onProgress = { progressPercent ->
                        if (itemList.size != 0) {
                            if (progressPercent == 100.0) {
                                totalPercent += progressPercent / itemList.size
                            }
                            if (totalPercent == 0.0) {
                                dialog.show()
                            }

                            progressBar?.progress = totalPercent.toInt()
                            percentTextView?.text = totalPercent.toInt().toString() + "%"

                            if (totalPercent == 100.0) {
                                async {
                                    delay(250)
                                    dialog.dismiss()
                                }
                            }
                        }
                    }
                )
            }
        } else {
            Toast.makeText(requireContext(), R.string.please_input_imei_1, Toast.LENGTH_LONG).show()
        }
    }

    private fun initFunctions() {
        scanOptions(options)
    }

    private fun dateInstall() {
        binding.buttonPanel.setOnClickListener {
            showDatePicker(requireContext(), date)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _recyclerView?.adapter = null
        _recyclerView = null
        _binding = null
        intent = null
        coroutineScope = null
        dialog.dismiss()
        progressBar = null
        percentTextView = null
//        phoneData = null
    }
}