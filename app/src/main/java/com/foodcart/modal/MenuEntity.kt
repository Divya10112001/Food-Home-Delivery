package com.foodcart.modal

import androidx.room.*

@Entity(tableName = "restaurants")
data class MenuEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
   // @ColumnInfo(name = "rating") val rating: String,
    @ColumnInfo(name = "cost_for_two") val costForTwo: String,
    //@ColumnInfo(name = "image_url") val imageUrl: String
)




