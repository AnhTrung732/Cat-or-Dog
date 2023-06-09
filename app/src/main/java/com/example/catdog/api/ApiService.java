package com.example.catdog.api;

import static com.example.catdog.ScanQrApiActivity.SCAN_DOMAIN;

import com.example.catdog.model.Animal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // https://5e19-14-161-13-207.ap.ngrok.io/api/detectUpload


    public static final String DOMAIN = SCAN_DOMAIN;

    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);


    @Multipart
    @POST("/api/detectUpload")
    Call<Animal> sendImageFile(@Part MultipartBody.Part file);

}
