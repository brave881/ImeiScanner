package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentMainBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.adapters.MainAdapter
import com.example.imeiscanner.ui.fragments.add_phone.PhoneAddFragment
import com.example.imeiscanner.ui.fragments.add_phone.PhoneInfoFragment
import com.example.imeiscanner.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val LOG = "MainFragment"
    private lateinit var rv: RecyclerView
    private lateinit var refPhoneData: DatabaseReference
    private lateinit var adapter: FirebaseRecyclerAdapter<PhoneDataModel, MainAdapter.PhonesHolder>
    private lateinit var options: FirebaseRecyclerOptions<PhoneDataModel>
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

    fun initFields() {
        rv = binding.rvMainFragment
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
        (adapter as MainAdapter).itemOnClickListener { item ->
            val bundle = Bundle()
            bundle.putSerializable(POSITION_ITEM, item)
            parentFragmentManager.setFragmentResult(DATA_FROM_MAIN_FRAGMENT, bundle)
            replaceFragment(PhoneInfoFragment())
        }

    }

    private fun mySearch(text: String) {

        val query = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID)

        options = FirebaseRecyclerOptions.Builder<PhoneDataModel>()
            .setQuery(
                query.orderByKey().equalTo(text),
                PhoneDataModel::class.java
            ).build()
        adapter = MainAdapter(options, false)
        adapter.startListening()
        rv.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val search: SearchView = item.actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mySearch(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mySearch(newText!!)
                return false
            }

        })
        return true
    }

}