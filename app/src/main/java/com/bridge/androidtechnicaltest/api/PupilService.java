package com.bridge.androidtechnicaltest.api;

import com.bridge.androidtechnicaltest.model.Pupils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Provide an API interface to execute GET/POST/DELETE etc. request
 */

public interface PupilService {

    @GET("api/pupils")
    Call<Pupils> getPupilsInfo(@Query("page") int pageIndex);

    @DELETE("/api/pupils/{pupilId}")
    Call<ResponseBody> deletePupil(@Path("pupilId") int pupilId);
}
