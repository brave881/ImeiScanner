package com.example.imeiscanner.database

import android.widget.EditText
import com.example.imeiscanner.models.UserModel
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.utilits.replaceFragment
import com.example.imeiscanner.utilits.showToast
import com.example.imeiscanner.utilits.toStringEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.mikepenz.materialdrawer.model.ProfileDrawerItem

fun addGoogleUserToFirebase(user: FirebaseUser?) {
    val uid = user?.uid
    val dataMap = mutableMapOf<String, Any>()

    dataMap[CHILD_ID] = user!!.uid
    dataMap[CHILD_EMAIL] = user.email.toString()
    dataMap[CHILD_FULLNAME] = user.displayName.toString()
    dataMap[CHILD_PHOTO_URL] = user.photoUrl.toString()
    dataMap[CHILD_TYPE] = GoogleAuthProvider.PROVIDER_ID

    val mapUser = mutableMapOf<String, Any>()
    mapUser["$NODE_USERS/$uid"] = dataMap

    val mapUserGoogle = mutableMapOf<String, Any>()
    mapUserGoogle["$NODE_GOOGLE_USERS/$uid"] = dataMap

    REF_DATABASE_ROOT.updateChildren(mapUser)
    REF_DATABASE_ROOT.updateChildren(mapUserGoogle)
}

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    CURRENT_USER = AUTH.currentUser?.uid.toString()
    CURRENT_PROVIDER_ID = AUTH.currentUser?.providerId.toString()
    USER = UserModel()
}

fun authGoogleOrPhone(): String {
    AUTH.currentUser.let {
        for (profile in it!!.providerData) {
            when (profile.providerId) {
                GoogleAuthProvider.PROVIDER_ID -> return "google"
                PhoneAuthProvider.PROVIDER_ID -> return "phone"
            }
        }
        return ""
    }
}

fun setValuesToFireBase(dateMap: HashMap<String, Any>) {
    val key = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).push().key
    if (key != null) {
        REF_DATABASE_ROOT
            .child(NODE_PHONE_DATA_INFO)
            .child(key)
            .setValue(dateMap)
            .addOnSuccessListener { replaceFragment(MainFragment()) }
            .addOnFailureListener { showToast(it.toString()) }
    }
}