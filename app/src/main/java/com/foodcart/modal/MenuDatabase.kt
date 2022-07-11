package com.foodcart.modal

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuEntity::class,orderentity::class], version = 1,exportSchema = true)
abstract class MenuDatabase : RoomDatabase() {

    abstract fun menuDao(): MenuDao

    abstract fun orderDao(): OrderDao

}
