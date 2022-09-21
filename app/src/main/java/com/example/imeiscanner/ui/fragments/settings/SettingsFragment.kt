package com.example.imeiscanner.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.imeiscanner.R
import com.example.imeiscanner.database.USER
import com.example.imeiscanner.database.authGoogleOrPhone
import com.example.imeiscanner.databinding.FragmentSettingsBinding
import com.example.imeiscanner.ui.fragments.BaseFragment
import com.example.imeiscanner.utilits.GOOGLE
import com.example.imeiscanner.utilits.photoDownloadAndSet
import com.example.imeiscanner.utilits.replaceFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initFields()
        initClicks()
    }

    private fun initClicks() {
        binding.settingsUserNameChange.setOnClickListener { replaceFragment(ChangeUserNameFragment()) }
        binding.settingsUserPhotoChange.setOnClickListener { replaceFragment(ChangeUserPhotoFragment()) }
        if (authGoogleOrPhone() == GOOGLE) {
            binding.settingsPhoneChange.setOnClickListener { replaceFragment(ChangeUserPhoneFragment()) }
        }
    }

    private fun initFields() {
        binding.settingsUserName.text = USER.name
        binding.settingsUserPhoto.photoDownloadAndSet(USER.photoUrl)
        if (authGoogleOrPhone() == GOOGLE) {
            binding.settingsUserEmailORPhone.text = USER.email
        } else {
            binding.settingsUserEmailORPhone.text = USER.phone
        }
    }
}
