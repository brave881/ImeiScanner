package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.NODE_PHONE_DATA_INFO
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.databinding.FragmentMainBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.adapters.Adapter
import com.example.imeiscanner.utilits.AppValueEventListener
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding
    private lateinit var rv: RecyclerView
    private lateinit var refItems: DatabaseReference
    private lateinit var refPhoneData: DatabaseReference
    private lateinit var refItemListener: AppValueEventListener
    private var mapListener = hashMapOf<DatabaseReference, AppValueEventListener>()
    private lateinit var adapter: FirebaseRecyclerAdapter<PhoneDataModel, PhonesHolder>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }



    private fun initFields() {
        rv = binding.rvMainFragment
        adapter = Adapter()

        refPhoneData.get().addOnSuccessListener { dataSnapshot ->
            listItems = dataSnapshot.children.map {
                it.getValue(PhoneDataModel::class.java) ?: PhoneDataModel()
            }
        }
        refPhoneData.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map {
                it.getValue(PhoneDataModel::class.java) ?: PhoneDataModel()
            }
        })
        rv.adapter = adapter
        adapter.initData(listItems)
    }

    override fun onResume() {
        super.onResume()
        rv = binding.rvMainFragment
        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
        binding.btnOpenPhoneFragment.setOnClickListener {
            replaceFragment(PhoneAddFragment())
        }
        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
        initRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        mapListener.forEach {
            it.key.removeEventListener(it.value)
        }

    }

    class PhonesHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_product)
        val imei: TextView = view.findViewById(R.id.tv_serial_number)
        val date: TextView = view.findViewById(R.id.tv_time_product)
    }


    private fun initRecyclerView() {
        refPhoneData = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO)
        val options = FirebaseRecyclerOptions.Builder<PhoneDataModel>()
            .setQuery(refPhoneData, PhoneDataModel::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<PhoneDataModel, PhonesHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhonesHolder {
                return PhonesHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_products, parent, false)
                )
            }

            override fun onBindViewHolder(
                holder: PhonesHolder,
                position: Int,
                model: PhoneDataModel) {
                refItems = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO)

                refItemListener = AppValueEventListener { datasnapshot ->
                    val item = datasnapshot.children.map {
                        it.getValue(PhoneDataModel::class.java) ?: PhoneDataModel()
                    }
                    initItems(holder, item, position)
                }
                refItems.addListenerForSingleValueEvent(refItemListener)
                mapListener[refItems] = refItemListener
            }

        }
        rv.adapter = adapter
        adapter.startListening()
    }

    private fun initItems(
        holder: PhonesHolder,
        item: List<PhoneDataModel>,
        position: Int
    ) {
        holder.name.text = item[position].phone_name
        holder.date.text = item[position].phone_added_date
        if (item[position].phone_serial_number.isNotEmpty())
            holder.imei.text = item[position].phone_serial_number
        else if (item[position].phone_imei1.isNotEmpty())
            holder.imei.text = item[position].phone_imei1
        else holder.imei.text = item[position].phone_imei2
    }


}