package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding


class PhoneAddFragment : Fragment() {
    private lateinit var binding: FragmentPhoneAddBinding

    override

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhoneAddBinding.inflate(inflater, container, false)
        return binding.root
    }

}