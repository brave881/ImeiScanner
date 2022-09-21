package com.example.imeiscanner.ui.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.imeiscanner.R
import com.example.imeiscanner.databinding.FragmentChangeUserPhoneBinding

class ChangeUserPhoneFragment : Fragment(R.layout.fragment_change_user_phone) {

    private lateinit var binding: FragmentChangeUserPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentChangeUserPhoneBinding.inflate(inflater,container,false)
        return binding.root
    }

}