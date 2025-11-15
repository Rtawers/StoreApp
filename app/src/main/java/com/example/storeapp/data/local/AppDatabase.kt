package com.example.storeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// @Database: Configura la base de datos.
// entities = [CartItem::class]: Le decimos qué tablas usar.
// version = 1: Es la versión de la base de datos.
@Database(entities = [CartItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Esta función permite obtener el DAO para usarlo
    abstract fun cartDao(): CartDao
}