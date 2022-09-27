package com.example.imeiscanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.CURRENT_USER
import com.example.imeiscanner.database.NODE_PHONE_DATA_INFO
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.utilits.AppChildEventListener
import com.example.imeiscanner.utilits.AppValueEventListener
import com.example.imeiscanner.utilits.getPhoneModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MainAdapter(var options: FirebaseRecyclerOptions<PhoneDataModel>, var bool: Boolean = true) :
    FirebaseRecyclerAdapter<PhoneDataModel, MainAdapter.PhonesHolder>(options) {

    private var itemClickListener: ((PhoneDataModel) -> Unit)? = null

    private fun initItems(
        holder: PhonesHolder, item: PhoneDataModel
    ) {
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
        val referenceItem =
            REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_USER)
                .child(model.id)
        var item = PhoneDataModel()
////////////////////////////
        if (bool) {
            referenceItem.addValueEventListener(AppValueEventListener {
                item = it.getPhoneModel()
                initItems(holder, item)
            })
        } else {
            referenceItem.addChildEventListener(AppChildEventListener {
                item = it.getPhoneModel()
                initItems(holder, item)
            })
        }
/////////////////////////////

        holder.item.setOnClickListener {
            itemClickListener?.invoke(item)
        }

    }


    inner class PhonesHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_product)
        val imei: TextView = view.findViewById(R.id.tv_serial_number)
        val date: TextView = view.findViewById(R.id.tv_time_product)
        val item: CardView = view.findViewById(R.id.main_list_item_container)

    }

}