package com.example.imeiscanner.models

import java.io.Serializable

data class PhoneDataModel(
    var id: String = "",
    var phone_name: String = "",
    var phone_imei1: String = "",
    var phone_imei2: String = "",
    var phone_serial_number: String = "",
    var phone_added_date: String = "",
    var phone_battery_info: String = "",
    var phone_price: String = "",
    var phone_memory: String = "",
    var favourite_state: Boolean =false
): Serializable
