package com.example.myasm;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    String DOMAIN = "http://10.24.33.63:3000/";//192.168.1.249:3000
    @GET("/api/list")
    Call<List<CarModel>> getCars();
    @POST("/api/addCar")
    Call<CarModel> addCar(@Body CarModel carModel);
    @PUT("/api/updateCar/{id}")
    Call<Response<CarModel>> updateCarById(@Path("id") String id, @Body CarModel carWithoutImage);
    @DELETE("/api/deleteCar/{id}")
    Call<Response<Void>> deleteCarById(@Path("id") String id);

    @GET("/api/search")
    Call<List<CarModel>> searchCars(@Query("query") String query);

    @GET("/api/sort")
    Call<List<CarModel>> sortCars(@Query("sortBy") String sortBy);
}
