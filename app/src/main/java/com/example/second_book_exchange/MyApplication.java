package com.example.second_book_exchange;

import android.app.Application;
import android.util.Log;

import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.ImageLoaderProvider;
import com.example.second_book_exchange.tool.StorageTool;


//開發都要先把這個建立起來
//然後到manifests 裡面的 name = ".MyApplication"
//這個物件在每次APP重新開啟時都會跑 所以有一些工具需要初始化的我們都會擺在這
//如果把Firebase物件化的話, 初始化打在這邊就可以

public class MyApplication extends Application {

    public static MyApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JoyceLog.i("MyApplication | 初始化");

        //需先建立Storage物件
        StorageTool.initStorage();
        JoyceLog.i("MyApplication | StorageTool初始化");

        //需先建立ImageLoaderProvider物件
        ImageLoaderProvider.getInstance().initImageLoader();
        JoyceLog.i("MyApplication | ImageLoaderProvider初始化");
    }

}
