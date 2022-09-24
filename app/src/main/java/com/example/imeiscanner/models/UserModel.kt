package com.example.imeiscanner.models

import com.example.imeiscanner.database.AUTH

data class UserModel(
    var name: String = AUTH.currentUser?.displayName.toString(),
    var phone: String = AUTH.currentUser?.phoneNumber.toString(),
    var email: String = AUTH.currentUser?.email.toString(),
    var photoUrl: String = (AUTH.currentUser?.photoUrl ?: "empty").toString()
)
