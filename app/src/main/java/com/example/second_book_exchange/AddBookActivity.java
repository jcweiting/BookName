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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.BookStatusDialog;
import com.example.second_book_exchange.tool.ClassifyDialog;
import com.example.second_book_exchange.tool.LoadingDialog;
import com.example.second_book_exchange.tool.ShipmentDialog;
import com.example.second_book_exchange.tool.StorageTool;
import com.example.second_book_exchange.tool.ViewDialog;
import com.example.second_book_exchange.widget.GlideEngine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class AddBookActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    public static final String BOOK_INFO = "Book Info";
    public static final String BOOK_LIST = "book list";
    private ImageView ivAddPicture, ivAddBtn, ivBack, ivSave, ivClear;
    private ImageView ivArrowName, ivArrowClassify, ivArrowDescription, ivArrowQty, ivArrowStatus, ivArrowShipment, ivArrowRemark, ivArrowUnitP, ivArrowTotalP;
    private EditText edName, edClassify, edDescription, edQty, edStatus, edRemark, edShipment, edUnitPrice, edTotalPrice;
    private LoadingDialog loadingDialog;
    private byte[] bytes;
    private int qty, unitPrice;
    private Bitmap bitmap;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        compositeDisposable = new CompositeDisposable();

        initView();

        ivAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //PictureSelector套件
                PictureSelector.create(AddBookActivity.this)
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
                                    ivAddBtn.setVisibility(View.GONE);
                                    ivAddPicture.setImageBitmap(bitmap);

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

        edName.setOnFocusChangeListener(this);
        edDescription.setOnFocusChangeListener(this);
        edQty.setOnFocusChangeListener(this);
        edUnitPrice.setOnFocusChangeListener(this);
        edTotalPrice.setOnFocusChangeListener(this);
        edRemark.setOnFocusChangeListener(this);

        edClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowClassify.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
                showAlertDialogClassify();
            }
        });

        edStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowStatus.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
                showAlertDialogBookStatus();
            }
        });

        edTotalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowTotalP.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            }
        });

        totalPriceData(edUnitPrice, edQty);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null){
                    showHint("請上傳照片");
                    return;
                }
                startToUpload(bitmap);

            }

        });

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("是否確認要刪除所有資料");
            }
        });
    }

    private void totalPriceData(EditText edUnitPrice, EditText edQty) {

        edQty.addTextChangedListener(new TextWatcher() {

            @Override   //監聽輸入之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override   //監聽輸入中
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override   //監聽輸入後
            public void afterTextChanged(Editable editable) {

                String strQty = edQty.getText().toString();

                if (edQty.getText().toString().isEmpty()){
                    return;
                }

                qty = Integer.parseInt(strQty);

                if (edUnitPrice.getText().toString().isEmpty()){
                    return;
                }

                edTotalPrice.setText("NTD "+qty*unitPrice);

            }
        });

        edUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String strUnitPrice = edUnitPrice.getText().toString();

                if (edUnitPrice.getText().toString().isEmpty()){
                    return;
                }

                //若是有除了數字以外的文字, 會顯示提示訊息
                boolean isNumber = edUnitPrice.getText().toString().matches("[+-]?\\d*(\\.\\d+)?");

                if (!isNumber){
                    showHint("請輸入純數字");
                    return;
                }

                unitPrice = Integer.parseInt(strUnitPrice);

                if (edQty.getText().toString().isEmpty()){
                    return;
                }

                edTotalPrice.setText("NTD "+qty*unitPrice);
            }
        });
    }


    private void upLoadData(String photoUrl){

        String bookName = edName.getText().toString();
        String bookClassify = edClassify.getText().toString();
        String bookDescription = edDescription.getText().toString();
        String bookQty = edQty.getText().toString();
        String bookUnitPrice = edUnitPrice.getText().toString();
        String bookStatus = edStatus.getText().toString();
        String bookRemark = edRemark.getText().toString();
        String uid = user.getUid();
        long time = System.currentTimeMillis(); //取得手機目前時間

        if (bookName.isEmpty()||bookClassify.isEmpty()||bookQty.isEmpty()||bookStatus.isEmpty()||bookUnitPrice.isEmpty()){
            showHint("請輸入必填欄位");
            return;
        }

        if (bookQty.equals("0")){
            showHint("數量不應為0");
            return;
        }

        String bookTotalPrice = Integer.parseInt(bookQty) * Integer.parseInt(bookUnitPrice)+"";
        String userEmail = user.getEmail();

        if (userEmail == null){
            Log.i("Joyce","取無使用者Email");
            Toast.makeText(this, "無法取得使用者Email", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("Joyce","upLoadData(String photoUrl) --> 取得userEmail");

        ApiTool.getRequestApi()
                .addBook(new AddBookBasicData(bookName, bookClassify, bookDescription, bookQty, bookUnitPrice, bookTotalPrice,bookStatus, bookRemark, uid, time, photoUrl, userEmail))
                .subscribeOn(Schedulers.io())  //讓程式碼在背景處理
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    //資料上傳成功
                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {
                        Log.i("Joyce","新增成功: "+addBookBasicData.getBookName());
                        JoyceLog.i("AddBookActivity | Uid: "+ new Gson().toJson(addBookBasicData));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("Joyce","e: "+e);
                        showHint("書本上傳失敗");
                    }

                    //確定上傳成功後,finish()
                    @Override
                    public void onComplete() {
                        Log.i("Joyce","onComplete");
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }

    private void showAlertDialogClassify() {

        ClassifyDialog classifyDialog = ClassifyDialog.newInstance();
        classifyDialog.show(getSupportFragmentManager(),"dialog");

        classifyDialog.setListener(new ClassifyDialog.OnClassifyDialogClickListener() {
            @Override
            public void onClassifyClick(String classify) {
                edClassify.setText(classify);
            }
        });
    }

    private void showAlertDialogShipment() {

        ShipmentDialog shipmentDialog = ShipmentDialog.newInstance();
        shipmentDialog.show(getSupportFragmentManager(),"dialog");

        shipmentDialog.setOnShipmentDialogClickListener(new ShipmentDialog.OnShipmentDialogClickListener() {
            @Override
            public void onLevel1Click(String level) {
                edShipment.setText(level);
            }

            @Override
            public void onLevel2Click(String level) {
                edShipment.setText(level);
            }

            @Override
            public void onLevel3Click(String level) {
                edShipment.setText(level);
            }

            @Override
            public void onLevel4Click(String level) {
                edShipment.setText(level);
            }
        });
    }

    private void showAlertDialogBookStatus() {

        //DialogFragment 初始化
        BookStatusDialog bookStatusDialog = BookStatusDialog.newInstance();
        bookStatusDialog.show(getSupportFragmentManager(),"dialog");

        bookStatusDialog.setOnBookStatusDialogClickListener(new BookStatusDialog.OnBookStatusDialogClickListener() {
            @Override
            public void onLevel1Click(String level) {
                edStatus.setText(level);
            }

            @Override
            public void onLevel2Click(String level) {
                edStatus.setText(level);
            }

            @Override
            public void onLevel3Click(String level) {
                edStatus.setText(level);
            }

            @Override
            public void onLevel4Click(String level) {
                edStatus.setText(level);
            }
        });
    }

    private void showAlertDialog(String content) {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(AddBookActivity.this,content);
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

    private void clearEd(){
        edName.setText("");
        edClassify.setText("");
        edDescription.setText("");
        edQty.setText("");
        edStatus.setText("");
        edRemark.setText("");
        edUnitPrice.setText("");
        edTotalPrice.setText("");
    }

    private void showHint(String content) {
        Toast.makeText(AddBookActivity.this,content,Toast.LENGTH_SHORT).show();
    }

    //處理照片                                                  //try catch另一種打法
    private void handlePhoto(CropImage.ActivityResult result) throws IOException {

        Uri uri = result.getUri();
        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

        //顯示照片
        ivAddBtn.setVisibility(View.GONE);
        ivAddPicture.setImageBitmap(bitmap);
    }

    private void startToUpload(Bitmap bitmap) {
        //壓縮照片,轉成byte上傳=============================
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 20;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        bytes = baos.toByteArray();
        //===============================================

        //顯示Loading Dialog
        showProgressDiaLog();

        //上傳照片
        uploadPhotoToStorage(bytes);
    }

    private void uploadPhotoToStorage(byte[] bytes) {

        StorageTool.uploadBookListPhoto(bytes, new StorageTool.OnUploadPhotoResultListener() {
            @Override
            public void onSuccess(String photoUrl) {
//                Log.i("Joyce","uploadPhotoToStorage(byte[] bytes) --> photoUrl: "+photoUrl);
                loadingDialog.dismiss();

//                Log.i("Joyce","uploadPhotoToStorage(byte[] bytes) --> 走完loadingDialog.dismiss()");

                upLoadData(photoUrl);
//                Log.i("Joyce","uploadPhotoToStorage(byte[] bytes) --> 走完upLoadData(url)");
            }

            @Override
            public void onFail(String error) {
                Log.i("Joyce","上傳照片錯誤");
                loadingDialog.dismiss();
                Toast.makeText(AddBookActivity.this,error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDiaLog() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getSupportFragmentManager(),"dialog");
    }

    private void initView() {

        ivAddPicture = findViewById(R.id.book_picture);
        ivAddBtn = findViewById(R.id.book_add);
        ivBack = findViewById(R.id.back);
        ivSave = findViewById(R.id.submit);
        ivClear = findViewById(R.id.clear);
        ivArrowName = findViewById(R.id.arrow);
        ivArrowClassify = findViewById(R.id.arrow1);
        ivArrowDescription = findViewById(R.id.arrow2);
        ivArrowQty = findViewById(R.id.arrow3);
        ivArrowUnitP = findViewById(R.id.arrow4);
        ivArrowTotalP = findViewById(R.id.arrow5);
        ivArrowStatus = findViewById(R.id.arrow6);
        ivArrowRemark = findViewById(R.id.arrow8);

        edName = findViewById(R.id.book_name);
        edClassify = findViewById(R.id.book_classify);
        edDescription = findViewById(R.id.book_description);
        edQty = findViewById(R.id.book_qty);
        edStatus = findViewById(R.id.book_status);
        edRemark = findViewById(R.id.book_remark);
        edUnitPrice = findViewById(R.id.book_unit_price);
        edTotalPrice = findViewById(R.id.book_total_price);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
    }

    //要先implements View.OnFocusChangeListener --> 可以集中管理
    //黃色箭頭跳動效果,因為是EditText,所以另外設了一個實作的方法
    //若是EditText的focusable是關閉的,就可以直接把動畫效果打在點擊事件裡
    //isFocus --> 點擊就是true (點其他按鈕就會變false)
    @Override
    public void onFocusChange(View view, boolean isFocus) {

        if (view.getId() == R.id.book_name && isFocus){
            ivArrowName.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_description && isFocus){
            ivArrowDescription.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_qty && isFocus){
            ivArrowQty.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_unit_price && isFocus){
            ivArrowUnitP.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_total_price && isFocus){
            ivArrowTotalP.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_remark && isFocus){
            ivArrowRemark.startAnimation(AnimationUtils.loadAnimation(AddBookActivity.this,R.anim.animation));
            return;
        }

    }
}