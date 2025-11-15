package com.example.storeapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Corresponde a: POST /auth/login
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Corresponde a: GET /products
    @GET("products")
    suspend fun getAllProducts(): Response<List<ProductDto>>
}