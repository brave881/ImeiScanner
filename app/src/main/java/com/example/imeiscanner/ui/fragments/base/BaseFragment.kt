package com.example.imeiscanner.ui.fragments.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.imeiscanner.utilits.MAIN_ACTIVITY

open class BaseFragment(val layout: Int) : Fragment() {

    protected lateinit var mRootView: View



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(layout, container, false)
        return mRootView
    }

    override fun onStart() {
        super.onStart()
        MAIN_ACTIVITY.mAppDrawer.disableDrawer()
    }
}