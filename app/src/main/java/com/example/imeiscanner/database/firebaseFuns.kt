package com.example.imeiscanner.database

import android.net.Uri
import android.util.Log
import android.widget.EditText
import com.example.imeiscanner.R
import com.example.imeiscanner.models.PhoneDataModel
import com.example.imeiscanner.models.UserModel
import com.example.imeiscanner.ui.fragments.mainFragment.MainFragment
import com.example.imeiscanner.ui.fragments.register.EnterCodeFragment
import com.example.imeiscanner.utilits.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
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
    AUTH.firebaseAuthSettings.setAppVerificationDisabledForTesting(true) // reCaaptcha ni o'chiradi
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    CURRENT_UID = AUTH.currentUser?.uid.toString()
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

fun setValuesToFireBase(
    dateMap: HashMap<String, Any>,
    id: String,
    imei1: String,
    boolean: Boolean
) {
    val reference = REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID)
    reference.addListenerForSingleValueEvent(AppValueEventListener { it ->
        if (boolean)
            reference
                .child(id)
                .setValue(dateMap)
                .addOnSuccessListener { replaceFragment(MainFragment()) }
                .addOnFailureListener { showToast(it.toString()) }
        else {
            for (i in it.children) {
                if (i.child(CHILD_IMEI1).value == imei1) {
                    showToast(MAIN_ACTIVITY.getString(R.string.imei_already_exists_text))
                    return@AppValueEventListener
                }
            }
            reference
                .child(id)
                .setValue(dateMap)
                .addOnSuccessListener { replaceFragment(MainFragment()) }
                .addOnFailureListener { showToast(it.toString()) }
        }
    })
}

inline fun putUserPhotoUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

    when (userGoogleOrPhone()) {
        GOOGLE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_GOOGLE_USERS).child(CURRENT_UID)
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

fun firebaseAuthWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    AUTH.signInWithCredential(credential).addOnSuccessListener {
        val user = AUTH.currentUser
        addGoogleUserToFirebase(user)
        restartActivity()
    }.addOnFailureListener {
        showToast(it.message.toString())
        addGoogleUserToFirebase(null)
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

fun updateFullNameFromDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
            showToast(MAIN_ACTIVITY.getString(R.string.name_changed_text))
            updateUserName(fullname)
            MAIN_ACTIVITY.supportFragmentManager.popBackStack()
            MAIN_ACTIVITY.mAppDrawer.updateHeader()
        }.addOnFailureListener { showToast(it.message.toString()) }

    when (userGoogleOrPhone()) {
        GOOGLE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_GOOGLE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
                .setValue(fullname).addOnFailureListener { showToast(it.message.toString()) }
        }
        PHONE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_PHONE_USERS).child(USER.phone).child(CHILD_FULLNAME)
                .setValue(fullname).addOnFailureListener { showToast(it.message.toString()) }
        }
    }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.fullname.isEmpty()) {
                USER.fullname = CURRENT_UID
            }
            function()
        })
}

fun addDatabaseImei(
    id: String,
    dateMap: HashMap<String, Any>,
    name: EditText,
    batteryInfo: EditText,
    memory: EditText,
    date: String,
    price: EditText,
    state: Boolean
): HashMap<String, Any> {
    dateMap[CHILD_PHONE_ID] = id
    dateMap[CHILD_PHONE_NAME] = toStringEditText(name)
    dateMap[CHILD_BATTERY_INFO] = toStringEditText(batteryInfo)
    dateMap[CHILD_PHONE_MEMORY] = toStringEditText(memory)
    dateMap[CHILD_PHONE_ADDED_DATE] = date
    dateMap[CHILD_PHONE_PRICE] = toStringEditText(price)
    dateMap[CHILD_FAVOURITE_STATE] = state
    return dateMap
}

fun deleteUser() {
    Firebase.auth.currentUser!!.delete()
        .addOnSuccessListener { showToast(MAIN_ACTIVITY.getString(R.string.account_deleted)) }
        .addOnFailureListener { showToast(it.message.toString()) }
    logOut()
}

