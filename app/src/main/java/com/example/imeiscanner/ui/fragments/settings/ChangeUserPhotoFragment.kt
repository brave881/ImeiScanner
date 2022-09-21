package com.example.imeiscanner.ui.fragments.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.databinding.FragmentChangeUserPhoneBinding
import com.example.imeiscanner.databinding.FragmentChangeUserPhotoBinding

class ChangeUserPhotoFragment : Fragment(R.layout.fragment_change_user_photo) {

    private lateinit var binding: FragmentChangeUserPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeUserPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

}