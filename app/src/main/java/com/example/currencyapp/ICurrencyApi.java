package com.example.currencyapp;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ICurrencyApi {
    @GET("symbols")
    Call<JsonObject> getCurrencies();

    @GET("latest?")
    Call<JsonObject> getRates(
            @Query("base") String base,
            @Query("symbols") String list
    );

    @GET("timeseries?")
    Call<JsonObject> getTimeSeries(
            @Query("start_date") String startDate,
            @Query("end_date") String end_date,
            @Query("base") String base,
            @Query("symbols") String symbols
    );
}
