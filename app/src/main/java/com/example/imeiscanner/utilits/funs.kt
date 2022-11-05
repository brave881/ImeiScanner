package com.example.imeiscanner.utilits

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.CountDownTimer
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.imeiscanner.R
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.database.deleteUser
import com.example.imeiscanner.database.deleteUserFromDatabase
import com.example.imeiscanner.database.getCallbacks
import com.example.imeiscanner.models.PhoneDataModel
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*
import java.util.concurrent.TimeUnit


fun showToast(string: String) {
    Toast.makeText(MAIN_ACTIVITY, string, Toast.LENGTH_LONG).show()
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
fun showDatePicker(context: Context, tv: TextView) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, { _, year1, month1, dayOfMonth ->
            tv.text = "$dayOfMonth/${month1 + 1}/$year1"
        }, year, month, day
    )
    datePickerDialog.show()
    datePickerDialog.datePicker.setBackgroundColor(MAIN_ACTIVITY.resources.getColor(R.color.button_color))
}

fun DataSnapshot.getPhoneModel(): PhoneDataModel {
    return getValue(PhoneDataModel::class.java) ?: PhoneDataModel()
}

fun scanOptions(options: ScanOptions) {
    options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
    options.setPrompt(MAIN_ACTIVITY.getString(R.string.scan_a_barcode_text))
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

fun changeLanguage() {
    val items = arrayOf("English", "Türkçe", "O'zbekcha")
    var itemState = 0
    if (sharedPreferences.getString(LANG, "") == "en") {
        itemState = 0
    } else if (sharedPreferences.getString(LANG, "") == "tr") {
        itemState = 1
    } else if (sharedPreferences.getString(LANG, "") == "uz") {
        itemState = 2
    }
    showAlertDialog(items, itemState)
}

fun showAlertDialog(items: Array<String>, itemState: Int) {
    var language = sharedPreferences.getString(LANG, "")
    DIALOG_BUILDER
        .setTitle(MAIN_ACTIVITY.getString(R.string.choice_language_text))
        .setSingleChoiceItems(items, itemState) { dialog, it ->
            when (it) {
                0 -> {
                    language = "en"
                }
                1 -> {
                    language = "tr"
                }
                2 -> {
                    language = "uz"
                }
            }
        }.setPositiveButton(MAIN_ACTIVITY.getString(R.string.ok_text)) { dialogInterface, it ->
            setLocale(language!!)
            restartActivity()
        }.setNegativeButton(MAIN_ACTIVITY.getString(R.string.cancel)) { dialogInterface, it ->
            dialogInterface.cancel()
        }
        .create().show()
}

fun setLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val configuration = Configuration()
    configuration.setLocale(locale)
    MAIN_ACTIVITY.resources.updateConfiguration(
        configuration,
        MAIN_ACTIVITY.resources.displayMetrics
    )
    editor.putString(LANG, language).apply()
}

fun loadLanguage() {
    val language = sharedPreferences.getString(LANG, "")
    setLocale(language!!)
}

fun startTimer(tvTimer: TextView): CountDownTimer {
    return object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            tvTimer.text =
                (" ${(millisUntilFinished / 1000)} ${MAIN_ACTIVITY.getString(R.string.seconds)}")
        }

        override fun onFinish() {
            tvTimer.text = " ${MAIN_ACTIVITY.getString(R.string.seconds)}"
        }
    }
}

fun resendCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken) {
    val options = PhoneAuthOptions.newBuilder(AUTH)
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(MAIN_ACTIVITY)
        .setCallbacks(getCallbacks(phoneNumber) {})
        .setForceResendingToken(token)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}



