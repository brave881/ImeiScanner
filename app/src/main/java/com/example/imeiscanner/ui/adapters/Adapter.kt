package com.example.imeiscanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.imeiscanner.databinding.ItemProductsBinding
import com.example.imeiscanner.models.PhoneDataModel

class Adapter : RecyclerView.Adapter<Adapter.VH>() {

    private val listItems = ArrayList<PhoneDataModel>()
    var setOnClickItem: ((PhoneDataModel) -> Unit)? = null

    class VH(val binding: ItemProductsBinding) : ViewHolder(binding.root) {
        fun bind(pim: PhoneDataModel) {
            binding.tvNameProduct.text = pim.phone_name
            binding.tvTimeProduct.text = pim.phone_added_date
            if (pim.phone_serial_number.isNotEmpty())
                binding.tvSerialNumber.text = pim.phone_serial_number
            else if (pim.phone_imei1.isNotEmpty())
                binding.tvSerialNumber.text = pim.phone_imei1
            else binding.tvSerialNumber.text = pim.phone_imei2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(listItems[position])
        holder.binding.mainListItemContainer.setOnClickListener {
            setOnClickItem?.invoke(listItems[position])
        }
    }

    fun initData(list: List<PhoneDataModel>) {
        listItems.clear()
        listItems.addAll(list)
        notifyDataSetChanged()
    }


    fun setOnClickItem(setOnClickItem: ((PhoneDataModel) -> Unit)?) {
        this.setOnClickItem = setOnClickItem
    }

    override fun getItemCount(): Int = listItems.size


}