package com.book.second_book_exchange.tool;

import android.util.TypedValue;

import com.book.second_book_exchange.MyApplication;

/**
 * 把int轉成dp的工具
 */

public class DpConvertTool {
    private static DpConvertTool instance = null;

    public static DpConvertTool getInstance(){
        if (instance == null){
            instance = new DpConvertTool();
            return instance;
        }
        return instance;
    }

    public int getDb(int pix){
        int dp = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pix, MyApplication.instance.getApplicationContext().getResources().getDisplayMetrics()));
        return dp;
    }

}