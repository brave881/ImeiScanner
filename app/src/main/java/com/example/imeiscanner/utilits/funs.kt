package com.example.imeiscanner.utilits

import android.widget.Toast

fun showToast(string: String){
    Toast.makeText(MAIN_ACTIVITY, string, Toast.LENGTH_SHORT).show()
}