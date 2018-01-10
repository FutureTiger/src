package com.example.xd.myapplication.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by xd on 17-11-6.
 */

public interface GankApi {
    @Headers("User-Agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
    @GET("info/suggestion")
    Call<ResponseBody> getApiInfo();
}
