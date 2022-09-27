package com.example.imeiscanner

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.database.initFirebase
import com.example.imeiscanner.database.initUser
import com.example.imeiscanner.databinding.ActivityMainBinding
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.ui.fragments.register.RegisterFragment
import com.example.imeiscanner.utilits.*

class MainActivity : AppCompatActivity() {

    lateinit var mToolbar: Toolbar
    private lateinit var binding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MAIN_ACTIVITY = this
        DIALOG_BUILDER = AlertDialog.Builder(this)
        initFirebase()
        initUser {
            initFields()
            initFunctions()
        }
        installSharedPreference()
    }

    private fun installSharedPreference() {
        SHARED_PREFERENCES = MAIN_ACTIVITY.getSharedPreferences("Favourites", Context.MODE_PRIVATE)
        SH_P_EDITOR = SHARED_PREFERENCES.edit()
    }

    private fun initFields() {
        mToolbar = binding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    private fun initFunctions() {
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null) {
            mAppDrawer.create()
            replaceFragment(MainFragment(), false)
        } else {
            replaceFragment(RegisterFragment(), false)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        MAIN_ACTIVITY.menuInflater.inflate(R.menu.search_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_search_btn -> {
//
//            }
//        }
//        return true
//    }
}