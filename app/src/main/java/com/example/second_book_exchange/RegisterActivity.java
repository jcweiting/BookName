package com.example.second_book_exchange;

import static com.example.second_book_exchange.fragment.MemberFragment.USER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.LoadingDialog;
import com.example.second_book_exchange.tool.StorageTool;
import com.example.second_book_exchange.tool.ViewDialog;
import com.example.second_book_exchange.widget.GlideEngine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack, ivClear, ivSubmit, ivUploadPic, ivUploadPicIcon;
    private EditText edAccount, edPassword, edNickName, edTel, edEmail;
    private TextInputLayout tilAccount, tilPassword, tilNickName, tilTel, tilEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Bitmap bitmap;
    private byte[] bytes;
    private LoadingDialog loadingDialog;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        JoyceLog.i("start add new book");

        compositeDisposable = new CompositeDisposable();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initView();

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

        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null){
                    showHint("請上傳照片");
                    return;
                }

                startToUploadPic(bitmap);
            }
        });

        ivUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //PictureSelector套件
                PictureSelector.create(RegisterActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setMaxSelectNum(1)
                        .setMinSelectNum(1)
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {

                                String localMedia = result.get(0).getPath();
                                JoyceLog.i("localMedia: "+result.get(0).getPath());

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
                
//                啟動選擇照片,導向onActivityResult
//                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
//                        .start(RegisterActivity.this);
            }
        });

        edAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                edEmail.setText(edAccount.getText().toString());
            }
        });
    }

    private void startToUploadPic(Bitmap bitmap) {

        //壓縮照片,轉成byte上傳
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 20;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        bytes = baos.toByteArray();

        //顯示Loading Dialog
        showProgressDialog();

        //上傳照片
        uploadPicToStorage(bytes);
    }

    private void uploadPicToStorage(byte[] bytes) {

        StorageTool.uploadBookListPhoto(bytes, new StorageTool.OnUploadPhotoResultListener() {
            @Override
            public void onSuccess(String userPhotoUrl) {

                Log.i("Joyce","Register --> uploadPicToStorage --> 上傳照片成功");
                loadingDialog.dismiss();

                uploadPersonalData(userPhotoUrl);
            }

            @Override
            public void onFail(String error) {

                Log.i("Joyce","Register --> uploadPicToStorage --> 上傳照片失敗");
                loadingDialog.dismiss();
                showHint("照片上傳失敗");
            }
        });
    }

    private void uploadPersonalData(String userPhotoUrl) {

        String account = edAccount.getText().toString();
        String password = edPassword.getText().toString();
        String nickName = edNickName.getText().toString();
        String tel = edTel.getText().toString();
        String email= edEmail.getText().toString();

        if (isCheckNewData(account, password, nickName, tel, email)) {
            return;
        }

        //註冊帳號須為email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Joyce", "註冊成功");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //註冊成功後,使用者要建置基本資料
                            updateUserBasicData(user, userPhotoUrl);
                            updateUI();

                        } else {
                            Log.i("Joyce", "failed + " + task.getException());
                            showHint("註冊失敗");
                        }
                    }
                });
    }

    private void showProgressDialog() {

        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getSupportFragmentManager(),"dialog");

        Log.i("Joyce","Register --> showProgressDiaLog() --> show Dialog");

    }

