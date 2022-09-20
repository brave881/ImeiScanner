package com.example.imeiscanner.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

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
}