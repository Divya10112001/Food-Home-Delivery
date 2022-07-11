package com.foodcart.modal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
class orderentity (@PrimaryKey val resId: String,
                   @ColumnInfo(name = "food_items") val foodItems: String)