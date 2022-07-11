package com.foodcart.modal

import androidx.room.*

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orderEntity: orderentity)

    @Delete
    fun deleteOrder(orderEntity: orderentity)

    @Query
        ("SELECT * FROM orders")
    fun getAllOrders(): List<orderentity>

    @Query("SELECT * from orders WHERE resId =:resId")
    fun getOrderbyId(resId : String):orderentity

    @Query("DELETE FROM orders WHERE resId = :resId")
    fun deleteOrders(resId: String)
}