fun deleteUserFromDatabase() {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }

    when (userGoogleOrPhone()) {
        GOOGLE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_GOOGLE_USERS).child(CURRENT_UID).removeValue()
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        PHONE_PROVIDER_ID -> {
            REF_DATABASE_ROOT.child(NODE_PHONE_USERS).child(USER.phone).removeValue()
        }
    }
}

fun signInWithPhone(uid: String, phoneNumber: String, name: String = "") {
    val dataMap = hashMapOf<String, Any>()
    if (name.isNotEmpty()) {
        dataMap[CHILD_FULLNAME] = name
    }
    dataMap[CHILD_PHONE] = phoneNumber
    dataMap[CHILD_ID] = uid
    dataMap[CHILD_TYPE] = PhoneAuthProvider.PROVIDER_ID
    Log.d(TAG, "signInWithPhone: $uid")
    FirebaseDatabase.getInstance().reference.child(NODE_USERS).setValue(name)
        .addOnFailureListener { showToast(it.message.toString()) }
//        .addOnSuccessListener {
//            restartActivity()
//            showToast(MAIN_ACTIVITY.getString(R.string.welcome))
//        }
    REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
        .addListenerForSingleValueEvent(AppValueEventListener {
            REF_DATABASE_ROOT.child(NODE_PHONE_USERS).child(phoneNumber).updateChildren(dataMap)
                .addOnFailureListener { showToast(it.message.toString()) }
                .addOnSuccessListener {

                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dataMap)
                        .addOnFailureListener { showToast(it.message.toString()) }
                        .addOnSuccessListener {
                            restartActivity()
                            showToast(MAIN_ACTIVITY.getString(R.string.welcome))
                        }
                }
        })
}

fun addFavourites(item: PhoneDataModel) {
    val ref = "$NODE_FAVOURITES/$CURRENT_UID/${item.id}"
    val dataMap = hashMapOf<String, Any>()
    dataMap[CHILD_ID] = item.id
    dataMap[CHILD_PHONE_ADDED_DATE] = item.phone_added_date
    dataMap[CHILD_BATTERY_INFO] = item.phone_battery_info
    dataMap[CHILD_IMEI1] = item.phone_imei1
    dataMap[CHILD_IMEI2] = item.phone_imei2
    dataMap[CHILD_PHONE_MEMORY] = item.phone_memory
    dataMap[CHILD_PHONE_NAME] = item.phone_name
    dataMap[CHILD_PHONE_PRICE] = item.phone_price
    dataMap[CHILD_SERIAL_NUMBER] = item.phone_serial_number
    dataMap[CHILD_FAVOURITE_STATE] = true

    val map = hashMapOf<String, Any>()
    map[ref] = dataMap
    REF_DATABASE_ROOT.updateChildren(map)
        .addOnFailureListener {
            showToast(it.message.toString())
        }

    val newMap = hashMapOf<String, Any>()
    newMap[CHILD_FAVOURITE_STATE] = true
    REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID).child(item.id)
        .updateChildren(newMap)
}

fun deleteFavouritesValue(value: String) {
    val dataMap = hashMapOf<String, Any>()
    dataMap[CHILD_FAVOURITE_STATE] = false
    REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID).child(value)
        .updateChildren(dataMap)

    REF_DATABASE_ROOT.child(NODE_FAVOURITES).child(CURRENT_UID).child(value).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getCallbacks(phoneNumber: String): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            AUTH.signInWithCredential(credential).addOnSuccessListener {
                restartActivity()
                showToast(MAIN_ACTIVITY.getString(R.string.welcome))
            }.addOnFailureListener { showToast(it.message.toString()) }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            showToast(e.message.toString())
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            replaceFragment(EnterCodeFragment(phoneNumber, verificationId, token))
        }
    }
    return callbacks
}

fun deleteSelectedItems(id: String) {
    REF_DATABASE_ROOT.child(NODE_PHONE_DATA_INFO).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
}