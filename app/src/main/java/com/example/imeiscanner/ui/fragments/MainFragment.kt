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
import com.example.imeiscanner.utilits.hideKeyboard
import com.example.imeiscanner.utilits.replaceFragment


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding
    private lateinit var rv: RecyclerView
    private val refPhoneData = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO)
    private var listItems = listOf<PhoneDataModel>()
    private lateinit var adapter: Adapter

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
        initFields()
        hideKeyboard()
        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
        binding.btnOpenPhoneFragment.setOnClickListener {
            replaceFragment(PhoneAddFragment())
        }
        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
    }
}