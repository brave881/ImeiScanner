package com.example.imeiscanner.ui.fragments.mainFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.CURRENT_UID
import com.example.imeiscanner.database.NODE_PHONE_DATA_INFO
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.database.deleteSelectedItems
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.utilits.AppValueEventListener
import com.example.imeiscanner.utilits.DIALOG_BUILDER
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.getPhoneModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

class MainAdapter(
    var options: FirebaseRecyclerOptions<PhoneDataModel>, private val showToolbar: (Boolean) -> Unit
) : FirebaseRecyclerAdapter<PhoneDataModel, MainAdapter.PhonesHolder>(options) {

    private val selectedItemsList = hashMapOf<MainAdapter.PhonesHolder, PhoneDataModel>()
    private val holdersList = hashMapOf<MainAdapter.PhonesHolder, PhoneDataModel>()
    private var itemClickListener: ((PhoneDataModel) -> Unit)? = null
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var countTextView: TextView
    private var count: Int = 0
    private var isEnable = false
    private var selectedVisibleItemsSize = 0
    private var bool = true

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
            LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        )
    }

    fun itemOnClickListener(v: (PhoneDataModel) -> Unit) {
        itemClickListener = v
    }

    override fun onBindViewHolder(
        holder: PhonesHolder, position: Int, model: PhoneDataModel
    ) {
        if (model.favourite_state) {
            holder.star_on.visibility = View.VISIBLE
            holder.star_off.visibility = View.GONE
        } else {
            holder.star_on.visibility = View.GONE
            holder.star_off.visibility = View.VISIBLE
        }

        if (model.id.isEmpty()) bool = false
        holdersList[holder] = model
        var item = PhoneDataModel()
        val referenceItem =
            REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID).child(model.id)
        referenceItem.addValueEventListener(AppValueEventListener {
            item = it.getPhoneModel()
            initItems(holder, item)
        })

        holder.item.setOnClickListener {
            if (holder.checkImage.isVisible) {
                selectedItemsList.remove(holder)
                countTextView.text = (--count).toString()
                holder.checkImage.visibility = View.GONE
                if (selectedItemsList.isEmpty()) {
                    showToolbar(false)
                    isEnable = false
                    MAIN_ACTIVITY.mToolbar.visibility = View.VISIBLE
                    floatingButton.visibility = View.VISIBLE
                }
            } else if (isEnable) {
                selectItem(holder, model)
            } else itemClickListener?.invoke(item)
        }

        holder.item.setOnLongClickListener {
            if (selectedItemsList.isEmpty()) {
                selectItem(holder, model)
            }
            true
        }
    }

    private fun selectItem(holder: PhonesHolder, model: PhoneDataModel) {
        floatingButton.visibility = View.GONE
        MAIN_ACTIVITY.mToolbar.visibility = View.GONE
        selectedItemsList[holder] = model
        countTextView.text = (++count).toString()
        showToolbar(true)
        isEnable = true
        holder.checkImage.visibility = View.VISIBLE
    }

    fun deleteSelectedItem() {
        DIALOG_BUILDER.setTitle(MAIN_ACTIVITY.getString(R.string.delete_data))
            .setMessage(MAIN_ACTIVITY.getString(R.string.delete_item_message))
            .setPositiveButton(MAIN_ACTIVITY.getString(R.string.delete_text)) { dialogInterFace, it ->
                deleteItems()
            }.setNegativeButton(MAIN_ACTIVITY.getString(R.string.cancel)) { dialogInterFace, it ->
                dialogInterFace.cancel()
                cancelItemSelecting()
            }.show()
    }

    private fun deleteItems() {
        count = 0
        selectedItemsList.forEach { (t, u) ->
            if (u.favourite_state)
                deleteFavourites(t, u)
            deleteSelectedItems(u.id)
        }
        clearSelectedList(selectedItemsList)
        showToolbar(false)
        isEnable = false
    }

    fun cancelItemSelecting() {
        count = 0
        clearSelectedList(selectedItemsList)
        showToolbar(false)
        isEnable = false
    }

    fun addFavouritesSelectedI() {
        selectedItemsIsVisible()
        when (selectedVisibleItemsSize) {
            selectedItemsList.size -> {
                selectedItemsList.forEach { (t, u) ->
                    deleteFavourites(t, u)
                }
            }
            0 -> {
                selectedItemsList.forEach { (t, u) ->
                    commitFavourites(t, u)
                }
            }
        }
        selectedVisibleItemsSize = 0
        count = 0
        clearSelectedList(selectedItemsList)
    }

    private fun selectedItemsIsVisible() {
        if (selectedItemsList.isNotEmpty()) {
            selectedItemsList.forEach { (it, model) ->
                if (model.favourite_state) selectedVisibleItemsSize++
            }
        }
    }

    fun initFloatButton(floatingActionButton: FloatingActionButton) {
        floatingButton = floatingActionButton
    }

    fun initCountView(t: TextView) {
        countTextView = t
    }

    fun unselectAll() {
        selectedItemsList.clear()
        count = 0
        countTextView.text = count.toString()
        holdersList.forEach { (t, _) ->
            t.checkImage.visibility = View.GONE
        }
    }

    fun selectAll() {
        selectedItemsList.clear()
        count = holdersList.size
        countTextView.text = count.toString()
        holdersList.forEach { (holder, model) ->
            holder.checkImage.visibility = View.VISIBLE
            selectedItemsList[holder] = model
        }
    }
}

