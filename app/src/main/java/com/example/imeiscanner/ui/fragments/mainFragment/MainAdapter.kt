package com.example.imeiscanner.ui.fragments.mainFragment

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

class MainAdapter(
    var options: FirebaseRecyclerOptions<PhoneDataModel>,
    private val showToolbar: (Boolean) -> Unit
) :
    FirebaseRecyclerAdapter<PhoneDataModel, MainAdapter.PhonesHolder>(options) {

    private val holderList = hashMapOf<MainAdapter.PhonesHolder, PhoneDataModel>()
    private var isEnable = false
    private var itemClickListener: ((PhoneDataModel) -> Unit)? = null
    private lateinit var floatingButton: FloatingActionButton

    inner class PhonesHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_product)
        val imei: TextView = view.findViewById(R.id.tv_serial_number)
        val date: TextView = view.findViewById(R.id.tv_time_product)
        val item: CardView = view.findViewById(R.id.main_list_item_container)
        val checkImage: CircleImageView = view.findViewById(R.id.item_product_check_image)
        val star_on: ImageView = view.findViewById(R.id.item_star_on_btn)
        val star_off: ImageView = view.findViewById(R.id.item_star_off_btn)
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
            if (holderList.contains(holder)) {
                holderList.remove(holder)
                holder.checkImage.visibility = View.GONE
                if (holderList.isEmpty()) {
                    showToolbar(false)
                    MAIN_ACTIVITY.mToolbar.visibility = View.VISIBLE
                    floatingButton.visibility = View.VISIBLE
                    isEnable = false
                }
            } else if (isEnable) {
                selectItem(holder, model)
            } else itemClickListener?.invoke(item)
        }

        holder.item.setOnLongClickListener {
            selectItem(holder, model)
            true
        }
    }

    private fun selectItem(holder: PhonesHolder, model: PhoneDataModel) {
        floatingButton.visibility = View.GONE
        MAIN_ACTIVITY.mToolbar.visibility = View.GONE
        isEnable = true
        holderList[holder] = model
        showToolbar(true)
        holder.checkImage.visibility = View.VISIBLE
    }

    fun deleteSelectedItem() {
        clearHolderList(holderList)
        showToolbar(false)
        isEnable = false
    }

    fun cancelItemSelecting() {
        clearHolderList(holderList)
        showToolbar(false)
        isEnable = false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addFavouritesSelectedI() {
        if (holderList.isNotEmpty()) {
            holderList.forEach { t, u ->
                commitFavourites(t, u)
            }
        }
        clearHolderList(holderList)
    }

    fun initFloatButton(floatingActionButton: FloatingActionButton) {
        floatingButton = floatingActionButton
    }
}

