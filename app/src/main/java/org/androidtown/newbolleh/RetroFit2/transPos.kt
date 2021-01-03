package org.androidtown.newbolleh.RetroFit2

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface transPos {
    @GET("/v2/local/geo/transcoord.json")
    fun getTransPos(
        @Header("Authorization") auth:String,
        @Query("x") x:String,
        @Query("y") y:String,
        @Query("input_coord") input_coord:String,
        @Query("output_coord") output_coord:String
    ): retrofit2.Call<Data>
}