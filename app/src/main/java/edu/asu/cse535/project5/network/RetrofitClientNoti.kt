package edu.asu.cse535.project5.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientNoti {
    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header(
                    "Authorization",
                    "key=AAAA65Iy_rM:APA91thjnbtnhtyzP5Ko9VGwpNA-7FtjgtjuitgtFjK_Zl7bi-UxHbtVp4VMtHON4MSYTdN2yPI49nk1wn2-6uSk2Rb_zDrtJRMuHHqR7twty5wDNb-CjqZ2IlVoE95movvqMnddBjtgjigT"
                )
                .build()
            chain.proceed(newRequest)
        }
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/fcm/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api = retrofit.create(ApiService::class.java)
}
