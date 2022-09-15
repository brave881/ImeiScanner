package com.example.imeiscanner.utilits

import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.ProfileDrawerItem

class AppDrawer {

    private lateinit var mDrawer: Drawer
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mHeader: AccountHeader
    private lateinit var mCurrentProfile: ProfileDrawerItem

    fun create(){
        initLoader()
        createHeader()
        createDrawer()
        mDrawerLayout=mDrawer.drawerLayout
    }

    private fun createDrawer() {


    }


    private fun createHeader() {

    }

    private fun initLoader() {

    }

}