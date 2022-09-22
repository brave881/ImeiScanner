package com.example.imeiscanner.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentSettingsBinding
import com.example.imeiscanner.ui.fragments.BaseFragment
import com.example.imeiscanner.utilits.*
import com.google.firebase.auth.ktx.userProfileChangeRequest

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
        binding.settingsUserPhotoChange.setOnClickListener { changePhoto() }
        if (userGoogleOrPhone() == GOOGLE_PROVIDER_ID) {
            binding.settingsPhoneChange.setOnClickListener { replaceFragment(ChangeUserPhoneFragment()) }
        }
    }

    private fun initFields() {
        binding.settingsUserName.text = USER.name
        binding.settingsUserPhoto.photoDownloadAndSet(USER.photoUrl)
        if (userGoogleOrPhone() == GOOGLE_PROVIDER_ID) {
            binding.settingsUserEmailORPhone.text = USER.email
        } else {
            binding.settingsUserEmailORPhone.text = USER.phone
        }
    }

    private fun changePhoto() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setCropShape(CropImageView.CropShape.OVAL)
                setRequestedSize(600, 600)
                setAspectRatio(1, 1)
            }
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        val uri = result.originalUri
        val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_USER)

        if (uri != null) {
            putFileToStorage(path, uri) {
                getUrlFromStorage(path) { task ->
                    putUserPhotoUrlToDatabase(task) {
                        binding.settingsUserPhoto.photoDownloadAndSet(task)
                        USER.photoUrl = task
                        showToast("Image Changed!")
                        MAIN_ACTIVITY.mAppDrawer.updateHeader()

                    }
                }
            }
        }
    }
}