package com.prakash.locationreminder.interfaces;

import com.prakash.locationreminder.models.LongLat;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIEndPoints {
    @FormUrlEncoded

    @GET("users/logout")
    Call<Void> logout(@Header("Cookie") String cookie);

    @POST("longlat")
    Call<ResponseBody> postlonglat(@Header("Cookie") String cookie, @Body LongLat longLat);

    @GET("longlat")
    Call<List<LongLat>> getlonglat(@Header("Cookie") String cookie);

    @PUT("longlat/{longlatId}")
    Call<Void> updatelonglat(@Header("Cookie") String cookie, @Path("longlatId") String longlatId, @Body LongLat longLat);

    @DELETE("longlat/{longlatId}")
    Call<Void> deletelonglat(@Header("Cookie")String cookie,  @Path("longlatId") String longlatId);

}


