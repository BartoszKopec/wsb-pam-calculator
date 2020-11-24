package com.foxdev.calculator.services

// this interface is using as
// anonymous parameter object in
// ViewModel class setMessageListener function
interface MessageListener {
    fun sendMessage(message: String)
}