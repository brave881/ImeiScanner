package com.example.imeiscanner.database

import com.google.firebase.auth.FirebaseUser

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
    GOOGLE_MODEL.email = user?.email.toString()
    GOOGLE_MODEL.fullname = user?.displayName.toString()
    GOOGLE_MODEL.photoUrl = user?.photoUrl.toString()
}