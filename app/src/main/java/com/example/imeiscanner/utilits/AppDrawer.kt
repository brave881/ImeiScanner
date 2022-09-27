package com.example.imeiscanner.utilits

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.ui.fragments.AboutFragment
import com.example.imeiscanner.ui.favourites.FavouritesFragment
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.ui.fragments.settings.SettingsFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

class AppDrawer {

    private lateinit var mDrawer: Drawer
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mHeader: AccountHeader
    private lateinit var mCurrentProfile: ProfileDrawerItem

    fun create() {
        initLoader()
        initProfile()
        createHeader()
        createDrawer()
        mDrawerLayout = mDrawer.drawerLayout
    }

    fun enableDrawer() {
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        MAIN_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mDrawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
        MAIN_ACTIVITY.mToolbar.setNavigationOnClickListener { mDrawer.openDrawer() }
        mDrawer.actionBarDrawerToggle!!.syncState() ///synxron ishlashiga yordam beradi
    }

    fun disableDrawer() {
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        MAIN_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDrawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        MAIN_ACTIVITY.mToolbar.setNavigationOnClickListener { MAIN_ACTIVITY.supportFragmentManager.popBackStack() }
    }

    private fun createDrawer() {
        mDrawer = DrawerBuilder()
            .withActivity(MAIN_ACTIVITY)
            .withToolbar(MAIN_ACTIVITY.mToolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(mHeader)
            .addDrawerItems(
                PrimaryDrawerItem()
                    .withIdentifier(100)
                    .withSelectable(false)
                    .withIconTintingEnabled(true)
                    .withName("Home")
                    .withIcon(R.drawable.ic_home),
                PrimaryDrawerItem()
                    .withIdentifier(101)
                    .withName("Favourites")
                    .withSelectable(false)
                    .withIconTintingEnabled(false)
                    .withIcon(R.drawable.ic_star),
                PrimaryDrawerItem()
                    .withIdentifier(102)
                    .withName("Settings")
                    .withSelectable(false)
                    .withIconTintingEnabled(true)
                    .withIcon(R.drawable.ic_settings),
                PrimaryDrawerItem()
                    .withIdentifier(103)
                    .withName("About")
                    .withSelectable(false)
                    .withIconTintingEnabled(true)
                    .withIcon(R.drawable.ic_info)
            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    clickToItem(position)
                    return false
                }
            }).build()
    }

    private fun clickToItem(position: Int) {
        when (position) {
            1 -> replaceFragment(MainFragment())
            2 -> replaceFragment(FavouritesFragment())
            3 -> replaceFragment(SettingsFragment())
            4 -> replaceFragment(AboutFragment())
        }
    }

    private fun initProfile(): ProfileDrawerItem {

        if (userGoogleOrPhone() == GOOGLE_PROVIDER_ID) {
            mCurrentProfile = ProfileDrawerItem()
                .withIdentifier(200)
                .withName(USER.fullname)
                .withEmail(USER.email)
                .withIcon(USER.photoUrl)
        } else {
            mCurrentProfile = ProfileDrawerItem()
                .withIdentifier(200)
                .withName(USER.fullname)
                .withEmail(USER.phone)
                .withIcon(USER.photoUrl)
            Log.d("AppDrawer", "initProfile:-> ${USER.fullname} ${USER.phone}")
        }
        return mCurrentProfile
    }

    private fun createHeader() {
        mHeader = AccountHeaderBuilder()
            .withActivity(MAIN_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(mCurrentProfile)
            .build()
    }

    private fun initLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.photoDownloadAndSet(uri.toString())  //kelyabturgan imageviewga urini rasmiini qo'yadi
            }
        })
    }

    fun updateHeader() {
        mCurrentProfile.withName(USER.fullname).withIcon(USER.photoUrl)
        if (userGoogleOrPhone() == PHONE_PROVIDER_ID)
            mCurrentProfile.withEmail(USER.phone)
        mHeader.updateProfile(mCurrentProfile)
    }
}