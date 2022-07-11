package com.foodcart.modal

import java.io.Serializable

data class MenuFood (
    val food_id : String,
    val foodname : String,
    val foodprice : Int,
    /*val resId : String*/):Serializable
//val id: String?, val name: String?, val cost: Int?