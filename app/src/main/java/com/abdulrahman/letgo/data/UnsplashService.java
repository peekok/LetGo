package com.abdulrahman.letgo.data;



import com.abdulrahman.letgo.data.model.Photo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface UnsplashService {

    String ENDPOINT = "https://unsplash.it";

    @GET("/list")
    void getFeed(Callback<List<Photo>> callback);

}
