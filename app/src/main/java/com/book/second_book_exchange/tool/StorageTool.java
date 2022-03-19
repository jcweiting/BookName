package com.book.second_book_exchange.tool;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * 工具化Firebase Storage
 */

public class StorageTool {

    //宣告
    private static FirebaseStorage storage = null;

    private static FirebaseAuth mAuth;
    private static FirebaseUser user;

    //初始化
    public static void initStorage() {
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    /**
     * 這個是針對BOOK LIST 要上傳圖片的時候需要的方法 交替者用
     */
    public static void uploadBookListPhoto(byte[] photoBytes, OnUploadPhotoResultListener onUploadPhotoResultListener) {

        StorageReference reference = storage.getReference();

        //給上傳的路徑&檔名 UUID.randomUUID().toString() 他會隨機給一個字串 我們就用這個字串來當 他的圖檔的黨名
        StorageReference river = reference.child("BookList/"+ UUID.randomUUID().toString() +".jpg");

        //把照片的byte放入上傳任務中
        UploadTask task = river.putBytes(photoBytes);

        //開始上傳資料
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("Joyce","上傳成功");

                //取得照片網址
                onCatchPhotoDownloadUrl(river, onUploadPhotoResultListener);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("Joyce","上傳失敗"+e.toString());

                onUploadPhotoResultListener.onFail(e.toString());
            }
        });

    }

    /**
     * 這個是針對使用者要上船大頭貼的時候要用的方法
     */
    public static void uploadPhoto(byte[] photoBytes, OnUploadPhotoResultListener onUploadPhotoResultListener) {

        StorageReference reference = storage.getReference();

        //給上傳的路徑&檔名
        //這個路徑 永遠都是一樣的 所以你不管怎麼上傳 他都只會有一張照片  可是我昨天就有很多不同張呀 不知道 他可能過一天後就變回原本的樣子的 所以要動態讓他變圖檔的黨名 personalPhoto 算是放大頭貼的為至
        //還要創一個屬於BOOKLIST的圖檔位置像這樣
        StorageReference river = reference.child("Personal_Photo/"+user.getUid()+"/"+user.getUid()+".jpg");

        //把照片的byte放入上傳任務中
        UploadTask task = river.putBytes(photoBytes);

        //開始上傳資料
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("Joyce","上傳成功");

                //取得照片網址
                onCatchPhotoDownloadUrl(river, onUploadPhotoResultListener);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("Joyce","上傳失敗"+e.toString());

                onUploadPhotoResultListener.onFail(e.toString());
            }
        });

    }

    private static void onCatchPhotoDownloadUrl(StorageReference river, OnUploadPhotoResultListener onUploadPhotoResultListener) {

        river.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                String photoDownLoadUrl = uri.toString();
                Log.i("Joyce","取得照片網址");

                //將照片網址導向監聽的地方
                onUploadPhotoResultListener.onSuccess(photoDownLoadUrl);
            }
        });
    }


    //設立接口監聽,上傳照片成功or失敗
    public interface OnUploadPhotoResultListener {
        void onSuccess(String url);

        void onFail(String error);
    }
}
