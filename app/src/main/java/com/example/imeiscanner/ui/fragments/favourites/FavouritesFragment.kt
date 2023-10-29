package com.example.imeiscanner.ui.fragments.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.imeiscanner.R
import com.example.imeiscanner.database.CURRENT_UID
import com.example.imeiscanner.database.NODE_FAVOURITES
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.databinding.FragmentFavouritesBinding
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.ui.fragments.add_phone.PhoneInfoFragment
import com.example.imeiscanner.utilits.DATA_FROM_MAIN_FRAGMENT
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.POSITION_ITEM
import com.example.imeiscanner.utilits.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding: FragmentFavouritesBinding get() = _binding!!
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!
    private var adapter: FirebaseRecyclerAdapter<PhoneDataModel, FavouritesAdapter.Holder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.favourites_text)
        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        _recyclerView = requireView().findViewById(R.id.rv_favourites_fragment)
        val refFavourites = REF_DATABASE_ROOT.child(NODE_FAVOURITES).child(CURRENT_UID)
        val options = FirebaseRecyclerOptions.Builder<PhoneDataModel>()
            .setQuery(refFavourites, PhoneDataModel::class.java).build()

        adapter = FavouritesAdapter(options)
        recyclerView.adapter = adapter
        adapter?.startListening()
        (adapter as FavouritesAdapter).itemOnCLickListener { item ->
            val bundle = Bundle()
            bundle.putSerializable(POSITION_ITEM, item)
            parentFragmentManager.setFragmentResult(DATA_FROM_MAIN_FRAGMENT, bundle)
            replaceFragment(PhoneInfoFragment())
        }
    }

    override fun onPause() {
        super.onPause()
        adapter = null
        _recyclerView?.adapter = null
        _recyclerView = null
        _binding = null
    }
}