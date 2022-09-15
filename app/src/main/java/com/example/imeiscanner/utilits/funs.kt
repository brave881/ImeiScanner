package com.example.imeiscanner.utilits

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R

fun showToast(string: String) {
    Toast.makeText(MAIN_ACTIVITY, string, Toast.LENGTH_SHORT).show()
}

fun replaceFragment(fragment: Fragment,addStack:Boolean=true) {
    if (addStack) {
        MAIN_ACTIVITY
            .supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
    }else{
        MAIN_ACTIVITY
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.data_container, fragment)
            .commit()
    }
}

fun restartActivity() {
    val intent = Intent(MAIN_ACTIVITY, MAIN_ACTIVITY::class.java)
    MAIN_ACTIVITY.startActivity(intent)
    MAIN_ACTIVITY.finish()
}
