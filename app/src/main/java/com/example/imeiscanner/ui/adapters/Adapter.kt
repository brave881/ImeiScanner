package com.example.imeiscanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.imeiscanner.databinding.ItemProductsBinding
import com.example.imeiscanner.models.ProductItemModel

class Adapter : RecyclerView.Adapter<Adapter.VH>() {

    private val list = ArrayList<ProductItemModel>()
    var setOnClickItem: ((ProductItemModel) -> Unit)? = null

    class VH(val binding: ItemProductsBinding) : ViewHolder(binding.root) {
        fun bind(pim: ProductItemModel) {
            binding.tvNameProduct.text = pim.pName
            binding.tvTimeProduct.text = pim.pTime
            binding.tvSerialNumber.text = pim.pSerialNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position])
        holder.binding.mainListItemContainer.setOnClickListener {
            setOnClickItem?.invoke(list[position])
        }
    }


    fun setOnClickItem(setOnClickItem: ((ProductItemModel) -> Unit)?) {
        this.setOnClickItem = setOnClickItem
    }

    override fun getItemCount(): Int = list.size


}