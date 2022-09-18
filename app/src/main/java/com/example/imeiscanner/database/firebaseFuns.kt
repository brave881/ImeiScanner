package com.example.imeiscanner.database

import com.example.imeiscanner.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

fun addGoogleUserToFirebase(user: FirebaseUser?) {
    val uid = user?.uid
    setGoogleUsersData(user)
    val dataMap = mutableMapOf<String, Any>()

    dataMap[CHILD_ID] = user!!.uid
    dataMap[CHILD_EMAIL] = user.email.toString()
    dataMap[CHILD_FULLNAME] = user.displayName.toString()
    dataMap[CHILD_PHOTO_URL] = user.photoUrl.toString()

    val mapUser = mutableMapOf<String, Any>()
    mapUser["$NODE_GOOGLE_USERS/$uid"] = dataMap

    REF_DATABASE_ROOT.setValue(dataMap)
}

fun setGoogleUsersData(user: FirebaseUser?) {
    USER_MODEL.phoneOrEmail = user?.email.toString()
    USER_MODEL.fullname = user?.displayName.toString()
    USER_MODEL.photoUrl = user?.photoUrl.toString()
}

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    USER_MODEL = UserModel()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    CURRENT_USER = AUTH.currentUser?.uid.toString()
    CURRENT_PROVIDER_ID = AUTH.currentUser?.providerId.toString()
}