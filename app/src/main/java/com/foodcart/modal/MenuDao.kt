package com.foodcart.modal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDao {
    @Insert
    fun insertRestaurant(restaurantEntity: MenuEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: MenuEntity)

    @Query("SELECT * from restaurants WHERE id =:resId")
    fun getOrderbyId(resId : String):MenuEntity

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<MenuEntity>

    @Query("SELECT * FROM restaurants WHERE id = :resId")
    fun getRestaurantById(resId: String): MenuEntity



}