//    照片轉成data格式,處理照片
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (result == null){
//                Log.i("Joyce","無任何資料");
//                return;
//            }
//
//            try {
//
//                //處理照片
//                Uri uri = result.getUri();
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
//
//                //顯示照片
//                ivUploadPicIcon.setVisibility(View.GONE);
//                ivUploadPic.setImageBitmap(bitmap);
//
//            }catch (Exception e ){
//                e.printStackTrace();
//            }
//        }
//    }

    private void updateUserBasicData(FirebaseUser user, String userPhotoUrl) {

        UserBasicData userBasicData = new UserBasicData();

        userBasicData.setUserUid(user.getUid());
        userBasicData.setEmail(user.getEmail());
        userBasicData.setNickName(edNickName.getText().toString());
        userBasicData.setTel(edTel.getText().toString());
        userBasicData.setUserPhotoUrl(userPhotoUrl);

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
                    public void onNext(@NonNull UserBasicData userBasicData) {
                        updateUI();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("RegisterActivity | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("RegisterActivity | checkUserData | onComplete");
                    }
                });
    }

    private void updateUI() {
        finish();
    }

    private boolean isCheckNewData(String account, String password, String nickName, String tel, String email) {

        //這邊的變數只是讓他去檢查所有的資料是否需要重新修正 所以只要有任何的錯誤馬 他就會變成True
        boolean isNeedToWriteData = false;

        //這邊是防止他甚麼都沒填,就案註冊
        if (account.isEmpty() && password.isEmpty() && nickName.isEmpty() && tel.isEmpty() && email.isEmpty()) {

            tilAccount.setError("請輸入帳號");
            tilPassword.setError("請輸入密碼");
            tilNickName.setError("請輸入用戶名稱");
            tilTel.setError("請輸入手機");
            tilEmail.setError("請輸入電子信箱");
            return true;
        }

        //必須要讓它群組起來(一起判斷)才不會發生同一個元件判斷兩次的問題 導致錯誤訊息出不來
        if (TextUtils.isEmpty(account)) {
            tilAccount.setError("請輸入帳號");
            isNeedToWriteData = true;
        } else if (!isEmailValid(account)) {
            isNeedToWriteData = true;
            tilAccount.setError("帳號格式需為Email");
        } else {
            tilAccount.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("請輸入密碼");
            isNeedToWriteData = true;
        } else if (password.length() > tilPassword.getCounterMaxLength() || password.length() < 6) {
            tilPassword.setError("密碼需為6~8個字元");
            isNeedToWriteData = true;
        } else if (!isPasswordUpperCase(password)) {
            tilPassword.setError("密碼首字需為大寫英文字");
            isNeedToWriteData = true;
        } else {
            Log.i("Joyce", "密碼需為6~8個字元 null");
            tilPassword.setError(null);
        }


        if (TextUtils.isEmpty(nickName)) {
            tilNickName.setError("請輸入用戶名稱");
            isNeedToWriteData = true;
        }else if (nickName.length() > tilNickName.getCounterMaxLength() || nickName.length() < 1) {
            tilNickName.setError("用戶名稱至少需1個字元");
            isNeedToWriteData = true;
        } else {
            tilNickName.setError(null);
        }

        if (TextUtils.isEmpty(tel)) {
            tilTel.setError("請輸入手機");
            isNeedToWriteData = true;
        }else if (tel.length() != 10) {
            tilTel.setError("手機號碼長度錯誤");
            isNeedToWriteData = true;
        } else {
            tilTel.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("請輸入Email");
            isNeedToWriteData = true;
        } else if (!isEmailValid(email)) {
            tilEmail.setError("Email格式錯誤");
            isNeedToWriteData = true;
        } else {
            tilEmail.setError(null);
        }

        return isNeedToWriteData;
    }

    private boolean isPasswordUpperCase(String password) {
        return Character.isUpperCase(password.charAt(0));
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showAlertDialog(String content) {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(RegisterActivity.this, content);
        alert.setOnAlertDialogClickListener(new ViewDialog.OnAlertDialogClickListener() {
            @Override
            public void onConfirm() {
                clearEd();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void showHint(String content){
        Toast.makeText(RegisterActivity.this,content,Toast.LENGTH_SHORT).show();
    }

    private void clearEd() {
        edAccount.setText("");
        edPassword.setText("");
        edNickName.setText("");
        edTel.setText("");
        edEmail.setText("");

        tilAccount.setError(null);
        tilPassword.setError(null);
        tilNickName.setError(null);
        tilTel.setError(null);
        tilEmail.setError(null);
    }

    private void initView() {

        JoyceLog.i("initView");

        ivBack = findViewById(R.id.back_register);
        ivClear = findViewById(R.id.clear);
        ivSubmit = findViewById(R.id.submit);
        ivUploadPic = findViewById(R.id.upload_picture);
        ivUploadPicIcon = findViewById(R.id.upload_add);

        edAccount = findViewById(R.id.ed_account);
        edPassword = findViewById(R.id.ed_password);
        edNickName = findViewById(R.id.ed_nickname);
        edTel = findViewById(R.id.ed_tel);
        edEmail = findViewById(R.id.ed_email);

        tilAccount = findViewById(R.id.lo_account);
        tilPassword = findViewById(R.id.lo_password);
        tilNickName = findViewById(R.id.lo_nickname);
        tilTel = findViewById(R.id.lo_tel);
        tilEmail = findViewById(R.id.lo_email);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}