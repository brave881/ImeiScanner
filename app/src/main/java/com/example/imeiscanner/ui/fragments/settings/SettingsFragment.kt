package com.example.imeiscanner.ui.fragments.settings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentSettingsBinding
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var dialogBuilder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
        initClicks()
        dialogBuilder = AlertDialog.Builder(MAIN_ACTIVITY)
//        updateName(binding.settingsUserName)
    }

    private fun initClicks() {
        binding.settingsUserNameChange.setOnClickListener { replaceFragment(ChangeUserNameFragment()) }
        binding.settingsUserPhotoChange.setOnClickListener { changePhoto() }
        if (userGoogleOrPhone() == PHONE_PROVIDER_ID) {
            binding.settingsPhoneChange.setOnClickListener { replaceFragment(ChangeUserPhoneFragment()) }
        }
    }

    private fun initFields() {
        binding.settingsUserName.text = USER.fullname
        binding.settingsPhoneChange.text = USER.email
        binding.settingsUserNameChange.text = USER.fullname
        binding.settingsUserPhoto.photoDownloadAndSet(USER.photoUrl)
        if (userGoogleOrPhone() == GOOGLE_PROVIDER_ID) {
            binding.settingsUserEmailORPhone.text = USER.email
        } else {
            binding.settingsUserEmailORPhone.text = USER.phone
            binding.settingsPhoneChange.text = USER.phone
            binding.settingsPhoneChange.setOnClickListener { replaceFragment(ChangeUserPhoneFragment()) }
        }
        binding.settingsUserNameChange.setOnClickListener { replaceFragment(ChangeUserNameFragment()) }
        binding.settingsLogOutBtn.setOnClickListener { logOut() }
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
                        showToast("Image Changed!")
                        MAIN_ACTIVITY.mAppDrawer.updateHeader()
                        updateUserPhotoUrl(task)
                        USER.photoUrl = task
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_delete_user -> showAlertDialog()
        }
        return true
    }

    private fun showAlertDialog() {
        dialogBuilder.setTitle(getString(R.string.delete_account_dialog))
            .setMessage(getString(R.string.alert_dialog_message))
            .setPositiveButton(getString(R.string.cancel)) { dialogIntereface, it ->
                deleteUser()
            }
            .setNegativeButton(getString(R.string.delete_text)) { dialogInterface, it ->
                dialogInterface.cancel()
            }.show()
    }
}