package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.databinding.FragmentEditBinding
import com.example.imeiscanner.utilits.DATA_FROM_PHONE_INFO_FRAGMENT


class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        installItemsToEditTexts()
    }

    private fun installItemsToEditTexts() {
        parentFragmentManager.setFragmentResultListener(
            DATA_FROM_PHONE_INFO_FRAGMENT,
            this
        ) { requestKey, result ->

        }
    }

}