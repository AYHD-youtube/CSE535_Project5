package edu.asu.cse535.project5.datamodel

data class NotiBody(
    val data: Data,
    val notification: Notification,
    val to: String
){
    data class Data(
        val body: String,
        val title: String
    )
    data class Notification(
        val body: String,
        val title: String
    )
}