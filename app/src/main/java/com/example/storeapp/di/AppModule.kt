package com.example.storeapp.di

import android.content.Context
import androidx.room.Room
import com.example.storeapp.data.local.AppDatabase
import com.example.storeapp.data.local.CartDao
import com.example.storeapp.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module // Le dice a Hilt que este objeto es un Módulo
@InstallIn(SingletonComponent::class) // Las dependencias vivirán mientras la app esté viva
object AppModule {

    // --- PROVEEDOR DE RETROFIT ---
    @Provides // Indica que esta función provee una dependencia
    @Singleton // Asegura que solo se cree una instancia de Retrofit en toda la app
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/") // La URL base de la API
            .addConverterFactory(GsonConverterFactory.create()) // El conversor de JSON
            .build()
    }

    // --- PROVEEDOR DE APISERVICE ---
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        // Hilt es inteligente: ve que necesita un Retrofit, lo busca en este módulo,
        // lo obtiene de provideRetrofit() y lo inyecta aquí.
        return retrofit.create(ApiService::class.java)
    }

    // --- PROVEEDOR DE LA BASE DE DATOS (ROOM) ---
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "store_app_db" // Nombre del archivo de la base de datos
        ).build()
    }

    // --- PROVEEDOR DEL CARTDAO ---
    @Provides
    @Singleton
    fun provideCartDao(appDatabase: AppDatabase): CartDao {
        // Al igual que con Retrofit, Hilt sabe cómo proveer AppDatabase
        // y nos lo da para que podamos obtener el Dao.
        return appDatabase.cartDao()
    }
}