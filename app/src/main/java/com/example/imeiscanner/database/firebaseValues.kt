package com.example.imeiscanner.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var CURRENT_USER:String
lateinit var CURRENT_PROVIDER_ID:String

const val NODE_PHONE_USERS = "phone_users"
const val NODE_GOOGLE_USERS = "google_users"
const val NODE_USERS="users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
    //data phones
const val NODE_PHONE_DATA_INFO = "phone_data_info"
const val CHILD_PHONE_NAME = "phone_name"
const val CHILD_IMEI1 = "phone_imei1"
const val CHILD_IMEI2 = "phone_imei2"
const val CHILD_SERIAL_NUMBER = "phone_serial_number"
const val CHILD_PHONE_ADDED_DATE = "phone_added_date"
const val CHILD_BATTERY_INFO = "phone_battery_info"
const val CHILD_PHONE_PRICE = "phone_price"
const val CHILD_PHONE_MEMORY = "phone_memory"
   // end data phones

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_EMAIL = "email"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_TYPE="type"


