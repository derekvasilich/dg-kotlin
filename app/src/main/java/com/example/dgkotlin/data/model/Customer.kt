package com.example.dgkotlin.data.model

class Customer {
    var id: Long? = null
    var legalName: String? = null
    var firstName: String? = null
    var lastName: String? = null

    var email: String? = null
    var phone: String? = null
    var mobile: String? = null

    var licenseNumber: String? = null

    var address_1: String? = null
    var city: String? = null
    var province: String? = null
    var postal: String? = null
    var country: String? = null

    var lat: Float? = null
    var lng: Float? = null

    val name: String?
        get() = if (legalName != null && legalName !== "") {
            legalName
        } else "$firstName $lastName"
}
