package com.example.myasm;

        import java.util.List;

        import okhttp3.MultipartBody;
        import okhttp3.RequestBody;
        import retrofit2.Call;
        import retrofit2.Response;
        import retrofit2.http.Body;
        import retrofit2.http.DELETE;
        import retrofit2.http.GET;
        import retrofit2.http.Multipart;
        import retrofit2.http.POST;
        import retrofit2.http.PUT;
        import retrofit2.http.Part;
        import retrofit2.http.Path;
        import retrofit2.http.Query;

public interface APIService {
    String DOMAIN = "http://10.24.12.29:3000/";//192.168.1.249:3000
    @GET("/api/list")
    Call<List<CarModel>> getCars();
    @POST("/api/addCar")
    Call<CarModel> addCar(@Body CarModel carModel);
    @PUT("/api/updateCar/{id}")
    Call<Response<CarModel>> updateCarById(@Path("id") String id, @Body CarModel carWithoutImage);
    @DELETE("/api/deleteCar/{id}")
    Call<Response<Void>> deleteCarById(@Path("id") String id);

//    @GET("/api/search")
//    Call<List<CarModel>> searchCars(@Query("query") String query);

    @GET("/api/search")
    Call<List<CarModel>> searchCarsByName(@Query("name") String name);

//    @GET("/api/sort")
//    Call<List<CarModel>> sortCars(@Query("sortBy") String sortBy);

    @GET("/api/sort")
    Call<List<CarModel>> sortCars(@Query("sortBy") String sortBy);

    @Multipart
    @POST("/api/add-car-with-images")
    Call<CarModel> addCarWithImage(
            @Part("ten") RequestBody ten,
            @Part("namSX") RequestBody namSX,
            @Part("hang") RequestBody hang,
            @Part("gia") RequestBody gia,
            @Part MultipartBody.Part anh // Đảm bảo khớp với key mà server của bạn mong đợi
    );

}
