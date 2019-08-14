package com.joonsang.retrofit.network;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    public UserInfo(String userId, String userPwd){
        this.userId = userId;
        this.userPwd = userPwd;
    }
    @SerializedName("userId")
    String userId;
    @SerializedName("userPwd")
    String userPwd;


    @SerializedName("userName")
   public String userName;
    @SerializedName("userCode")
   public String userCode;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


}
