package com.example.imeiscanner.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

lateinit var AUTH :FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NODE_PHONE_USERS = "phone_users"
const val NODE_GOOGLE_USERS = "google_users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_EMAIL = "email"
const val CHILD_PHOTO_URL = "photoUrl"
