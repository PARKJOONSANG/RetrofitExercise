package com.joonsang.retrofit.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit retrofit = null;
    public static String BaseURL = "https://reqres.in";

            //  "https://reqres.in" 이곳에 들어가면 샘플데이터를 얻을 수 있다. request 형식과 response 형식을 볼 수 있다.
            //get 방식 url = https://reqres.in/api/users?page=2
            //post 방식 url = https://reqres.in/api/users?

    public static Retrofit getClient(){

        //HttpClient를 구성한다.
        //OKHttp에 인터셉터를 구성한다.만약에 인코딩이 필요하다 이 부분에서 인코딩 디코딩 작업을 시행 한다.
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
        .baseUrl(BaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build();
        //다음 레트로핏 구성은 기본구성 이라고 할 수 있다.
        //첫줄 baseUrl 적용
        //두번째 줄 parsing  모듈 적용 GsonConverterFactory.create() 이다. 자동으로 json데이터를 파싱해준다.
        //세번째 줄 위에서 선언한 OkHttpClient 적

        return retrofit;

    }

}


