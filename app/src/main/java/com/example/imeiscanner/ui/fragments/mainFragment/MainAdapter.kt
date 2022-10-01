package com.example.imeiscanner.ui.fragments.mainFragment

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

class MainAdapter(var options: FirebaseRecyclerOptions<PhoneDataModel>) :
    FirebaseRecyclerAdapter<PhoneDataModel, MainAdapter.PhonesHolder>(options) {

    private var itemClickListener: ((PhoneDataModel) -> Unit)? = null

    inner class PhonesHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_product)
        val imei: TextView = view.findViewById(R.id.tv_serial_number)
        val date: TextView = view.findViewById(R.id.tv_time_product)
        val item: CardView = view.findViewById(R.id.main_list_item_container)
        val star_on: ImageView = view.findViewById(R.id.item_star_on_btn)
        val star_off: ImageView = view.findViewById(R.id.item_star_off_btn)
    }

    private fun initItems(
        holder: PhonesHolder, item: PhoneDataModel
    ) {
        holder.star_off.setOnClickListener {
            holder.star_on.visibility = View.VISIBLE
            holder.star_off.visibility = View.GONE
            item.favourite_state=true
            addFavourites(item)
        }
        holder.star_on.setOnClickListener {
            holder.star_on.visibility = View.GONE
            holder.star_off.visibility = View.VISIBLE
            item.favourite_state=false
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhonesHolder {
        return PhonesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_products, parent, false)
        )
    }

    fun itemOnClickListener(v: (PhoneDataModel) -> Unit) {
        itemClickListener = v
    }

    override fun onBindViewHolder(
        holder: PhonesHolder,
        position: Int,
        model: PhoneDataModel
    ) {
        if (model.favourite_state) {
            holder.star_on.visibility = View.VISIBLE
            holder.star_off.visibility = View.GONE
        }

        var item = PhoneDataModel()
        val referenceItem =
            REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID)
                .child(model.id)


        referenceItem.addValueEventListener(AppValueEventListener {
            item = it.getPhoneModel()
            initItems(holder, item)
        })

        holder.item.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }
}