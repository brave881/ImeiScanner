package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.databinding.FragmentMainBinding


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initFields()
    }


    private fun initFields() {
        rv = binding.rvMainFragment
    }


    override fun onResume() {
        super.onResume()

        binding.btnOpenPhoneFragment.setOnClickListener {
            replaceFragment(PhoneAddFragment())
        }

    }



    override fun onResume() {
        super.onResume()

        MAIN_ACTIVITY.mAppDrawer.enableDrawer()
    }
}