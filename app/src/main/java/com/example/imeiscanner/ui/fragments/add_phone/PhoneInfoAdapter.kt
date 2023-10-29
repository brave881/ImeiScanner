package com.example.imeiscanner.ui.fragments.add_phone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imeiscanner.R
import com.example.imeiscanner.utilits.MAIN_ACTIVITY

class PhoneInfoAdapter(private val listPhotos: List<String>) :
    RecyclerView.Adapter<PhoneInfoAdapter.Holder>() {

    private var openPhotoClickListener: ((Int) -> Unit?)? = null

    fun openPhotoOnClickListener(v: (Int) -> Unit) {
        openPhotoClickListener = v
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_info_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_images_info, parent, false)
        )
    }

    override fun getItemCount() = listPhotos.count()

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(MAIN_ACTIVITY).load(listPhotos[position]).into(holder.imageView)
        holder.imageView.setOnClickListener { openPhotoClickListener?.invoke(position) }
    }
}