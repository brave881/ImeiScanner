package com.example.imeiscanner.ui.fragments.settings

import android.Manifest
import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentSettingsBinding
import com.example.imeiscanner.ui.fragments.base.BaseFragment
import com.example.imeiscanner.utilits.*
import java.util.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val CAMERA_SELF_PERMISSION = ContextCompat.checkSelfPermission(MAIN_ACTIVITY, CAMERA)

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
        MAIN_ACTIVITY.title = getString(R.string.setttings)
        initFields()
        initClicks()
        binding.settingsLanguageBtnBlock.setOnClickListener { changeLanguage() }
    }

    private fun changeLanguage() {
        val items = arrayOf("English", "Türkçe", "O'zbekcha")
        var language = sharedPreferences.getString(LANG, "")
        DIALOG_BUILDER
            .setTitle(getString(R.string.choice_language_text))
            .setSingleChoiceItems(items, -1) { dialog, it ->
                when (it) {
                    0 -> {
                        language = "en"
                    }
                    1 -> {
                        language = "tr"
                    }
                    2 -> {
                        language = "uz"
                    }
                }
            }.setPositiveButton(getString(R.string.ok_text)) { dialogInterface, it ->
                setLocale(language!!)
                restartActivity()
            }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .create().show()
    }

    override fun onStop() {
        super.onStop()
        MAIN_ACTIVITY.title = getString(R.string.app_name)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) changePhoto()
        }

    private fun initClicks() {
        binding.settingsUserPhotoChange.setOnClickListener {
            if (CAMERA_SELF_PERMISSION == PackageManager.PERMISSION_GRANTED) changePhoto()
            else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        binding.settingsUserNameChange.setOnClickListener { replaceFragment(ChangeUserNameFragment()) }
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
        binding.settingsLogOutBtn.setOnClickListener { logOutDialog() }
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
        val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

        if (uri != null) {
            putFileToStorage(path, uri) {
                getUrlFromStorage(path) { task ->
                    putUserPhotoUrlToDatabase(task) {
                        binding.settingsUserPhoto.photoDownloadAndSet(task)
                        MAIN_ACTIVITY.mAppDrawer.updateHeader()
                        USER.photoUrl = task
                        updateUserPhotoUrl(task)
                        MAIN_ACTIVITY.mAppDrawer.updateHeader()
                        showToast(getString(R.string.image_changed))
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MAIN_ACTIVITY.menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_delete_user -> accountDeleteDialog()
        }
        return true
    }

}