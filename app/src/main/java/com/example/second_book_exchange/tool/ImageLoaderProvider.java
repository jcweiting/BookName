package com.example.second_book_exchange.tool;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.second_book_exchange.MyApplication;
import com.example.second_book_exchange.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderProvider {

    private static ImageLoaderProvider instance = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    synchronized
    public static ImageLoaderProvider getInstance(){
        if (instance == null){
            instance = new ImageLoaderProvider();

            return instance;
        }

        return instance;
    }

    public void initImageLoader(){
        options = new DisplayImageOptions.Builder()
                //若無照片,則顯示以下照片
                .showImageForEmptyUri(R.drawable.book)
                //若讀取失敗,則顯示以下照片
                .showImageOnFail(R.drawable.book)
                //若在loading,則顯示以下照片
                .showImageOnLoading(R.drawable.book)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
                                                                                             //根據MyApplication初始化的名字
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MyApplication.instance)
                .defaultDisplayImageOptions(options).build();
        imageLoader.init(config);
    }

    public void setImage(String photoUrl, ImageView imageView){
        imageLoader.displayImage(photoUrl,imageView);
    }
}
