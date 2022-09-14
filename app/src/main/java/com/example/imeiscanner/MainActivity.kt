package com.example.imeiscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imeiscanner.utilits.MAIN_ACTIVITY

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MAIN_ACTIVITY = this
    }

}