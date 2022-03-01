package com.example.second_book_exchange.log;

import android.util.Log;

public class JoyceLog {

    //因為方法是 static, 所以不需要NEW物件 可以直接呼叫

    public static void i(String msg){

        Log.i("Joyce",msg);
    }

}
