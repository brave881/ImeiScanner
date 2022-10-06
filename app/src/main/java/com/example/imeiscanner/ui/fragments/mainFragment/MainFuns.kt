package com.example.imeiscanner.ui.fragments.mainFragment

import android.annotation.SuppressLint
import android.view.View
import com.example.imeiscanner.database.addFavourites
import com.example.imeiscanner.database.deleteFavouritesValue
import com.example.imeiscanner.models.PhoneDataModel

fun initItems(
    holder: MainAdapter.PhonesHolder, item: PhoneDataModel
) {
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
    item.favourite_state = false
    holder.star_off.visibility = View.VISIBLE
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

fun clearHolderList(holderList: HashMap<MainAdapter.PhonesHolder, PhoneDataModel>) {
    if (holderList.isNotEmpty()) {
        holderList.forEach { (it, _) ->
            it.checkImage.visibility = View.GONE
        }
        holderList.clear()
    }
}