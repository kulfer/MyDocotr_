package com.MyPobmor

class Message {
    var message: String? = null
    var sender: String? = null
    var timestamp: Long = System.currentTimeMillis() // ฟิลด์ timestamp

    constructor(){}

    constructor(message: String?, senderId: String?){
        this.message = message
        this.sender = senderId
    }
}