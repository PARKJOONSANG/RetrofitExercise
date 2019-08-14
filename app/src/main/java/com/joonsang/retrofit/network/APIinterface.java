package com.joonsang.retrofit.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIinterface {


    @FormUrlEncoded
    @POST("retrieveLoginInfo.do")
    Call<UserInfo> userInfo(@Field("userId") String userId, @Field("userPwd") String userPwd);




}
