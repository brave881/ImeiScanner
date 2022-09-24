package com.example.imeiscanner.utilits

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.imeiscanner.R
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.database.deleteUser
import com.example.imeiscanner.database.deleteUserFromDatabase
import com.example.imeiscanner.databinding.FragmentPhoneAddBinding
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*

fun showToast(string: String) {
    Toast.makeText(MAIN_ACTIVITY, string, Toast.LENGTH_SHORT).show()
}

fun ImageView.photoDownloadAndSet(url: String) {
    Glide.with(this).load(url).fitCenter().centerCrop().into(this)
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        MAIN_ACTIVITY
            .supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
    } else {
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

@SuppressLint("SetTextI18n")
fun showDatePicker(binding: FragmentPhoneAddBinding, context: Context): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    var res = ""

    val datePickerDialog = DatePickerDialog(
        context, { _, year1, month1, dayOfMonth ->
            binding.btnDate.text = "$dayOfMonth/${month1 + 1}/$year1"
            res = "$dayOfMonth/${month1 + 1}/$year1"
        }, year, month, day
    )
    datePickerDialog.show()
    return res
}

fun scanOptions(options: ScanOptions) {
    options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
    options.setPrompt("Scan a barcode")
    options.setCameraId(0) // Use a specific camera of the device
    options.setBeepEnabled(false)
    options.setBarcodeImageEnabled(true)
    options.setOrientationLocked(false)
}

fun toStringEditText(e: EditText): String {
    return e.text.toString()
}

fun updateUserPhotoUrl(url: String) {
    val profile = userProfileChangeRequest {
        photoUri = Uri.parse(url)
    }
    AUTH.currentUser?.updateProfile(profile)

}

fun updateUserName(name: String) {
    val prof = userProfileChangeRequest {
        displayName = name
    }
    AUTH.currentUser!!.updateProfile(prof)
}

fun logOut() {
    AUTH.signOut()
    restartActivity()
}

fun hideKeyboard() {
    val imm: InputMethodManager =
        MAIN_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(MAIN_ACTIVITY.window.decorView.windowToken, 0)
}

fun accountDeleteDialog() {
    DIALOG_BUILDER.setTitle(R.string.delete_account_dialog)
        .setMessage(R.string.alert_dialog_message)
        .setNegativeButton(R.string.cancel) { dialogIntereface, it ->
            dialogIntereface.cancel()
        }
        .setPositiveButton(R.string.delete_text) { dialogInterface, it ->
            deleteUserFromDatabase()
            deleteUser()
        }.show()
}

fun logOutDialog() {
    DIALOG_BUILDER.setTitle(R.string.log_out_dialog_text)
        .setMessage(R.string.log_out_dialog_message)
        .setPositiveButton(R.string.log_out_text) { dialogInterFace, it ->
            logOut()
        }
        .setNegativeButton(R.string.cancel) { dialogInterFace, it ->
            dialogInterFace.cancel()
        }.show()
}



