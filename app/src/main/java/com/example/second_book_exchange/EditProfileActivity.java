package com.example.second_book_exchange;

import static com.example.second_book_exchange.fragment.MemberFragment.USER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.api.UserAllInformation;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.ImageLoaderProvider;
import com.example.second_book_exchange.tool.LoadingDialog;
import com.example.second_book_exchange.tool.StorageTool;
import com.example.second_book_exchange.tool.ViewDialog;
import com.example.second_book_exchange.widget.GlideEngine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivClear, ivSave, ivUploadPic, ivUploadPicIcon;
    private EditText edAccount, edNickname, edTel, edEmail;
    private TextInputLayout tilNickName, tilTel;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private UserBasicData userBasicData;
    private Bitmap bitmap;
    private byte[] bytes;
    private LoadingDialog loadingDialog;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        compositeDisposable = new CompositeDisposable();
        userBasicData = new UserBasicData();

        initView();

        //找出會員資料顯示
        getMemberInfo();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("是否確認刪除所有資料");
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //照片欄是空的,錯誤提醒
                if (bitmap == null && userBasicData.getUserPhotoUrl()==null) {
                    showHint("請上傳圖片");
                    return;
                }

                //有上傳新照片
                if (bitmap != null){
                    startUploadPic(bitmap);
                    return;
                }

                //照片欄原本已有照片,沒有要再上傳新照片
                if (userBasicData.getUserPhotoUrl() != null) {
                    uploadPersonalData(userBasicData.getUserPhotoUrl());
                    return;
                }
            }
        });

        ivUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //PictureSelector套件
                PictureSelector.create(EditProfileActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setMaxSelectNum(1)
                        .setMinSelectNum(1)
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {

                                String localMedia = result.get(0).getPath();
                                Uri uri = Uri.parse(localMedia);

                                try {
                                    //處理照片
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                                    //顯示照片
                                    ivUploadPicIcon.setVisibility(View.GONE);
                                    ivUploadPic.setImageBitmap(bitmap);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
            }
        });
    }

    private void startUploadPic(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 20;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        bytes = baos.toByteArray();

        showProgressDialog();
        uploadPicToStorage(bytes);

    }

    private void uploadPicToStorage(byte[] bytes) {
        StorageTool.uploadBookListPhoto(bytes, new StorageTool.OnUploadPhotoResultListener() {
            @Override
            public void onSuccess(String userPhotoUrl) {

                Log.i("Joyce", "EditProfile --> uploadPicToStorage --> 上傳照片成功");
                loadingDialog.dismiss();
                uploadPersonalData(userPhotoUrl);
            }

            @Override
            public void onFail(String error) {

                Log.i("Joyce", "EditProfile --> uploadPicToStorage --> 上傳照片失敗");
                loadingDialog.dismiss();
                showHint("照片上傳失敗");
            }
        });
    }

    private void uploadPersonalData(String userPhotoUrl) {


        //使用者輸入其他資料
        String nickName = edNickname.getText().toString();
        String tel = edTel.getText().toString();

        if (isCheckNewData(nickName, tel)) {
            return;
        }

        userBasicData.setNickName(nickName);
        JoyceLog.i("EditProfileActivity | setNickName: "+nickName);

        userBasicData.setTel(tel);
        JoyceLog.i("EditProfileActivity | setTel: "+tel);

        userBasicData.setUserPhotoUrl(userPhotoUrl);
        JoyceLog.i("EditProfileActivity | 上傳的PhotoUrl: " + userPhotoUrl);

        JoyceLog.i("EditProfileActivity | userBasicData: "+new Gson().toJson(userBasicData));

        ApiTool.getRequestApi()
                .editUserData(userBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserBasicData userBasicData) {
                        showHint("個人資料儲存成功");
                        finish();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("EditProfileActivity | editUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("EditProfileActivity | editUserData | onComplete");
                    }
                });
    }

    private void showHint(String content) {
        Toast.makeText(EditProfileActivity.this, content, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {

        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getSupportFragmentManager(), "dialog");

        Log.i("Joyce", "EditProfile --> showProgressDiaLog() --> show Dialog");

    }

    private boolean isCheckNewData(String nickName, String tel) {
        boolean isNeedToWriteData = false;

        if (nickName.isEmpty() && tel.isEmpty()) {
            tilNickName.setError("請輸入暱稱");
            tilTel.setError("請輸入電話");
            return true;
        }

        if (TextUtils.isEmpty(nickName)) {
            tilNickName.setError("請輸入暱稱");
            isNeedToWriteData = true;
        } else if (nickName.length() > tilNickName.getCounterMaxLength() || nickName.length() < 1) {
            tilNickName.setError("用戶名稱至少需1個字元");
            isNeedToWriteData = true;
        } else {
            tilNickName.setError(null);
        }

        if (TextUtils.isEmpty(tel)) {
            tilTel.setError("請輸入手機");
            isNeedToWriteData = true;
        } else if (tel.length() != 10) {
            tilTel.setError("手機號碼長度錯誤");
            isNeedToWriteData = true;
        } else {
            tilTel.setError(null);
        }

        return isNeedToWriteData;
    }

    private void showAlertDialog(String content) {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(EditProfileActivity.this, content);
        alert.setOnAlertDialogClickListener(new ViewDialog.OnAlertDialogClickListener() {
            @Override
            public void onConfirm() {
                edNickname.setText("");
                edTel.setText("");
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void getMemberInfo() {

        userBasicData.setUserUid(user.getUid());
        userBasicData.setEmail(user.getEmail());

        ApiTool.getRequestApi()
                .checkUserData(userBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserBasicData userData) {

                        JoyceLog.i("EditProfileActivity | userData: "+new Gson().toJson(userData));
                        userBasicData = userData;

                        edAccount.setText(userData.getEmail());
                        edEmail.setText(userData.getEmail());
                        edNickname.setText(userData.getNickName());
                        edTel.setText(userData.getTel());
                        ImageLoaderProvider.getInstance().setImage(userData.getUserPhotoUrl(), ivUploadPic);

                        if (userData.getUserPhotoUrl() != null) {
                            ivUploadPicIcon.setVisibility(View.GONE);
                            Log.i("Joyce", "EditProfileActivity | 原本的PhotoUrl: " + userData.getUserPhotoUrl());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("EditProfileActivity | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("EditProfileActivity | checkUserData | onComplete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void initView() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ivBack = findViewById(R.id.back);
        ivClear = findViewById(R.id.clear);
        ivSave = findViewById(R.id.save);
        ivUploadPic = findViewById(R.id.upload_picture);
        ivUploadPicIcon = findViewById(R.id.upload_add);

        edAccount = findViewById(R.id.ed_account);
        edNickname = findViewById(R.id.ed_nickname);
        edTel = findViewById(R.id.ed_tel);
        edEmail = findViewById(R.id.ed_email);

        tilNickName = findViewById(R.id.lo_nickname);
        tilTel = findViewById(R.id.lo_tel);
    }
}