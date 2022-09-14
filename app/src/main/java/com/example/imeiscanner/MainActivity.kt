package com.example.imeiscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.ui.fragments.RegisterFragment
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.replaceFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MAIN_ACTIVITY = this
        AUTH = FirebaseAuth.getInstance()

        replaceFragment(RegisterFragment())


        }

}