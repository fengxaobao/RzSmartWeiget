package com.rz.smart.event

/**
 * Created by zhou on 2021/1/11 18:43.
 */
class LoginEvent : BaseEvent {

    var userName : String

    var password : String


    constructor(userName: String, password: String) : super() {
        this.userName = userName
        this.password = password
    }
}