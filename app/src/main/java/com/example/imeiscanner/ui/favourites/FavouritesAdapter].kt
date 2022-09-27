package com.example.imeiscanner.ui.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.utilits.AppValueEventListener
import com.example.imeiscanner.utilits.getPhoneModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FavouritesAdapter(val options: FirebaseRecyclerOptions<PhoneDataModel>) :
    FirebaseRecyclerAdapter<PhoneDataModel, FavouritesAdapter.Holder>(options) {

    private var itemClickListener: ((PhoneDataModel) -> Unit)? = null

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_product)
        val imei: TextView = view.findViewById(R.id.tv_serial_number)
        val date: TextView = view.findViewById(R.id.tv_time_product)
        val item: CardView = view.findViewById(R.id.main_list_item_container)
        val star_on: ImageView = view.findViewById(R.id.item_star_on_btn)
        val star_off: ImageView = view.findViewById(R.id.item_star_off_btn)
    }

    private fun initItems(
        holder: Holder, item: PhoneDataModel
    ) {
        holder.star_on.setOnClickListener {
            holder.star_on.visibility = View.GONE
            holder.star_off.visibility = View.VISIBLE
            deleteFavouritesValue(item.id)
        }
        holder.name.text = item.phone_name
        holder.date.text = item.phone_added_date
        if (item.phone_serial_number.isNotEmpty())
            holder.imei.text = item.phone_serial_number
        else if (item.phone_imei1.isNotEmpty())
            holder.imei.text = item.phone_imei1
        else holder.imei.text = item.phone_imei2
    }


    fun itemOnCLickListener(v: (PhoneDataModel) -> Unit) {
        itemClickListener = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_products, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int, model: PhoneDataModel) {
        var item = PhoneDataModel()
        REF_DATABASE_ROOT.child(NODE_FAVOURITES).child(CURRENT_UID).child(model.id)
            .addValueEventListener(AppValueEventListener {
                item = it.getPhoneModel()
                initItems(holder, item)
            })
        holder.item.setOnClickListener { itemClickListener?.invoke(item) }    ///
    }
}