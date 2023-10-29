package com.example.imeiscanner.ui.fragments.add_phone

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imeiscanner.R
import com.example.imeiscanner.utilits.MAIN_ACTIVITY

class PhoneAddAdapter : RecyclerView.Adapter<PhoneAddAdapter.Holder>() {

    private var addPhotoClickListener: (() -> Unit?)? = null
    private var deletePhotoClickListener: ((Int) -> Unit?)? = null
    private var openPhotoClickListener: ((Int) -> Unit?)? = null
    private val imageList: ArrayList<Uri> = arrayListOf()

    fun addPhotoOnClickListener(v: () -> Unit) {
        addPhotoClickListener = v
    }

    fun deletePhotoOnClickListener(v: (Int) -> Unit) {
        deletePhotoClickListener = v
    }

    fun openPhotoOnClickListener(v: (Int) -> Unit) {
        openPhotoClickListener = v
    }

    fun submitItems(item: List<Uri>) {
        imageList.clear()
        imageList.addAll(item)
    }

    fun addItems(item: List<Uri>) {
        imageList.addAll(item)
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val itemContainer: ConstraintLayout = view.findViewById(R.id.item_image_container)
        val imageContainer: ConstraintLayout = view.findViewById(R.id.item_image_container_image)
        val addPhotoContainer: ConstraintLayout = view.findViewById(R.id.item_add_image_container)
        val deleteImage: ImageButton = view.findViewById(R.id.item_image_delete_btn)
        val image: ImageView = view.findViewById(R.id.item_image_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_images, parent, false)
        )
    }

    override fun getItemCount() = imageList.count()

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (imageList.size == position + 1) {
            holder.addPhotoContainer.visibility = View.VISIBLE
            holder.imageContainer.visibility = View.GONE
            holder.itemContainer.setOnClickListener { addPhotoClickListener?.invoke() }
        } else {
            holder.addPhotoContainer.visibility = View.GONE
            holder.imageContainer.visibility = View.VISIBLE
            Glide.with(MAIN_ACTIVITY).load(imageList[position + 1]).into(holder.image)
            holder.image.setOnClickListener { openPhotoClickListener?.invoke(position+1) }
            holder.deleteImage.setOnClickListener { deletePhotoClickListener?.invoke(position+1) }
        }
    }
}