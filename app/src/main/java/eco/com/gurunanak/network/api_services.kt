package com.tudle.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import eco.com.gurunanak.adapter.data_faq
import eco.com.gurunanak.model.data_pls
import eco.com.gurunanak.model.data_resources
import org.json.JSONArray
import retrofit2.http.*


interface api_services {


    @POST("register")
    fun register(@Body action: HashMap<String, String>):
            retrofit2.Call<JsonObject>

    @POST("updatePassword")
    fun updatePassword(@Body action: HashMap<String, String>):
            retrofit2.Call<JsonObject>

    @POST("plantationRecord")
    fun plantationRecord(@Header("Accept")  Accept:String,@Header("Content-Type")  contentRange:String,
                         @Body action: JsonObject):
            retrofit2.Call<JsonObject>

    @POST("query")
    fun putQuery(@Header("token")  contentRange:String,@Body action: HashMap<String, String>):
            retrofit2.Call<JsonObject>

    @GET("faqs")
    fun getQuery():
            retrofit2.Call<List<data_faq>>

    @GET("resourceCategories")
    fun getResourceCAt():
            retrofit2.Call<List<String>>


    @GET
    fun deniedPlant(@Url url: String ):
            retrofit2.Call<List<data_pls>>

    @GET
    fun getResourceDEtail(@Header("token")  contentRange:String,@Url url: String ):
            retrofit2.Call<List<data_resources>>


    @GET
    fun getPlantationDetail(@Header("token")  contentRange:String,@Url url: String ):
            retrofit2.Call<JsonObject>

}