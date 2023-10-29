package com.example.imeiscanner.ui.fragments.add_phone

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.imeiscanner.R
import com.example.imeiscanner.databinding.FragmentPhoneDataBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.fragments.EditFragment
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*

class PhoneInfoFragment : BaseFragment(R.layout.fragment_phone_data) {

    private var items: PhoneDataModel? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: PhoneInfoAdapter? = null
    private var binding: FragmentPhoneDataBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneDataBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.phone_information)
        installData()

        if (items!!.photoList.isNotEmpty()) {
            adapter = PhoneInfoAdapter(items!!.photoList)
            recyclerView = binding?.infoRvImages
            recyclerView?.adapter = adapter
            (adapter as PhoneInfoAdapter).openPhotoOnClickListener { openPhoto(it) }
        }
        changeToEdit()
    }

    private fun openPhoto(index: Int) {
        val dialog = Dialog(MAIN_ACTIVITY)
        dialog.setContentView(R.layout.dialog_zoom_image)
        val imageView: ImageView = dialog.findViewById(R.id.dialog_zoom_image)
        val layoutParams = imageView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        imageView.layoutParams = layoutParams
        Glide.with(MAIN_ACTIVITY).load(items!!.photoList[index]).into(imageView)
        dialog.show()
    }

    private fun changeToEdit() {
        binding!!.btnEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(POSITION_ITEM, items)
            parentFragmentManager.setFragmentResult(DATA_FROM_PHONE_INFO_FRAGMENT, bundle)
            replaceFragment(PhoneAddFragment(), false)
        }
    }

    private fun installData() {
        parentFragmentManager.setFragmentResultListener(
            DATA_FROM_MAIN_FRAGMENT,
            this
        ) { _, result ->
            items = result.getSerializable(POSITION_ITEM) as PhoneDataModel
            binding!!.tvPhoneName.text = items!!.phone_name
            binding!!.tvPhoneSerial.text = items!!.phone_serial_number
            binding!!.tvPhoneImei1.text = items!!.phone_imei1
            binding!!.tvPhoneImei2.text = items!!.phone_imei2
            binding!!.tvPhoneDate.text = items!!.phone_added_date
            binding!!.tvPhoneMemory.text = items!!.phone_memory
            binding!!.tvPhoneState.text = items!!.phone_battery_info
            binding!!.tvPhonePrice.text = items!!.phone_price
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        items = null
        binding = null
        recyclerView = null
        adapter = null
    }
}