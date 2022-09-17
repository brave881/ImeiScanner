package com.example.imeiscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.database.GOOGLE_MODEL
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.databinding.ActivityMainBinding
import com.example.imeiscanner.databinding.FragmentMainBinding
import com.example.imeiscanner.models.GoogleModel
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.ui.fragments.RegisterFragment
import com.example.imeiscanner.utilits.AppDrawer
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.replaceFragment
import com.example.imeiscanner.utilits.restartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var mToolbar: Toolbar
    private lateinit var binding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MAIN_ACTIVITY = this
        AUTH = FirebaseAuth.getInstance()
        GOOGLE_MODEL= GoogleModel()
        REF_DATABASE_ROOT= FirebaseDatabase.getInstance().reference

        initFields()
        initFunctions()
        mAppDrawer.create()

    }

    private fun initFields() {
        mToolbar=binding.mainToolbar
        mAppDrawer=AppDrawer()
    }

    private fun initFunctions() {
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser!=null){
            replaceFragment(MainFragment(),false)
        }else{
            replaceFragment(RegisterFragment(),false)
        }
    }

}