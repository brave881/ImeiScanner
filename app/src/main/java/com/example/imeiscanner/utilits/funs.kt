package com.example.imeiscanner.utilits

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R

fun showToast(string: String){
    Toast.makeText(MAIN_ACTIVITY, string, Toast.LENGTH_SHORT).show()
}

fun replaceFragment(fragment: Fragment){
    MAIN_ACTIVITY
        .supportFragmentManager
        .beginTransaction()
        .replace(R.id.data_container,fragment)
        .commit()
}