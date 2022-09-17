package com.example.imeiscanner.utilits

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import com.example.imeiscanner.MainActivity
import com.example.imeiscanner.R
import com.example.imeiscanner.database.GOOGLE_MODEL
import com.example.imeiscanner.ui.fragments.AboutFragment
import com.example.imeiscanner.ui.fragments.FavouritesFragment
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.ui.fragments.SettingsFragment
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
        createHeaderGoogle()
        createDrawer()
        mDrawerLayout = mDrawer.drawerLayout
    }

    fun enableDrawer() {
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        MAIN_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mDrawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
        MAIN_ACTIVITY.mToolbar.setNavigationOnClickListener { mDrawer.openDrawer() }
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
                    .withIconTintingEnabled(true)
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


    private fun createHeaderGoogle() {
        mCurrentProfile = ProfileDrawerItem()
            .withName(GOOGLE_MODEL.fullname)
            .withEmail(GOOGLE_MODEL.email)
            .withIcon(GOOGLE_MODEL.photoUrl)
            .withIdentifier(200)
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

}