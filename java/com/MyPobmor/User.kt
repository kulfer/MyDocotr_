package com.MyPobmor
class User{
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var specialty: String? = null
    val profileImage: String? = null

    constructor(){}

    constructor(name: String, email: String?, uid: String?, specialty: String){
        this.name = name
        this.email = email
        this.uid = uid
        this.specialty = specialty

    }
}
