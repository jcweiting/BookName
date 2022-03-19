package com.book.second_book_exchange.tool;

import android.content.Context;
import android.graphics.Typeface;

import androidx.collection.SimpleArrayMap;

/**
 * 如果他原本就是那個字體就不會再重複拿取，避免記憶體不足
 */
public class TypeFaceHelper {

    private static TypeFaceHelper instance = null;

    private static final SimpleArrayMap<String, Typeface> TYPEFACE_CACHE = new SimpleArrayMap<>();

    public static TypeFaceHelper getInstance(){
        if (instance == null){

            instance = new TypeFaceHelper();

            return instance;
        }
        return instance;
    }

    public Typeface getTypeFace(Context context,String fontName){

        if (!TYPEFACE_CACHE.containsKey(fontName)){

            try{

                Typeface typeface = Typeface.createFromAsset(context.getAssets(),fontName);
                TYPEFACE_CACHE.put(fontName,typeface);

            }catch (Exception e){
                e.printStackTrace();
            }


        }
        return TYPEFACE_CACHE.get(fontName);


    }


}
