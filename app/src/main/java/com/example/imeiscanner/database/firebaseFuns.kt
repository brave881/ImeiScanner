package com.example.imeiscanner.database

import android.net.Uri
import com.example.imeiscanner.models.UserModel
import com.example.imeiscanner.ui.fragments.MainFragment
import com.example.imeiscanner.utilits.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


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
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    CURRENT_USER = AUTH.currentUser?.uid.toString()
    CURRENT_USER_EMAIL = AUTH.currentUser?.email.toString()
    CURRENT_USER_PHONE = AUTH.currentUser?.phoneNumber.toString()
    CURRENT_PROVIDER_ID = AUTH.currentUser?.providerId.toString()
    USER = UserModel()
}

fun userGoogleOrPhone(): String {
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

inline fun putUserPhotoUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_USER).child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

    when (userGoogleOrPhone()) {
        GOOGLE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_GOOGLE_USERS).child(CURRENT_USER)
                .child(CHILD_PHOTO_URL)
                .setValue(url).addOnFailureListener { showToast(it.message.toString()) }
        }
        PHONE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_PHONE_USERS).child(USER.phone)
                .child(CHILD_PHOTO_URL)
                .setValue(url).addOnFailureListener { showToast(it.message.toString()) }

        }
    }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (String) -> Unit) {
    path.downloadUrl.addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFileToStorage(path: StorageReference, uri: Uri, crossinline function: () -> Unit) {
    path.putFile(uri).addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setUsernameToDatabase(username: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_USER).child(CHILD_FULLNAME)
        .setValue(username).addOnSuccessListener {
            showToast("Name Changed!")
//            updateUserName(username)
            MAIN_ACTIVITY.supportFragmentManager.popBackStack()
            MAIN_ACTIVITY.mAppDrawer.updateHeader()
        }.addOnFailureListener { showToast(it.message.toString()) }

    when (userGoogleOrPhone()) {
        GOOGLE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_GOOGLE_USERS).child(CURRENT_USER).child(CHILD_FULLNAME)
                .setValue(username).addOnFailureListener { showToast(it.message.toString()) }
        }
        PHONE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_PHONE_USERS).child(USER.phone).child(CHILD_FULLNAME)
                .setValue(username).addOnFailureListener { showToast(it.message.toString()) }
        }
    }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_USER)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.fullname.isEmpty()) {
                USER.fullname = CURRENT_USER
            }
            function()
        })
}

