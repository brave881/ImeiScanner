package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.CHILD_IMEI1
import com.example.imeiscanner.database.CURRENT_UID
import com.example.imeiscanner.database.NODE_PHONE_DATA_INFO
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.databinding.FragmentMainBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.adapters.MainAdapter
import com.example.imeiscanner.ui.fragments.add_phone.PhoneAddFragment
import com.example.imeiscanner.ui.fragments.add_phone.PhoneInfoFragment
import com.example.imeiscanner.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val LOG = "MainFragment"
    private lateinit var rv: RecyclerView
    private lateinit var refPhoneData: DatabaseReference
    private lateinit var adapter: FirebaseRecyclerAdapter<PhoneDataModel, MainAdapter.PhonesHolder>
    private lateinit var scannerButton: Button
    private lateinit var scanOptions: ScanOptions
    private lateinit var options: FirebaseRecyclerOptions<PhoneDataModel>
    private lateinit var searchView: SearchView
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            showToast(getString(R.string.cancelled_from_barcode))
        } else {
            installResultForET(result.contents)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
        initFields()
        hideKeyboard()
        binding.btnOpenPhoneFragment.setOnClickListener {
            replaceFragment(PhoneAddFragment())
        }
        initRecyclerView()
    }


    private fun initFields() {
        rv = binding.rvMainFragment
        scanOptions = ScanOptions()
        scanOptions(scanOptions)
    }

    private fun installResultForET(result: String) {
        searchView.setQuery(result, false)
        searchInit()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }


    private fun initRecyclerView() {
        refPhoneData = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID)
        val options = FirebaseRecyclerOptions.Builder<PhoneDataModel>()
            .setQuery(refPhoneData, PhoneDataModel::class.java).build()
        adapter = MainAdapter(options)
        rv.adapter = adapter
        adapter.startListening()
        clickItem()
    }

    private fun clickItem() {
        (adapter as MainAdapter).itemOnClickListener { item ->
            val bundle = Bundle()
            bundle.putSerializable(POSITION_ITEM, item)
            parentFragmentManager.setFragmentResult(DATA_FROM_MAIN_FRAGMENT, bundle)
            replaceFragment(PhoneInfoFragment())
        }
    }

    private fun mySearch(text: String) {
        options = FirebaseRecyclerOptions.Builder<PhoneDataModel>()
            .setQuery(
                refPhoneData.orderByChild(CHILD_IMEI1).startAt(text).endAt(text + "\uf8ff"),
                PhoneDataModel::class.java
            ).build()
        adapter = MainAdapter(options)
        adapter.startListening()
        rv.adapter = adapter
        clickItem()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.menu_search_btn)
        val searchWithQRCode = menu.findItem(R.id.menu_scanner_btn)
        scannerButton = searchWithQRCode.actionView as Button
        searchView = searchItem.actionView as SearchView
        searchInit()
        scannerButton.setOnClickListener {
            barcodeLauncher.launch(scanOptions)
        }
    }

    private fun searchInit() {
        searchView.setOnQueryTextListener(AppSearchView {
            mySearch(it)
        })
    }

}