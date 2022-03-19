package com.book.second_book_exchange.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiTool {

    //後端工程師會給一串基礎網址(不用理解網址裡面的意思),放在""裡
    private static final String BASE_URL = "http://springbootmysqlsecondbookaws-env.eba-sny9gwm2.us-east-1.elasticbeanstalk.com/";

    //初始化Retrofit套件=================================================================
    private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    private static final Retrofit retrofit = retrofitBuilder.build();
    //===================================================================================


    //把interface實體化===================================================================
    private static final RequestApi requestApi = retrofit.create(RequestApi.class);

    public static RequestApi getRequestApi(){
        return requestApi;
    }
    //===================================================================================

}
