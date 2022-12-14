package com.example.imeiscanner.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.imeiscanner.R
import com.example.imeiscanner.database.USER
import com.example.imeiscanner.database.updateFullNameFromDatabase
import com.example.imeiscanner.databinding.FragmentChangeUserNameBinding
import com.example.imeiscanner.ui.fragments.base.BaseChangeFragment
import com.example.imeiscanner.ui.fragments.mainFragment.MainAdapter
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.showToast

class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {

    private lateinit var binding: FragmentChangeUserNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.your_name)
        binding.stChangeUsernameInput.setText(USER.fullname)
    }

    override fun change() {
        val username = binding.stChangeUsernameInput.text.toString()
        if (username.isEmpty()) {
            showToast(getString(R.string.Username_is_empty_toast))
        } else {
            updateFullNameFromDatabase(username)
            USER.fullname = username
            MAIN_ACTIVITY.mAppDrawer.updateHeader()
        }
    }
}