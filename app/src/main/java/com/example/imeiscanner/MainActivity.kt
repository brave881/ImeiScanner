package com.example.imeiscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.ActivityMainBinding
import com.example.imeiscanner.models.UserModel
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.ui.fragments.RegisterFragment
import com.example.imeiscanner.utilits.AppDrawer
import com.example.imeiscanner.utilits.AppValueEventListener
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.replaceFragment

class MainActivity : AppCompatActivity() {

    lateinit var mToolbar: Toolbar
    private lateinit var binding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MAIN_ACTIVITY = this
        initFields()
        initFirebase()
        initUser()
        initFunctions()

    }

    private fun initUser() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_USER)
            .addListenerForSingleValueEvent(AppValueEventListener {
                USER = it.getValue(UserModel::class.java) ?: UserModel()
                if (USER.name.isEmpty()) {
                    USER.name = CURRENT_USER
                }
            })
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

}