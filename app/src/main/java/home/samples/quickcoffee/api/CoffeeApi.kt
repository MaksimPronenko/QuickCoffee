package home.samples.quickcoffee.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CoffeeApi {
//    @GET("/v3/d144777c-a67f-4e35-867a-cacc3b827473")
//    suspend fun getHotel(): Hotel?
//
//    @GET("/v3/8b532701-709e-4194-a41c-1a903af00195")
//    suspend fun getRoomsList(): RoomsList?
//
//    @GET("/v3/63866c74-d593-432c-af8e-f279d1a8d2ff")
//    suspend fun getBookingData(): BookingData?
}

val retrofit: CoffeeApi = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl("https://run.mocky.io")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(CoffeeApi::class.java)