package com.example.imeiscanner.ui.fragments.mainFragment

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.imeiscanner.database.addFavourites
import com.example.imeiscanner.database.deleteFavouritesValue
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.TAG

fun initItems(
    holder: MainAdapter.PhonesHolder, item: PhoneDataModel
) {

    if (item.photoList.isNotEmpty()) {
        Glide.with(MAIN_ACTIVITY).load(item.photoList.first()).centerCrop()
            .into(holder.productImage)
    }

    holder.star_off.setOnClickListener {
        commitFavourites(holder, item)
    }
    holder.star_on.setOnClickListener {
        deleteFavourites(holder, item)
    }
    holder.name.text = item.phone_name
    holder.date.text = item.phone_added_date
    if (item.phone_serial_number.isNotEmpty())
        holder.imei.text = item.phone_serial_number
    else if (item.phone_imei1.isNotEmpty())
        holder.imei.text = item.phone_imei1
    else holder.imei.text = item.phone_imei2
}

fun deleteFavourites(
    holder: MainAdapter.PhonesHolder,
    item: PhoneDataModel
) {
    holder.star_on.visibility = View.GONE
    holder.star_off.visibility = View.VISIBLE
    item.favourite_state = false
    deleteFavouritesValue(item.id)
}

fun commitFavourites(
    holder: MainAdapter.PhonesHolder,
    item: PhoneDataModel
) {
    holder.star_on.visibility = View.VISIBLE
    holder.star_off.visibility = View.GONE
    item.favourite_state = true
    addFavourites(item)
}

fun clearSelectedList(holderList: HashMap<PhoneDataModel, MainAdapter.PhonesHolder>) {
    if (holderList.isNotEmpty()) {
        holderList.forEach { (_, it) ->
            it.checkImage.visibility = View.GONE
            it.star_off.isClickable = true
            it.star_on.isClickable = true
        }
        holderList.clear()
    }
}