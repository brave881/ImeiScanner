package com.example.imeiscanner.models

data class UserModel(
    var id: String = "",
    var fullname: String = "",
    var email: String = "",
    var photoUrl: String = "empty",
    var phone: String = "",
    var type: String = ""
)
