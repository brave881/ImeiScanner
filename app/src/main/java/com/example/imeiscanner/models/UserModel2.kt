package com.example.imeiscanner.models

import com.example.imeiscanner.database.AUTH

data class UserModel2(
    var name: String = AUTH.currentUser?.displayName.toString(),
    var phone: String = AUTH.currentUser?.phoneNumber.toString(),
    var email: String = AUTH.currentUser?.email ?: "empty",
    var photoUrl: String = AUTH.currentUser?.photoUrl.toString()
)

