package com.example.imeiscanner.ui.fragments.base

import androidx.fragment.app.Fragment
import com.example.imeiscanner.utilits.MAIN_ACTIVITY

class BaseChangeFragment(layout:Int):Fragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        MAIN_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }
   fun hideKeyboard(){

   }
}